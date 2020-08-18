package kr.co.greencomm.middleware.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

import kr.co.greencomm.middleware.main.BluetoothLEManager;
import kr.co.greencomm.middleware.utils.StateApp;
import kr.co.greencomm.middleware.wrapper.MWControlCenter;

public class MWService extends Service {
    private static final String tag = MWService.class.getSimpleName();
    /**
     * Version
     **/
    public static final String version = "1.1.1.1";

    /**
     * Variable
     **/
    public static StateApp STATE_APPLICATION = StateApp.STATE_NO_RESPONSE;

    public static final String ACTION_START_SERVICE = "kr.co.greencomm.ibody24.coach.Mw.startService";
    public static final String NAME = "kr.co.greencomm.ibody24.coach.mw.service.MWService";

    private static boolean TEST = false;

    /**
     * Instance
     **/
    private MWControlCenter mw;

    private AlarmManager mAlarm;
    private MWBroadcastTop mBrTop;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(getApplicationContext(), "서비스 시작-", Toast.LENGTH_SHORT).show();
        Log.v(tag, "서비스 시작- version:" + version);

        /**
         * 서비스 시작
         * 1. Web에서 오늘날짜 데이터를 받는다. 있으면 미들웨어로 대입, 없으면 말고...
         * 2. 데이터를 받는 결과를 알기전까지 블루투스 연결을 하면 안됨.???->네트워크가 불안하면?? 곤란함.
         * 3. 그대로 진행하고, 데이터 들어오면 sort. 들어와도 과거 데이터니까 문제없을듯. sync 안맞나... -> 그냥 add하지 말고 데이터를 받은다음에 sort해서 다시 대입.
         */
        mBrTop = new MWBroadcastTop(getApplicationContext());

        initMW();
        initSetting();
        //TEST!!!!
        if (TEST) {
            /** 양정은 **/
            //mDBHelper.setMac("D4:1A:7B:1F:F6:9F"); // 양정은 000015
            //mDBHelper.setMac("DB:05:A5:D4:78:C1"); // 양정은 000017
            //mDBHelper.setUserCode("49f2a5e7-55ed-46fa-9c11-0217eb8a04a9"); // jungeun_yang
            /** 김동현 **/
            //mDBHelper.setMac("E1:43:58:26:D5:56"); // 김동현
            //mDBHelper.setUserCode("12c3dc80-eff5-4985-9fb5-d76df3fc8b01"); // dhkim

            /** S4-L test **/
            //mDBHelper.setMac("CA:64:B6:7E:56:47"); // 900001
            //mDBHelper.setUserCode("2f138986-cb1d-4dae-839b-506698fe92a9"); // tttest

            /** S6-L test **/
            //mDBHelper.setMac("E2:33:79:D1:4D:5B"); // 900004
            //mDBHelper.setUserCode("c39f80ef-2cc6-4aeb-b3d5-4650b31a1947"); // ttttest

            /** test 2 **/
//            mDBHelper.setMac("D7:E5:18:E7:AA:72"); // 001018
//            mDBHelper.setUserCode("2f138986-cb1d-4dae-839b-506698fe92a9"); // tttest

            //mDBHelper.setUserDietPeriod(4);
            //mDBHelper.setUserProfile(30, 1, 1, 170, 72, 70);
        }
        //test end..

        if (mAlarm == null)
            mAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        startServiceLiveMonitoring(getApplicationContext());
        startWakeLockMonitoring(getApplicationContext());
        //startCheckFirmVersion(getApplicationContext());

        boolean isLive = mw.mBleManager.isLiveApp();
        /** mw work **/
        // 서비스 시작되었을 때, 모든 준비가 완료되었다고 판단되면 미들웨어 시작.
        if (BluetoothLEManager.isEnabled()) {
            mw.tryConnectionBluetooth(); // 여긴 service의 첫 시작이므로 무한정 scantimer를 돌리면 좋지 못할듯..한번만 돌리고 앱의 켜짐을 기다리자.
        }

