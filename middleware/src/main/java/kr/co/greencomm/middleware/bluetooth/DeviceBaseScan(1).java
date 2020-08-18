package kr.co.greencomm.middleware.bluetooth;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.Log;

import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.main.BluetoothLEManager;
import kr.co.greencomm.middleware.service.MWBroadcastTop;
import kr.co.greencomm.middleware.wrapper.MWControlCenter;

/**
 * 양정은 대리가 구현한 BluetoothLEManager 에서
 * BluetoothDevice 스캔하는 부분을 따로 분리함
 */
public abstract class DeviceBaseScan extends SharedBase implements BluetoothAdapter.LeScanCallback {
    private static final String tag = "Bluetooth_DeviceBase";

    public MWBroadcastTop mBrTop;

    private final long SCAN_PERIOD = 4500;
    protected final int DEF_RSSI = -100;


    //규창 170124 스캔 방식 배치스캔 제거 -> onScanResult방식 오픈
    // 규창 16.12.13 스캔 리포트 딜레이 가변적으로 설정하기위한 기본값 1초
    private long mLastBatchTimestamp = 1000;


    // 규창 16.12.25 자동 스캔 5회 넘을시 블루투스 중지
    private static int countsAutoScan = 0;

    private static ConnectStatus mConnectionState = ConnectStatus.STATE_DISCONNECTED;

    protected boolean mScanning = false;

    private ScanMode scanMode = ScanMode.AUTO;

    protected abstract void startScanTimer();

    protected abstract void cancelScanTimer();

    /**
     * 현재 블루투스 연결 상태 정보를 반환한다. 연결 상태에 아무런 변화도 이루어지지 않았을 경우, 기본은 STATE_DISCONNECTED 이다.
     *
     * @return 블루투스 연결 상태 정보. (0:STATE_DISCONNECTED, 1:STATE_CONNECTING, 2:STATE_CONNECTED)
     */
    public ConnectStatus getConnectionState() {
        return mConnectionState;
    }

    protected boolean isConnect() {
        if (mConnectionState == ConnectStatus.STATE_DISCONNECTED) return false;
        if (mConnectionState == ConnectStatus.STATE_EXIT) return false;
        return true;
    }

    public void setScanMode(ScanMode scanMode) {
        this.scanMode = scanMode;
    }

    public ScanMode getScanMode() {
        return scanMode;
    }

    //private static boolean isEnabledFlag = false;
    //private static boolean isSupported = false;

    public static final BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();

    private ScanCallback mScanCallback;
    private ScanSettings mScanSetting;
    private ArrayList<ScanFilter> mFilter;

    @TargetApi(Build.VERSION_CODES.M)
    private BluetoothLeScanner getBluetoothLEScanner() {
        return mBTAdapter.getBluetoothLeScanner();
    }

    protected boolean isValidAdapter() {
        return (mBTAdapter != null);
    }

    protected BluetoothDevice getDevice(String address) {
        return mBTAdapter.getRemoteDevice(address);
    }

    /**
     * 공개된 콜백으로 앱에서 사용하기 위함.
     */
    protected LeCallback applicationCb = null;

    /**
     * 블루투스 연결 상태를 확인하는 callback register
     *
     * @param cb LeCallback interface 객체를 등록한다.
     */
    public void registerLeCallback(LeCallback cb) {
        applicationCb = cb;
    }

    /**
     * 블루투스 연결 상태 callback unregist.
     */
    public void unregisterLeCallback() {
        applicationCb = null;
    }

    protected void raiseDeviceList() {
    }

    protected void raiseConnectionFailed() {
        if (applicationCb != null) {
            applicationCb.onConnectionFailed();
        }
    }

    protected void raiseConnectionState(ConnectStatus state) {
        mConnectionState = state;
        if (applicationCb != null) {
            applicationCb.onConnectionState(mConnectionState);
        }
    }

