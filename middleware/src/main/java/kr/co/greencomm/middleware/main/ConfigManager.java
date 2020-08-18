package kr.co.greencomm.middleware.main;

import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;

import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.utils.container.UserProfile;
import kr.co.greencomm.middleware.utils.container.UserWeightProfile;

/**
 * M/W에 필요한 기본적인 정보를 관리하며, 일부 계산을 담당합니다.
 */
public final class ConfigManager {
    private static final String tag = "ConfigManager";
    // config 객체
    private static ConfigManager mConfig = null;
    //private PreferencesManager mPre;

    public static final int SEX_MALE = 1;
    public static final int SEX_FEMALE = 2;

    public static final int COUNTRY_NORTH_AMERICA = 1;
    public static final int COUNTRY_JAPAN = 2;
    public static final int COUNTRY_KOREA = 3;

    private final WeakReference<Context> WContext;

    /**
     * 성공.
     */
    public static final int SUCCESS = 0;
    /**
     * 실패.
     */
    public static final int FAIL = 1;
    /**
     * null 혹은 0인 데이터가 들어왔습니다.
     */
    public static final int DO_NOT_USE_NULLDATA = 2;
    public static final int INCORRECT_DATA = 3;

    /**
     * EngineConfiguration 의 생성자
     *
     * @param context Application의 context.
     */
    private ConfigManager(Context context) {
        WContext = new WeakReference<>(context);
    }

    protected Context getContext() {
        return WContext.get();
    }

    public static ConfigManager getInstance(Context context) {
        if (mConfig == null) {
            mConfig = new ConfigManager(context);
        }
        return mConfig;
    }

    /**
     * 사용자 Profile 정보를 기본 DB에 작성한다. Parameter가 null 혹은 zero 값이 들어오면 DO_NOT_USE_NULLDATA 코드를 반환한다.
     * 허용되지 않는 데이터가 입력되면  INCORRECT_DATA 코드를 반환한다.
     *
     * @param sex        사용자의 성별.
     * @param age        사용자의 나이.
     * @param height     사용자의 키.
     * @param weight     사용자의 체중.
     * @param goalWeight 사용자의 목표 체중.
     * @return 2:INCORRECT_DATA, 1:DO_NOT_USE_NULLDATA, 0:SUCCESS
     */
    public int setUserProfile(int age, int sex, int height, float weight, float goalWeight) {
        if (age == 0 || height == 0 || weight == 0) {
            Log.e(tag, "do not use null&zero data : setUserProfile");
            return DO_NOT_USE_NULLDATA;
        }

        if (age < 2) {
            return INCORRECT_DATA;
        }

        if (sex < 1 || sex > 2) {
            return INCORRECT_DATA;
        }

        Preference.putAge(getContext(), age);
        Preference.putSex(getContext(), sex);
        Preference.putHeight(getContext(), height);
        Preference.putWeight(getContext(), weight);
        Preference.putGoalWeight(getContext(), goalWeight);

        return SUCCESS;
    }

    /**
     * 사용자 Profile 정보를 기본 DB에서 받아온다. Contact.Profile 클래스로 반환.
     *
     * @return Contact.Profile 객체 반환.
     */
    public UserProfile getUserProfile() {
        int age = Preference.getAge(getContext());
        int sex = Preference.getSex(getContext());
        int height = Preference.getHeight(getContext());
        float weight = Preference.getWeight(getContext());
        float goalWeight = Preference.getGoalWeight(getContext());

        UserProfile db = new UserProfile();
        db.setUserProfile(sex, age, height, weight, goalWeight);

        return db;
    }

    public UserWeightProfile getUserWeightProfile() {
        float weight = Preference.getWeight(getContext());
        float goalWeight = Preference.getGoalWeight(getContext());
        int dietPeriod = Preference.getDietPeriod(getContext());

        UserWeightProfile db = new UserWeightProfile();
        db.setUserWeightProfile(weight, goalWeight, dietPeriod);

        return db;
    }

    /**
     * 사용자의 DietPeriod 정보를 기본 DB에 작성한다. 이 메서드가 호출되는 시점의 시간이 기본 DB에 저장된다.
     * Parameter가 null 혹은 zero 값이 들어오면 DO_NOT_USE_NULLDATA 코드를 반환한다.
     *
     * @param dietPeriod 다이어트 기간 정보. (주 단위)
     * @return 1:DO_NOT_USE_NULLDATA, 0:SUCCESS
     */
    public int setUserDietPeriod(int dietPeriod) {
        if (dietPeriod < 1) {
            Log.e(tag, "do not use null&zero data : setUserDietPeriod");
            return DO_NOT_USE_NULLDATA;
        }

        Preference.putDietPeriod(getContext(), dietPeriod);

        return SUCCESS;
    }

    /**
     * 사용자의 DietPeriod 정보를 기본 DB에서 받아온다. DietPeriod를 반환. (반환값은 주 단위)
     *
     * @return DietPeriod 정보(주 단위).
     */
    public int getUserDietPeriod() {
        int dietPeriod = Preference.getDietPeriod(getContext());

        return dietPeriod;
    }

    public void setMac(String mac) {
        Preference.putBluetoothMac(getContext(), mac);
    }

    public String getMac() {
        return Preference.getBluetoothMac(getContext());
    }

    public void setUserCode(String userCode) {
        Preference.putUrlUsercode(getContext(), userCode);
    }

    public String getUserCode() {
        return Preference.getUrlUsercode(getContext());
    }

    public void setEmail(String email) {
        Preference.putEmail(getContext(), email);
    }

    public String getEmail() {
        return Preference.getEmail(getContext());
    }

    public void setPassword(String password) {
        Preference.putPassword(getContext(), password);
    }

    public String getPassword() {
        return Preference.getPassword(getContext());
    }
}
