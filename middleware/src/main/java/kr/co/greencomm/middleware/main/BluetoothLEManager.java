package kr.co.greencomm.middleware.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telecom.ConnectionService;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import kr.co.greencomm.middleware.bluetooth.BluetoothCommand;
import kr.co.greencomm.middleware.bluetooth.ConnectStatus;
import kr.co.greencomm.middleware.bluetooth.DeviceBaseScan;
import kr.co.greencomm.middleware.bluetooth.DeviceUUID;
import kr.co.greencomm.middleware.bluetooth.RequestAction;
import kr.co.greencomm.middleware.bluetooth.ScanMode;
import kr.co.greencomm.middleware.bluetooth.StatePeripheral;
import kr.co.greencomm.middleware.bluetooth.TimerBase;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.provider.ProviderValues;
import kr.co.greencomm.middleware.provider.SQLHelper;
import kr.co.greencomm.middleware.service.MWBroadcastTop;
import kr.co.greencomm.middleware.utils.CourseLabel;
import kr.co.greencomm.middleware.utils.FileManager;
import kr.co.greencomm.middleware.utils.MessageClass;
import kr.co.greencomm.middleware.utils.NoticeIndex;
import kr.co.greencomm.middleware.utils.ProductCode;
import kr.co.greencomm.middleware.utils.SMSHelper;
import kr.co.greencomm.middleware.utils.container.ActivityData;
import kr.co.greencomm.middleware.utils.container.Battery;
import kr.co.greencomm.middleware.utils.container.CommQueue;
import kr.co.greencomm.middleware.utils.container.IndexTimeData;
import kr.co.greencomm.middleware.utils.container.SleepData;
import kr.co.greencomm.middleware.video.INordicFormat;
import kr.co.greencomm.middleware.video.iStressNormal;
import kr.co.greencomm.middleware.service.DfuService;


