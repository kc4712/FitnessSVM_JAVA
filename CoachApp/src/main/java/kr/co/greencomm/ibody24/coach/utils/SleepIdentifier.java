package kr.co.greencomm.ibody24.coach.utils;

import android.graphics.Color;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.ActivityMain;

/**
 * Created by jeyang on 16. 9. 23..
 */
public enum SleepIdentifier {
    Enough(1), Normal(2), Few(3), Lack(4);


    private final int id;
    SleepIdentifier(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public String toString() {
        String ret;
        switch (this) {
            case Enough:
                ret = ActivityMain.MainContext.getString(R.string.enough);
                break;
            case Normal:
                ret = ActivityMain.MainContext.getString(R.string.normal);
                break;
            case Few:
                ret = ActivityMain.MainContext.getString(R.string.few);
                break;
            case Lack:
                ret = ActivityMain.MainContext.getString(R.string.lack);
                break;
            default:
                ret = "";
        }

        return ret;
    }

    public int getColor() {
        int color;
        switch (this) {
            case Enough:
                color = Color.parseColor("#ED6D2D");
                break;
            case Normal:
                color = Color.parseColor("#F2E34D");
                break;
            case Few:
                color = Color.parseColor("#CBD024");
                break;
            case Lack:
                color = Color.parseColor("#D01B1B");
                break;
            default:
                color = Color.parseColor("#CBD024");
        }

        return color;
    }

    public static SleepIdentifier getSleepIdentifier(int id) {
        SleepIdentifier ret = Enough;
        switch (id) {
            case 1:
                ret = Enough;
                break;
            case 2:
                ret = Normal;
                break;
            case 3:
                ret = Few;
                break;
            case 4:
                ret = Lack;
                break;
            default:
                ret = null;
        }
        return ret;
    }
}
