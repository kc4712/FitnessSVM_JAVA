package kr.co.greencomm.middleware.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

import java.util.Locale;

/**
 * Created by jeyang on 2016-10-31.
 */
public class PhoneHelper {
    private static final String tag = PhoneHelper.class.getSimpleName();

    public static Cursor read(Context context) {
        Uri URI = CallLog.Calls.CONTENT_URI;

        // List required columns
        String[] reqCols = new String[]{CallLog.Calls.TYPE};

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver cr = context.getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        // 미들웨어에서는 ActivityCompat 라이브러리를 사용하지 않으므로, 권한 확인을 하지 않고, try로 처리.
        Cursor cursor = null;
        try {
            //cursor = cr.query(URI, reqCols, CallLog.Calls.TYPE + "=?", new String[]{String.valueOf(CallLog.Calls.MISSED_TYPE)}, CallLog.Calls.DEFAULT_SORT_ORDER);
            cursor = cr.query(URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        } catch (SecurityException e) {
            Log.e(tag, "permission has NOT been granted. do NOT read call-log");
        }

        return cursor;
    }

    public static int getMissedCount(Cursor cursor) {
        if (cursor == null)
            return 0;

        int count = 0;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.e(tag, String.format(Locale.getDefault(), "getMissedCount type %d number %s read %d",cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)),
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)), cursor.getInt(cursor.getColumnIndex(CallLog.Calls.IS_READ))));
            count += 1;
            cursor.moveToNext();
        }

        cursor.close();

        return count;
    }
}
