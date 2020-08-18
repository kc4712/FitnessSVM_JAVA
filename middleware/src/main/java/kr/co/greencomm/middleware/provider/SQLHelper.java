package kr.co.greencomm.middleware.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import kr.co.greencomm.middleware.utils.container.ActivityData;
import kr.co.greencomm.middleware.utils.container.CoachActivityData;
import kr.co.greencomm.middleware.utils.container.IndexTimeData;

/**
 * M/W의 데이터 베이스(이하 DB)를 관리하는 클래스로 선언되어 있는 static 변수들을 제외하고는 접근할 수 없습니다.
 */
public final class SQLHelper extends SQLiteOpenHelper {
    private static final String tag = SQLHelper.class.getSimpleName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ibody.db";

    /**
     * table var
     **/
    public static final String TABLE_COACH_ACTIVITY_DATA = "coachActivityData";
    // Consume calorie Columns names
    private static final String KEY_CA_INDEX = "ca_index";
    private static final String KEY_CA_VIDEO_IDX = "ca_video_idx";
    private static final String KEY_CA_VIDEO_FULL_COUNT = "ca_video_full_count";
    private static final String KEY_CA_EXER_IDX = "ca_exer_idx";
    private static final String KEY_CA_EXER_COUNT = "ca_exer_count";
    private static final String KEY_CA_START_TIME = "ca_start_time";
    private static final String KEY_CA_END_TIME = "ca_end_time";
    private static final String KEY_CA_CONSUME_CALORIE = "ca_consume_calorie";
    private static final String KEY_CA_COUNT = "ca_count";
    private static final String KEY_CA_COUNT_PERCENT = "ca_count_percent";
    private static final String KEY_CA_PERFECT_COUNT = "ca_perfect_count";
    private static final String KEY_CA_MIN_ACCURACY = "ca_min_accuracy";
    private static final String KEY_CA_MAX_ACCURACY = "ca_max_accuracy";
    private static final String KEY_CA_AVG_ACCURACY = "ca_avg_accuracy";
    private static final String KEY_CA_MIN_HEARTRATE = "ca_min_heartrate";
    private static final String KEY_CA_MAX_HEARTRATE = "ca_max_heartrate";
    private static final String KEY_CA_AVG_HEARTRATE = "ca_avg_heartrate";
    private static final String KEY_CA_CMP_HEARTRATE = "ca_compared_heartrate";
    private static final String KEY_CA_POINT = "ca_point";
    private static final String KEY_CA_EXER_RESERVED_1 = "ca_reserved_1";
    private static final String KEY_CA_EXER_RESERVED_2 = "ca_reserved_2";

    public static final String TABLE_ACTIVITY_DATA = "activityData";
    // Columns
    private static final String KEY_AD_INDEX = "cd_index";
    private static final String KEY_AD_LABEL = "cd_label";
    private static final String KEY_AD_CALORIE = "cd_calorie";
    private static final String KEY_AD_START_DATE = "cd_start_date";
    private static final String KEY_AD_END_DATE = "cd_end_date";
    private static final String KEY_AD_INTENSITY_L = "cd_intensity_L";
    private static final String KEY_AD_INTENSITY_M = "cd_intensity_M";
    private static final String KEY_AD_INTENSITY_H = "cd_intensity_H";
    private static final String KEY_AD_INTENSITY_D = "cd_intensity_D";
    private static final String KEY_AD_MAXHR = "cd_max_hr";
    private static final String KEY_AD_MINHR = "cd_min_hr";
    private static final String KEY_AD_AVGHR = "cd_avg_hr";
    private static final String KEY_AD_UPLOAD = "cd_upload";

    public static final String TABLE_INDEX_START_TIME = "indexStartTime";
    // Columns
    private static final String KEY_IS_INDEX = "is_index";
    private static final String KEY_IS_LABEL = "is_label";
    private static final String KEY_IS_START_TIME = "is_start_time";
    private static final String KEY_IS_END_TIME = "is_end_time";

    private static SQLHelper mDBHelper = null;

    /**
     * 기본 DB 테이블이 존재합니다.
     */
    public static final int SET_UPLOAD = 1;
    public static final int NONSET_UPLOAD = 0;

    protected static final int FAILED = -1;
    protected static final int SUCEESS = 1;


