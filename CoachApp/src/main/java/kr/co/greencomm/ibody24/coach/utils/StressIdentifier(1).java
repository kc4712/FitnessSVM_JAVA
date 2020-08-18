package kr.co.greencomm.ibody24.coach.utils;

import android.graphics.Color;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.ActivityMain;

/**
 * Created by jeyang on 16. 9. 23..
 */
public enum StressIdentifier {
    VeryGood(1), Good(2), Normal(3), Bad(4);


    private final int id;
    StressIdentifier(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public String toString() {
        String ret;
        switch (this) {
            case VeryGood:
                ret = ActivityMain.MainContext.getString(R.string.very_good);
                break;
            case Good:
                ret = ActivityMain.MainContext.getString(R.string.good);
                break;
            case Normal:
                ret = ActivityMain.MainContext.getString(R.string.normal);
                break;
            case Bad:
                ret = ActivityMain.MainContext.getString(R.string.bad);
                break;
            default:
                ret = ActivityMain.MainContext.getString(R.string.normal);
        }

        return ret;
    }

    public int getColor() {
        int color;
        switch (this) {
            case VeryGood:
                color = Color.parseColor("#ED6D2D");
                break;
            case Good:
                color = Color.parseColor("#F2E34D");
                break;
            case Normal:
                color = Color.parseColor("#CBD024");
                break;
            case Bad:
                color = Color.parseColor("#D01B1B");
                break;
            default:
                color = Color.parseColor("#CBD024");
        }

        return color;
    }
}