    /**
     * BI name
     */
    public static String getSelectedDeviceName() {
        return MWControlCenter.m_productCode.getBluetoothDeviceName();
    }
    //private final static String DEVICE_NAME_START_DFUT = "DfuTarg";
    public static String getDeviceNameStartDFUT() {
        return MWControlCenter.m_productCode.getDfuBluetoothDeviceName();
    }
    private HashMap<String, DeviceRecord> arList = new HashMap<>();

    protected abstract void writeCharLog(BluetoothGatt gatt, BluetoothGattCharacteristic mChar, byte[] getFrame);

    public abstract boolean connect(final String address);

    private void requestConnect(final String address, int rssi) {
        stopScan();

        Log.d(tag, "connect......");
        Log.d(tag, "address"+address);
//        if (rssi < DEF_RSSI) {
//            Log.d(tag, "->not connect, because high rssi");
//            raiseConnectionFailed();
//            return;
//        }
        connect(address);
    }

    public boolean requestConnect(final String address) {
        stopScan();

        Log.d(tag, "connect......");
        return connect(address);
    }

    protected void stopScan() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mBTAdapter.stopLeScan(this);
        } else {
            BluetoothLeScanner scanner = getBluetoothLEScanner();
            if (scanner != null) {
                //규창 170124 스캔 방식 배치스캔 제거 -> onScanResult방식 오픈
                //if (mScanCallback != null && mBTAdapter != null)
                //    scanner.flushPendingScanResults(mScanCallback);
                scanner.stopScan(mScanCallback);
            }
        }
    }

    protected Handler m_handle;

    private Runnable mRunnableBLE = new Runnable() {
        @Override
        public void run() {
            countsAutoScan++;
            Log.i(tag,"countAutoScan:"+countsAutoScan);
            if (countsAutoScan > 5){
                mBrTop.sendAutoScanMaximum();
                countsAutoScan = 0;

                //규창 17.01.09 백그라운드에서 스캔타이머 동작 못하도록 처리
                MWControlCenter.getInstance(getContext()).stopBluetooth();
                setScanMode(ScanMode.MANUAL);
            }
            if (getScanMode() == ScanMode.MANUAL) {
                mScanning = false;
                stopScan();
                mBrTop.sendBroadcastEndScan();
                return;
            }

            setScanMode(ScanMode.MANUAL);

            if (mScanning == false)
                return;
            //Log.d(tag,"11111");
            stopScan();

            String DBMac = Preference.getBluetoothMac(getContext());
            boolean DBMacFlag = false;
            if (DBMac != null)
                DBMacFlag = true;
            //Log.d(tag,"22222");
            // 한번도 접속한적이 없는 경우, 새기기를 찾는 경우.
            int len = getSelectedDeviceName().length();
            int len_dfu = getDeviceNameStartDFUT().length();
            boolean multiDeviceFlag = false;
            boolean equalMac = false;
            int getDevCount = 0;
            int rssi = 0;
            String mac = null;
            if (DBMacFlag) {
                //Log.d(tag,"44444");
                // DB에 블루투스 정보가 있는경우.
                for (DeviceRecord dev : arList.values()) {
                    //Log.i(tag, "arList Inform name:"+dev.getName());
                    String getDevName = dev.getName();
                    if (getDevName == null)
                        continue;
                    if (getDevName.length() < len)
                        continue;

                    //String name = getDevName.substring(0, len);
                    if (getDevName.startsWith(getSelectedDeviceName())) {
                        mac = dev.getMac();
                        if (mac.equals(DBMac)) {
                            rssi = dev.getRssi();
                            equalMac = true;
                            break;
                        }
                    } else if (getDevName.length() >= len_dfu && getDevName.startsWith(getDeviceNameStartDFUT())) {
                        mac = dev.getMac();
                        if (mac.equals(DBMac)) {
                            equalMac = true;
                            break;
                        }
                    }
                }
            }

            if (equalMac == true && mac != null && DBMacFlag == true) {
                //if(mConnectionState == STATE_DISCONNECTED && isConnected == false) {
                if (isConnect() == false) {
                    Log.d(tag, "connect....exist");
//                    if (rssi < DEF_RSSI) {
//                        Log.d(tag, "->not connect, because high rssi");
//                        raiseConnectionFailed();
//                        return;
//                    }
                    connect(mac);
                }
            } else {
                if (isConnect() == false) {
                    Log.d(tag, "connect....failed");
                    raiseConnectionFailed();
                }
            }
            mScanning = false;
        }
    };

    /**
     * BLE scan 콜백. 스캔된 device 정보를 리스트에 추가하고
     * 저장된 어드레의 장비를 발견하면 접속을 시도한다.
     */
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

        if (arList == null) return;
        // 장치의 기본 정보 확인
        final String addr = device.getAddress();
        final String name = device.getName();
        // 장치 명칭이 없는 경우 리턴
        if (name == null) return;
        // 검색된 장치가 리스트에 없으면 추가한다.
        if (getScanMode() == ScanMode.MANUAL && !name.startsWith(getSelectedDeviceName())) {
            return;
        }
        if (arList.containsKey(addr) == true) return;
        DeviceRecord rec = new DeviceRecord(device, addr, name, rssi);
        if (name.startsWith(getSelectedDeviceName())) {
            arList.put(addr, rec);

            if (getScanMode() == ScanMode.MANUAL) {
                // 스캔 리스트 방송 수행.
                mBrTop.sendBroadcastScanList(arList);
                return;
            }
        }

        // 이미 연결이 성립된 경우 리턴
        if (isConnect() == true) return;
        // 저장된 장치 주소가 없는 경우 리턴
        String saveMac = Preference.getBluetoothMac(getContext());
        if (saveMac == null) return;
        //규창 피트니스의 경우엔 DFU에서 끝자리 맥주소가 +1 되는 방어코드가 삽입
        //따라서 저장된 장치주소의 끝자리를 +1 시켜서 추가 저장

        String convertlastAddr = ConvertAddr(saveMac);
        if (BluetoothLEManager.DfuMode && convertlastAddr == null) return;
        Log.i(tag, "킷캣은 여기에 들어오는 것인가?");
        Log.i(tag, "현재 저장된 맥주소?"+saveMac+" DfuMode?:"+BluetoothLEManager.DfuMode+"/이름:"+device.getName());

        // 저장된 장치 주소와 검색된 장치 주소가 동일하지 않으면 리턴
        // 규창 저장된 장치 주소 및 변환된 DFU 장치주소와 검색된 장치 주소가 동일하지 않으면 리턴
        if (!BluetoothLEManager.DfuMode && !saveMac.equals(addr)) {
            Log.d(tag,"!saveMac.equals(addr)"+saveMac+" "+ addr+ getSelectedDeviceName());
            return;
        }
        if (BluetoothLEManager.DfuMode && !addr.equals(convertlastAddr)) {
            Log.d(tag,"!addr.equals(convertlastAddr)");
            return;
        }


        // 장치 명칭이 플래너 장치 형식이면 연결요청
        // 규창 부트모드 밴드의 광고 맥주소 끝자리 +1로 인해 일반 연결 요청 조건 강화
        if (name.startsWith(getSelectedDeviceName()) && saveMac.equals(addr) && BluetoothLEManager.DfuMode == false ) {
            Log.d(tag,"일반 연결 요청 루틴");
            requestConnect(addr, rssi);
            return;
        }


        // 규창 코치 미니일 경우
        if ((getDeviceNameStartDFUT() == "C1_DfuT") && (getDeviceNameStartDFUT() == name) && BluetoothLEManager.DfuMode == true ) {
            Log.d(tag,"코치미니 펌업 연결 요청 루틴");
            requestConnect(addr, rssi);
            return;
        }


        // 장치 명칭이 펌웨어 업데이트 형식이면 연결요청
        // 규창 검색된 장치이름이 null일 때가 있어 펌웨어 업데이트를 못할 수 있다
        // 따라서 검색된 주소가 저장주소끝자리+1된 주소와 같고, 현재 MW가 DFU모드면 변환주소로 접속 시킨다
        Log.d(tag,"saveMac.equals(addr) : "+addr.length()+ " save "+ convertlastAddr.length()+".equals()" + addr.equals(convertlastAddr));
        if (addr.equals(convertlastAddr) && BluetoothLEManager.DfuMode == true) {
            Log.d(tag,"피트니스 펌업 연결 요청 루틴");
            requestConnect(convertlastAddr, rssi);
            return;
        }
    }

    /**
     * 규창
     * BLE saveMac Dfu일시에 밴드의 광고 맥은  맥주소 끝자리 + 1이 된다 그에 따른 변환 메서드
     */
    private String ConvertAddr(String saveAddr){
        String convertlastAddr = null;
        //String convertlastAddr2 = null;
        if (saveAddr.length() > 3) {
            final String convertAddr = saveAddr;
            final String lastAddr1 = convertAddr.substring(convertAddr.length() - 2);
            final String lastAddr2 = convertAddr.substring(convertAddr.length() - 1);
            convertlastAddr = String.format("%s", convertAddr.substring(0, saveAddr.length() - 1));
            //Log.i(tag, "** **" + lastAddr );
            int parseInt1 = lastAddr1.charAt(0) + 1;
            int parseInt2 = lastAddr2.charAt(0) + 1;
            //Log.i(tag, "** **" + parseInt );
            //byte b = Byte.parseByte(parseByte);
            //b += (byte)1;

            Log.i(tag, "** **" + lastAddr1 + "  " + convertlastAddr);
            if (lastAddr2.charAt(0) == 'F' || lastAddr2.charAt(0) == 'f') {
                if (lastAddr1.charAt(0) == 'F' || lastAddr1.charAt(0) == 'f') {
                    convertlastAddr = String.format("%s", convertAddr.substring(0, convertAddr.length() - 2) + "0");
                }else if (lastAddr1.charAt(0) == '9') {
                    convertlastAddr = String.format("%s", convertAddr.substring(0, convertAddr.length() - 2) + "A");
                }else {
                    convertlastAddr = String.format("%s%c", convertAddr.substring(0, convertAddr.length() - 2), parseInt1);
                }
                convertlastAddr = String.format("%s", convertlastAddr + "0");
            }else if (lastAddr2.charAt(0) == '9') {
                convertlastAddr = String.format("%s", convertlastAddr + "A");
            }else {
                convertlastAddr = String.format("%s%c", convertlastAddr, parseInt2);
            }

            Log.i(tag, "** **" + lastAddr2 + "  " + convertlastAddr);

        }
        return convertlastAddr;
    }


    //규창 170321 페어링 해제 리플렉션(숨겨진 OS API사용)메서드 오픈
    private void unpairDevice(BluetoothDevice device){
        try{
            Log.i(tag,"unpair device:" +device.getName());
            Method method = device.getClass().getMethod("removeBond",(Class[]) null);
            method.invoke(device,(Object[]) null);
        }catch (Exception e){
            Log.i(tag,"unpair device error:"+ e );
            e.printStackTrace();
        }
    }


    /**
     * BLE device 스캔 시작, 중지.
     *
     * @param enable true: 시작, false: 중지.
     */
    protected void scanLeDevice(final boolean enable) {
        Log.d(tag, "scanLeDevice : " + enable);
        if (enable) {
            //규창 170321 스캔을 시작하는 경우(앱 새로 시작, 기기설정하기-기기 삭제후 스캔시, 블루투스 이격상황, 앱내 BLE기능 쓰는 버튼 등)
            //OS가 페어링 하고 있는 디바이스를 확인해서 우리 기기일 경우 삭제
            final Set<BluetoothDevice> pairDevices = mBTAdapter.getBondedDevices();
            if (pairDevices.size() > 0) {
                for (BluetoothDevice device : pairDevices){
                    Log.i(tag, "Your Paired Devices:" + device.getName());
                    if (device.getName().startsWith(getSelectedDeviceName())) {
                        unpairDevice(device);
                    }
                }
            }

            arList.clear();
            // Stops scanning after a pre-defined scan period.
            if (m_handle != null)
                m_handle.postDelayed(mRunnableBLE, SCAN_PERIOD);

            mScanning = true;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

                mBTAdapter.startLeScan(this);
            } else {
                if (getBluetoothLEScanner() != null) {
                    //getBluetoothLEScanner().startScan(null, mScanSetting, mScanCallback);
                    getBluetoothLEScanner().startScan(mFilter, mScanSetting, mScanCallback);
                    //규창 170124 스캔 방식 배치스캔 제거 -> onScanResult방식 오픈
                    /*mLastBatchTimestamp = SystemClock.elapsedRealtime();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                        if (mScanCallback != null && mBTAdapter != null)
                            getBluetoothLEScanner().flushPendingScanResults(mScanCallback);
                    }*/
                    //getBluetoothLEScanner().startScan(mScanCallback);
                } else {
                    Log.e(tag, "getBluetoothLEScanner is null");
                }
            }
        } else {
            if (m_handle != null) {
                m_handle.removeCallbacks(mRunnableBLE);
            }
            mScanning = false;
            stopScan();
            raiseDeviceList();
        }
    }

    private static boolean isSupported() {
        if (mBTAdapter == null) {
            Log.i(tag, "bt_not_available");
            return false;
        }
        return true;
    }

    public static boolean isEnabled() {
        if (isSupported() == false || mBTAdapter.isEnabled() == false) {
            return false;
        }
        return true;
    }

    @Override
    protected void dispose() {
        super.baseDispose();
        if (m_handle != null) {
            m_handle.removeCallbacks(mRunnableBLE);
            m_handle = null;
            mRunnableBLE = null;
        }
        applicationCb = null;
        mConnectionState = ConnectStatus.STATE_EXIT;
        arList = null;
    }

    private void onBatchScanResults(List<ScanResult> results) {
        if (arList == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final ScanFilter scanFilter_nus = new ScanFilter.Builder()
                    .setServiceUuid(new ParcelUuid(DeviceUUID.RX_SERVICE_UUID))
                    .build();
            final ScanFilter scanFilter_dfu = new ScanFilter.Builder()
                    .setServiceUuid(new ParcelUuid(DeviceUUID.DFU_SERVICE_UUID))
                    .build();

            for (ScanResult result : results) {
                if (!scanFilter_nus.matches(result) && !scanFilter_dfu.matches(result)) {
                    continue;
                }

                BluetoothDevice device = result.getDevice();
                int rssi = result.getRssi();

//                            Set<BluetoothDevice> list = mBTAdapter.getBondedDevices();
//                            if (list != null) {
//                                for (BluetoothDevice d : list) {
//                                    Log.d(tag, "bonded device -> " + d.getName());
//                                }
//                            }

                ScanRecord scanRecord = result.getScanRecord();
                Log.i(tag, "2. -> name:" + device.getName() + " addr:" + device.getAddress() + " rssi:" + rssi + " scan name:" + (scanRecord == null ? "null" : scanRecord.getDeviceName()));
                //Log.i(tag, "getScanMode()"+getScanMode());
                // 장치의 기본 정보 확인
                final String addr = device.getAddress();
                final String name = scanRecord == null ? "" : scanRecord.getDeviceName();
                // 장치 명칭이 없는 경우 리턴
                Log.i(tag, "2. -> name!!!!"+ name);
                if (name == null) continue;
                // 검색된 장치가 리스트에 없으면 추가한다.
                if (arList.containsKey(addr)) continue;
                DeviceRecord rec = new DeviceRecord(device, addr, name, rssi);
                if (name.startsWith(getSelectedDeviceName())) {
                    arList.put(addr, rec);
                    if (getScanMode() == ScanMode.MANUAL) {
                        // 스캔 리스트 방송 수행.
                        mBrTop.sendBroadcastScanList(arList);
                        //continue;
                    }
                }
                // 이미 연결이 성립된 경우 리턴
                if (isConnect()) return;

                // 저장된 장치 주소가 없는 경우 리턴
                String saveMac = Preference.getBluetoothMac(getContext());
                if (saveMac == null) continue;
                //규창 피트니스의 경우엔 DFU에서 끝자리 맥주소가 +1 되는 방어코드가 삽입
                //따라서 저장된 장치주소의 끝자리를 +1 시켜서 추가 저장
                final String convertlastAddr = ConvertAddr(saveMac);
                if (BluetoothLEManager.DfuMode && convertlastAddr == null) continue;

                Log.i(tag, "현재 저장된 맥주소?"+saveMac+" DfuMode?:"+BluetoothLEManager.DfuMode+"/이름:"+device.getName()+"/변경된 맥주소:"+convertlastAddr);

                // 저장된 장치 주소와 검색된 장치 주소가 동일하지 않으면 리턴
                // 규창 저장된 장치 주소, 변환된 DFU 장치주소와 검색된 장치 주소가 동일하지 않으면 리턴
                if (!BluetoothLEManager.DfuMode && !saveMac.equals(addr)) {
                    Log.d(tag,"!saveMac.equals(addr)"+saveMac+" "+ addr+ getSelectedDeviceName());
                    continue;
                }
                if (BluetoothLEManager.DfuMode && !addr.equals(convertlastAddr)) {
                    Log.d(tag,"!addr.equals(convertlastAddr)");
                    continue;
                }
                //Log.d(tag,"saveMac.equals(addr) : "+saveMac.equals(addr) + " save "+ saveMac+ " mac "+addr);
                // 장치 명칭이 플래너 장치 형식이면 연결요청
                // 규창 부트모드 밴드의 광고 맥주소 끝자리 +1로 인해 일반 연결 요청 조건 강화
                if (name.startsWith(getSelectedDeviceName()) && saveMac.equals(addr) && BluetoothLEManager.DfuMode == false ) {
                    Log.d(tag,"일반 연결 요청 루틴");
                    requestConnect(addr, rssi);
                    return;
                }

                // 규창 코치 미니일 경우
                if ((getDeviceNameStartDFUT() == "C1_DfuT") && (getDeviceNameStartDFUT() == name) && BluetoothLEManager.DfuMode == true ) {
                    Log.d(tag,"코치미니 펌업 연결 요청 루틴");
                    requestConnect(addr, rssi);
                    return;
                }

                // 장치 명칭이 펌웨어 업데이트 형식이면 연결요청
                // 규창 검색된 장치이름이 null일 때가 있어 펌웨어 업데이트를 못할 수 있다
                // 따라서 검색된 주소가 저장주소끝자리+1된 주소와 같고, 현재 MW가 DFU모드면 변환주소로 접속 시킨다
                Log.d(tag,"saveMac.equals(addr) : "+addr.length()+ " save "+ convertlastAddr.length()+".equals()" + addr.equals(convertlastAddr));
                if (addr.equals(convertlastAddr) && BluetoothLEManager.DfuMode == true) {
                    Log.d(tag,"피트니스 펌업 연결 요청 루틴");
                    requestConnect(convertlastAddr, rssi);
                    return;
                }
            }
        }
    }

    // 규창 170124 스캔 방식 배치스캔 제거 -> onScanResult방식 오픈
    // callbacktype은 현재 기기의 스캔 스테이트를 알고 싶을 때 쓰인다고 조사됨 아직은 베타 진행해봐야 아는 문제
    private void onScanResult(int callbackType, ScanResult result) {
        if (arList == null) return;
        Log.i(tag, "callbackType="+callbackType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final ScanFilter scanFilter_nus = new ScanFilter.Builder()
                    .setServiceUuid(new ParcelUuid(DeviceUUID.RX_SERVICE_UUID))
                    .build();
            final ScanFilter scanFilter_dfu = new ScanFilter.Builder()
                    .setServiceUuid(new ParcelUuid(DeviceUUID.DFU_SERVICE_UUID))
                    .build();

            if (!scanFilter_nus.matches(result) && !scanFilter_dfu.matches(result)) {
                return;
            }

            BluetoothDevice device = result.getDevice();
            int rssi = result.getRssi();

//                            Set<BluetoothDevice> list = mBTAdapter.getBondedDevices();
//                            if (list != null) {
//                                for (BluetoothDevice d : list) {
//                                    Log.d(tag, "bonded device -> " + d.getName());
//                                }
//                            }

            ScanRecord scanRecord = result.getScanRecord();
            Log.i(tag, "2. -> name:" + device.getName() + " addr:" + device.getAddress() + " rssi:" + rssi + " scan name:" + (scanRecord == null ? "null" : scanRecord.getDeviceName()));
            //Log.i(tag, "getScanMode()"+getScanMode());
            // 장치의 기본 정보 확인
            String addr = device.getAddress();
            String name = scanRecord == null ? "" : scanRecord.getDeviceName();
            // 장치 명칭이 없는 경우 리턴
            Log.i(tag, "2. -> name!!!!"+ name + addr);
            if (name == null) return;
            // 검색된 장치가 리스트에 없으면 추가한다.
            if (arList.containsKey(addr)) return;
            DeviceRecord rec = new DeviceRecord(device, addr, name, rssi);
            if (name.startsWith(getSelectedDeviceName())) {
                arList.put(addr, rec);
                if (getScanMode() == ScanMode.MANUAL) {
                    // 스캔 리스트 방송 수행.
                    mBrTop.sendBroadcastScanList(arList);
                    //return;
                }
            }
            // 이미 연결이 성립된 경우 리턴
            if (isConnect()) return;

            // 저장된 장치 주소가 없는 경우 리턴
            String saveMac = Preference.getBluetoothMac(getContext());
            if (saveMac == null) return;
            //규창 피트니스의 경우엔 DFU에서 끝자리 맥주소가 +1 되는 방어코드가 삽입
            //따라서 저장된 장치주소의 끝자리를 +1 시켜서 추가 저장
            final String convertlastAddr = ConvertAddr(saveMac);
            if (BluetoothLEManager.DfuMode && convertlastAddr == null) return;

            Log.i(tag, "현재 저장된 맥주소?"+saveMac+" DfuMode?:"+BluetoothLEManager.DfuMode+"/이름:"+device.getName()+"/변경된 맥주소:"+convertlastAddr);

            // 저장된 장치 주소와 검색된 장치 주소가 동일하지 않으면 리턴
            // 규창 저장된 장치 주소, 변환된 DFU 장치주소와 검색된 장치 주소가 동일하지 않으면 리턴

            if (!BluetoothLEManager.DfuMode && !saveMac.equals(addr)) {
                Log.d(tag,"!saveMac.equals(addr)"+saveMac+" "+ addr+ getSelectedDeviceName());
                return;
            }
            if (BluetoothLEManager.DfuMode && !addr.equals(convertlastAddr)) {
                Log.d(tag,"!addr.equals(convertlastAddr)");
                return;
            }

            //Log.d(tag,"saveMac.equals(addr) : "+saveMac.equals(addr) + " save "+ saveMac+ " mac "+addr);
            // 장치 명칭이 플래너 장치 형식이면 연결요청
            // 규창 부트모드 밴드의 광고 맥주소 끝자리 +1로 인해 일반 연결 요청 조건 강화
            if (name.startsWith(getSelectedDeviceName()) && saveMac.equals(addr) && !BluetoothLEManager.DfuMode) {
                Log.d(tag,"일반 연결 요청 루틴");
                requestConnect(addr, rssi);
                return;
            }

            // 규창 코치 미니일 경우
            if ((getDeviceNameStartDFUT() == "C1_DfuT") && (getDeviceNameStartDFUT() == name) && BluetoothLEManager.DfuMode) {
                Log.d(tag,"코치미니 펌업 연결 요청 루틴");
                requestConnect(addr, rssi);
                return;
            }

            // 장치 명칭이 펌웨어 업데이트 형식이면 연결요청
            // 규창 검색된 장치이름이 null일 때가 있어 펌웨어 업데이트를 못할 수 있다
            // 따라서 검색된 주소가 저장주소끝자리+1된 주소와 같고, 현재 MW가 DFU모드면 변환주소로 접속 시킨다
            Log.d(tag,"saveMac.equals(addr) : "+addr.length()+ " save "+ convertlastAddr.length()+".equals()" + addr.equals(convertlastAddr));
            if (addr.equals(convertlastAddr) && BluetoothLEManager.DfuMode) {
                Log.d(tag,"피트니스 펌업 연결 요청 루틴");
                requestConnect(convertlastAddr, rssi);
                return;
            }
        }
    }

    protected DeviceBaseScan(Context context) {
        super(context);

        // 규창... 소니폰 마시멜로폰에서 스캔완료 후 connect는 뜨나 콜백 래퍼 없다고 나오는 문제 발견
        // 참고 http://stackoverflow.com/questions/20069507/gatt-callback-fails-to-register
        // m_handle = new Handler();
        m_handle = new Handler(Looper.getMainLooper());

        mBrTop = new MWBroadcastTop(getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFilter = new ArrayList<>();
            /*mFilter = new ArrayList<>();
            ScanFilter.Builder fBuilder = new ScanFilter.Builder();
            mFilter.add(fBuilder.setServiceUuid(new ParcelUuid(DeviceUUID.RX_SERVICE_UUID)).build());*/

            ScanSettings.Builder sbuilder = new ScanSettings.Builder();
            //규창 170124 스캔 방식 배치스캔 제거 -> onScanResult방식 오픈
            mScanSetting = sbuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
            //mScanSetting = sbuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED).setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).
                    //setReportDelay(mLastBatchTimestamp).setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE).build();
            mScanCallback = new ScanCallback() {
                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.e(tag, "onScanFailed : " + errorCode);
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    Log.i(tag, "onBatchScanResults");
                    
                    //getBluetoothLEScanner().flushPendingScanResults(mScanCallback);
                    /*for (ScanResult s : results) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            Log.i(tag, "1. -> name:" + s.getDevice().getName() + " addr:" + s.getDevice().getAddress() + " rssi:" + s.getRssi());
                    }*/

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            DeviceBaseScan.this.onBatchScanResults(results);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                // 규창 16.12.13 스캔 리포트 딜레이에는 부팅후 시간을 이용하며 콜백완료 때마다 다를 것이고
                                // 이전 부팅 후 시간과 지금 부팅 후 시간의 차이값을 넣어서 스캔시 마다 리포트 딜레이를 계속 재설정한다
                                final long now = SystemClock.elapsedRealtime();
                                //규창 170124 스캔 방식 배치스캔 제거 -> onScanResult방식 오픈
                                //mLastBatchTimestamp = SystemClock.elapsedRealtime();
                                if (mLastBatchTimestamp != now) {
                                    Log.i(tag, "mLastBatchTimestamp"+mLastBatchTimestamp+" now:"+now + "ElapsedTime:" + (now - mLastBatchTimestamp));
                                    mLastBatchTimestamp = now - mLastBatchTimestamp;
                                }
                                Log.i(tag, "mScanSetting.getReportDelayMillis()"+mScanSetting.getReportDelayMillis());
                                mScanSetting = sbuilder.setReportDelay(mLastBatchTimestamp).build();
                                Log.i(tag, "mScanSetting.getReportDelayMillis()"+mScanSetting.getReportDelayMillis());
                            }

                        }
                    });
                }


                //규창 170124 스캔 방식 배치스캔 제거 -> onScanResult방식 오픈
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Log.d(tag, "onScanResult");
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            DeviceBaseScan.this.onScanResult(callbackType,result);
                        }
                    });
                }
            };
        }
    }
}
