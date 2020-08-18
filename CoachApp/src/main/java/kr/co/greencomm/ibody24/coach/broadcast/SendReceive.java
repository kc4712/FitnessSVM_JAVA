package kr.co.greencomm.ibody24.coach.broadcast;

import android.content.Context;
import android.content.Intent;

import kr.co.greencomm.middleware.bluetooth.BluetoothCommand;
import kr.co.greencomm.middleware.bluetooth.RequestAction;
import kr.co.greencomm.middleware.bluetooth.ScanMode;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver.Action;
import kr.co.greencomm.middleware.utils.NoticeIndex;
import kr.co.greencomm.middleware.utils.ProductCode;
import kr.co.greencomm.middleware.utils.StateApp;

/**
 * Created by young on 2015-12-09.
 */
public class SendReceive {
    private static final String tag = SendReceive.class.getSimpleName();

    /**
     * 사용자 프로필을 설정한다.
     *
     * @param context       컨텍스트
     * @param age           연령
     * @param sex           성별
     * @param job           직업
     * @param height        신장
     * @param currentWeight 현재 체중
     * @param targetWeight  목표 체중
     */
    public static void sendSetUserProfile(Context context, int age, int sex, int job, int height, float currentWeight, float targetWeight) {
        Intent intent = new Intent(Action.Ec.ACTION_APP_USER_PROFILE);
        intent.putExtra(Action.EXTRA_NAME_INT_1, age);
        intent.putExtra(Action.EXTRA_NAME_INT_2, sex);
        intent.putExtra(Action.EXTRA_NAME_INT_3, job);
        intent.putExtra(Action.EXTRA_NAME_INT_4, height);
        intent.putExtra(Action.EXTRA_NAME_FLOAT_1, currentWeight);
        intent.putExtra(Action.EXTRA_NAME_FLOAT_2, targetWeight);
        context.sendBroadcast(intent);
    }

    /**
     * 다이어트 기간을 설정한다.
     *
     * @param context 컨텍스트
     * @param period  다이어트 기간
     */
    public static void sendSetUserDietPeriod(Context context, int period) {
        Intent intent = new Intent(Action.Ec.ACTION_APP_USER_DIETPERIOD);
        intent.putExtra(Action.EXTRA_NAME_INT_1, period);
        context.sendBroadcast(intent);
    }


