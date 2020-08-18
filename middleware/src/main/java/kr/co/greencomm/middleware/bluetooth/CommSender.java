package kr.co.greencomm.middleware.bluetooth;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import kr.co.greencomm.middleware.main.BluetoothLEManager;
import kr.co.greencomm.middleware.service.MWBroadcastTop;
import kr.co.greencomm.middleware.utils.MessageClass;
import kr.co.greencomm.middleware.utils.NoticeIndex;
import kr.co.greencomm.middleware.utils.ProductCode;
import kr.co.greencomm.middleware.utils.container.CommQueue;

/**
 * Created by jeyang on 2016-08-25.
 */

public class CommSender {
    private static final String tag = CommSender.class.getSimpleName();

    /**
     * Flag
     **/
    private static boolean busy = false;
    private boolean isTimerCancel = false;
    private boolean isReady = false;

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    private boolean isSync = false;
    private boolean runFlag = false;

    /**
     * Handler
     **/
    private Handler mDelay_Handler;
    private Runnable mDelay_Runnable;

    /**
     * Variable
     **/
    private long m_timeout = 3000; // 3 sec

    /**
     * Instance
     **/
    private ArrayList<CommQueue> Queue;
    private BluetoothLEManager mBle;

    // 규창 17.03.16 밴드 응답없음 현상 발생시 연결해제 후 재 연결!!!!!
    private int mEmergencyCnt = 0;


    private static CommSender instance;
    public CommQueue LastSender;

    private Thread mThr;
    private Runnable mThr_Runnable;

    private MWBroadcastTop m_notify;

    private WeakReference<Context> WContext;

    private CommSender(Context context) {
        WContext = new WeakReference<>(context);
        LastSender = new CommQueue(BluetoothCommand.Acc, 0L, RequestAction.Start, false);

        m_notify = new MWBroadcastTop(WContext.get());

        mDelay_Handler = new Handler();
        mDelay_Runnable = new Runnable() {
            @Override
            public void run() {
                if (isTimerCancel) {
                    Log.d(tag, "is timer cancel....");
                    return;
                }
                Log.d(tag, "go Timer!!!!");
                busy = false;
                if (!Queue.isEmpty()) {
                    Queue.remove(0);
                }
            }
        };

        Queue = new ArrayList<>();

        mThr_Runnable = new Runnable() {
            @Override
            public void run() {
                mBle = BluetoothLEManager.getInstance(WContext.get());
                Log.d(tag, "RUN!!! SENDER");

                while (runFlag) {
                    try {
                        if (!isReady) {
                            Thread.sleep(50);
                            continue;
                        }

                        if (mBle.getConnectionState() == ConnectStatus.STATE_DISCONNECTED) {
                            Thread.sleep(1000);
                            continue;
                        }

                        if (Queue.isEmpty()) {
                            notifySync(false);
                            Thread.sleep(50);
                            continue;
                        }
                        Log.i(tag,"3 LastSender.getResp:"+LastSender.getResp() + "CommSender.busy:"+busy +"LastSender.cmd:"+LastSender.getCmd());


                        if (busy) {
                            Log.d(tag, "4");
                            //Log.i(tag,"LastSender.getResp: "+LastSender.getResp());
                            //Log.i(tag,"LastSender.getCmd: "+LastSender.getCmd());

                            if (LastSender.getResp()) {
                                // 규창 17.03.16 밴드 응답없음 현상 회피 루틴 추가
                                if (Queue.size() > 0 ) {
                                    Log.i(tag, "Queue.size()" + Queue.size());


                                    try {
                                        final BluetoothCommand nowLastSenderCmd = Queue.get(0).getCmd();

                                        Log.i(tag, "nowLastSenderCmd: " + nowLastSenderCmd);
                                        if (nowLastSenderCmd != LastSender.getCmd()) {
                                            mEmergencyCnt++;
                                            Log.i(tag, "응답없음 횟수:" + mEmergencyCnt + " [" + nowLastSenderCmd + "] " + "[" + LastSender.getCmd() + "]");
                                            if (mEmergencyCnt > 100) {
                                                mEmergencyCnt = 0;
                                                cancelTimer();
                                                if (Queue.size() > 0) {
                                                    Queue.remove(0);
                                                }

                                                busy = false;
                                                mBle.emergencyReconnect();
                                            }
                                        } else {
                                            mEmergencyCnt++;
                                            Log.i(tag, "응답없음 횟수:" + mEmergencyCnt + " [" + nowLastSenderCmd + "] " + "[" + LastSender.getCmd() + "]");
                                            if (mEmergencyCnt > 100) {
                                                mEmergencyCnt = 0;
                                                cancelTimer();
                                                if (Queue.size() > 0) {
                                                    Queue.remove(0);
                                                }

                                                busy = false;
                                                mBle.emergencyReconnect();

                                            }
                                        }
                                    } catch (IndexOutOfBoundsException e) {
                                        Log.d(tag, "Emergency reconnect index err sender");
                                    } catch (Exception e) {
                                        Log.d(tag, "Emergency reconnect internal interrupt thread");
                                        e.printStackTrace();
                                    }
                                } else {
                                    mEmergencyCnt = 0;
                                    Log.i(tag, "큐에 쌓인 명령어 없음 초기화"+ mEmergencyCnt + " " + LastSender.getCmd());
                                }
                                Log.d(tag, "5");
                                Thread.sleep(50);
                                continue;
                            }
                            cancelTimer();
                            if(Queue.size() > 0) {
                                Queue.remove(0);
                            }

                            busy = false;
                            continue;
                        }
                        Log.d(tag, "6");
                        busy = true;
                        notifySync(true);

                        cancelTimer();
                        startTimer();
                        isTimerCancel = false;
                        Log.d(tag, "7");

                        try {
                            CommQueue msg = Queue.get(0);
                            LastSender = msg;
                            sendBluetoothCmd(LastSender.getCmd(), LastSender.getAction(), LastSender.getTime(),
                                    LastSender.getCls(), LastSender.getCount(), LastSender.getIndex());
                            Log.d(tag, "8 cmd->" + LastSender.getCmd());
                            //send
                        } catch (IndexOutOfBoundsException e) {
                            Log.d(tag, "index err sender");
                        } catch (Exception e) {
                            Log.d(tag, "internal interrupt thread");
                            e.printStackTrace();
                        }

                        Log.d(tag, "9");
                        Thread.sleep(50);
                    } catch (IndexOutOfBoundsException e) {
                        Log.d(tag, "outernal err sender");
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        Log.d(tag, "interrupt thread~");
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.d(tag, "outernal err");
                        e.printStackTrace();
                    }
                }
                Log.d(tag, "stop..................");
            }
        };
    }

