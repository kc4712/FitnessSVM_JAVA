package kr.co.greencomm.middleware.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 안드로이드OS를 사용하는 부분. preference 관리.
 *
 * @author jeyang
 */
public abstract class Preference {
    public abstract void dispose();

    private static final String FILE_NAME = "kr.co.greencomm.coachplus.preference";

    private static void clearSharedPreference(Context context) {
        Editor editor = getEditor(context);
        editor.clear();
        editor.commit();
    }

    public static void removeAll(Context context) {
        Editor editor = getEditor(context);
        editor.clear();
        editor.commit();
    }

    public static void removeXmlStore(Context context) {
        putXmlVersion002(context, null);
        putXmlVersion003(context, null);
        putXmlVersion004(context, null);
        putXmlVersion101(context, null);
        putXmlVersion102(context, null);
        putXmlVersion201(context, null);
        putXmlVersion202(context, null);
        putXmlVersion301(context, null);
        putXmlVersion302(context, null);
    }

    public static void removeForFirmUp(Context context) {
        putPeripheralState(context, 0);
        putSleepAwaken(context, 0);
        putSleepTime(context, 0);
        putSleepRolled(context, 0);
        putSleepStabilityHR(context, 0);
        putActivityState(context, 0);
        putSleepState(context, 0);
        putStressState(context, 0);
    }

    private static void remove(Context context, String key) {
        Editor editor = getEditor(context);
        editor.remove(key);
        editor.commit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    private static Editor getEditor(Context context) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.edit();
    }