public final class
BluetoothLEManager extends TimerBase {
    private static final String tag = "BluetoothLEManager";

    private static boolean DEBUG = true;

    private SQLHelper mSQLHelper;

    public static final int SUCCESS = 0;
    public static final int FAILED = 1;

    private boolean isLiveApp = false;
    private short timeZoneOffset = 0;

    // 규창 16.11.10 펌웨어 업데이트 대응 변수 추가
    public static boolean DfuMode = false;
    public static String path = null;

    // 규창 16.12.09 블루투스 강제로 끄고 켜는 플래그
    // G4, 엑페는 끄고 켜야만 부트모드에 접속이 가능
    public static boolean Dfu_Force_Ble = false;
    // DFU밴드가 스캔은 되고 연결시도는 하나 밴드가 연결을 거부하여 다시 스캔하게 된다
    // 블루투스를 강제로 끄고 켤 경우 연결이 되는데 SteadyForceHWBLE 3회째에는 블루투스를 강제로 Off-On하여 G4, 엑페같은 폰들에게 적용
    private static int SteadyForceHWBLE = 0;

    public static boolean FirmUpComplete = false;


    // 규창 16.11.28 펌웨어 업데이트 프로그레스를 보고 복구 모드 들어갈지 정하기 위해 저장
    public static int Progress = 0;

    private Handler packetHandler1;
    private Runnable runnablePacket1 = new Runnable() {
        @Override
        public void run() {
            if (mNordicCb != null)
                mNordicCb.onSensor(Packet1.packet2());
        }
    };

    //규창 -- 코치 노말/미니 스트레스매니저 객체
    private StressNManager mStressNManger;

    //규창 -- 코치 노말/미니 스트레스측정 센서 데이터 전송용 인터페이스
    private Handler packetHandler2;
    private Runnable runnablePacket2 = new Runnable() {
        @Override
        public void run() {
            if (mStressN != null) {
                mStressN.onStresshr(Packet1.packet2());
                Log.d(tag, "runnablePacket2" + Packet1.packet2());
            }
        }
    };


    private Timer mTimer;// = new Timer();
    //규창 -- 펌웨어 업데이트 블루투스 하드웨어 컨트롤 ON을 무조건 하게 하기 위함
    private Handler hwbluetoothCtrl;
    private Runnable tryhwBTON = new Runnable() {
        @Override
        public void run() {
            if (!DeviceBaseScan.mBTAdapter.isEnabled()){
                run_BTon();
            }
        }
    };
    private void run_BTon(){
        if (mTimer != null) return;
        mTimer = new Timer("BTonTimer");
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(Dfu_Force_Ble == true || FirmUpComplete == true) {
                    DeviceBaseScan.mBTAdapter.enable();
                    Dfu_Force_Ble = false;
                    FirmUpComplete = false;
                    SteadyForceHWBLE = 0;
                }
                if (DeviceBaseScan.mBTAdapter.isEnabled()){
                    //new Handler().postDelayed(new Runnable() {
                    //@Override
                    //    public void run() {
                    BTonSucess();
                    //    }
                    //}, 2000);
                }
            }
        }, 0, 1000);
    }

    private void BTonSucess(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        //task = null;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startBluetooth();
                hwbluetoothCtrl.removeCallbacks(tryhwBTON);
            }
        }, 1000);
        //startBluetooth();
        //hwbluetoothCtrl.removeCallbacks(tryhwBTON);
    }


    private static class Packet1 {
        static byte cmd;
        static short seq; // 번호를 양수로 표시하기 위해..

        static short acc_x0;
        static short acc_y0;
        static short acc_z0;
        static short press0;

        static short acc_x1;
        static short acc_y1;
        static short acc_z1;
        static short press1;

        static short hr0;
        static short hr1;

        /** 배열의 0,1,2 = 가속도 x,y,z **/
        /** 배열의 3,4,5 = 자이로 x,y,z **/
        /** 배열의 6 = 기압 **/
        /**
         * 배열의 7 = 심박
         **/
        static double[] packet1() {
            double[] packet = new double[8];
            packet[0] = ((double) acc_x0) / 100 * 2;
            packet[1] = ((double) acc_y0) / 100 * 2;
            packet[2] = ((double) acc_z0) / 100 * 2;
            packet[3] = 0;
            packet[4] = 0;
            packet[5] = 0;
            packet[6] = ((double) press0) / 100;
            packet[7] = ((double) (hr0 & 0xff));

            return packet;
        }

        static double[] packet2() {
            double[] packet = new double[8];
            packet[0] = ((double) acc_x1) / 100 * 2;
            packet[1] = ((double) acc_y1) / 100 * 2;
            packet[2] = ((double) acc_z1) / 100 * 2;
            packet[3] = 0;
            packet[4] = 0;
            packet[5] = 0;
            packet[6] = ((double) press1) / 100;
            packet[7] = ((double) (hr1 & 0xff));

            return packet;
        }

        static void init() {
            acc_x0 = acc_y0 = acc_z0 = press0 = hr0 = 0;
            acc_x1 = acc_y1 = acc_z1 = press1 = hr1 = 0;
        }
    }

    private static BluetoothLEManager mInstance = null;

    private static INordicFormat mNordicCb;
    //규창 - 스트레스 인터페이스
    private static iStressNormal mStressN;


    protected static void registDataCallback(INordicFormat cb) {
        Log.d("jeyang", "Coach Bluetooth registDataCallback : " + cb);
        mNordicCb = cb;
    }

    protected static void unregistDataCallback() {
        Log.d("jeyang", "Coach Bluetooth unregistDataCallback");
        mNordicCb = null;

        //규창 - 스트레스 인터페이스 초기화
        mStressN = null;
    }

    // 규창 - 스트레스 인터페이스 등록
    protected static void registDataCallback(iStressNormal iSN) {
        Log.d("Stress_Normal", "Coach Bluetooth registDataCallback : " + iSN);
        mStressN = iSN;
    }


    public static BluetoothLEManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BluetoothLEManager(context);
        }

        return mInstance;
    }

    private BluetoothLEManager(Context context) {
        super(context);
        mSQLHelper = SQLHelper.getInstance(getContext());
        packetHandler1 = new Handler();
        //규창 - 코치 노말 스트레스 인터페이스와 객체 초기화
        packetHandler2 = new Handler();
        //규창 - 펌웨어업데이트 블루투스 ON 핸들러
        hwbluetoothCtrl = new Handler();
        mStressNManger = StressNManager.getInstance(getContext());
    }

    public String getRemoteMac() {
        if (mBluetoothGatt == null)
            return null;
        return mBluetoothGatt.getDevice().getAddress();
    }

    private boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        } catch (Exception localException) {
            Log.e(tag, "An exception occured while refreshing device");
        }
        return false;
    }

    /**
     * GATT 객체에서 Service, characteristic UUID 정보를 리스트에 추가.
     *
     * @param gatt gatt 객체.
     */
    private void getServiceFromGATT(BluetoothGatt gatt) {
        List<BluetoothGattService> gattServices = gatt.getServices();

        Log.i(tag, "getServiceFromGATT!!!");

        // 서비스 밑에 캐릭터가 달려 있으므로, 리스트뷰로 작성하는게 맞음. -> 걍 어레이로...
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                final int charaProp = gattCharacteristic.getProperties();

                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    gatt.setCharacteristicNotification(gattCharacteristic, true);
                } else {
                    gatt.setCharacteristicNotification(gattCharacteristic, false);
                }
                Log.i(tag, gattCharacteristic.getUuid().toString());
            }
        }

        if (DfuMode == false) {
            BluetoothGattService service = gatt.getService(DeviceUUID.RX_SERVICE_UUID);
            if (service != null) {
                BluetoothGattCharacteristic mChar = service.getCharacteristic(DeviceUUID.TX_CHAR_UUID);
                BluetoothGattDescriptor descriptor = mChar.getDescriptor(DeviceUUID.CCCD);
                Log.d(tag, "1.descriptor : " + descriptor);
                Log.d(tag, "cccd : " + BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE.length);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                Log.i(tag, "descriptor:" + descriptor + "BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE" + BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
            sender.setReady(true);
        }


        // 규창 16.11.10 펌웨어 업데이트 대응 변수 추가
        if (DfuMode == true)  {
            BluetoothGattService DFUservice = gatt.getService(DeviceUUID.DFU_SERVICE_UUID);
            if (DFUservice != null) {
                //DfuMode = true;
                Log.i(tag, "DFU Clear!!1.");
                BluetoothGattCharacteristic controlPointCharacteristic = DFUservice.getCharacteristic(DeviceUUID.DFU_CONTROL_POINT_UUID);
                //BluetoothGattCharacteristic packetCharacteristic = DFUservice.getCharacteristic(DeviceUUID.DFU_CONTROL_POINT_UUID);
                Log.i(tag, "DFU Clear!!2.");
                BluetoothGattDescriptor descriptor = controlPointCharacteristic.getDescriptor(DeviceUUID.CCCD);
                Log.d(tag, "2.descriptor : " + descriptor);
                Log.d(tag, "cccd : " + BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE.length);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                //gatt.writeDescriptor(descriptor);
                Log.i(tag, "descriptor:" + descriptor + "BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE" + BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            }
        }

        // if (DfuMode == false) {
        //     sender.setReady(true);
        // }

    }

    /**
     * GATT 콜백. 연결에 대한 이벤트를 받을수 있다. (연결 상태, 읽기, 쓰기, 상태 변화)
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(tag, "Connected to GATT server.");
                synchronized (this) {
                    try {
                        wait(600);
                    } catch (InterruptedException e) {
                        //do nothing
                    }
                }

                // Attempts to discover services after successful connection.
                Log.i(tag, "Attempting to start service discovery:" + gatt.discoverServices());

                scanLeDevice(false);
                cancelScanTimer();

                if (!DfuMode) {
                    sender.start();
                    if (getSelectedDeviceName().equals(ProductCode.Fitness.getBluetoothDeviceName())) {
                        m_handle.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sender.append(new CommQueue(BluetoothCommand.State, 0L, RequestAction.Start, true));
                                sender.append(new CommQueue(BluetoothCommand.UserData, 0L, RequestAction.Start, false));
                                sender.append(new CommQueue(BluetoothCommand.RTC, 0L, RequestAction.Start, false));
                                sender.append(new CommQueue(BluetoothCommand.Battery, 0L, RequestAction.Start, true));
                                startStepCalorieTimer();
                                sender.append(new CommQueue(BluetoothCommand.NoticeONOFF, 0L,
                                        Preference.getNoticePhoneONOFF(getContext()) ? RequestAction.Start : RequestAction.End, false,
                                        MessageClass.Phone_Idle, 0, NoticeIndex.Phone));
                                sender.append(new CommQueue(BluetoothCommand.NoticeONOFF, 0L,
                                        Preference.getNoticeSmsONOFF(getContext()) ? RequestAction.Start : RequestAction.End, false,
                                        MessageClass.Phone_Idle, 0, NoticeIndex.SMS));
                                sender.append(new CommQueue(BluetoothCommand.Version, 0L, RequestAction.Start, true));

                                int count = SMSHelper.getNotReadCount(SMSHelper.read(getContext()));
                                if (count > 0)
                                    sender.append(new CommQueue(BluetoothCommand.NoticeMessage, 0L, RequestAction.Start, false, MessageClass.SMS, count, NoticeIndex.Empty));
                            }
                        }, 200);
                    } else if (getSelectedDeviceName().equals(ProductCode.Coach.getBluetoothDeviceName())) {
                        sender.append(new CommQueue(BluetoothCommand.Battery, 0L, RequestAction.Start, true));
                        sender.append(new CommQueue(BluetoothCommand.Version, 0L, RequestAction.Start, true));
                        mDataBase.setHeartrate_stable((short) 0);
                    }
                    raiseConnectionState(ConnectStatus.STATE_CONNECTED);

                    startRssiTimer();
                    cancelConnectTimer();
                } else {
                    Log.i(tag, "DfuMode!!!!!"+ DfuMode);
                    if (path != null && DfuMode == true) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                            Log.i(tag, "Kitkat, Lolipop에서의 처리");
                            synchronized (this) {
                                try {
                                    wait(600);
                                } catch (InterruptedException e) {
                                    //do nothing
                                }
                            }
                            refreshDeviceCache(gatt);
                            update(gatt.getDevice().getAddress());
                        }else {
                            Log.i(tag, "마시멜로 이상에서의 처리");
                            synchronized (this) {
                                try {
                                    wait(600);
                                } catch (InterruptedException e) {
                                    //do nothing
                                }
                            }
                            //raiseConnectionState(ConnectStatus.STATE_DISCONNECTED);
                            //scanLeDevice(false);

                            //cancelScanTimer();
                            //        cancelRssiTimer();
                            //cancelConnectTimer();
                            //disconnect();
                            refreshDeviceCache(gatt);
                            update(gatt.getDevice().getAddress());
                        }
                    }
                }

//                startRssiTimer();
//                if (Preference.getConnectedTime(getContext()) == 0) {
//                    Preference.putConnectedTime(getContext(), getConvertedTime(System.currentTimeMillis()));
//                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(tag, "Disconnected from GATT server.");

                raiseDisconnect();

                if (status != BluetoothGatt.GATT_SUCCESS)
                    if (isLiveApp)
                        startBluetooth();
                    else
                        tryBluetooth();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                getServiceFromGATT(gatt);
                if (mCharRX == null && DfuMode == false)
                    mCharRX = mBluetoothGatt.getService(DeviceUUID.RX_SERVICE_UUID).getCharacteristic(DeviceUUID.RX_CHAR_UUID);
                if (mCharRX == null && DfuMode == true)
                    mCharRX = mBluetoothGatt.getService(DeviceUUID.DFU_SERVICE_UUID).getCharacteristic(DeviceUUID.DFU_CONTROL_POINT_UUID);
            } else {
                Log.w(tag, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(tag, "GATT_SUCCESS READ status: " + status);
            } else {
                Log.i(tag, "GATT_FAIL READ status: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(tag, "onCharacteristicWrite : status->" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            getDataFrame(gatt, characteristic);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.i(tag, "ReadRssi():" + rssi);
            if (isRssiDisconnect(rssi))
                disconnectReasonRSSI();
        }
    };

    public String[] getDeviceInformation() {
        if (mBluetoothGatt == null)
            return null;

        return new String[]{mBluetoothDeviceName, mBluetoothDeviceAddress};
    }

    @Override
    public int startBluetooth() {
        startScanTimer();
        Log.d(tag, "startBluetooth");
        return SUCCESS;
    }

    @Override
    public int tryBluetooth() {
        /*if(mDBHelper.isEmptyUserData())
            return FAILED;*/
        if (!isConnect()) {
            if (mBluetoothGatt != null) {
                mBluetoothGatt.close();

                mBluetoothGatt = null;
                mBluetoothDeviceAddress = null;
                mBluetoothDeviceName = null;
            }
            Log.d(tag, "tryBluetooth->scan");
            scanLeDevice(true);
        }

        return SUCCESS;
    }

    @Override
    public void stopBluetooth() {
        Log.d(tag, "stopBluetooth");
        scanLeDevice(false);

        cancelScanTimer();
//        cancelRssiTimer();
        cancelConnectTimer();

        if (getConnectionState() != ConnectStatus.STATE_DISCONNECTED)
            disconnect();
    }

    /**
     * BLE connect. connect 정보를 멤버 변수로 저장한다. GATT 객체, MAC 어드레스.
     *
     * @param address 연결하려는 MAC 주소.
     * @return true: 성공, false: 실패.
     */
    @Override
    public boolean connect(final String address) {
        mBluetoothGatt = null;
        if (!isValidAdapter() || address == null) {
            Log.w(tag, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        final BluetoothDevice device = getDevice(address);
        if (device == null) {
            Log.w(tag, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.

        raiseConnectionState(ConnectStatus.STATE_CONNECTING);

        if (mBluetoothGatt == null) {
            m_handle.post(new Runnable() {
                @Override
                public void run() {
                    Log.w(tag, "Device connect." + device + address);

                    //규창 17.1.18 마시멜로 이상부터는 블루투스 연결을 OS 내부 Ble자원에 직접 접근해서 연결
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //mBluetoothGatt = device.connectGatt(getContext(), false, mGattCallback, device.TRANSPORT_LE);
                        refreshDeviceCache(mBluetoothGatt);
                        stopScan();
                        /*new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mBluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                                mBluetoothGatt = device.connectGatt(getContext(), false, mGattCallback, device.TRANSPORT_LE);
                            }
                        }, 1000);*/


                        Method connectGattMethod = null;

                        try {
                            connectGattMethod = device.getClass().getMethod("connectGatt", Context.class, boolean.class, BluetoothGattCallback.class, int.class);
                        }catch (NoSuchMethodException e){
                            e.printStackTrace();
                        }

                        try{
                            synchronized (this) {
                                try {
                                    wait(600);
                                } catch (InterruptedException e) {
                                    //do nothing
                                }
                            }
                            mBluetoothGatt = (BluetoothGatt)connectGattMethod.invoke(device, getContext(),false, mGattCallback, device.TRANSPORT_LE);
                            //  device.connectGatt(getContext(), false, mGattCallback, device.TRANSPORT_LE);
                        }catch(IllegalAccessException e){
                            e.printStackTrace();
                        }catch(IllegalArgumentException e){
                            e.printStackTrace();
                        }catch(InvocationTargetException e){
                            e.printStackTrace();
                        }

                    }else {
                        synchronized (this) {
                            try {
                                wait(600);
                            } catch (InterruptedException e) {
                                //do nothing
                            }
                        }
                        mBluetoothGatt = device.connectGatt(getContext(), false, mGattCallback);
                    }
                    //stopScan();
                    //mBluetoothGatt = device.connectGatt(getContext(), false, mGattCallback);

                    // DFU밴드가 스캔은 되고 연결시도는 하나 밴드가 연결을 거부하여 다시 스캔하게 된다
                    // 블루투스를 강제로 끄고 켤 경우 연결이 되는데 SteadyForceHWBLE 3회째에는 블루투스를 강제로 Off-On하여 G4, 엑페같은 폰들에게 펌업을 시킴
                    if (DfuMode == true && SteadyForceHWBLE > 2){
                        refreshDeviceCache(mBluetoothGatt);
                        Log.i(tag,"DFU진행 중 블루투스 접속 실패 "+SteadyForceHWBLE+"회! 3회면 강제로 BT끄고 켠다");
                        raiseConnectionState(ConnectStatus.STATE_DISCONNECTED);
                        scanLeDevice(false);

                        cancelScanTimer();
                        //        cancelRssiTimer();
                        cancelConnectTimer();
                        refreshDeviceCache(mBluetoothGatt);
                        disconnect();
                        mBluetoothGatt = null;

                        DeviceBaseScan.mBTAdapter.disable();
                        //규창 -- 펌웨어 업데이트 블루투스 하드웨어 컨트롤 ON을 무조건 하게 하기 위함
                        hwbluetoothCtrl.postDelayed(tryhwBTON, 5000);
                        Dfu_Force_Ble = true;
                        SteadyForceHWBLE = 0;
                    }
                    if (DfuMode == true)
                        SteadyForceHWBLE++;
                }
            });
            mBluetoothDeviceAddress = address;
            mBluetoothDeviceName = device.getName();
            setScanMode(ScanMode.AUTO);
            Log.d(tag, "Trying to create a new connection. : " + mBluetoothDeviceName);
        } else {
            Log.d(tag, "already create a connection.");
            raiseConnectionState(ConnectStatus.STATE_CONNECTED);
        }

        return true;
    }

    public void disconnect() {
        Log.d(tag, "disconnect Manager!!!");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }

    private void raiseDisconnect() {
        Log.i(tag, "Peripheral Disconnected");
        Packet1.init();

        scanLeDevice(false);
        packetHandler1.removeCallbacks(runnablePacket1);
        //규창 코치 노말 스트레스
        packetHandler2.removeCallbacks(runnablePacket2);
//        cancelScanTimer();
        cancelRssiTimer();
        cancelStepCalorieTimer();

        sender.setReady(false);
        sender.cancel();

        Log.i(tag, "Disconnected from GATT server.");

        refreshDeviceCache(mBluetoothGatt);
        if(mBluetoothGatt != null) {
            Log.d(tag,"mBluetoothGatt not null");
            mBluetoothGatt.close();
        } else
            Log.d(tag,"mBluetoothGatt null");

        mCharRX = null;
        mBluetoothGatt = null;
        mBluetoothDeviceName = mBluetoothDeviceAddress = null;

        raiseConnectionState(ConnectStatus.STATE_DISCONNECTED);

        //접속을 어떻게 할것인지..바로 접속? 아니면, 접속을 얼마동안 지속하고 안되면 딜레이 증가???
        if (getScanMode() == ScanMode.MANUAL)
            return;
        if (isLiveApp)
            startBluetooth();
        else
            startConnectTimer();
    }

    private void disconnectReasonRSSI() {
        Log.d(tag, "disconnectReasonRSSI");
        raiseDisconnect();
    }

    @Override
    protected void writeCharLog(BluetoothGatt gatt, BluetoothGattCharacteristic mChar, byte[] getFrame) {
        if (gatt == null || mChar == null) {
            return;
        }

        if (DEBUG) {
            Log.i(tag, "\n\n*****send Message start len:" + getFrame.length + "*****");
            String tmpString = "";
            for (byte b : getFrame) {
                tmpString += String.format("[%x]:", b);
            }
            Log.i(tag, tmpString);
            Log.i(tag, "*****send Message end*****");
        }

        try {
            mChar.setValue(getFrame);
            gatt.writeCharacteristic(mChar);
        } catch (NullPointerException e) {
            Log.d(tag, "-->NullPointerException:" + e);
            // 이건 disconnect 상태라고 봐야함.-> 그러면 어짜피 disconnect 스킴이 진행됨.
            // 펌웨어 업그레이드 관련, 롤리팝 관련-> disconnect가 되지 않는 정상 연결상태를 유지하는 경우 존재함. 따라서 강제 disconnect->connect 수행.
        } catch (Throwable ignored){
            Log.d(tag, "-->DeadObjectException:" + ignored);
            emergencyReconnect();
        }
    }

    public void scanBluetooth(boolean scan) {
        if (getScanMode() == ScanMode.MANUAL)
            scanLeDevice(scan);
    }

    @Override
    public void setIsLiveApp(boolean isLiveApp) {
        this.isLiveApp = isLiveApp;

        scanLeDevice(false);
        if (isLiveApp) {
            // 앱 O
            /**
             * 앱 실행되면, connect-timer 중지. scan-timer 동작.
             */
            cancelConnectTimer();
            startScanTimer();
        } else {
            cancelScanTimer();
            startConnectTimer();
        }
    }

    @Override
    public boolean isLiveApp() {
        return isLiveApp;
    }

    public void setTimeZoneOffsetForInit(short offset) {
        if (timeZoneOffset == offset) {
            return;
        }

        timeZoneOffset = offset;
    }

    @Override
    public void setTimeZoneOffset(short offset) {
        if (timeZoneOffset == offset)
            return;

        Global_Calendar.setTimeZone(TimeZone.getDefault());
        // time zone 변경 올때마다 rtc 전송해야함. 연결 되어 있지 않으면???->한번 연결 하자.
        if (mBluetoothGatt == null && getConnectionState() != ConnectStatus.STATE_CONNECTED) {
            if (isLiveApp)
                startBluetooth();
            else
                startConnectTimer();
        } else {
            // 연결되어있으면? 그냥 바로 rtc 전송. 이 경우, 밴드의 처리를 어떻게 할 것인가에 따라 달라질듯...
            //밴드가 모든 데이터를 다 처리한다 그러면, 연결 도중 rtc를 전송. 데이터를 reset 한다면, re connect
            /** 펌웨어 처리 부분. 나중에 수정해야함. 현재는 들어갈일 없음.**/
            disconnect();

            cancelScanTimer();
            if (isLiveApp)
                startBluetooth();
            else
                startConnectTimer();
        }
        timeZoneOffset = offset;
    }

    @Override
    public short getTimeZoneOffset() {
        return timeZoneOffset;
    }

    @Override
    protected void getDataFrame(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        UUID tmp = characteristic.getUuid();
        if (tmp.equals(DeviceUUID.TX_CHAR_UUID)) {


            short temp_cmd = 0;
            String product = DeviceBaseScan.getSelectedDeviceName();
            byte[] getFrame = characteristic.getValue();

            if (DEBUG) {
                Log.i(tag, "\n\n*****getframe start len:" + getFrame.length + "*****");
                String tmpString = "";
                for (byte b : getFrame) {
                    tmpString += String.format("[%x]:", b);
                }
                Log.i(tag, tmpString);
                Log.i(tag, "*****getframe end*****");
            }

            if (product.equals(ProductCode.Fitness.getBluetoothDeviceName()) && getFrame == null && getFrame.length > 20 ){
                Log.i(tag,"블루투스 데이터에 문제 있음");
                return;
            }
            // product에 따라, 명령어 바꿔야함.
            if (product.equals(ProductCode.Fitness.getBluetoothDeviceName())) {
                //규창 171108자바의 바이트는 음수가 존재하기 때문에 피쳐데이터 요청의 0xa1과 같은 경우 음수로 표현된다 따라서 아래의 루틴으로 한번더 명령어를 파싱해낸다
                //temp_cmd = (short) ((getFrame[0]) | (getFrame[1] & 0xff) << 8);
                temp_cmd = (short) ((getFrame[0]&0xff) | (getFrame[1] & 0xff) << 8);
                //Log.i(tag, "getFrame[0]"+ String.format("[%x]:", getFrame[0]) + " getFrame[1]"+ getFrame[1] + "????" + (short)((getFrame[0]&0xff) | (getFrame[1] & 0xff) << 8) + "????" + (short)0xa1 + (short)(getFrame[0]&0xff));
                /*BluetoothCommand cmd = BluetoothCommand.getCommand(temp_cmd);
                if (cmd == null) {
                    temp_cmd = (short)((getFrame[0]&0xff) | (getFrame[1] & 0xff) << 8);
                }*/
            } else if (product.equals(ProductCode.Coach.getBluetoothDeviceName())) {
                temp_cmd = (getFrame[0]);
                if (temp_cmd == BluetoothCommand.CoachReceiveAcc.getCommand()) {
                    temp_cmd = BluetoothCommand.Acc.getCommand();
                }
            }


            BluetoothCommand cmd = BluetoothCommand.getCommand(temp_cmd);
            if (cmd == null) {
                Log.e(tag, "illegal bluetooth command");
                return;
            }

            if (sender.compare(cmd)) {
                sender.resetBusy();
            }

            if (DEBUG) {
                Log.i(tag, "\n\n*****getframe start len:" + getFrame.length + "*****");
                String tmpString = "";
                for (byte b : getFrame) {
                    tmpString += String.format("[%x]:", b);
                }
                Log.i(tag, tmpString);
                Log.i(tag, "*****getframe end*****");
            }

            // 이하 명령들은 noti들이 필요함.
            switch (cmd) {
                case Battery:
                    Log.i(tag, "get Battery:"+cmd);
                    short status = 0;
                    short voltage = 0;
                    if (product.equals(ProductCode.Fitness.getBluetoothDeviceName())) {
                        status = getDataShort(getFrame, 2);
                        voltage = getDataShort(getFrame, 4);
                        mDataBase.setBattery(new Battery(status, voltage));
                    } else if (product.equals(ProductCode.Coach.getBluetoothDeviceName())) {
                        status = getFrame[3];
                        voltage = getFrame[6];
                    }

                    mDataBase.setBattery(new Battery(status, voltage));
                    mBrTop.sendBroadcastBattery(mDataBase.getBattery());
                    break;
                case Acc:
                    Log.i(tag, "get Acc:"+cmd);
                    if (product.equals(ProductCode.Fitness.getBluetoothDeviceName())) {

                        Packet1.acc_x0 = getDataShort(getFrame, 2);
                        Packet1.acc_y0 = getDataShort(getFrame, 4);
                        Packet1.acc_z0 = getDataShort(getFrame, 6);
                        Packet1.hr0 = getDataShort(getFrame, 8);

                        Packet1.acc_x1 = getDataShort(getFrame, 10);
                        Packet1.acc_y1 = getDataShort(getFrame, 12);
                        Packet1.acc_z1 = getDataShort(getFrame, 14);
                        Packet1.hr1 = getDataShort(getFrame, 16);
                        Log.i(tag, "get Acc:"+cmd +" x:"+Packet1.acc_x0+" y:" +Packet1.acc_y0+" z:" +Packet1.acc_z0 );
                    } else if (product.equals(ProductCode.Coach.getBluetoothDeviceName())) {
                        Packet1.acc_x0 = getDataShort(getFrame, 2);
                        Packet1.acc_y0 = getDataShort(getFrame, 4);
                        Packet1.acc_z0 = getDataShort(getFrame, 6);

                        Packet1.acc_x1 = getDataShort(getFrame, 10);
                        Packet1.acc_y1 = getDataShort(getFrame, 12);
                        Packet1.acc_z1 = getDataShort(getFrame, 14);

                        Packet1.hr0 = getFrame[18];
                        Packet1.hr1 = getFrame[19];
                        Log.i(tag, "get Acc:"+cmd +" x:"+Packet1.acc_x0+" y:" +Packet1.acc_y0+" z:" +Packet1.acc_z0 );
                    }

                    if (mNordicCb != null) {
                        mNordicCb.onSensor(Packet1.packet1());
                        packetHandler1.postDelayed(runnablePacket1, 50);
                    }

                    //규창 코치 노말 스트레스 인터페이스로 센서데이터 전송
                    if (mStressN != null && mStressNManger.isMeasuring == true) {

                        //Log.i(BluetoothLEManager.tag, msg: "\(sensorData1)")
                        mStressN.onStresshr(Packet1.packet1());
                        Log.d(tag, "acc_case"+ Packet1.packet1());
                        packetHandler2.postDelayed(runnablePacket2, 50);
                        //mStressNManger.isMeasuring = false
                    }

                    break;
                case StepCount_Calorie:
                    Log.i(tag, "get Step:"+cmd);
                    Log.i(tag,"Step:"+ getDataShort(getFrame, 2));
                    Log.i(tag,"ActivityCal:"+ ((double) getDataInt(getFrame, 4)) / 1000);
                    Log.i(tag,"SleepCal:"+ ((double) getDataInt(getFrame, 8)) / 1000);
                    Log.i(tag,"DailyCal:"+ ((double) getDataInt(getFrame, 12)) / 1000);
                    Log.i(tag,"CoachCal:"+ ((double) getDataInt(getFrame, 16)) / 1000);
                    mDataBase.setStep(getDataShort(getFrame, 2));
                    mDataBase.setTotal_activity_calorie(((double) getDataInt(getFrame, 4)) / 1000);
                    mDataBase.setTotal_sleep_calorie(((double) getDataInt(getFrame, 8)) / 1000);
                    mDataBase.setTotal_daily_calorie(((double) getDataInt(getFrame, 12)) / 1000);
                    mDataBase.setTotal_coach_calorie(((double) getDataInt(getFrame, 16)) / 1000);
                    mBrTop.sendBroadcastStepNCalorie(mDataBase.getStep(), mDataBase.getTotal_activity_calorie(), mDataBase.getTotal_sleep_calorie(),
                            mDataBase.getTotal_daily_calorie(), mDataBase.getTotal_coach_calorie());



                    //규창 1분마다 피쳐 데이터 요청
                    requestFeature(System.currentTimeMillis());


                    break;
                case Activity:
                    Log.i(tag, "get Activity:"+cmd);
                    double act_calorie = ((double) getDataInt(getFrame, 2)) / 1000;
                    short intensityL = getDataShort(getFrame, 6);
                    short intensityM = getDataShort(getFrame, 8);
                    short intensityH = getDataShort(getFrame, 10);
                    short intensityD = getDataShort(getFrame, 12);
                    short minHR = getDataShort(getFrame, 14);
                    short maxHR = getDataShort(getFrame, 16);
                    short avgHR = getDataShort(getFrame, 18);

                    long start_time = sender.LastSender.getTime();
                    if (m_handle != null) {
                        m_handle.post(new Runnable() {
                            @Override
                            public void run() {
                                ContentResolver res = getContext().getContentResolver();
                                IndexTimeData idx = mSQLHelper.getIndexTimeProvider(start_time, res);
                                if (idx == null) {
                                    Log.d(tag, "Index time is null. break... s time " + start_time);
                                    return;
                                }

                                if (idx.getStart_time() == 0 || idx.getEnd_time() == 0) {
                                    Log.d(tag, "Index time is zero. (start "+idx.getStart_time()+" end "+idx.getEnd_time()+") break...");
                                    return;
                                }

                                ActivityData activityData = new ActivityData(0, CourseLabel.Activity.getLabel(), act_calorie, intensityL,
                                        intensityM, intensityH, intensityD, minHR, maxHR, avgHR, idx.getStart_time()/1000*1000, idx.getEnd_time()/1000*1000, 0);
                                ProviderValues values = mSQLHelper.addActivityDataProvider(activityData);

                                ProviderValues check = mSQLHelper.getActivityDataProvider(activityData.getStart_time());
                                Cursor cursor = res.query(check.getUri(), check.getProjection(), check.getSelection(), check.getSelectionArgs(), check.getSortOrder());
                                if (cursor == null) {
                                    Log.i(tag, "create activity data ::> start : " + activityData.getStart_time() + " end : " + activityData.getEnd_time());
                                    res.insert(values.getUri(), values.getValues());
                                } else {
                                    Log.i(tag, "failed insert reason : duplicate time -> " + activityData.getStart_time());
                                }

                                values = mSQLHelper.deleteIndexTimeProvider(start_time);
                                int del = res.delete(values.getUri(), values.getSelection(), values.getSelectionArgs());
                                Log.i(tag, "delete idx ::> count : " + del);

                                mBrTop.sendBroadcastGenerateActivityData(activityData.getStart_time());
                            }
                        });
                    }
                    //mDataBase.setActivityData(activityData);
                    //mBrTop.sendBroadcastActivityData(activityData);
                    break;
                case Sleep:
                    Log.i(tag, "get Sleep:"+cmd);
                    short rolled = getDataShort(getFrame, 2);
                    short awaken = getDataShort(getFrame, 4);
                    short stabilityHR = getDataShort(getFrame, 6);

                    SleepData sleepData = new SleepData(rolled, awaken, stabilityHR, sender.LastSender.getTime());
                    mDataBase.setSleepData(sleepData);
                    mDataBase.setHeartrate_stable(stabilityHR);
                    mBrTop.sendBroadcastSleepData(sleepData);
                    break;
                case Stress:
                    Log.i(tag, "get Stress:"+cmd);
                    //피트니스든 미니든 스트레스 측정을 앱에서 처리
                    //mDataBase.setStress(getDataShort(getFrame, 2));
                    //mBrTop.sendBroadcastStressData(mDataBase.getStress());
                    if (product.equals(ProductCode.Fitness.getBluetoothDeviceName())) {
                        Packet1.acc_x0 = getDataShort(getFrame, 2);
                        Packet1.acc_y0 = getDataShort(getFrame, 4);
                        Packet1.acc_z0 = getDataShort(getFrame, 6);
                        Packet1.hr0 = getDataShort(getFrame, 8);

                        Packet1.acc_x1 = getDataShort(getFrame, 10);
                        Packet1.acc_y1 = getDataShort(getFrame, 12);
                        Packet1.acc_z1 = getDataShort(getFrame, 14);
                        Packet1.hr1 = getDataShort(getFrame, 16);
                    } else if (product.equals(ProductCode.Coach.getBluetoothDeviceName())) {
                        Packet1.acc_x0 = getDataShort(getFrame, 2);
                        Packet1.acc_y0 = getDataShort(getFrame, 4);
                        Packet1.acc_z0 = getDataShort(getFrame, 6);

                        Packet1.acc_x1 = getDataShort(getFrame, 10);
                        Packet1.acc_y1 = getDataShort(getFrame, 12);
                        Packet1.acc_z1 = getDataShort(getFrame, 14);

                        Packet1.hr0 = getFrame[18];
                        Packet1.hr1 = getFrame[19];
                    }

                    //규창 코치 노말 스트레스 측정용
                    if (mStressN != null && mStressNManger.isMeasuring == true) {
                        //Log.i(BluetoothLEManager.tag, msg: "\(sensorData1)")
                        mStressN.onStresshr(Packet1.packet1());
                        packetHandler2.postDelayed(runnablePacket2, 50);
                        Log.d(tag, "stresscase"+ Packet1.packet1());
                        //mStressNManger.isMeasuring = false
                    }

                    break;
                case Version:
                    Log.i(tag, "get Version:"+cmd);
                    if (product.equals(ProductCode.Fitness.getBluetoothDeviceName())) {
                        int len = getDataShort(getFrame, 2);
                        mDataBase.setVersion(getString(getFrame, 4, len));
                    } else if (product.equals(ProductCode.Coach.getBluetoothDeviceName())) {
                        int len = getFrame[2];
                        mDataBase.setVersion(getString(getFrame, 4, len));
                    }
                    Log.i(tag,"version ->"+ mDataBase.getVersion());
                    mBrTop.sendBroadcastFirmVersion(mDataBase.getVersion());
                    break;
                case State:
                    Log.i(tag, "get State:"+cmd);
                    mDataBase.setState(getDataShort(getFrame, 2));
                    if (getFrame.length > 4)
                        mDataBase.setHeartrate_stable(getDataShort(getFrame, 4));

                    if (mDataBase.getState() == StatePeripheral.IDLE.getState()) {
                        if (m_handle != null) {
                            m_handle.post(new Runnable() {
                                @Override
                                public void run() {
                                    SQLHelper sql = SQLHelper.getInstance(getContext());
                                    ProviderValues values = sql.deleteIndexTimeProvider();

                                    ContentResolver res = getContext().getContentResolver();
                                    res.delete(values.getUri(), values.getSelection(), values.getSelectionArgs());
                                }
                            });
                        }
                    }

                    Preference.putPeripheralState(getContext(), mDataBase.getState());
                    mBrTop.sendBroadcastPeripheralState(mDataBase.getState());
                    break;

                //규창 현재 피쳐데이터 요청 루틴 확보
                case FeatureSet1:
                    Log.i(tag, "get Feature:"+cmd);
                    //피트니스든 미니든 스트레스 측정을 앱에서 처리
                    //mDataBase.setStress(getDataShort(getFrame, 2));
                    //mBrTop.sendBroadcastStressData(mDataBase.getStress());

                    int time = getDataInt(getFrame, 2);
                    double norm_var = getDataInt(getFrame, 6) / 100000;
                    double x_var = getDataShort(getFrame, 10) / 1000;
                    double y_var = getDataShort(getFrame, 12) / 1000;
                    double z_var = getDataShort(getFrame, 14) / 1000;
                    double x_mean = getDataShort(getFrame, 16) / 1000;
                    double y_mean = getDataShort(getFrame, 18) / 1000;

                    Log.i(tag, "FeatureSet1:time"+ time +" norm_var" +norm_var + " x_var" + x_var+ " y_var"+y_var + " z_var"+ z_var + " x_mean"+x_mean + " y_mean"+y_mean);
                    break;
                case FeatureSet2:
                    double z_mean = getDataShort(getFrame, 2) / 1000;
                    double norm_mean = getDataShort(getFrame, 4) / 1000;
                    int n_step = getDataShort(getFrame, 6);
                    int JSwing = getDataShort(getFrame, 8);
                    int SSwing = getDataShort(getFrame, 10);
                    int LSwing = getDataShort(getFrame, 12);
                    double PRESS_VAR = getDataShort(getFrame, 14);
                    int AStep = getDataShort(getFrame, 16);
                    int DStep = getDataShort(getFrame, 18);
                    Log.i(tag, "FeatureSet2:z_mean"+ z_mean +" norm_mean" +norm_mean + " n_step" + n_step+ " JSwing"+JSwing + " SSwing"+ SSwing + " LSwing"+LSwing + " PRESS_VAR"+PRESS_VAR
                            + " AStep"+AStep + " DStep"+DStep);
                    break;
                case FeatureSet3:
                    double press = getDataInt(getFrame, 2) / 100;
                    int pulse = getDataShort(getFrame, 6);
                    Log.i(tag, "FeatureSet3:press"+ press +" pulse" +pulse);
                    break;
            }


            //규창 171108 피쳐 후위처리...
        }
    }

    private String getString(byte[] frame, int loc, int len) {
        byte[] tmp = new byte[len];
        System.arraycopy(frame, loc, tmp, 0, len);

        return new String(tmp);
    }

    private short getDataShort(byte[] frame, int loc) {
        short ret = 0;
        try {
            if(frame.length < loc+2){
                Log.i(tag, "전송받은 데이터 이상 발생!!"+ frame.length );
                ret = 0;
                return ret;
            }
            for (int i = loc; i < loc + 2; i++) {
                ret |= (frame[i] & 0xff) << (8 * (i - loc));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.i(tag, "전송받은 데이터 이상 발생" + e);
            ret = 0;
            return ret;
        } catch (Exception e) {
            Log.i(tag, "전송받은 데이터 이상 발생" + e);
            ret = 0;
            return ret;
        }

        return ret;
    }

    private int getDataInt(byte[] frame, int loc) {
        int ret = 0;
        try {
            if(frame.length < loc+4){
                Log.i(tag, "전송받은 데이터 이상 발생!!"+frame.length );
                ret = 0;
                return ret;
            }
            for (int i = loc; i < loc + 4; i++) {
                ret |= (frame[i] & 0xff) << (8 * (i - loc));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.i(tag, "전송받은 데이터 이상 발생" + e);
            ret = 0;
            return ret;
        } catch(Exception e){
            Log.i(tag, "전송받은 데이터 이상 발생"+e);
            ret = 0;
            return ret;
        }
        return ret;
    }

    public void writeReset(String path) {
        if (mBluetoothGatt == null || mCharRX == null) {
            return;
        }
        Log.i(tag, "writeReset");
        this.path = path;
        Log.i(tag, "********** RESET BOOT!!! **********");

        sender.setReady(false);
        sender.cancel();
        mCharRX.setValue("reset boot");
        mBluetoothGatt.writeCharacteristic(mCharRX);
        DfuMode = true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i(tag, "Lolipop, Kitkat일때의 예외처리 ");
                    refreshDeviceCache(mBluetoothGatt);
                    startBluetooth();
                }
            }, 6000);
        } else {
            Log.i(tag, "마시멜로일 때의 예외처리 ");

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Dfu_Force_Ble 플래그는 HW BT를 강제로 끄고 켜는 행위를 한다. 폰에 따라서 HW BT OnOFF를 달리해야한다.
                    if (Dfu_Force_Ble == true) {
                        raiseConnectionState(ConnectStatus.STATE_DISCONNECTED);
                        scanLeDevice(false);

                        cancelScanTimer();
                        //        cancelRssiTimer();
                        cancelConnectTimer();
                        refreshDeviceCache(mBluetoothGatt);
                        disconnect();
                        mBluetoothGatt.close();
                        mBluetoothGatt = null;
                        DeviceBaseScan.mBTAdapter.disable();

                        //규창 -- 펌웨어 업데이트 블루투스 하드웨어 컨트롤 ON을 무조건 하게 하기 위함
                        hwbluetoothCtrl.postDelayed(tryhwBTON, 5000);
                    }else {
                        raiseConnectionState(ConnectStatus.STATE_DISCONNECTED);
                        scanLeDevice(false);

                        cancelScanTimer();
                        //        cancelRssiTimer();
                        cancelConnectTimer();
                        refreshDeviceCache(mBluetoothGatt);
                        disconnect();
                        mBluetoothGatt.close();
                        mBluetoothGatt = null;
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startBluetooth();
                            }
                        }, 3000);
                    }
                }
            }, 1000);


        }
    }
    public void update(final String address){

        Log.i(tag, "in DFU!!!!! INIT!!! ----1"+ path);
        if (path != null && DfuMode == true) {
            final Intent DFUGO = new Intent().setAction("kr.co.greencomm.middleware.service.startDfuService");
            DFUGO.setPackage(getContext().getPackageName());
            Log.i(tag, "mBluetoothDeviceAddress"+ address);
            DFUGO.putExtra(DfuService.EXTRA_DEVICE_ADDRESS, address);
            DFUGO.putExtra(DfuService.EXTRA_DEVICE_NAME, mBluetoothDeviceName);
            DFUGO.putExtra(DfuService.EXTRA_FILE_TYPE, DfuService.TYPE_AUTO);
            //DFUGO.putExtra(DfuService.EXTRA_DISABLE_NOTIFICATION, false);
            DFUGO.putExtra(DfuService.EXTRA_FILE_PATH, path);
            DFUGO.putExtra(DfuService.EXTRA_KEEP_BOND, false);

            getContext().getApplicationContext().startService(DFUGO);
            Log.i(tag, "in DFU!!!!! StateGO!!! ----1" + path);

        }else if(path == null && DfuMode == false) {
            Log.i(tag, "Path나 BleManager의 DfuMode가 아닌 이유로 펌웨어 업데이트 불가");
            final MWBroadcastTop mBrTop = new MWBroadcastTop(getContext());
            mBrTop.sendBroadcastFirmUpFailed();
            //return;
        }
    }

    // 규창 170320 펌웨어 업데이트 연결복구함수 킷캣/마시멜로 루틴 정리
    // MWBroadcastReceiver에서 성공, 실패를 라이브러리에서 받아 이 함수를 호출
    public void recoveryConnect(){
        Log.i(tag, "recoveryConnect ");
        DfuMode = false;
        Progress = 0;
        if (path != null) {
            FileManager.deleteFile(path);
        }
        path = null;
        /*
            펌웨어 업데이트 성공/실패 후 연결 복구 시 킷캣/롤리팝에서는 다시 붙더라도 높은 빈도로 연결이 끊어진다
            이후의 결과는 밴드 상에서만 연결이 되었다고 OLED에 표기되고 MW/APP에서는 미연결로 뜨는 문제 발견
            이에 대한 대처로 밴드와 붙든 말든 전부 비정상으로 간주, 연결 완전히 끊고 3초 후 재접속 처리
        */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(tag, "Kitkat, Lolipop일 때의 예외처리");
            /*scanLeDevice(false);
            cancelScanTimer();
            //        cancelRssiTimer();
            cancelConnectTimer();

            disconnect();
            mBluetoothGatt.close();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startBluetooth();
                }
            }, 3000);*/
            //Dfu_Force_Ble 플래그는 HW BT를 강제로 끄고 켜는 행위를 한다. 폰에 따라서 HW BT OnOFF를 달리해야한다.
            if (Dfu_Force_Ble == true && FirmUpComplete == false) {
                Log.i(tag, "HW BT OFF!!");
                raiseConnectionState(ConnectStatus.STATE_DISCONNECTED);
                scanLeDevice(false);

                cancelScanTimer();
                //        cancelRssiTimer();
                cancelConnectTimer();
                refreshDeviceCache(mBluetoothGatt);
                disconnect();
                mBluetoothGatt.close();
                mBluetoothGatt = null;

                DeviceBaseScan.mBTAdapter.disable();
                //규창 -- 펌웨어 업데이트 블루투스 하드웨어 컨트롤 ON을 무조건 하게 하기 위함
                hwbluetoothCtrl.postDelayed(tryhwBTON, 5000);
                SteadyForceHWBLE = 0;
            }else if(Dfu_Force_Ble == false && FirmUpComplete == false) {
                Log.i(tag, "SOFT BT RESET!!");
                refreshDeviceCache(mBluetoothGatt);
                startBluetooth();
            }else if (Dfu_Force_Ble == true && FirmUpComplete == true) {
                Log.i(tag, "HW BT off!! && DFU Routine Complete");
                raiseConnectionState(ConnectStatus.STATE_DISCONNECTED);
                scanLeDevice(false);

                cancelScanTimer();
                //        cancelRssiTimer();
                cancelConnectTimer();
                refreshDeviceCache(mBluetoothGatt);
                disconnect();
                mBluetoothGatt.close();
                mBluetoothGatt = null;

                DeviceBaseScan.mBTAdapter.disable();
                //규창 -- 펌웨어 업데이트 블루투스 하드웨어 컨트롤 ON을 무조건 하게 하기 위함
                hwbluetoothCtrl.postDelayed(tryhwBTON, 5000);
                SteadyForceHWBLE = 0;
            }
            else if (Dfu_Force_Ble == false && FirmUpComplete == true) {
                Log.i(tag, "SW BT RESET!! && DFU Routine Complete");

                SteadyForceHWBLE = 0;
                //startBluetooth();
                refreshDeviceCache(mBluetoothGatt);
                startBluetooth();

            }
        }else{
            Log.i(tag, "마시멜로 이상일 때의 예외처리");
            //Dfu_Force_Ble 플래그는 HW BT를 강제로 끄고 켜는 행위를 한다. 폰에 따라서 HW BT OnOFF를 달리해야한다.
            if (Dfu_Force_Ble == true && FirmUpComplete == false) {
                Log.i(tag, "HW BT OFF!!");
                raiseConnectionState(ConnectStatus.STATE_DISCONNECTED);
                scanLeDevice(false);

                cancelScanTimer();
                //        cancelRssiTimer();
                cancelConnectTimer();
                refreshDeviceCache(mBluetoothGatt);
                disconnect();
                mBluetoothGatt.close();
                mBluetoothGatt = null;

                DeviceBaseScan.mBTAdapter.disable();
                //규창 -- 펌웨어 업데이트 블루투스 하드웨어 컨트롤 ON을 무조건 하게 하기 위함
                hwbluetoothCtrl.postDelayed(tryhwBTON, 5000);
                SteadyForceHWBLE = 0;
            }else if(Dfu_Force_Ble == false && FirmUpComplete == false) {
                Log.i(tag, "SOFT BT RESET!!");
                refreshDeviceCache(mBluetoothGatt);
                startBluetooth();
            }else if (Dfu_Force_Ble == true && FirmUpComplete == true) {
                Log.i(tag, "HW BT off!! && DFU Routine Complete");
                raiseConnectionState(ConnectStatus.STATE_DISCONNECTED);
                scanLeDevice(false);

                cancelScanTimer();
                //        cancelRssiTimer();
                cancelConnectTimer();
                refreshDeviceCache(mBluetoothGatt);
                disconnect();
                mBluetoothGatt.close();
                mBluetoothGatt = null;

                DeviceBaseScan.mBTAdapter.disable();
                //규창 -- 펌웨어 업데이트 블루투스 하드웨어 컨트롤 ON을 무조건 하게 하기 위함
                hwbluetoothCtrl.postDelayed(tryhwBTON, 5000);
                SteadyForceHWBLE = 0;
            }
            else if (Dfu_Force_Ble == false && FirmUpComplete == true) {
                Log.i(tag, "SW BT RESET!! && DFU Routine Complete");
                SteadyForceHWBLE = 0;
                refreshDeviceCache(mBluetoothGatt);
                startBluetooth();
            }
        }
    }
    // 규창 16.11.17 밴드가 부트모드일때 접속을 못하거나 연결을 실패했을 경우 Service모드로 들어가게 하기 위함
    public void recoveryDFUMODE(){
        if(BluetoothLEManager.Progress == 0 && DfuMode == true) {
            Log.i(tag, "recoveryDFUMODE ");
            DfuMode = false;
            Dfu_Force_Ble = false;
            SteadyForceHWBLE = 0;
            Progress = 0;
            if (path != null) {
                FileManager.deleteFile(path);
            }
            path = null;
            //refreshDeviceCache(mBluetoothGatt);
            //raiseDisconnect();
            //connect(mBluetoothDeviceAddress);
            //startBluetooth();
            Log.i(tag, "밴드한테 부트모드 진입 명령 날려놓고 DFU로 접속을 못할 경우의 예외처리!!! ");
            final MWBroadcastTop mBrTop = new MWBroadcastTop(getContext());
            mBrTop.sendBroadcastFirmUpFailed();



            // DFU밴드가 3회째 BT강제 Off-On 플래그 초기화 및 재 시도
            /*if (Dfu_Force_Ble == false){
                Dfu_Force_Ble = true;
                SteadyForceHWBLE = 0;
            }else {

            }*/

            recoveryConnect();

        }
    }

    // 규창 17.03.16 밴드 응답없음 현상 발생시 연결해제 후 재 연결!!!!!
    public void emergencyReconnect(){
        if(mBluetoothGatt != null) {
            Log.i(tag, "Reconnect Reason: No Response");

            raiseConnectionState(ConnectStatus.STATE_DISCONNECTED);
            scanLeDevice(false);

            cancelScanTimer();
            //        cancelRssiTimer();
            cancelConnectTimer();
            refreshDeviceCache(mBluetoothGatt);

            disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;

            startBluetooth();
        }

    }

}