package kr.co.greencomm.middleware.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

/**
 * Created by jeyang on 2016-10-31.
 */
public class SMSHelper {
    private static final String tag = SMSHelper.class.getSimpleName();

    public static Cursor read(Context context) {
        // Create Inbox box URI
        Uri inboxURI = Telephony.Sms.Inbox.CONTENT_URI;

        // List required columns
        String[] reqCols = new String[] {Telephony.Sms.READ};

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver cr = context.getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor cursor = cr.query(inboxURI, reqCols, Telephony.Sms.READ + "=?", new String[]{"0"}, Telephony.Sms.DEFAULT_SORT_ORDER);

        return cursor;
    }

    public static int getNotReadCount(Cursor cursor) {
        if (cursor == null)
            return 0;

        int count = 0;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
//            int read = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.READ));
//            if (read == 0) {
//                // not read
//                count += 1;
//            }
            count += 1;
            cursor.moveToNext();
        }

        cursor.close();

        return count;
    }
}
