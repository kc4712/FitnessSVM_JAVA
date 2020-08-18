package kr.co.greencomm.middleware.service;

import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import kr.co.greencomm.middleware.bluetooth.ConnectStatus;
import kr.co.greencomm.middleware.bluetooth.DeviceRecord;
import kr.co.greencomm.middleware.main.BluetoothLEManager;
import kr.co.greencomm.middleware.utils.container.ActivityData;
import kr.co.greencomm.middleware.utils.container.Battery;
import kr.co.greencomm.middleware.utils.container.SleepData;
import kr.co.greencomm.middleware.wrapper.MWControlCenter;
import no.nordicsemi.android.dfu.DfuBaseService;

public class MWBroadcastTop {
    private static final String tag = MWBroadcastTop.class.getSimpleName();

    /**
     * Instance
     **/
    private MWControlCenter mw;
    private final WeakReference<Context> WContext;

    public MWBroadcastTop(Context context) {
        WContext = new WeakReference<>(context);
    }

    private Context getContext() {
        return WContext.get();
    }

    private BluetoothLEManager getBLEManager() {
        mw = MWControlCenter.getInstance(getContext());
        return mw.mBleManager;
    }

    /**
     * Broadcast
     **/
    public void sendBroadcastStepNCalorie(short step, double activity, double sleep, double daily, double coach) {
        if (!getBLEManager().isLiveApp())
            return;
        double[] value = new double[]{activity, coach, sleep, daily};

        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Am.ACTION_MW_STEP_CALORIE);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_1, step);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_DOUBLE_ARRAY, value);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastStepNCalorie(short step, double[] value) {
        if (!getBLEManager().isLiveApp())
            return;

        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Am.ACTION_MW_STEP_CALORIE);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_1, step);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_DOUBLE_ARRAY, value);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastConnectionState(ConnectStatus state) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Bm.ACTION_MW_CONNECTION_STATE);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_1, state.ordinal());
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastConnectionFailed() {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Bm.ACTION_MW_CONNECTION_FAILED);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastConnectionSuccess() {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Bm.ACTION_MW_CONNECTION_SUCCESS);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastRequestUserProfile() {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Ec.ACTION_MW_USER_PROFILE);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastRequestUserDietPeriod() {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Ec.ACTION_MW_USER_DIETPERIOD);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastRequestUserCode() {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Sm.ACTION_MW_LOGIN_INFORMATION);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastRequestIsLiveApp() {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Sm.ACTION_MW_IS_LIVE_APPLICATION);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastBattery(short[] bat) {
        if (!getBLEManager().isLiveApp())
            return;
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Ec.ACTION_MW_BATTERY);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_ARRAY, bat);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastBattery(Battery bat) {
        if (!getBLEManager().isLiveApp())
            return;
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Ec.ACTION_MW_BATTERY);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_ARRAY, new short[]{bat.getStatus(), bat.getVoltage()});
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastActivityData(double act_calorie, short[] data, long start_time) {
        if (!getBLEManager().isLiveApp())
            return;
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Am.ACTION_MW_ACTIVITY_DATA);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_DOUBLE_1, act_calorie);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_ARRAY, data);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_LONG_1, start_time);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastActivityData(ActivityData data) {
        if (!getBLEManager().isLiveApp())
            return;

        short[] arr = new short[]{data.getIntensityL(), data.getIntensityM(), data.getIntensityH(), data.getIntensityD(), data.getMinHR(), data.getMaxHR(), data.getAvgHR()};

        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Am.ACTION_MW_ACTIVITY_DATA);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_DOUBLE_1, data.getAct_calorie());
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_ARRAY, arr);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_LONG_1, data.getStart_time());
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastSleepData(short[] data, long start_time) {
        if (!getBLEManager().isLiveApp())
            return;
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Am.ACTION_MW_SLEEP_DATA);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_ARRAY, data);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_LONG_1, start_time);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastSleepData(SleepData data) {
        if (!getBLEManager().isLiveApp())
            return;

        short[] arr = new short[]{data.getRolled(), data.getAwaken(), data.getStabilityHR()};


        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Am.ACTION_MW_SLEEP_DATA);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_ARRAY, arr);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_LONG_1, data.getStart_time());
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastStressData(short data) {
        if (!getBLEManager().isLiveApp())
            return;
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Am.ACTION_MW_STRESS_DATA);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_1, data);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastPeripheralState(short state) {
        if (!getBLEManager().isLiveApp())
            return;
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Bm.ACTION_MW_PERIPHERAL_STATE);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_1, state);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastFirmUpCode(int code) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Bm.ACTION_MW_START_FIRMUP);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_1, code);
        getContext().sendBroadcast(intent);
    }
    public void sendBroadcastFirmUpFailed() {
        Intent intent = new Intent();
        intent.setAction(DfuBaseService.DFU_Failed);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastFirmProgress(int progress) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Bm.ACTION_MW_FIRM_PROGRESS);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_1, progress);
        getContext().sendBroadcast(intent);
    }

