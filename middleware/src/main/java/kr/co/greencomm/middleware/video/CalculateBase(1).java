package kr.co.greencomm.middleware.video;

import java.math.BigDecimal;

import kr.co.greencomm.middleware.utils.DataBaseUtil;
import kr.co.greencomm.middleware.utils.container.DataBase;
import kr.co.greencomm.middleware.utils.container.UserProfile;

/**
 * Created by jeyang on 2016-08-26.
 */
public class CalculateBase {
    static int[] main_HrArray = new int[]{0, 0, 0, 0, 0};

    public static int sumInterval = 2; // 초단위

    private static DataBase mDatabase = DataBase.getInstance();

    //---구현
    // 심박수 zone을 계산.
    public static float[] getHeartRateDangerZone(UserProfile profile) {
        int age = profile.getAge();
        float maxHR = 220 - age;
        float largeHR = maxHR * 0.8f;

        return new float[]{maxHR, largeHR};
    }

    //--- 최대 심박수 대비 현재 심박수 계산(%)
    public static int avgHeartRate(int hr) {
        int ret = 0;
        int count = 0;

        for (int i = 0; i < main_HrArray.length - 1; i++) {
            main_HrArray[i] = main_HrArray[i + 1];
        }
        main_HrArray[4] = hr;

        for (int h : main_HrArray) {
            if (h != 0) {
                ret += h;
                count += 1;
            }
        }

        return count == 0 ? 0 : ret / count;
    }

    public static void reset() {
        for (int i = 0; i < main_HrArray.length; i++) {
            main_HrArray[i] = 0;
        }
    }

    public static float getConsumeCalorie(float weight, float MET) {
        BigDecimal bigWeight = new BigDecimal(weight);
        BigDecimal bigMETdefine = new BigDecimal(0.0175);
        BigDecimal bigMET = new BigDecimal(MET);

        return (bigMET.multiply(bigWeight).multiply(bigMETdefine).floatValue() * 1440);
    }

    //--- 최대 심박수 대비 현재 심박수를 반환.(%)
    //--- @param hr 현재 심박수
    //--- @return 반환하는 심박수(%)
    public static int getHeartRateCompared(int hr, UserProfile profile) {
        short stable = mDatabase.getHeartrate_stable();
        if (stable == 0)
            stable = 60;

        float ret = hr - stable;
        ret = ret / (220 - profile.getAge() - stable) * 100;

        return (int) (ret < 0 ? 0 : ret);
    }

    public static int getHeartRateCompared(int hr, int stable, int age) {
        if (stable == 0)
            stable = 60;

        float ret = hr - stable;
        ret = ret / (220 - age - stable) * 100;

        return (int) (ret < 0 ? 0 : ret);
    }

    //--- point 계산. 정확도의 횟수의 평균치를 계산한다.
    //--- @param videoID
    //--- @param accuracy
    //--- @param count
    //--- @return
    public static int getPoint(int count, int accuracy) {
        // 카운트와 정확도는 항상 100%를 넘을 수 있음. 넘으면 100%로 계산.
        // Log.d(tag,"getPoint : "+((accuracy * count)/100) + " count:"+count+"
        // acc:"+accuracy);
        return (accuracy * count) / 100;
    }

    //--- sumInterval 간격으로 칼로리를 합산.
    //--- @param pulseRate
    //---            sumInterval 간격의 평균 값으로 계산된 심박수.
    //--- @return 계산된 소비 칼로리.(kcal)
    public static float formulaHeartRate(int pulseRate, UserProfile profile) {
        float calorie = 0;
        float weight = profile.getWeight();

        float metabolic_rate_per_sec = getMetabolicRate(profile) / 86400;

        if (profile.getSex() == DataBaseUtil.SEX_MALE) {
            calorie = (float) (-8477.604 + (weight * 6.481) + (pulseRate * 51.426) + (weight * pulseRate * 1.018)) * ((float) sumInterval / 60 / 1000);
            if (metabolic_rate_per_sec * sumInterval > calorie)
                calorie = metabolic_rate_per_sec * sumInterval;
            return calorie;
        } else {
            calorie = (float) (100.127 + (weight * -106.729) + (pulseRate * 12.580) + (weight * pulseRate * 1.251)) * ((float) sumInterval / 60 / 1000);
            if (metabolic_rate_per_sec * sumInterval > calorie)
                calorie = metabolic_rate_per_sec * sumInterval;
            return calorie;
        }
    }

    public static float getMetabolicRate(UserProfile profile) {
        float rate = 0;
        float weight = profile.getWeight();
        int height = profile.getHeight();
        int age = profile.getAge();

        if (profile.getSex() == DataBaseUtil.SEX_MALE) {
            rate = (float) ((88.362 + (13.397 * weight)) + (4.799 * height) - (5.677 * age));
        } else {
            rate = (float) (447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age));
        }

        return rate;
    }

    public static float getMetabolicRate(float weight, int height, int age, int sex) {
        float rate = 0;

        if (sex == DataBaseUtil.SEX_MALE) {
            rate = (float) ((88.362 + (13.397 * weight)) + (4.799 * height) - (5.677 * age));
        } else {
            rate = (float) (447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age));
        }

        return rate;
    }
}