        mBrTop.sendBroadcastRequestIsLiveApp(); // 사실 위에 응답이 도착하면 앱은 생존한거라 봐도 무방하긴 함...
    }

    private void initSetting() {
    }

    private void initMW() {
        if (mw == null) {
            Log.d(tag, "MWService Control-center");
            mw = MWControlCenter.getInstance(getApplicationContext());
        }
    }

    private void startServiceLiveMonitoring(Context context) {
        Intent intent = new Intent(MWBroadcastReceiver.ACTION_RESTART_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= 19)
            mAlarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 1000 * 30, sender);
        else
            mAlarm.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 1000 * 30, sender);
    }

    private void stopServiceLiveMonitoring(Context context) {
        Intent intent = new Intent(MWBroadcastReceiver.ACTION_RESTART_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        mAlarm.cancel(sender);
    }

    private void startWakeLockMonitoring(Context context) {
        Intent intent = new Intent(MWBroadcastReceiver.ACTION_WAKELOCK_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= 19)
            mAlarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 600000, sender);
        else
            mAlarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 600000, sender);
    }

    private void stopWakeLockMonitoring(Context context) {
        Intent intent = new Intent(MWBroadcastReceiver.ACTION_WAKELOCK_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        mAlarm.cancel(sender);
    }

    private void startCheckFirmVersion(Context context) {
        Calendar mCal = Calendar.getInstance();
        mCal.set(Calendar.HOUR_OF_DAY, 3); // 새벽 3시..

        Intent intent = new Intent(MWBroadcastReceiver.ACTION_CHECK_FIRM_VERSION);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= 19)
            mAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, mCal.getTimeInMillis(), 1000 * 60 * 60 * 24, sender);
        else
            mAlarm.setRepeating(AlarmManager.RTC_WAKEUP, mCal.getTimeInMillis(), 1000 * 60 * 60 * 24, sender);
    }

    private void stopCheckFirmVersion(Context context) {
        Intent intent = new Intent(this.getClass().getName() + "FirmVersion");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        mAlarm.cancel(sender);
    }

    /**
     * 1. 블루투스DB가 o
     * 서비스 시작과 동시에 블루투스 Scan 시작하여 동작을 시작한다.
     * 2. 블루투스DB가 x
     * 서비스 시작하면 pending을 하며, Application으로부터 블루투스 Start BR을 기다린다.
     * 밴드를 찾기에 따라 success, failed BR을 함.
     * success
     * - 그냥 진행
     * failed
     * - Application에서 블루투스 재시작 BR을 받음.
     * <p>
     * -->이후는 MW 정상 구동에 돌입.
     * <p>
     * *블루투스 정보
     * 블루투스 상태 조회 BR 필요.
     * *데이터 발생 callback의 BR화
     * *추천 운동 동작과 관련된 BR
     * -List 전송.(Provider???)
     * -coachInform 전송.(Provider???)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(getApplicationContext(), "서비스 중..", Toast.LENGTH_SHORT).show();
        Log.v(tag, "서비스 중.." + version);
//        if (intent != null) {
//            String action = intent.getAction();
//            if (action != null) {
//                if (action.equals(MWBroadcastReceiver.ACTION_START_FIRMUP)) {
//                    FirmDeviceService mFirm = new FirmDeviceService(getApplicationContext(), intent.getStringExtra(Action.EXTRA_NAME_STRING_1));
//                    mFirm.start();
//                    // progress callback 등록. 이럴때만 사용하기 때문에 굳이, 전역으로 등록하지 않음.
//                } else if (action.equals(MWBroadcastReceiver.ACTION_START_FIRM_VERSION)) {
//                    FirmInfo firm = new FirmInfo(getApplicationContext(), ROOT_PATH + FIRM_FOLDER, intent.getStringExtra(Action.EXTRA_NAME_STRING_1));
//                    firm.start();
//                } else if (action.equals(PackageBroadcastReceiver.ACTION_SET_TODAY_ACTION)) {
//                    mDBHelper.clearActDB();
//                    new ActionInfo().start();
//                }
//            }
//        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(getApplicationContext(), "서비스 종료!!", Toast.LENGTH_SHORT).show();
        Log.v(tag, "서비스 종료!!!");
        super.onDestroy();

//        actionQ.setWebQueueListener(null);
//        actionQ.cancel();
//        try {
//            actionQ.join();
//        } catch (InterruptedException e) {
//        }
//        actionQ = null;

        mw = null;
        mBrTop = null;
        mAlarm = null;
    }

    @Override
    public void finalize() throws Throwable {
        //Toast.makeText(getApplicationContext(), "서비스 종료!!", Toast.LENGTH_SHORT).show();
        Log.v(tag, "서비스 종료!!!");
        super.finalize();

//        actionQ.setWebQueueListener(null);
//        actionQ.cancel();
//        try {
//            actionQ.join();
//        } catch (InterruptedException e) {
//        }
//        actionQ = null;

        mw = null;
        mBrTop = null;
        mAlarm = null;
    }
}