//    public void sendBroadcastFirmVersion(int[] version) {
//        Intent intent = new Intent();
//        intent.setAction(Action.Bm.ACTION_MW_FIRM_VERSION);
//        intent.putExtra(Action.EXTRA_NAME_INT_ARRAY, version);
//        getContext().sendBroadcast(intent);
//    }

    public void sendBroadcastFirmVersion(String version) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Bm.ACTION_MW_FIRM_VERSION);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_STRING_1, version);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastScanList(HashMap<String, DeviceRecord> arList) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Bm.ACTION_MW_GENERATE_SCANLIST);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_OBJECT, arList);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastEndScan() {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Bm.ACTION_MW_END_OF_SCANLIST);
        getContext().sendBroadcast(intent);
    }

    //규창 16.12.25 미들웨어에서 자동스캔이 5회를 넘어갔을 시에 앱쪽에 스캔, BLE 중지요청을 한다
    public void sendAutoScanMaximum() {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Bm.ACTION_MW_AUTO_SCAN_MAXIMUM);
        //intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_1, progress);
        getContext().sendBroadcast(intent);
    }


    public void sendBroadcastNotifySync(boolean isSync) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Bm.ACTION_MW_DATASYNC);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_BOOL_1, isSync);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastIsBusySender(boolean isBusy) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Bm.ACTION_MW_IS_BUSY_SENDER);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_BOOL_1, isBusy);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastStressError() {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Am.ACTION_MW_STRESS_ERR);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastGenerateCoachExerData() {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Am.ACTION_MW_GENERATE_COACH_EXER_DATA);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastGenerateActivityData(long start_time) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Am.ACTION_MW_GENERATE_ACTIVITY_DATA);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_LONG_1, start_time);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastQueryCode(int code) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Vm.ACTION_MW_GET_QUERY_CODE);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_1, code);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastMainUI(int accuracy, int point, int count, float calorie, int HRcmp) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Vm.ACTION_MW_MAINUI);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_1, accuracy);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_2, point);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_3, count);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_4, HRcmp);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_FLOAT_1, calorie);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastTopComment(String activityName) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Vm.ACTION_MW_TOP_COMMENT);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_STRING_1, activityName);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastBottomComment(String comment) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Vm.ACTION_MW_BOTTOM_COMMENT);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_STRING_1, comment);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastWarnning(String warnning) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Vm.ACTION_MW_WARNNING);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_STRING_1, warnning);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastShowUI(boolean isShow) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Vm.ACTION_MW_SHOWUI);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_BOOL_1, isShow);
        getContext().sendBroadcast(intent);
    }

    public void sendBroadcastTotalScore(double duration, int point, int count_percent, int accuracy_percent, String comment) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Vm.ACTION_MW_TOTAL_SCORE);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_DOUBLE_1, duration);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_1, point);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_2, count_percent);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_3, accuracy_percent);
        intent.putExtra(MWBroadcastReceiver.Action.EXTRA_NAME_STRING_1, comment);
        getContext().sendBroadcast(intent);
    }
    /** End Broadcast... **/
}