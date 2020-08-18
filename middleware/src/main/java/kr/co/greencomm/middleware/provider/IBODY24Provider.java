package kr.co.greencomm.middleware.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


/**
 * Created by jeyang on 2016-08-31.
 */
public class IBODY24Provider extends ContentProvider {
    private static final String tag = IBODY24Provider.class.getSimpleName();
    private final int Coach = 1;
    private final int Fitness = 2;
    private final int IndexTime = 3;

    private UriMatcher Matcher = new UriMatcher(UriMatcher.NO_MATCH);

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Matcher.addURI(CoachContract.AUTHORITY, CoachContract.Coach.PATH, Coach);
        Matcher.addURI(CoachContract.AUTHORITY, CoachContract.Fitness.PATH, Fitness);
        Matcher.addURI(CoachContract.AUTHORITY, CoachContract.IndexTime.PATH, IndexTime);

        db = SQLHelper.getInstance(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String table = null;
        switch (Matcher.match(uri)) {
            case Coach:
                table = SQLHelper.TABLE_COACH_ACTIVITY_DATA;
                break;
            case Fitness:
                table = SQLHelper.TABLE_ACTIVITY_DATA;
                break;
            case IndexTime:
                table = SQLHelper.TABLE_INDEX_START_TIME;
                break;
        }

        String limit = null;
        if (sortOrder != null && sortOrder.startsWith("LIMIT")) {
            limit = sortOrder.trim().substring(6);
            sortOrder = null;
        }

        Cursor cursor = db.query(table, projection, selection, selectionArgs, null, null, sortOrder, limit);

//        if (table.equals(SQLHelper.TABLE_ACTIVITY_DATA)) {
//            getContext().getContentResolver().notifyChange(uri, null);
//        }

        return cursor.getCount() == 0 ? null : cursor;
    }

    @Override
    public String getType(Uri uri) {
        String mime;
        switch (Matcher.match(uri)) {
            case Coach:
                mime = "vnd.kr.co.greencomm.ibody24.coach";
                break;
            case Fitness:
                mime = "vnd.kr.co.greencomm.ibody24.fitness";
                break;
            case IndexTime:
                mime = "vnd.kr.co.greencomm.ibody24.index_time";
                break;
            default:
                mime = null;
        }
        return mime;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table = null;
        switch (Matcher.match(uri)) {
            case Coach:
                table = SQLHelper.TABLE_COACH_ACTIVITY_DATA;
                break;
            case Fitness:
                table = SQLHelper.TABLE_ACTIVITY_DATA;
                break;
            case IndexTime:
                table = SQLHelper.TABLE_INDEX_START_TIME;
                break;
        }

        long rowID = db.insert(table, null, values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(uri, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table = null;
        switch (Matcher.match(uri)) {
            case Coach:
                table = SQLHelper.TABLE_COACH_ACTIVITY_DATA;
                break;
            case Fitness:
                table = SQLHelper.TABLE_ACTIVITY_DATA;
                break;
            case IndexTime:
                table = SQLHelper.TABLE_INDEX_START_TIME;
                break;
        }

        int count = db.delete(table, selection, selectionArgs);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table = null;
        switch (Matcher.match(uri)) {
            case Coach:
                table = SQLHelper.TABLE_COACH_ACTIVITY_DATA;
                break;
            case Fitness:
                table = SQLHelper.TABLE_ACTIVITY_DATA;
                break;
            case IndexTime:
                table = SQLHelper.TABLE_INDEX_START_TIME;
                break;
        }

        int count = db.update(table, values, selection, selectionArgs);

        return count;
    }
}