    private static void putString(Context context, String key, String value) {
        Editor editor = getEditor(context);
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(Context context, String key, String defValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getString(key, defValue);
    }

    private static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    private static void putInt(Context context, String key, int value) {
        Editor editor = getEditor(context);
        editor.putInt(key, value);
        editor.commit();
    }

    private static int getInt(Context context, String key, int defValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getInt(key, defValue);
    }

    private static int getInt(Context context, String key) {
        return getInt(context, key, 0);
    }

    private static void putLong(Context context, String key, long value) {
        Editor editor = getEditor(context);
        editor.putLong(key, value);
        editor.commit();
    }

    private static long getLong(Context context, String key, long defValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getLong(key, defValue);
    }

    private static long getLong(Context context, String key) {
        return getLong(context, key, 0);
    }

    private static void putFloat(Context context, String key, float value) {
        Editor editor = getEditor(context);
        editor.putFloat(key, value);
        editor.commit();
    }

    private static float getFloat(Context context, String key, float defValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getFloat(key, defValue);
    }

    private static float getFloat(Context context, String key) {
        return getFloat(context, key, 0);
    }

    private static void putBoolean(Context context, String key, boolean value) {
        Editor editor = getEditor(context);
        editor.putBoolean(key, value);
        editor.commit();
    }

    private static boolean getBoolean(Context context, String name, boolean defValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(name, defValue);
    }

    private static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    // ---------------------------------------------------------------------

    private static final String KEY_CONNECTED_TIME = "ConnectedTime";
    private static final String KEY_DISCONNECTED_TIME = "DisconnectedTime";

    private static final String KEY_SEX = "sex";
    private static final String KEY_AGE = "age";
    private static final String KEY_JOB = "job";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_WEIGHT = "weight";

    private static final String KEY_DIETPERIOD = "diet_Period";
    private static final String KEY_GOALWEIGHT = "goal_weight";

    private static final String KEY_BLUETOOTH_MAC = "bluetooth_mac";
    private static final String KEY_BLUETOOTH_NAME = "bluetooth_name";

    private static final String KEY_URL_USERCODE = "url_usercode";

    private static final String KEY_FIRM_VERSION = "firm_version";


    public static void putSex(Context context, Integer input) {
        putInt(context, KEY_SEX, input);
    }

    public static int getSex(Context context) {
        return getInt(context, KEY_SEX);
    }

    public static void putAge(Context context, Integer input) {
        putInt(context, KEY_AGE, input);
    }

    public static int getAge(Context context) {
        return getInt(context, KEY_AGE);
    }

    public static void putJob(Context context, Integer input) {
        putInt(context, KEY_JOB, input);
    }

    public static int getJob(Context context) {
        return getInt(context, KEY_JOB);
    }

    public static void putHeight(Context context, Integer input) {
        putInt(context, KEY_HEIGHT, input);
    }

    public static int getHeight(Context context) {
        return getInt(context, KEY_HEIGHT);
    }

    public static void putWeight(Context context, Float input) {
        putFloat(context, KEY_WEIGHT, input);
    }

    public static float getWeight(Context context) {
        return getFloat(context, KEY_WEIGHT);
    }

    public static void putDietPeriod(Context context, Integer input) {
        putInt(context, KEY_DIETPERIOD, input);
    }

    public static int getDietPeriod(Context context) {
        return getInt(context, KEY_DIETPERIOD);
    }

    public static void putGoalWeight(Context context, Float input) {
        putFloat(context, KEY_GOALWEIGHT, input);
    }

    public static float getGoalWeight(Context context) {
        return getFloat(context, KEY_GOALWEIGHT);
    }

    public static void putBluetoothMac(Context context, String input) {
        putString(context, KEY_BLUETOOTH_MAC, input);
    }

    public static String getBluetoothMac(Context context) {
        return getString(context, KEY_BLUETOOTH_MAC);
    }

    public static void putBluetoothName(Context context, String input) {
        putString(context, KEY_BLUETOOTH_NAME, input);
    }

    public static String getBluetoothName(Context context) {
        return getString(context, KEY_BLUETOOTH_NAME);
    }

    public static void putUrlUsercode(Context context, String input) {
        putString(context, KEY_URL_USERCODE, input);
    }

    public static String getUrlUsercode(Context context) {
        return getString(context, KEY_URL_USERCODE);
    }

    public static void putFirmVersion(Context context, String input) {
        putString(context, KEY_FIRM_VERSION, input);
    }

    public static String getFirmVersion(Context context) {
        return getString(context, KEY_FIRM_VERSION);
    }

    public static void putConnectedTime(Context context, int time) {
        putInt(context, KEY_CONNECTED_TIME, time);
    }

    public static int getConnectedTime(Context context) {
        return getInt(context, KEY_CONNECTED_TIME);
    }

    public static void putDisconnectedTime(Context context, int time) {
        putInt(context, KEY_DISCONNECTED_TIME, time);
    }

    public static int getDisconnectedTime(Context context) {
        return getInt(context, KEY_DISCONNECTED_TIME);
    }

    //--------------------------------------------------------------
    // UI 에서 사용하는 환경 설정
    //--------------------------------------------------------------
    private static String KEY_MAIN_AUTO_LOGIN = "MAIN_AUTO_LOGIN";
    private static String KEY_USER_EMAIL = "USER_EMAIL";
    private static String KEY_USER_PASSWORD = "USER_PASSWORD";

    private static String KEY_TODAY_CALORIE = "TODAY_CALORIE";

    private static String KEY_PERIPHERAL_STATE = "KEY_PERIPHERAL_STATE";
    private static String KEY_ACTIVITY_STATE = "ACTIVITY_STATE";
    private static String KEY_SLEEP_STATE = "SLEEP_STATE";
    private static String KEY_SLEEP_TIME = "SLEEP_TIME";
    private static String KEY_SLEEP_ROLLED = "SLEEP_ROLLED";
    private static String KEY_SLEEP_AWAKEN = "SLEEP_AWAKEN";
    private static String KEY_SLEEP_STABILITY_HR = "SLEEP_STABILITY_HR";
    private static String KEY_STRESS_STATE = "STRESS_STATE";
    private static String KEY_PRODUCT_CODE = "PRODUCT_CODE";

    private static String KEY_XML_VERSION_002 = "XML_VERSION_002";
    private static String KEY_XML_VERSION_003 = "XML_VERSION_003";
    private static String KEY_XML_VERSION_004 = "XML_VERSION_004";
    private static String KEY_XML_VERSION_101 = "XML_VERSION_101";
    private static String KEY_XML_VERSION_102 = "XML_VERSION_102";
    private static String KEY_XML_VERSION_201 = "XML_VERSION_201";
    private static String KEY_XML_VERSION_202 = "XML_VERSION_202";
    private static String KEY_XML_VERSION_301 = "XML_VERSION_301";
    private static String KEY_XML_VERSION_302 = "XML_VERSION_302";

    private static String KEY_LANGUAGE = "LANGUAGE";

    private static String KEY_NOTICE_PHONE_ONOFF = "NOTICE_PHONE_ONOFF";
    private static String KEY_NOTICE_SMS_ONOFF = "NOTICE_SMS_ONOFF";

    private static String KEY_MAIN_STEP = "MAIN_STEP";
    private static String KEY_MAIN_CALORIE_ACTIVITY = "MAIN_CALORIE_ACTIVITY";
    private static String KEY_MAIN_CALORIE_COACH = "MAIN_CALORIE_COACH";
    private static String KEY_MAIN_CALORIE_SLEEP = "MAIN_CALORIE_SLEEP";
    private static String KEY_MAIN_CALORIE_DAILY = "MAIN_CALORIE_DAILY";

    public static void putAutoLogin(Context context, boolean input) {
        putBoolean(context, KEY_MAIN_AUTO_LOGIN, input);
    }

    public static boolean getAutoLogin(Context context) {
        return getBoolean(context, KEY_MAIN_AUTO_LOGIN);
    }

    public static void putEmail(Context context, String input) {
        putString(context, KEY_USER_EMAIL, input);
    }

    public static String getEmail(Context context) {
        return getString(context, KEY_USER_EMAIL);
    }

    public static void putPassword(Context context, String input) {
        putString(context, KEY_USER_PASSWORD, input);
    }

    public static String getPassword(Context context) {
        return getString(context, KEY_USER_PASSWORD);
    }

    public static void putTodayCalorie(Context context, int input) {
        putInt(context, KEY_TODAY_CALORIE, input);
    }

    public static int getTodayCalorie(Context context) {
        return getInt(context, KEY_TODAY_CALORIE);
    }
//
    public static void putPeripheralState(Context context, int input) {
        putInt(context, KEY_PERIPHERAL_STATE, input);
    }

    public static int getPeripheralState(Context context) {
        return getInt(context, KEY_PERIPHERAL_STATE);
    }

    public static void putActivityState(Context context, int input) {
        putInt(context, KEY_ACTIVITY_STATE, input);
    }

    public static int getActivityState(Context context) {
        return getInt(context, KEY_ACTIVITY_STATE);
    }

    public static void putSleepState(Context context, int input) {
        putInt(context, KEY_SLEEP_STATE, input);
    }

    public static int getSleepState(Context context) {
        return getInt(context, KEY_SLEEP_STATE);
    }

    public static void putSleepTime(Context context, int input) {
        putInt(context, KEY_SLEEP_TIME, input);
    }

    public static int getSleepTime(Context context) {
        return getInt(context, KEY_SLEEP_TIME);
    }

    public static void putSleepRolled(Context context, int input) {
        putInt(context, KEY_SLEEP_ROLLED, input);
    }

    public static int getSleepRolled(Context context) {
        return getInt(context, KEY_SLEEP_ROLLED);
    }

    public static void putSleepAwaken(Context context, int input) {
        putInt(context, KEY_SLEEP_AWAKEN, input);
    }

    public static int getSleepAwaken(Context context) {
        return getInt(context, KEY_SLEEP_AWAKEN);
    }

    public static void putSleepStabilityHR(Context context, int input) {
        putInt(context, KEY_SLEEP_STABILITY_HR, input);
    }

    public static int getSleepStabilityHR(Context context) {
        return getInt(context, KEY_SLEEP_STABILITY_HR);
    }

    public static void putStressState(Context context, int input) {
        putInt(context, KEY_STRESS_STATE, input);
    }

    public static int getStressState(Context context) {
        return getInt(context, KEY_STRESS_STATE);
    }

    public static void putProductCode(Context context, int input) {
        putInt(context, KEY_PRODUCT_CODE, input);
    }

    public static int getProductCode(Context context) {
        return getInt(context, KEY_PRODUCT_CODE);
    }

    public static void putXmlVersion002(Context context, String input) {
        putString(context, KEY_XML_VERSION_002, input);
    }

    public static String getXmlVersion002(Context context) {
        return getString(context, KEY_XML_VERSION_002);
    }

    public static void putXmlVersion003(Context context, String input) {
        putString(context, KEY_XML_VERSION_003, input);
    }

    public static String getXmlVersion003(Context context) {
        return getString(context, KEY_XML_VERSION_003);
    }

    public static void putXmlVersion004(Context context, String input) {
        putString(context, KEY_XML_VERSION_004, input);
    }

    public static String getXmlVersion004(Context context) {
        return getString(context, KEY_XML_VERSION_004);
    }

    public static void putXmlVersion101(Context context, String input) {
        putString(context, KEY_XML_VERSION_101, input);
    }

    public static String getXmlVersion101(Context context) {
        return getString(context, KEY_XML_VERSION_101);
    }

    public static void putXmlVersion102(Context context, String input) {
        putString(context, KEY_XML_VERSION_102, input);
    }

    public static String getXmlVersion102(Context context) {
        return getString(context, KEY_XML_VERSION_102);
    }

    public static void putXmlVersion201(Context context, String input) {
        putString(context, KEY_XML_VERSION_201, input);
    }

    public static String getXmlVersion201(Context context) {
        return getString(context, KEY_XML_VERSION_201);
    }

    public static void putXmlVersion202(Context context, String input) {
        putString(context, KEY_XML_VERSION_202, input);
    }

    public static String getXmlVersion202(Context context) {
        return getString(context, KEY_XML_VERSION_202);
    }

    public static void putXmlVersion301(Context context, String input) {
        putString(context, KEY_XML_VERSION_301, input);
    }

    public static String getXmlVersion301(Context context) {
        return getString(context, KEY_XML_VERSION_301);
    }

    public static void putXmlVersion302(Context context, String input) {
        putString(context, KEY_XML_VERSION_302, input);
    }

    public static String getXmlVersion302(Context context) {
        return getString(context, KEY_XML_VERSION_302);
    }

    public static void putLanguage(Context context, String input) {
        putString(context, KEY_LANGUAGE, input);
    }

    public static String getLanguage(Context context) {
        return getString(context, KEY_LANGUAGE);
    }

    public static void putNoticePhoneONOFF(Context context, boolean input) {
        putBoolean(context, KEY_NOTICE_PHONE_ONOFF, input);
    }

    public static boolean getNoticePhoneONOFF(Context context) {
        return getBoolean(context, KEY_NOTICE_PHONE_ONOFF);
    }

    public static void putNoticeSmsONOFF(Context context, boolean input) {
        putBoolean(context, KEY_NOTICE_SMS_ONOFF, input);
    }

    public static boolean getNoticeSmsONOFF(Context context) {
        return getBoolean(context, KEY_NOTICE_SMS_ONOFF);
    }

    public static void putMainStep(Context context, int input) {
        putInt(context, KEY_MAIN_STEP, input);
    }

    public static int getMainStep(Context context) {
        return getInt(context, KEY_MAIN_STEP);
    }

    public static void putMainCalorieActivity(Context context, int input) {
        putInt(context, KEY_MAIN_CALORIE_ACTIVITY, input);
    }

    public static int getMainCalorieActivity(Context context) {
        return getInt(context, KEY_MAIN_CALORIE_ACTIVITY);
    }

    public static void putMainCalorieCoach(Context context, int input) {
        putInt(context, KEY_MAIN_CALORIE_COACH, input);
    }

    public static int getMainCalorieCoach(Context context) {
        return getInt(context, KEY_MAIN_CALORIE_COACH);
    }

    public static void putMainCalorieSleep(Context context, int input) {
        putInt(context, KEY_MAIN_CALORIE_SLEEP, input);
    }

    public static int getMainCalorieSleep(Context context) {
        return getInt(context, KEY_MAIN_CALORIE_SLEEP);
    }

    public static void putMainCalorieDaily(Context context, int input) {
        putInt(context, KEY_MAIN_CALORIE_DAILY, input);
    }

    public static int getMainCalorieDaily(Context context) {
        return getInt(context, KEY_MAIN_CALORIE_DAILY);
    }
}