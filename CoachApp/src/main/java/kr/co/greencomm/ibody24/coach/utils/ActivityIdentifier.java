package kr.co.greencomm.ibody24.coach.utils;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.ActivityMain;

/**
 * Created by jeyang on 16. 9. 23..
 */
public enum ActivityIdentifier {
    Low(1), Mid(2), High(3), Danger(4);

    private final int id;

    ActivityIdentifier(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public String toString() {
        String ret;
        switch (this) {
            case Low:
                ret = ActivityMain.MainContext.getString(R.string.low);
                break;
            case Mid:
                ret = ActivityMain.MainContext.getString(R.string.mid);
                break;
            case High:
                ret = ActivityMain.MainContext.getString(R.string.high);
                break;
            case Danger:
                ret = ActivityMain.MainContext.getString(R.string.danger);
                break;
            default:
                ret = ActivityMain.MainContext.getString(R.string.mid);
        }

        return ret;
    }

    public static ActivityIdentifier getActivityIdentifier(int id) {
        ActivityIdentifier ret = Low;
        switch (id) {
            case 1:
                ret = Low;
                break;
            case 2:
                ret = Mid;
                break;
            case 3:
                ret = High;
                break;
            case 4:
                ret = Danger;
                break;
        }

        return ret;
    }
}