    /**
     * 블루투스 장치의 연결을 요청한다.
     *
     * @param context 컨텍스트
     */
    public static void sendRequestConnection(Context context) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_TRY_CONNECTION_BLUETOOTH);
        context.sendBroadcast(intent);
    }

    /**
     * 블루투스 연결 장치의 정보를 요청한다.
     *
     * @param context 컨텍스트
     */

    //규창 16.11.10 펌웨어 업데이트
    public static void sendFirmUpdateStart(Context context,String path) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_START_FIRMUP);
        intent.putExtra(Action.EXTRA_NAME_STRING_1, path);
        context.sendBroadcast(intent);
    }

    public static void sendRequestDeviceIncfo(Context context) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_DEVICE_INFORMATION);
        context.sendBroadcast(intent);
    }

    public static void sendStartScan(Context context) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_START_SCAN);
        context.sendBroadcast(intent);
    }

    public static void sendStopScan(Context context) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_STOP_SCAN);
        context.sendBroadcast(intent);
    }

    public static void sendConnect(Context context, String mac) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_BL_CONNECT);
        intent.putExtra(Action.EXTRA_NAME_STRING_1, mac);
        context.sendBroadcast(intent);
    }

    public static void sendProductCode(Context context, ProductCode code) {
        Intent intent = new Intent(Action.Ec.ACTION_APP_SELECT_PRODUCT);
        intent.putExtra(Action.EXTRA_NAME_INT_1, code.getProductCode());
        context.sendBroadcast(intent);
    }

    public static void getSelectedProductCode(Context context) {
        Intent intent = new Intent(Action.Ec.ACTION_APP_GET_SELECTED_PRODUCT);
        context.sendBroadcast(intent);
    }

    public static void sendStopBluetooth(Context context) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_STOP_BLUETOOTH);
        context.sendBroadcast(intent);
    }

    public static void sendSetScanMode(Context context, ScanMode mode) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_SET_SCANMODE);
        intent.putExtra(Action.EXTRA_NAME_INT_1, mode.ordinal());
        context.sendBroadcast(intent);
    }

    public static void getActivityData(Context context) {
        Intent intent = new Intent(Action.Am.ACTION_APP_ACTIVITY_DATA);
        context.sendBroadcast(intent);
    }

    public static void getStressData(Context context) {
        Intent intent = new Intent(Action.Am.ACTION_APP_STRESS_DATA);
        context.sendBroadcast(intent);
    }

    public static void getSleepData(Context context) {
        Intent intent = new Intent(Action.Am.ACTION_APP_SLEEP_DATA);
        context.sendBroadcast(intent);
    }

    public static void getStepNCalorie(Context context) {
        Intent intent = new Intent(Action.Am.ACTION_APP_STEP_CALORIE);
        context.sendBroadcast(intent);
    }

    public static void getConnectionState(Context context) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_CONNECTION_STATE);
        context.sendBroadcast(intent);
    }

    public static void getBatteryState(Context context) {
        Intent intent = new Intent(Action.Ec.ACTION_APP_BATTERY);
        context.sendBroadcast(intent);
    }

    public static void isBusySender(Context context) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_IS_BUSY_SENDER);
        context.sendBroadcast(intent);
    }

    public static void appendBluetoothMessage(Context context, BluetoothCommand cmd, long time, RequestAction action, boolean resp, NoticeIndex index) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_APPEND_BL_MESSAGE);

        intent.putExtra(Action.EXTRA_NAME_SHORT_1, cmd.getCommand());
        intent.putExtra(Action.EXTRA_NAME_LONG_1, time);
        intent.putExtra(Action.EXTRA_NAME_SHORT_2, action.getAction());
        intent.putExtra(Action.EXTRA_NAME_BOOL_1, resp);
        intent.putExtra(Action.EXTRA_NAME_SHORT_3, (short) index.ordinal());

        context.sendBroadcast(intent);
    }

    public static void appendBluetoothMessage(Context context, BluetoothCommand cmd, long time, RequestAction action, boolean resp) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_APPEND_BL_MESSAGE);

        intent.putExtra(Action.EXTRA_NAME_SHORT_1, cmd.getCommand());
        intent.putExtra(Action.EXTRA_NAME_LONG_1, time);
        intent.putExtra(Action.EXTRA_NAME_SHORT_2, action.getAction());
        intent.putExtra(Action.EXTRA_NAME_BOOL_1, resp);

        context.sendBroadcast(intent);
    }
    /*public static void appendBluetoothMessage(Context context, BluetoothCommand cmd, long time, RequestAction action, boolean resp) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_APPEND_BL_MESSAGE);

        intent.putExtra(Action.EXTRA_NAME_SHORT_1, cmd.getCommand());
        intent.putExtra(Action.EXTRA_NAME_LONG_1, time);
        intent.putExtra(Action.EXTRA_NAME_SHORT_2, action.getAction());
        intent.putExtra(Action.EXTRA_NAME_BOOL_1, resp);

        context.sendBroadcast(intent);
    }*/
    /*public static void appendBluetoothMessage(Context context, boolean start) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_APPEND_BL_MESSAGE);
        intent.putExtra(Action.EXTRA_NAME_BOOL_1, start);

        context.sendBroadcast(intent);
    }*/
    /*public static void sendStressMeasure(Context context, boolean start) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_NORMAL_STRESS);
        intent.putExtra(Action.EXTRA_NAME_BOOL_1, start);
        context.sendBroadcast(intent);
    }*/
    public static void sendStress(Context context, boolean start) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_STRESS);
        intent.putExtra(Action.EXTRA_NAME_BOOL_1, start);
        context.sendBroadcast(intent);
    }

    public static void sendIsLiveApplication(Context context, StateApp state) {
        Intent intent = new Intent(Action.Sm.ACTION_APP_IS_LIVE_APPLICATION);
        intent.putExtra(Action.EXTRA_NAME_INT_1, state.ordinal());
        context.sendBroadcast(intent);
    }

    public static void getFirmVersion(Context context) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_FIRM_VERSION);
        context.sendBroadcast(intent);
    }

    public static void sendNormalStress(Context context, boolean start) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_NORMAL_STRESS);
        intent.putExtra(Action.EXTRA_NAME_BOOL_1, start);
        context.sendBroadcast(intent);
    }

    public static void logout(Context context) {
        Intent intent = new Intent(Action.Ec.ACTION_APP_LOGOUT);
        context.sendBroadcast(intent);
    }

    public static void getCourseQueryCode(Context context, int programCode) {
        Intent intent = new Intent(Action.Vm.ACTION_APP_GET_QUERY_CODE);
        intent.putExtra(Action.EXTRA_NAME_INT_1, programCode);
        context.sendBroadcast(intent);
    }

    public static void setCurrentPosition(Context context, int position) {
        Intent intent = new Intent(Action.Vm.ACTION_APP_SET_CURRENT_TIME_POSITION);
        intent.putExtra(Action.EXTRA_NAME_INT_1, position);
        context.sendBroadcast(intent);
    }

    public static void setPlay(Context context, String xml) {
        Intent intent = new Intent(Action.Vm.ACTION_APP_PLAY);
        intent.putExtra(Action.EXTRA_NAME_STRING_1, xml);
        context.sendBroadcast(intent);
    }

    public static void setEnd(Context context) {
        Intent intent = new Intent(Action.Vm.ACTION_APP_END);
        context.sendBroadcast(intent);
    }

    public static void sendPeripheralState(Context context) {
        Intent intent = new Intent(Action.Bm.ACTION_APP_PERIPHERAL_STATE);
        context.sendBroadcast(intent);
    }

    public static void generateActivityData(Context context) {
        Intent intent = new Intent();
        intent.setAction(MWBroadcastReceiver.Action.Am.ACTION_MW_GENERATE_ACTIVITY_DATA);
        context.sendBroadcast(intent);
    }
}
