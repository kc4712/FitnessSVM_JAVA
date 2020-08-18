package kr.co.greencomm.ibody24.coach.utils;

/**
 * Created by jeyang on 2016-08-26.
 */
public class Height {
    public static int Minimum = 60;
    public static int Maximum = 280;
    public static int Default = 170;

    public static int setLength() {
        return Maximum - Minimum + 1;
    }

    public static int ToIndex(int value) {
        return (value < Minimum ? 0 : value - Minimum);
    }

    public static int FromIndex(int index) {
        return index + Minimum;
    }

    public static String ToString(int height) {
        return height + " cm";
    }
}
