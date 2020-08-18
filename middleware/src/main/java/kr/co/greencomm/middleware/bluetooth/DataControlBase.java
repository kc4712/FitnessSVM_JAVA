package kr.co.greencomm.middleware.bluetooth;

import android.content.Context;
import android.util.Log;


public abstract class DataControlBase extends DeviceBaseTrans {
    private static final boolean DEBUG = false;

    private static final String tag = DataControlBase.class.getSimpleName();
    //private static final long ref_millisec = 1420038000000L; // 2015.1.1 0:0:0.000 KST
    public static final long ref_millisec_utc = 946684800000L; // 2000-01-01 00:00:00.000 (msec) UTC

    protected DataControlBase(Context context) {
        super(context);
    }

    // 1970년 기준 시간을 ->UTC 2000.1.1 00:00:00.000
    public static int getConvertedTime(long time) {
        if (DEBUG) {
            Log.d(tag, "getConvertedTime : input time->" + time);
            Log.d(tag, "getConvertedTime : output time->" + (Long.valueOf((time - ref_millisec_utc) / (1000 * 60)).intValue()));
        }
        return Long.valueOf((time - ref_millisec_utc) / (1000 * 60)).intValue();
    }

    public static long getReturnedTime(int time) {
        if (DEBUG) {
            Log.d(tag, "getReturnedTime : input time->" + time);
            Log.d(tag, "getReturnedTime : output time->" + ((time * (1000 * 60L)) + ref_millisec_utc));
        }
        return (time * (1000 * 60L)) + ref_millisec_utc;
    }

    @Override
    protected void dispose() {
        super.dispose();
    }
}
