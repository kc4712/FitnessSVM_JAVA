package kr.co.greencomm.ibody24.coach.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.activity.ActivityMain;
import kr.co.greencomm.middleware.provider.CoachContract;
import kr.co.greencomm.middleware.provider.ProviderValues;
import kr.co.greencomm.middleware.utils.container.ActivityData;
import kr.co.greencomm.middleware.utils.container.CoachActivityData;
import kr.co.greencomm.middleware.utils.container.IndexTimeData;

/**
 * Created by jeyang on 2016-08-31.
 */
public class CoachResolver {
    private static final String tag = CoachResolver.class.getSimpleName();

    public static final int SET_UPLOAD = 1;
    public static final int NONSET_UPLOAD = 0;

    protected static final int FAILED = -1;
    protected static final int SUCEESS = 1;

    public CoachResolver() {

    }

    public int addIndexTimeProvider(int label, long start_time) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);

        ContentValues values = new ContentValues();
        values.put(CoachContract.IndexTime.KEY_IS_LABEL, label);
        values.put(CoachContract.IndexTime.KEY_IS_START_TIME, start_time);
        ret.setValues(values);

        return addIndexTime(ret);
    }

    private int addIndexTime(ProviderValues data) {
        ContentResolver resolver = getResolver();
        if (resolver == null)
            return FAILED;
        Uri uri = resolver.insert(data.getUri(), data.getValues());

        return uri != null ? SUCEESS : FAILED;
    }

    public int updateIndexTimeProvider(long start_time, long end_time) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);

        ContentValues values = new ContentValues();
        values.put(CoachContract.IndexTime.KEY_IS_END_TIME, end_time);

        ret.setValues(values);
        ret.setSelection(CoachContract.IndexTime.KEY_IS_START_TIME + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(start_time)});

        return updateIndexTime(ret);
    }

    private int updateIndexTime(ProviderValues data) {
        ContentResolver resolver = getResolver();
        if (resolver == null)
            return FAILED;
        int count = resolver.update(data.getUri(), data.getValues(), data.getSelection(), data.getSelectionArgs());

        return count > 0 ? SUCEESS : FAILED;
    }

    public IndexTimeData getIndexTimeProvider(long start_time) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);
        ret.setSelection(CoachContract.IndexTime.KEY_IS_START_TIME + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(start_time)});

        ContentResolver resolver = getResolver();
        if (resolver == null)
            return null;
        Cursor cursor = resolver.query(ret.getUri(), ret.getProjection(), ret.getSelection(), ret.getSelectionArgs(), ret.getSortOrder());

        return getIndexTime(cursor);
    }

    private IndexTimeData getIndexTime(Cursor cursor) {
        IndexTimeData result = new IndexTimeData();
        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result.setLabel(cursor.getInt(cursor.getColumnIndex(CoachContract.IndexTime.KEY_IS_LABEL)));
            result.setStart_time(cursor.getLong(cursor.getColumnIndex(CoachContract.IndexTime.KEY_IS_START_TIME)));
            result.setEnd_time(cursor.getLong(cursor.getColumnIndex(CoachContract.IndexTime.KEY_IS_END_TIME)));
        }

        cursor.close();

        return result;
    }

    public ArrayList<IndexTimeData> getIndexTimeProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);
        ret.setSortOrder(CoachContract.IndexTime.KEY_IS_START_TIME + " ASC");

        ContentResolver resolver = getResolver();
        if (resolver == null)
            return null;
        Cursor cursor = resolver.query(ret.getUri(), ret.getProjection(), ret.getSelection(), ret.getSelectionArgs(), ret.getSortOrder());

        return getIndexTimeArray(cursor);
    }

    private ArrayList<IndexTimeData> getIndexTimeArray(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        ArrayList<IndexTimeData> arr = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            IndexTimeData result = new IndexTimeData();
            result.setLabel(cursor.getInt(cursor.getColumnIndex(CoachContract.IndexTime.KEY_IS_LABEL)));
            result.setStart_time(cursor.getLong(cursor.getColumnIndex(CoachContract.IndexTime.KEY_IS_START_TIME)));
            result.setEnd_time(cursor.getLong(cursor.getColumnIndex(CoachContract.IndexTime.KEY_IS_END_TIME)));

            arr.add(result);
            cursor.moveToNext();
        }

        cursor.close();

        return arr;
    }

    public IndexTimeData getIndexTimeProvider(int label) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);

        ret.setSelection(CoachContract.IndexTime.KEY_IS_LABEL + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(label)});
        ret.setSortOrder(CoachContract.IndexTime.KEY_IS_START_TIME + " ASC LIMIT 1");

        ContentResolver resolver = getResolver();
        if (resolver == null)
            return null;
        Cursor cursor = resolver.query(ret.getUri(), ret.getProjection(), ret.getSelection(),
                ret.getSelectionArgs(), ret.getSortOrder());

        return getIndexTimeLabel(cursor);
    }

    private IndexTimeData getIndexTimeLabel(Cursor cursor) {
        IndexTimeData result = new IndexTimeData();
        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result.setLabel(cursor.getInt(cursor.getColumnIndex(CoachContract.IndexTime.KEY_IS_LABEL)));
            result.setStart_time(cursor.getLong(cursor.getColumnIndex(CoachContract.IndexTime.KEY_IS_START_TIME)));
            result.setEnd_time(cursor.getLong(cursor.getColumnIndex(CoachContract.IndexTime.KEY_IS_END_TIME)));
        }

        cursor.close();

        return result;
    }

    public int deleteIndexTimeProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);

        return deleteIndexTime(ret);
    }

    public int deleteIndexTimeProvider(long start_time) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.IndexTime.CONTENT_URI);

        ret.setSelection(CoachContract.IndexTime.KEY_IS_START_TIME + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(start_time)});

        return deleteIndexTime(ret);
    }

    private int deleteIndexTime(ProviderValues data) {
        ContentResolver resolver = getResolver();
        if (resolver == null)
            return FAILED;
        int count = resolver.delete(data.getUri(), data.getSelection(), data.getSelectionArgs());
        return count > 0 ? SUCEESS : FAILED;
    }

    public ArrayList<ActivityData> getActivityDataArray() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);
        ret.setSortOrder(CoachContract.Fitness.KEY_AD_START_DATE + " DESC");

        ContentResolver resolver = getResolver();
        if (resolver == null)
            return null;
        Cursor cursor = resolver.query(ret.getUri(), ret.getProjection(), ret.getSelection(), ret.getSelectionArgs(), ret.getSortOrder());

        return getActivityDataArray(cursor);
    }

    private ArrayList<ActivityData> getActivityDataArray(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        ArrayList<ActivityData> arr = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ActivityData result = new ActivityData();
            result.setIndex(cursor.getInt(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INDEX)));
            result.setLabel(cursor.getInt(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_LABEL)));
            result.setAct_calorie(cursor.getDouble(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_CALORIE)));
            result.setStart_time(cursor.getLong(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_START_DATE)));
            result.setEnd_time(cursor.getLong(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_END_DATE)));
            result.setIntensityL(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INTENSITY_L)));
            result.setIntensityM(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INTENSITY_M)));
            result.setIntensityH(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INTENSITY_H)));
            result.setIntensityD(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INTENSITY_D)));
            result.setMaxHR(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_MAXHR)));
            result.setMinHR(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_MINHR)));
            result.setAvgHR(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_AVGHR)));
            result.setUpload(cursor.getInt(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_UPLOAD)));

            arr.add(result);
            cursor.moveToNext();
        }

        cursor.close();

        return arr;
    }

    public int addActivityDataProvider(ActivityData data) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);

        ContentValues values = new ContentValues();
        values.put(CoachContract.Fitness.KEY_AD_LABEL, data.getLabel());
        values.put(CoachContract.Fitness.KEY_AD_CALORIE, data.getAct_calorie());
        values.put(CoachContract.Fitness.KEY_AD_START_DATE, data.getStart_time());
        values.put(CoachContract.Fitness.KEY_AD_END_DATE, data.getEnd_time());
        values.put(CoachContract.Fitness.KEY_AD_INTENSITY_L, data.getIntensityL());
        values.put(CoachContract.Fitness.KEY_AD_INTENSITY_M, data.getIntensityM());
        values.put(CoachContract.Fitness.KEY_AD_INTENSITY_H, data.getIntensityH());
        values.put(CoachContract.Fitness.KEY_AD_INTENSITY_D, data.getIntensityD());
        values.put(CoachContract.Fitness.KEY_AD_MAXHR, data.getMaxHR());
        values.put(CoachContract.Fitness.KEY_AD_MINHR, data.getMinHR());
        values.put(CoachContract.Fitness.KEY_AD_AVGHR, data.getAvgHR());
        values.put(CoachContract.Fitness.KEY_AD_UPLOAD, data.getUpload());
        ret.setValues(values);

        return addActivityData(ret);
    }

    private int addActivityData(ProviderValues data) {
        ContentResolver resolver = getResolver();
        if (resolver == null)
            return FAILED;
        Uri uri = resolver.insert(data.getUri(), data.getValues());
        return uri != null ? SUCEESS : FAILED;
    }

    public ProviderValues addCoachActivityDataProvider(CoachActivityData data) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Coach.CONTENT_URI);

        ContentValues values = new ContentValues();
        values.put(CoachContract.Coach.KEY_CA_VIDEO_IDX, data.getVideoIdx());
        values.put(CoachContract.Coach.KEY_CA_VIDEO_FULL_COUNT, data.getVideo_full_count());
        values.put(CoachContract.Coach.KEY_CA_EXER_IDX, data.getExer_idx());
        values.put(CoachContract.Coach.KEY_CA_EXER_COUNT, data.getExer_count());
        values.put(CoachContract.Coach.KEY_CA_START_TIME, data.getStart_time());
        values.put(CoachContract.Coach.KEY_CA_END_TIME, data.getEnd_time());
        values.put(CoachContract.Coach.KEY_CA_CONSUME_CALORIE, data.getConsume_calorie());
        values.put(CoachContract.Coach.KEY_CA_COUNT, data.getCount());
        values.put(CoachContract.Coach.KEY_CA_COUNT_PERCENT, data.getCount_percent());
        values.put(CoachContract.Coach.KEY_CA_PERFECT_COUNT, data.getPerfect_count());
        values.put(CoachContract.Coach.KEY_CA_MIN_ACCURACY, data.getMinAccuracy());
        values.put(CoachContract.Coach.KEY_CA_MAX_ACCURACY, data.getMaxAccuracy());
        values.put(CoachContract.Coach.KEY_CA_AVG_ACCURACY, data.getAvgAccuracy());
        values.put(CoachContract.Coach.KEY_CA_MIN_HEARTRATE, data.getMinHeartRate());
        values.put(CoachContract.Coach.KEY_CA_MAX_HEARTRATE, data.getMaxHeartRate());
        values.put(CoachContract.Coach.KEY_CA_AVG_HEARTRATE, data.getAvgHeartRate());
        values.put(CoachContract.Coach.KEY_CA_CMP_HEARTRATE, data.getCmpHeartRate());
        values.put(CoachContract.Coach.KEY_CA_POINT, data.getPoint());
        values.put(CoachContract.Coach.KEY_CA_EXER_RESERVED_1, data.getReserved_1());
        values.put(CoachContract.Coach.KEY_CA_EXER_RESERVED_2, data.getReserved_2());
        ret.setValues(values);

        return ret;
    }

    public int updateActivityDataProvider(ActivityData data, int flag) {
        data.setUpload(flag);
        return updateActivityDataProvider(data);
    }

    public int updateActivityDataProvider(ActivityData data) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);

        ContentValues values = new ContentValues();
        values.put(CoachContract.Fitness.KEY_AD_LABEL, data.getLabel());
        values.put(CoachContract.Fitness.KEY_AD_CALORIE, data.getAct_calorie());
        values.put(CoachContract.Fitness.KEY_AD_START_DATE, data.getStart_time());
        values.put(CoachContract.Fitness.KEY_AD_END_DATE, data.getEnd_time());
        values.put(CoachContract.Fitness.KEY_AD_INTENSITY_L, data.getIntensityL());
        values.put(CoachContract.Fitness.KEY_AD_INTENSITY_M, data.getIntensityM());
        values.put(CoachContract.Fitness.KEY_AD_INTENSITY_H, data.getIntensityH());
        values.put(CoachContract.Fitness.KEY_AD_INTENSITY_D, data.getIntensityD());
        values.put(CoachContract.Fitness.KEY_AD_MAXHR, data.getMaxHR());
        values.put(CoachContract.Fitness.KEY_AD_MINHR, data.getMinHR());
        values.put(CoachContract.Fitness.KEY_AD_AVGHR, data.getAvgHR());
        values.put(CoachContract.Fitness.KEY_AD_UPLOAD, data.getUpload());
        ret.setValues(values);

        ret.setSelection(CoachContract.Fitness.KEY_AD_START_DATE + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(data.getStart_time())});

        return updateActivityData(ret);
    }

    private int updateActivityData(ProviderValues data) {
        ContentResolver resolver = getResolver();
        if (resolver == null)
            return FAILED;
        int count = resolver.update(data.getUri(), data.getValues(), data.getSelection(), data.getSelectionArgs());

        return count > 0 ? SUCEESS : FAILED;
    }

    public ProviderValues getCoachActivityDataProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Coach.CONTENT_URI);

        ret.setSortOrder(CoachContract.Coach.KEY_CA_START_TIME + " ASC");

        return ret;
    }

    public CoachActivityData getCoachActivityDataNeedUploadProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Coach.CONTENT_URI);

        ret.setSortOrder("LIMIT 1");

        ContentResolver resolver = getResolver();
        if (resolver == null)
            return null;
        Cursor cursor = resolver.query(ret.getUri(), ret.getProjection(), ret.getSelection(), ret.getSelectionArgs(), ret.getSortOrder());

        return getCoachActivityDataNeedUpload(cursor);
    }

    private CoachActivityData getCoachActivityDataNeedUpload(Cursor cursor) {
        CoachActivityData result = new CoachActivityData();
        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result.setIndex(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_INDEX)));
            result.setVideoIdx(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_VIDEO_IDX)));
            result.setVideo_full_count(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_VIDEO_FULL_COUNT)));
            result.setExer_idx(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_EXER_IDX)));
            result.setExer_count(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_EXER_COUNT)));
            result.setStart_time(cursor.getLong(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_START_TIME)));
            result.setEnd_time(cursor.getLong(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_END_TIME)));
            result.setConsume_calorie(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_CONSUME_CALORIE)));
            result.setCount(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_COUNT)));
            result.setCount_percent(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_COUNT_PERCENT)));
            result.setPerfect_count(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_PERFECT_COUNT)));
            result.setMinAccuracy(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_MIN_ACCURACY)));
            result.setMaxAccuracy(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_MAX_ACCURACY)));
            result.setAvgAccuracy(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_AVG_ACCURACY)));
            result.setMinHeartRate(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_MIN_HEARTRATE)));
            result.setMaxHeartRate(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_MAX_HEARTRATE)));
            result.setAvgHeartRate(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_AVG_HEARTRATE)));
            result.setCmpHeartRate(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_CMP_HEARTRATE)));
            result.setPoint(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_POINT)));
            result.setReserved_1(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_EXER_RESERVED_1)));
            result.setReserved_2(cursor.getInt(cursor.getColumnIndex(CoachContract.Coach.KEY_CA_EXER_RESERVED_2)));
        }

        cursor.close();

        return result;
    }

    public ProviderValues getActivityDataProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);

        ret.setSortOrder(CoachContract.Fitness.KEY_AD_START_DATE + " ASC");

        return ret;
    }

    public ActivityData getActivityDataProvider(long start_date) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);
        ret.setSelection(CoachContract.Fitness.KEY_AD_START_DATE + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(start_date)});

        ContentResolver resolver = getResolver();
        if (resolver == null)
            return null;
        Cursor cursor = resolver.query(ret.getUri(), ret.getProjection(), ret.getSelection(), ret.getSelectionArgs(), ret.getSortOrder());

        return getActivityData(cursor);
    }

    private ActivityData getActivityData(Cursor cursor) {
        ActivityData result = new ActivityData();
        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result.setIndex(cursor.getInt(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INDEX)));
            result.setLabel(cursor.getInt(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_LABEL)));
            result.setAct_calorie(cursor.getDouble(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_CALORIE)));
            result.setStart_time(cursor.getLong(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_START_DATE)));
            result.setEnd_time(cursor.getLong(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_END_DATE)));
            result.setIntensityL(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INTENSITY_L)));
            result.setIntensityM(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INTENSITY_M)));
            result.setIntensityH(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INTENSITY_H)));
            result.setIntensityD(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INTENSITY_D)));
            result.setMaxHR(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_MAXHR)));
            result.setMinHR(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_MINHR)));
            result.setAvgHR(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_AVGHR)));
            result.setUpload(cursor.getInt(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_UPLOAD)));
        }

        cursor.close();

        return result;
    }

    public ProviderValues getActivityDataProvider(int index) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);
        ret.setSelection(CoachContract.Fitness.KEY_AD_INDEX + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(index)});

        return ret;
    }

    public ActivityData getActivityDataNeedUploadProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);
        ret.setSelection(CoachContract.Fitness.KEY_AD_UPLOAD + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(NONSET_UPLOAD)});
        ret.setSortOrder(CoachContract.Fitness.KEY_AD_START_DATE + " ASC LIMIT 1");

        ContentResolver resolver = getResolver();
        if (resolver == null)
            return null;
        Cursor cursor = resolver.query(ret.getUri(), ret.getProjection(), ret.getSelection(), ret.getSelectionArgs(), ret.getSortOrder());

        return getActivityDataNeedUpload(cursor);
    }

    private ActivityData getActivityDataNeedUpload(Cursor cursor) {
        ActivityData result = new ActivityData();
        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result.setIndex(cursor.getInt(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INDEX)));
            result.setLabel(cursor.getInt(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_LABEL)));
            result.setAct_calorie(cursor.getDouble(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_CALORIE)));
            result.setStart_time(cursor.getLong(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_START_DATE)));
            result.setEnd_time(cursor.getLong(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_END_DATE)));
            result.setIntensityL(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INTENSITY_L)));
            result.setIntensityM(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INTENSITY_M)));
            result.setIntensityH(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INTENSITY_H)));
            result.setIntensityD(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_INTENSITY_D)));
            result.setMaxHR(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_MAXHR)));
            result.setMinHR(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_MINHR)));
            result.setAvgHR(cursor.getShort(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_AVGHR)));
            result.setUpload(cursor.getInt(cursor.getColumnIndex(CoachContract.Fitness.KEY_AD_UPLOAD)));
        }

        cursor.close();

        return result;
    }

    public int deleteCoachActivityDataProvider(int index) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Coach.CONTENT_URI);
        ret.setSelection(CoachContract.Coach.KEY_CA_INDEX + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(index)});

        ContentResolver resolver = getResolver();
        if (resolver == null)
            return FAILED;
        int count = resolver.delete(ret.getUri(), ret.getSelection(), ret.getSelectionArgs());
        return count > 0 ? SUCEESS : FAILED;
    }

    public int deleteCoachActivityDataProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Coach.CONTENT_URI);

        ContentResolver resolver = getResolver();
        if (resolver == null)
            return FAILED;
        int count = resolver.delete(ret.getUri(), ret.getSelection(), ret.getSelectionArgs());
        return count > 0 ? SUCEESS : FAILED;
    }

    public int deleteActivityDataProvider(int index) {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);
        ret.setSelection(CoachContract.Fitness.KEY_AD_INDEX + "=?");
        ret.setSelectionArgs(new String[]{String.valueOf(index)});

        return deleteActivityData(ret);
    }

    private int deleteActivityData(ProviderValues data) {
        ContentResolver resolver = getResolver();
        if (resolver == null)
            return FAILED;
        int count = resolver.delete(data.getUri(), data.getSelection(), data.getSelectionArgs());
        return count > 0 ? SUCEESS : FAILED;
    }

    public int deleteActivityDataProvider() {
        ProviderValues ret = new ProviderValues();
        ret.setUri(CoachContract.Fitness.CONTENT_URI);

        ContentResolver resolver = getResolver();
        if (resolver == null)
            return FAILED;
        int count = resolver.delete(ret.getUri(), ret.getSelection(), ret.getSelectionArgs());
        return count > 0 ? SUCEESS : FAILED;
    }

    public static ContentResolver getResolver() {
        return ActivityMain.MainContext == null ? null : ActivityMain.MainContext.getContentResolver();
    }
}
