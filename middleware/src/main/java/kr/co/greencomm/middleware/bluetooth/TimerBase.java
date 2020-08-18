package kr.co.greencomm.middleware.bluetooth;

import android.content.Context;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.greencomm.middleware.utils.container.CommQueue;

/**
 * 안드로이드의 Timer를 사용하는 안드로이드OS에 밀접한 연결이 있는 부분. 이 윗단은 안드로이드 블루투스를 제외한 기능만 구현한다.
 *
 * @author jeyang
 */
public abstract class TimerBase extends DataControlBase {
    private static final String tag = TimerBase.class.getSimpleName();

    /**
     * Variable
     **/
    private static final int SIZE_RSSI_Q = 10;
    private int[] rssi_Q = new int[SIZE_RSSI_Q];
    private final long TIMER_SCAN_DELAY = 0;
    private final long TIMER_SCAN_PERIOD = 5000;

    /**
     * Instance
     **/
    protected String mBluetoothDeviceAddress, mBluetoothDeviceName;
    public CommSender sender;

    /**
     * Abstract
     **/
    public abstract void setIsLiveApp(boolean isLiveApp);

    public abstract boolean isLiveApp();

    public abstract int startBluetooth();

    public abstract int tryBluetooth();

    public abstract void stopBluetooth();

    /**
     * Timer
     **/
    private Timer m_timer_scan = null, mRssi_Timer = null, mTimerConnect = null, mStepCalorie_Timer = null;

    public TimerBase(Context context) {
        super(context);
        sender = CommSender.getInstance(context);
    }

    /**
     * Method
     **/
    @Override
    protected void startScanTimer() {
        if (m_timer_scan != null) return;
        m_timer_scan = new Timer("ScanTimer");
        m_timer_scan.schedule(new TimerTask() {
            @Override
            public void run() {
                //Log.i(tag,"ble connection state:"+mConnectionState);
                if (isConnect() == false) {
                    //queue.clear();
                    if (mScanning == false) {
                        Log.d(tag, "scanLe start!!!!!!!!!");
                        scanLeDevice(true);
                    }
                    //규창 17.01.09 백그라운드에서 스캔타이머 동작 못하도록 처리
                } else if (isConnect() == true || getScanMode() == ScanMode.MANUAL){
                    cancelScanTimer();
                }
            }
        }, TIMER_SCAN_DELAY, TIMER_SCAN_PERIOD);
    }

    @Override
    protected void cancelScanTimer() {
        if (m_timer_scan != null) {
            m_timer_scan.cancel();
            m_timer_scan = null;
        }
    }

    protected void startStepCalorieTimer() {
        if (mStepCalorie_Timer == null) {
            mStepCalorie_Timer = new Timer("StepCalorie");
            mStepCalorie_Timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sender.append(new CommQueue(BluetoothCommand.StepCount_Calorie, System.currentTimeMillis(), RequestAction.Start, true));
                }
            }, 0, 60000);
        }
    }

    protected void cancelStepCalorieTimer() {
        if (mStepCalorie_Timer != null) {
            mStepCalorie_Timer.cancel();
            mStepCalorie_Timer = null;
        }
    }

    protected void startConnectTimer() {
        if (mTimerConnect == null) {
            mTimerConnect = new Timer("Connect-Timer");
            mTimerConnect.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(tag, "Connect-Timer...");
                    if (!isLiveApp())
                        return;

                    //규창 17.01.09 백그라운드에서 스캔타이머 동작 못하도록 처리
                    if (!isConnect() && getScanMode() == ScanMode.AUTO) {
                        if (mBluetoothGatt != null) {
                            mBluetoothGatt.close();

                            mBluetoothGatt = null;
                            mBluetoothDeviceAddress = null;
                            mBluetoothDeviceName = null;
                        }
                        Log.d(tag, "Connect-Timer...->scan");
                        scanLeDevice(true);
                    }
                }
            }, 60 * 1000, 60 * 1000);
        }
    }

    protected void cancelConnectTimer() {
        if (mTimerConnect != null)
            mTimerConnect.cancel();
        mTimerConnect = null;
    }

    protected void startRssiTimer() {
        if (mRssi_Timer != null)
            return;

        mRssi_Timer = new Timer("Rssi-Timer");
        mRssi_Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mBluetoothGatt != null && getConnectionState() == ConnectStatus.STATE_CONNECTED)
                    mBluetoothGatt.readRemoteRssi();
            }
        }, 0, 1000);
    }

    protected void cancelRssiTimer() {
        if (mRssi_Timer == null)
            return;
        mRssi_Timer.cancel();
        mRssi_Timer = null;

        for (int i = 0; i < rssi_Q.length; i++)
            rssi_Q[i] = 0;
    }

    protected boolean isRssiDisconnect(int rssi) {
        int sum = getRssi(rssi);
        if (sum != 0 && sum < DEF_RSSI) {
            return true;
        }

        return false;
    }

    protected int getRssi(int rssi) {
        int ret = 0, count = 0;
        for (int i = 0; i < SIZE_RSSI_Q - 1; i++) {
            rssi_Q[i] = rssi_Q[i + 1];
        }
        rssi_Q[SIZE_RSSI_Q - 1] = rssi;

        for (int i : rssi_Q) {
            if (i != 0) {
                ret += i;
                count++;
            }
        }

        if (count != 10)
            return 0;

        return ret / count;
    }

    @Override
    protected void dispose() {
        super.dispose();
        m_timer_scan = mRssi_Timer = mStepCalorie_Timer = mTimerConnect = null;
        rssi_Q = null;
        mBluetoothDeviceAddress = mBluetoothDeviceName = null;
    }
}
