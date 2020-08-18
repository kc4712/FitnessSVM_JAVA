package kr.co.greencomm.ibody24.coach.utils;

/**
 * Created by jeyang on 2016-08-26.
 */
public class Weight {
    public static int Minimum = 30;
    public static int Maximum = 240;
    public static int Default = 60;

    private static float BMI_LOW_WEIGHT = 18.5f;
    private static float BMI_NORMAL_WEIGHT = 24.9f;

    private static float INLAB_RATE = 7.7f;

    public static int getLength() {
        return Maximum - Minimum + 1;
    }

    public static int ToIndex(int value) {
        return (value < Minimum ? 0 : value - Minimum);
    }

    public static int FromIndex(int index) {
        return index + Minimum;
    }

    public static String ToString(int weight) {
        return weight + " kg";
    }

    public static double getCalorieConsumeDaily(float weight, float goalWeight, int dietPeriod) {
        if (weight < goalWeight || dietPeriod < 1) {
            return 0;
        }

        double ans = Math.floor((weight - goalWeight) * 1000 * INLAB_RATE) / (dietPeriod * 7);
        return ans;
    }

    /**
     * @Parameter : Int32 height (키)
     * @return :    normal_below (scale[0]), normal_morethan (scale[1])
    (표준이하 <= scale[0]:) (scale[0] < 표준 <= scale[1]) (scale[1] < 표준이상).
     * @Description : 체중의 표준 범위를 구함.
     */
    public static float[] getScaleWeight(int height) {
        float tmp = height / 100.0f;

        float lowWeight = BMI_LOW_WEIGHT*tmp*tmp;
        float normalWeight = BMI_NORMAL_WEIGHT*tmp*tmp;

        return new float[]{lowWeight, normalWeight};
    }

    /**
     * @Parameter : Float32 weight (현재 체중), Float32 goalWeight(목표 체중)
     * @return :    enough (scale[0]), suitable (scale[1]), over (scale[2])
    (scale[0] < 충분 <= scale[1]) (scale[1] < 적당 <= scale[2]) (scale[2] < 무리).
     * @Description : 체중에 따른 감량 기간 판단 범위를 구함.
     */
    public static int[] getScaleDietPeriod(float weight, float goalWeight) {
        if (weight < goalWeight) {
            return new int[]{0, 0, 0};
        }

        int start = (int)((weight - goalWeight) / 0.3 * 4);
        int enough = (int)((weight - goalWeight) / 0.5 * 4);
        int normal = (int)((weight - goalWeight) / 1.2 * 4);

        return new int[]{start, enough, normal};
    }
}