    /****/
    private SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        getWritableDatabase().execSQL("PRAGMA synchronous=OFF");
        getWritableDatabase().execSQL("PRAGMA temp_store=2");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA auto_vacuum=2");

        String CREATE_TABLE_COACH_ACTIVITY_DATA = "CREATE TABLE " + TABLE_COACH_ACTIVITY_DATA
                + "(" + KEY_CA_INDEX + " INTEGER PRIMARY KEY," + KEY_CA_VIDEO_IDX + " INTEGER," // 1 , 2
                + KEY_CA_VIDEO_FULL_COUNT + " INTEGER," + KEY_CA_EXER_IDX + " INTEGER,"
                + KEY_CA_EXER_COUNT + " INTEGER," + KEY_CA_START_TIME + " INTEGER,"
                + KEY_CA_END_TIME + " INTEGER," + KEY_CA_CONSUME_CALORIE + " INTEGER,"
                + KEY_CA_COUNT + " INTEGER," + KEY_CA_COUNT_PERCENT + " INTEGER,"
                + KEY_CA_PERFECT_COUNT + " INTEGER," + KEY_CA_MIN_ACCURACY + " INTEGER,"
                + KEY_CA_MAX_ACCURACY + " INTEGER," + KEY_CA_AVG_ACCURACY + " INTEGER,"
                + KEY_CA_MIN_HEARTRATE + " INTEGER," + KEY_CA_MAX_HEARTRATE + " INTEGER,"
                + KEY_CA_AVG_HEARTRATE + " INTEGER," + KEY_CA_CMP_HEARTRATE + " INTEGER,"
                + KEY_CA_POINT + " INTEGER," + KEY_CA_EXER_RESERVED_1 + " INTEGER,"
                + KEY_CA_EXER_RESERVED_2 + " INTEGER" + ")";

        String CREATE_TABLE_ACTIVITY_DATA = "CREATE TABLE " + TABLE_ACTIVITY_DATA
                + "(" + KEY_AD_INDEX + " INTEGER PRIMARY KEY," + KEY_AD_LABEL + " INTEGER," // 1 , 2
                + KEY_AD_CALORIE + " INTEGER," + KEY_AD_START_DATE + " INTEGER,"
                + KEY_AD_END_DATE + " INTEGER," + KEY_AD_INTENSITY_L + " INTEGER,"
                + KEY_AD_INTENSITY_M + " INTEGER," + KEY_AD_INTENSITY_H + " INTEGER,"
                + KEY_AD_INTENSITY_D + " INTEGER," + KEY_AD_MAXHR + " INTEGER,"
                + KEY_AD_MINHR + " INTEGER," + KEY_AD_AVGHR + " INTEGER,"
                + KEY_AD_UPLOAD + " INTEGER" + ")";