    public static CommSender getInstance(Context context) {
        if (instance == null) {
            instance = new CommSender(context);
        }
        return instance;
    }

    private void startTimer() {
        mDelay_Handler.postDelayed(mDelay_Runnable, m_timeout);
    }

    private void cancelTimer() {
        isTimerCancel = true;
        Log.d(tag, "cancelTimer");
        mDelay_Handler.removeCallbacks(mDelay_Runnable);
    }

    public synchronized void append(CommQueue data) {
        if (!checkDuplicate(data)) {
            Queue.add(data);
            Log.d(tag, "append!!!! Q size->" + Queue.size() + " cmd->" + data.getCmd());
        }
    }

    private boolean checkDuplicate(CommQueue data) {
        for (CommQueue dat : Queue) {
            if (dat == data) {
                return true;
            }
        }

        return false;
    }

    public static boolean isBusy() {
        return busy;
    }

    public boolean compare(BluetoothCommand cmd) {
        //android return (busy && LastSender.getResp()) && cmd == LastSender.getCmd();
        //ios return (isBusy() && LastSender.getResp()) ? cmd == LastSender.getCmd():false;
        return (isBusy() && LastSender.getResp()) ? cmd == LastSender.getCmd():false;
    }

    public void resetBusy() {
        Log.d(tag, "resetBusy");
        if (!Queue.isEmpty()) {
            Queue.remove(0);
        }
        // 규창 17.03.16 밴드 응답없음 현상 회피 카운트 초기화
        mEmergencyCnt = 0;
        cancelTimer();
        busy = false;
    }

    public synchronized void start() {
        if (mThr == null) {
            mThr = new Thread(mThr_Runnable);
            runFlag = true;
            busy = false;

            mThr.start();
        }
        Log.d(tag, "start");
    }

    public synchronized void cancel() {
        try {
            runFlag = false;
            busy = false;
            if (mThr != null) {
                mThr.join();
                mThr.interrupt();
                mThr = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //규창 16.12.27 강제 동기화
    //public void notifySync(boolean sync)
    public void notifySync(boolean sync) {
        if (isSync != sync) {
            m_notify.sendBroadcastNotifySync(sync);
            isSync = sync;
        }
    }

    private synchronized void sendBluetoothCmd(BluetoothCommand cmd, RequestAction action, long time, MessageClass cls, int count, NoticeIndex index) {
        if (mBle.getConnectionState() == ConnectStatus.STATE_DISCONNECTED) {
            return;
        }

        String product = DeviceBaseScan.getSelectedDeviceName();
        Log.i(tag, "sendBluetoothCmd"+product+"!!!!!"+cmd);
        switch (cmd) {
            case State:
                mBle.requestState();
                break;
            case Activity:
                mBle.requestActivity(action, time);
                break;
            case StepCount_Calorie:
                mBle.requestStepCount_Calorie(time); // 해당 날의 총 스탭수를 줌.
                break;
            case Battery:
                if (product.equals(ProductCode.Fitness.getBluetoothDeviceName())) {
                    mBle.requestBattery();
                } else if (product.equals(ProductCode.Coach.getBluetoothDeviceName())) {
                    mBle.requestBatteryProductCoach();
                }
                break;
            case Acc:
                if (product.equals(ProductCode.Fitness.getBluetoothDeviceName())) {
                    mBle.requestAcc(action);
                } else if (product.equals(ProductCode.Coach.getBluetoothDeviceName())) {
                    mBle.requestAccProductCoach(action);
                }
                break;
            case UserData:
                mBle.sendUserData();
                break;
            case RTC:
                mBle.sendRTC();
                break;
            case Sleep:
                mBle.requestSleep(action, time);
                break;
            case Stress:
                mBle.requestStress(action, time);
                break;
            case Version:
                if (product.equals(ProductCode.Fitness.getBluetoothDeviceName())) {
                    mBle.requestVersion();
                } else if (product.equals(ProductCode.Coach.getBluetoothDeviceName())) {
                    mBle.requestVersionProductCoach();
                }
                break;
            case CoachCalorie:
                mBle.sendCalorie((int)time);
                break;
            case NoticeONOFF:
                mBle.sendNoticeONOFF(action, index);
                break;
            case NoticeMessage:
                mBle.sendNoticeMessage(cls, (short) count);
                break;
            default:
                break;
        }
    }
}
