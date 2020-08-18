package kr.co.greencomm.middleware.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

import kr.co.greencomm.middleware.main.ConfigManager;
import kr.co.greencomm.middleware.utils.MessageClass;
import kr.co.greencomm.middleware.utils.NoticeIndex;
import kr.co.greencomm.middleware.utils.container.DataBase;
import kr.co.greencomm.middleware.utils.container.UserProfile;

/**
 * 밴드의 실질적인 통신 기능이 구현되는 클래스. 블루투스의 통신 데이터가 들어와서 parse되고 db에 쌓인다는 가정에 시작한다.
 * 실제 블루투스 데이터가 들어오는 부분은 이곳에서 처리할수 없으므로 넘긴다.
 */
public abstract class DeviceBaseTrans extends DeviceBaseScan {
    private static final String tag = DeviceBaseTrans.class.getSimpleName();
    /**
     * Flag
     **/
    private static boolean DEBUG = false;
    protected boolean trigger = false, pre_trigger = false;

    /**
     * Variable
     **/
    protected BluetoothGatt mBluetoothGatt = null;
    protected BluetoothGattCharacteristic mCharRX = null; // 밴드 기준

    /**
     * Abstract
     **/
    protected abstract void getDataFrame(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    /**
     * Instance
     **/
    protected DataBase mDataBase;
    protected ConfigManager mConfig;

    /**
     * Handler
     **/
    protected DeviceBaseTrans(Context context) {
        super(context);
        mDataBase = DataBase.getInstance();
        mConfig = ConfigManager.getInstance(getContext());
    }

    protected void requestAcc(RequestAction action) {
        short cmd = BluetoothCommand.Acc.getCommand();

        byte[] frame = new byte[4];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (action.getAction());
        frame[3] = (byte) (action.getAction() >> 8);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestAccProductCoach(RequestAction action) {
        short cmd = BluetoothCommand.Acc.getCommand();

        byte[] frame = new byte[3];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (action.getAction());
        frame[2] = 0;

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void sendUserData() {
        UserProfile profile = mConfig.getUserProfile();
        int height = profile.getHeight(); // 1byte
        int weight = profile.getWeight().intValue(); // 1byte
        int age = profile.getAge(); // 1byte
        int sex = profile.getSex();
        // 현재까진 총 4byte

        short cmd = BluetoothCommand.UserData.getCommand();

        byte[] frame = new byte[10];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (sex);
        frame[3] = (byte) (sex >> 8);
        frame[4] = (byte) (age);
        frame[5] = (byte) (age >> 8);
        frame[6] = (byte) (height);
        frame[7] = (byte) (height >> 8);
        frame[8] = (byte) (weight);
        frame[9] = (byte) (weight >> 8);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestVibrate(RequestAction action) {
        short cmd = BluetoothCommand.Vibrate.getCommand();

        byte[] frame = new byte[4];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (action.getAction());
        frame[3] = (byte) (action.getAction() >> 8);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestVibrateProductCoach(RequestAction action) {
        short cmd = BluetoothCommand.Vibrate.getCommand();

        byte[] frame = new byte[2];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (action.getAction());

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestDataClear() {
        short cmd = BluetoothCommand.DataClear.getCommand();

        byte[] frame = new byte[2];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestState() {
        short cmd = BluetoothCommand.State.getCommand();

        byte[] frame = new byte[2];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    public void sendNoticeMessage(MessageClass cls, short count) {
        short cmd = BluetoothCommand.NoticeMessage.getCommand();

        short platform = 2; // android
        short message_cls = (short) cls.getCls();

        byte[] frame = new byte[8];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (platform);
        frame[3] = (byte) (platform >> 8);
        frame[4] = (byte) (message_cls);
        frame[5] = (byte) (message_cls >> 8);
        frame[6] = (byte) (count);
        frame[7] = (byte) (count >> 8);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void sendNoticeONOFF(RequestAction action, NoticeIndex index) {
        short cmd = BluetoothCommand.NoticeONOFF.getCommand();

        short platform = 2; // android

        byte[] frame = new byte[8];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (platform);
        frame[3] = (byte) (platform >> 8);
        frame[4] = (byte) (index.ordinal());
        frame[5] = (byte) (index.ordinal() >> 8);
        frame[6] = (byte) (action.getAction());
        frame[7] = (byte) (action.getAction() >> 8);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestBattery() {
        short cmd = BluetoothCommand.Battery.getCommand();

        byte[] frame = new byte[2];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestBatteryProductCoach() {
        short cmd = BluetoothCommand.Battery.getCommand();

        byte[] frame = new byte[3];
        frame[0] = (byte) (cmd);
        frame[1] = 1;
        frame[2] = 0;

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestVersion() {
        short cmd = BluetoothCommand.Version.getCommand();

        byte[] frame = new byte[2];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestVersionProductCoach() {
        short cmd = BluetoothCommand.Version.getCommand();

        byte[] frame = new byte[3];
        frame[0] = (byte) (cmd);
        frame[1] = 1;
        frame[2] = 0;

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestStepCount_Calorie(long time) {
        short cmd = BluetoothCommand.StepCount_Calorie.getCommand();
        int ret = DataControlBase.getConvertedTime(time);

        byte[] frame = new byte[6];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (ret);
        frame[3] = (byte) (ret >> 8);
        frame[4] = (byte) (ret >> 16);
        frame[5] = (byte) (ret >> 24);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestActivity(RequestAction action, long time) {
        short cmd = BluetoothCommand.Activity.getCommand();
        int ret = DataControlBase.getConvertedTime(time);

        byte[] frame = new byte[8];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (action.getAction());
        frame[3] = (byte) (action.getAction() >> 8);
        frame[4] = (byte) (ret);
        frame[5] = (byte) (ret >> 8);
        frame[6] = (byte) (ret >> 16);
        frame[7] = (byte) (ret >> 24);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void sendCalorie(float calorie) {
        short cmd = BluetoothCommand.CoachCalorie.getCommand();
        int cal = (int) (calorie * 1000);

        byte[] frame = new byte[6];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (cal);
        frame[3] = (byte) (cal >> 8);
        frame[4] = (byte) (cal >> 16);
        frame[5] = (byte) (cal >> 24);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void sendCalorieInt(float calorie) {
        short cmd = BluetoothCommand.CoachCalorie.getCommand();
        int cal = (int) calorie;

        byte[] frame = new byte[6];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (cal);
        frame[3] = (byte) (cal >> 8);
        frame[4] = (byte) (cal >> 16);
        frame[5] = (byte) (cal >> 24);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestSleep(RequestAction action, long time) {
        short cmd = BluetoothCommand.Sleep.getCommand();
        int ret = DataControlBase.getConvertedTime(time);

        byte[] frame = new byte[8];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (action.getAction());
        frame[3] = (byte) (action.getAction() >> 8);
        frame[4] = (byte) (ret);
        frame[5] = (byte) (ret >> 8);
        frame[6] = (byte) (ret >> 16);
        frame[7] = (byte) (ret >> 24);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void requestStress(RequestAction action, long time) {
        short cmd = BluetoothCommand.Stress.getCommand();
        int ret = DataControlBase.getConvertedTime(time);

        byte[] frame = new byte[8];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (action.getAction());
        frame[3] = (byte) (action.getAction() >> 8);
        frame[4] = (byte) (ret);
        frame[5] = (byte) (ret >> 8);
        frame[6] = (byte) (ret >> 16);
        frame[7] = (byte) (ret >> 24);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }

    protected void sendRTC() {
        Calendar mCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        short offset = getTimeZoneOffset();

        short cmd = BluetoothCommand.RTC.getCommand();

        int day_of_week = mCal.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week < 1)
            day_of_week = 7;

        byte[] frame = new byte[18];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (mCal.get(Calendar.YEAR));
        frame[3] = (byte) (mCal.get(Calendar.YEAR) >> 8);
        frame[4] = (byte) (mCal.get(Calendar.MONTH) + 1);
        frame[5] = (byte) ((mCal.get(Calendar.MONTH) + 1) >> 8);
        frame[6] = (byte) (mCal.get(Calendar.DAY_OF_MONTH));
        frame[7] = (byte) (mCal.get(Calendar.DAY_OF_MONTH) >> 8);
        frame[8] = (byte) (mCal.get(Calendar.HOUR_OF_DAY));
        frame[9] = (byte) (mCal.get(Calendar.HOUR_OF_DAY) >> 8);
        frame[10] = (byte) (mCal.get(Calendar.MINUTE));
        frame[11] = (byte) (mCal.get(Calendar.MINUTE) >> 8);
        frame[12] = (byte) (mCal.get(Calendar.SECOND));
        frame[13] = (byte) (mCal.get(Calendar.SECOND) >> 8);
        frame[14] = (byte) (day_of_week);
        frame[15] = (byte) (day_of_week >> 8);
        frame[16] = (byte) (offset);
        frame[17] = (byte) (offset >> 8);

        writeCharLog(mBluetoothGatt, mCharRX, frame);
    }


    //규창 171108 피쳐 요청 메서드 호출시 System.currentTimeMillis() 호출한 값 넣어서 호출
    public void requestFeature(long reqtime) {

        Calendar mCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        mCal.setTimeInMillis(reqtime);
        //규창 171108 셋 메서드 사용.. 이건 아마 시간만 변경
        String min = String.valueOf(mCal.get(Calendar.MINUTE));


        Log.i(tag, "min?"+min);
        min = min.substring(0, min.length()-1);

        min = min + "0";
        Log.i(tag, "min?"+min);
        mCal.set(Calendar.MINUTE, Integer.valueOf(min));

        //규창 171108 셋 메서드 사용.. 이건 전체시간
        //mCal.set(mCal.get(Calendar.YEAR),mCal.get(Calendar.MONTH),mCal.get(Calendar.DAY_OF_MONTH),0,0);

        short offset = getTimeZoneOffset();

        short cmd = BluetoothCommand.FeatureSet1.getCommand();


        //for(int i = 0; i<10 ; i++){
        int ret = DataControlBase.getConvertedTime(mCal.getTimeInMillis()) - 20;

        byte[] frame = new byte[6];
        frame[0] = (byte) (cmd);
        frame[1] = (byte) (cmd >> 8);
        frame[2] = (byte) (ret);
        frame[3] = (byte) (ret >> 8);
        frame[4] = (byte) (ret >> 16);
        frame[5] = (byte) (ret >> 24);
        Log.i(tag, "요청한 피쳐시간 "+ mCal.getTimeInMillis()+ "  "+ ret);
        writeCharLog(mBluetoothGatt, mCharRX, frame);
        //}
    }




    @Override
    protected void dispose() {
        super.dispose();
        mCharRX = null;
        mBluetoothGatt = null;
    }
}