        String CREATE_TABLE_INDEX_START_TIME = "CREATE TABLE " + TABLE_INDEX_START_TIME
                + "(" + KEY_IS_INDEX + " INTEGER PRIMARY KEY," + KEY_IS_LABEL + " INTEGER," // 1 , 2
                + KEY_IS_START_TIME + " INTEGER," + KEY_IS_END_TIME + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE_COACH_ACTIVITY_DATA);
        db.execSQL(CREATE_TABLE_ACTIVITY_DATA);
        db.execSQL(CREATE_TABLE_INDEX_START_TIME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(tag, "Upgrade SQL Databse.. all drop table & create");
        dropAllTable(db);
        onCreate(db);
    }

    private void dropAllTable(SQLiteDatabase db) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INDEX_START_TIME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACH_ACTIVITY_DATA);
    }

    public static SQLHelper getInstance(Context context) {
        if (mDBHelper == null) {
            mDBHelper = new SQLHelper(context);
        }
        return mDBHelper;
    }

    private void dipose() {
        mDBHelper = null;
    }

    /**
     * SQL
     **/
    protected void execVacuum() {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db.inTransaction())
            Log.d(tag, "true inTransaction");
        else
            Log.d(tag, "false inTransaction");

        Cursor cursor = db.rawQuery("PRAGMA incremental_vacuum=0", null);
        cursor.close();
        Log.d(tag, "execVacuum");
    }

    public int addIndexTime(int label, long start_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + TABLE_INDEX_START_TIME + " (" + KEY_IS_LABEL + ", " + KEY_IS_START_TIME + ") VALUES (" + label + ", " + start_time + ")";

        Cursor cursor = db.rawQuery(query, null);
        cursor.close();

        return SUCEESS;
    }

    public ProviderValues addIndexTimeProvider(int label, long start_time) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);

        ContentValues values = new ContentValues();
        values.put(KEY_IS_LABEL, label);
        values.put(KEY_IS_START_TIME, start_time);
        ret.setValues(values);

        return ret;
    }

    public int updateIndexTime(long start_time, long end_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_INDEX_START_TIME + " SET " + KEY_IS_END_TIME + "=" + end_time + " WHERE " + KEY_IS_START_TIME + "=" + start_time;

        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();

        return count > 0 ? SUCEESS : FAILED;
    }

    public ProviderValues updateIndexTimeProvider(long start_time, long end_time) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);

        ContentValues values = new ContentValues();
        values.put(KEY_IS_END_TIME, end_time);

        ret.setSelection(KEY_IS_START_TIME + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(start_time)});

        return ret;
    }

    public IndexTimeData getIndexTime(long start_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_INDEX_START_TIME + " WHERE " + KEY_IS_START_TIME + "=" + start_time;

        IndexTimeData result = new IndexTimeData();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result.setLabel(cursor.getInt(1));
            result.setStart_time(cursor.getLong(2));
            result.setEnd_time(cursor.getLong(3));
        }

        cursor.close();

        return result;
    }

    public IndexTimeData getIndexTimeProvider(long start_time, ContentResolver res) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);
        ret.setSelection(CoachContract.IndexTime.KEY_IS_START_TIME + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(start_time)});

        Cursor cursor = res.query(ret.getUri(), ret.getProjection(), ret.getSelection(), ret.getSelectionArgs(), ret.getSortOrder());

        return getIndexTime(cursor);
    }

    private IndexTimeData getIndexTime(Cursor cursor) {
        IndexTimeData result = new IndexTimeData();
        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result.setLabel(cursor.getInt(1));
            result.setStart_time(cursor.getLong(2));
            result.setEnd_time(cursor.getLong(3));
        }

        cursor.close();

        return result;
    }

    public ArrayList<IndexTimeData> getIndexTime() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_INDEX_START_TIME + " ORDER BY " + KEY_IS_START_TIME + " ASC";

        ArrayList<IndexTimeData> arr = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            IndexTimeData result = new IndexTimeData();
            result.setLabel(cursor.getInt(1));
            result.setStart_time(cursor.getLong(2));
            result.setEnd_time(cursor.getLong(3));

            arr.add(result);
            cursor.moveToNext();
        }

        cursor.close();

        return arr;
    }

    public ProviderValues getIndexTimeProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);
        ret.setSortOrder(KEY_IS_START_TIME + " ASC");

        return ret;
    }

    protected IndexTimeData getIndexTime(int label) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_INDEX_START_TIME + " WHERE " + KEY_IS_LABEL + "=" + label + " ORDER BY " + KEY_IS_START_TIME + " ASC LIMIT 1";

        IndexTimeData result = new IndexTimeData();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result.setLabel(cursor.getInt(1));
            result.setStart_time(cursor.getLong(2));
            result.setEnd_time(cursor.getLong(3));
        }

        cursor.close();

        return result;
    }

    public ProviderValues getIndexTimeProvider(int label) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);

        ret.setSelection(KEY_IS_LABEL + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(label)});
        ret.setSortOrder(KEY_IS_START_TIME + " ASC LIMIT 1");

        return ret;
    }

    public int deleteIndexTime(long start_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_INDEX_START_TIME + " WHERE " + KEY_IS_START_TIME + "=" + start_time;

        Cursor cursor = db.rawQuery(query, null);
        cursor.close();

        return SUCEESS;
    }

    public ProviderValues deleteIndexTimeProvider(long start_time) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);

        ret.setSelection(KEY_IS_START_TIME + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(start_time)});

        return ret;
    }

    public ProviderValues deleteIndexTimeProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);

        return ret;
    }

    protected int addActivityData(ActivityData data) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + TABLE_ACTIVITY_DATA + " (" + KEY_AD_LABEL + ", " + KEY_AD_CALORIE + ", " + KEY_AD_START_DATE + ", "
                + KEY_AD_END_DATE + ", " + KEY_AD_INTENSITY_L + ", " + KEY_AD_INTENSITY_M + ", " + KEY_AD_INTENSITY_H + ", " + KEY_AD_INTENSITY_D + ", " + KEY_AD_MAXHR
                + ", " + KEY_AD_MINHR + ", " + KEY_AD_AVGHR + ", " + KEY_AD_UPLOAD + ") VALUES (" + data.getLabel() + ", " + data.getAct_calorie() + ", "
                + data.getStart_time() + ", " + data.getEnd_time() + ", " + data.getIntensityL() + ", " + data.getIntensityM() + ", " + data.getIntensityH() + ", " + data.getIntensityD() + ", "
                + data.getMaxHR() + ", " + data.getMinHR() + ", " + data.getAvgHR() + ", " + data.getUpload() + ")";

        Cursor cursor = db.rawQuery(query, null);
        cursor.close();

        return SUCEESS;
    }

    public ProviderValues addActivityDataProvider(ActivityData data) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);

        ContentValues values = new ContentValues();
        values.put(KEY_AD_LABEL, data.getLabel());
        values.put(KEY_AD_CALORIE, data.getAct_calorie());
        values.put(KEY_AD_START_DATE, data.getStart_time());
        values.put(KEY_AD_END_DATE, data.getEnd_time());
        values.put(KEY_AD_INTENSITY_L, data.getIntensityL());
        values.put(KEY_AD_INTENSITY_M, data.getIntensityM());
        values.put(KEY_AD_INTENSITY_H, data.getIntensityH());
        values.put(KEY_AD_INTENSITY_D, data.getIntensityD());
        values.put(KEY_AD_MAXHR, data.getMaxHR());
        values.put(KEY_AD_MINHR, data.getMinHR());
        values.put(KEY_AD_AVGHR, data.getAvgHR());
        values.put(KEY_AD_UPLOAD, data.getUpload());
        ret.setValues(values);

        return ret;
    }

    protected int addCoachActivityData(CoachActivityData data) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + TABLE_COACH_ACTIVITY_DATA + " (" + KEY_CA_VIDEO_IDX + ", " + KEY_CA_VIDEO_FULL_COUNT + ", " + KEY_CA_EXER_IDX + ", "
                + KEY_CA_EXER_COUNT + ", " + KEY_CA_START_TIME + ", " + KEY_CA_END_TIME + ", " + KEY_CA_CONSUME_CALORIE + ", " + KEY_CA_COUNT + ", " + KEY_CA_COUNT_PERCENT + ", " + KEY_CA_PERFECT_COUNT
                + ", " + KEY_CA_MIN_ACCURACY + ", " + KEY_CA_MAX_ACCURACY + ", " + KEY_CA_AVG_ACCURACY + ", " + KEY_CA_MIN_HEARTRATE + ", " + KEY_CA_MAX_HEARTRATE + ", " + KEY_CA_AVG_HEARTRATE
                + ", " + KEY_CA_CMP_HEARTRATE + ", " + KEY_CA_POINT + ", " + KEY_CA_EXER_RESERVED_1 + ", " + KEY_CA_EXER_RESERVED_2 + ") VALUES (" + data.getVideoIdx() + ", " + data.getVideo_full_count()
                + ", " + data.getExer_idx() + ", " + data.getExer_count() + ", " + data.getStart_time() + ", " + data.getEnd_time() + ", " + data.getConsume_calorie() + ", " + data.getCount()
                + ", " + data.getCount_percent() + ", " + data.getPerfect_count() + ", " + data.getMinAccuracy() + ", " + data.getMaxAccuracy() + ", " + data.getAvgAccuracy()
                + ", " + data.getMinHeartRate() + ", " + data.getMaxHeartRate() + ", " + data.getAvgHeartRate() + ", " + data.getCmpHeartRate() + ", " + data.getPoint()
                + ", " + data.getReserved_1() + ", " + data.getReserved_2() + ")";

        Cursor cursor = db.rawQuery(query, null);
        cursor.close();

        return SUCEESS;
    }

    public ProviderValues addCoachActivityDataProvider(CoachActivityData data) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Coach.CONTENT_URI);

        ContentValues values = new ContentValues();
        values.put(KEY_CA_VIDEO_IDX, data.getVideoIdx());
        values.put(KEY_CA_VIDEO_FULL_COUNT, data.getVideo_full_count());
        values.put(KEY_CA_EXER_IDX, data.getExer_idx());
        values.put(KEY_CA_EXER_COUNT, data.getExer_count());
        values.put(KEY_CA_START_TIME, data.getStart_time());
        values.put(KEY_CA_END_TIME, data.getEnd_time());
        values.put(KEY_CA_CONSUME_CALORIE, data.getConsume_calorie());
        values.put(KEY_CA_COUNT, data.getCount());
        values.put(KEY_CA_COUNT_PERCENT, data.getCount_percent());
        values.put(KEY_CA_PERFECT_COUNT, data.getPerfect_count());
        values.put(KEY_CA_MIN_ACCURACY, data.getMinAccuracy());
        values.put(KEY_CA_MAX_ACCURACY, data.getMaxAccuracy());
        values.put(KEY_CA_AVG_ACCURACY, data.getAvgAccuracy());
        values.put(KEY_CA_MIN_HEARTRATE, data.getMinHeartRate());
        values.put(KEY_CA_MAX_HEARTRATE, data.getMaxHeartRate());
        values.put(KEY_CA_AVG_HEARTRATE, data.getAvgHeartRate());
        values.put(KEY_CA_CMP_HEARTRATE, data.getCmpHeartRate());
        values.put(KEY_CA_POINT, data.getPoint());
        values.put(KEY_CA_EXER_RESERVED_1, data.getReserved_1());
        values.put(KEY_CA_EXER_RESERVED_2, data.getReserved_2());
        ret.setValues(values);

        return ret;
    }

    public int updateActivityData(ActivityData data) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_ACTIVITY_DATA + " SET " + KEY_AD_LABEL + "=" + data.getLabel() + ", " + KEY_AD_CALORIE + "=" + data.getAct_calorie() + ", " + KEY_AD_START_DATE + "=" + data.getStart_time() + ", "
                + KEY_AD_END_DATE + "=" + data.getEnd_time() + ", " + KEY_AD_INTENSITY_L + "=" + data.getIntensityL() + ", " + KEY_AD_INTENSITY_M + "=" + data.getIntensityM() + ", " + KEY_AD_INTENSITY_H + "=" + data.getIntensityH() + ", "
                + KEY_AD_INTENSITY_D + "=" + data.getIntensityD() + ", " + KEY_AD_MAXHR + "=" + data.getMaxHR() + ", " + KEY_AD_MINHR + "=" + data.getMinHR() + ", " + KEY_AD_AVGHR + "=" + data.getAvgHR() + ", "
                + KEY_AD_UPLOAD + "=" + data.getUpload() + ", " + " WHERE " + KEY_AD_START_DATE + "=" + data.getStart_time();

        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();

        return count > 0 ? SUCEESS : FAILED;
    }

    public ProviderValues updateActivityDataProvider(ActivityData data) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);

        ContentValues values = new ContentValues();
        values.put(KEY_AD_LABEL, data.getLabel());
        values.put(KEY_AD_CALORIE, data.getAct_calorie());
        values.put(KEY_AD_START_DATE, data.getStart_time());
        values.put(KEY_AD_END_DATE, data.getEnd_time());
        values.put(KEY_AD_INTENSITY_L, data.getIntensityL());
        values.put(KEY_AD_INTENSITY_M, data.getIntensityM());
        values.put(KEY_AD_INTENSITY_H, data.getIntensityH());
        values.put(KEY_AD_INTENSITY_D, data.getIntensityD());
        values.put(KEY_AD_MAXHR, data.getMaxHR());
        values.put(KEY_AD_MINHR, data.getMinHR());
        values.put(KEY_AD_AVGHR, data.getAvgHR());
        values.put(KEY_AD_UPLOAD, data.getUpload());
        ret.setValues(values);

        ret.setSelection(KEY_AD_START_DATE + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(data.getStart_time())});

        return ret;
    }

    public int updateActivityData(ActivityData data, int flag) {
        data.setUpload(flag);
        int ret = updateActivityData(data);
        Log.d(tag, "->2. input label:" + data.getLabel() + " time:" + data.getStart_time() + " flag:" + data.getUpload() + " updateConsme ret:" + ret);

        return ret;
    }

    protected ArrayList<CoachActivityData> getCoachActivityData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_COACH_ACTIVITY_DATA + " ORDER BY " + KEY_CA_START_TIME + " ASC";

        ArrayList<CoachActivityData> arr = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CoachActivityData result = new CoachActivityData();
            result.setIndex(cursor.getInt(0));
            result.setVideoIdx(cursor.getInt(1));
            result.setVideo_full_count(cursor.getInt(2));
            result.setExer_idx(cursor.getInt(3));
            result.setExer_count(cursor.getInt(4));
            result.setStart_time(cursor.getLong(5));
            result.setEnd_time(cursor.getLong(6));
            result.setConsume_calorie(cursor.getInt(7));
            result.setCount(cursor.getInt(8));
            result.setCount_percent(cursor.getInt(9));
            result.setPerfect_count(cursor.getInt(10));
            result.setMinAccuracy(cursor.getInt(11));
            result.setMaxAccuracy(cursor.getInt(12));
            result.setAvgAccuracy(cursor.getInt(13));
            result.setMinHeartRate(cursor.getInt(14));
            result.setMaxHeartRate(cursor.getInt(15));
            result.setAvgHeartRate(cursor.getInt(16));
            result.setCmpHeartRate(cursor.getInt(17));
            result.setPoint(cursor.getInt(18));
            result.setReserved_1(cursor.getInt(19));
            result.setReserved_2(cursor.getInt(20));

            arr.add(result);
            cursor.moveToNext();
        }

        cursor.close();

        return arr;
    }

    public ProviderValues getCoachActivityDataProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Coach.CONTENT_URI);

        ret.setSortOrder(KEY_CA_START_TIME + " ASC");

        return ret;
    }

    protected CoachActivityData getCoachActivityDataNeedUpload() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_COACH_ACTIVITY_DATA + " LIMIT 1";

        CoachActivityData result = new CoachActivityData();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result.setIndex(cursor.getInt(0));
            result.setVideoIdx(cursor.getInt(1));
            result.setVideo_full_count(cursor.getInt(2));
            result.setExer_idx(cursor.getInt(3));
            result.setExer_count(cursor.getInt(4));
            result.setStart_time(cursor.getLong(5));
            result.setEnd_time(cursor.getLong(6));
            result.setConsume_calorie(cursor.getInt(7));
            result.setCount(cursor.getInt(8));
            result.setCount_percent(cursor.getInt(9));
            result.setPerfect_count(cursor.getInt(10));
            result.setMinAccuracy(cursor.getInt(11));
            result.setMaxAccuracy(cursor.getInt(12));
            result.setAvgAccuracy(cursor.getInt(13));
            result.setMinHeartRate(cursor.getInt(14));
            result.setMaxHeartRate(cursor.getInt(15));
            result.setAvgHeartRate(cursor.getInt(16));
            result.setCmpHeartRate(cursor.getInt(17));
            result.setPoint(cursor.getInt(18));
            result.setReserved_1(cursor.getInt(19));
            result.setReserved_2(cursor.getInt(20));
        }

        cursor.close();

        return result;
    }

    public ProviderValues getCoachActivityDataNeedUploadProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Coach.CONTENT_URI);

        ret.setSortOrder("LIMIT 1");

        return ret;
    }

    protected ArrayList<ActivityData> getActivityData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ACTIVITY_DATA + " ORDER BY " + KEY_AD_START_DATE + " ASC";

        ActivityData result = new ActivityData();
        ArrayList<ActivityData> arr = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.setIndex(cursor.getInt(0));
            result.setLabel(cursor.getInt(1));
            result.setAct_calorie(cursor.getDouble(2));
            result.setStart_time(cursor.getLong(3));
            result.setEnd_time(cursor.getLong(4));
            result.setIntensityL(cursor.getShort(5));
            result.setIntensityM(cursor.getShort(6));
            result.setIntensityH(cursor.getShort(7));
            result.setIntensityD(cursor.getShort(8));
            result.setMaxHR(cursor.getShort(9));
            result.setMinHR(cursor.getShort(10));
            result.setAvgHR(cursor.getShort(11));
            result.setUpload(cursor.getInt(12));

            arr.add(result);
            cursor.moveToNext();
        }

        cursor.close();

        return arr;
    }

    public ProviderValues getActivityDataProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);

        ret.setSortOrder(KEY_AD_START_DATE + " ASC");

        return ret;
    }

    protected ActivityData getActivityData(long start_date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ACTIVITY_DATA + " WHERE " + KEY_AD_START_DATE + "=" + start_date;

        ActivityData result = new ActivityData();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result.setIndex(cursor.getInt(0));
            result.setLabel(cursor.getInt(1));
            result.setAct_calorie(cursor.getDouble(2));
            result.setStart_time(cursor.getLong(3));
            result.setEnd_time(cursor.getLong(4));
            result.setIntensityL(cursor.getShort(5));
            result.setIntensityM(cursor.getShort(6));
            result.setIntensityH(cursor.getShort(7));
            result.setIntensityD(cursor.getShort(8));
            result.setMaxHR(cursor.getShort(9));
            result.setMinHR(cursor.getShort(10));
            result.setAvgHR(cursor.getShort(11));
            result.setUpload(cursor.getInt(12));
        }

        cursor.close();

        return result;
    }

    public ProviderValues getActivityDataProvider(long start_date) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);
        ret.setSelection(KEY_AD_START_DATE + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(start_date)});

        return ret;
    }

    protected ActivityData getActivityData(int index) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ACTIVITY_DATA + " WHERE " + KEY_AD_INDEX + "=" + index;

        ActivityData result = new ActivityData();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result.setIndex(cursor.getInt(0));
            result.setLabel(cursor.getInt(1));
            result.setAct_calorie(cursor.getDouble(2));
            result.setStart_time(cursor.getLong(3));
            result.setEnd_time(cursor.getLong(4));
            result.setIntensityL(cursor.getShort(5));
            result.setIntensityM(cursor.getShort(6));
            result.setIntensityH(cursor.getShort(7));
            result.setIntensityD(cursor.getShort(8));
            result.setMaxHR(cursor.getShort(9));
            result.setMinHR(cursor.getShort(10));
            result.setAvgHR(cursor.getShort(11));
            result.setUpload(cursor.getInt(12));
        }

        cursor.close();

        return result;
    }

    public ProviderValues getActivityDataProvider(int index) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);
        ret.setSelection(KEY_AD_INDEX + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(index)});

        return ret;
    }

    protected ActivityData getActivityDataNeedUpload() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ACTIVITY_DATA + " WHERE " + KEY_AD_UPLOAD + "=" + NONSET_UPLOAD + " ORDER BY " + KEY_AD_START_DATE + "ASC LIMIT 1";

        ActivityData result = new ActivityData();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result.setIndex(cursor.getInt(0));
            result.setLabel(cursor.getInt(1));
            result.setAct_calorie(cursor.getDouble(2));
            result.setStart_time(cursor.getLong(3));
            result.setEnd_time(cursor.getLong(4));
            result.setIntensityL(cursor.getShort(5));
            result.setIntensityM(cursor.getShort(6));
            result.setIntensityH(cursor.getShort(7));
            result.setIntensityD(cursor.getShort(8));
            result.setMaxHR(cursor.getShort(9));
            result.setMinHR(cursor.getShort(10));
            result.setAvgHR(cursor.getShort(11));
            result.setUpload(cursor.getInt(12));
        }

        cursor.close();

        return result;
    }

    public ProviderValues getActivityDataNeedUploadProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);
        ret.setSelection(KEY_AD_UPLOAD + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(NONSET_UPLOAD)});
        ret.setSortOrder(KEY_AD_START_DATE + "ASC LIMIT 1");

        return ret;
    }

    protected int deleteCoachActivityData(int index) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_COACH_ACTIVITY_DATA + " WHERE " + KEY_CA_INDEX + "=" + index;

        Cursor cursor = db.rawQuery(query, null);
        cursor.close();

        return SUCEESS;
    }

    public ProviderValues deleteCoachActivityDataProvider(int index) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Coach.CONTENT_URI);
        ret.setSelection(KEY_CA_INDEX + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(index)});

        return ret;
    }

    protected int deleteActivityData(int index) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_ACTIVITY_DATA + " WHERE " + KEY_AD_INDEX + "=" + index;

        Cursor cursor = db.rawQuery(query, null);
        cursor.close();

        return SUCEESS;
    }

    public ProviderValues deleteActivityDataProvider(int index) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);
        ret.setSelection(KEY_AD_INDEX + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(index)});

        return ret;
    }

    protected int deleteActivityData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_ACTIVITY_DATA;

        Cursor cursor = db.rawQuery(query, null);
        cursor.close();

        return SUCEESS;
    }

    public ProviderValues deleteActivityDataProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);

        return ret;
    }

    public void dispose() {
    }
}