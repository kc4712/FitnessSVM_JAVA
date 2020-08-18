package kr.co.greencomm.middleware.provider;

import android.net.Uri;

/**
 * Created by jeyang on 2016-08-31.
 */
public class CoachContract {
    public static final String AUTHORITY = "kr.co.greencomm.ibody24";
    public static class Coach {
        public static final Uri CONTENT_URI = Uri.parse("content://kr.co.greencomm.ibody24/coach");

        public static final String PATH = "coach";

        public static final String KEY_CA_INDEX = "ca_index";
        public static final String KEY_CA_VIDEO_IDX = "ca_video_idx";
        public static final String KEY_CA_VIDEO_FULL_COUNT = "ca_video_full_count";
        public static final String KEY_CA_EXER_IDX = "ca_exer_idx";
        public static final String KEY_CA_EXER_COUNT = "ca_exer_count";
        public static final String KEY_CA_START_TIME = "ca_start_time";
        public static final String KEY_CA_END_TIME = "ca_end_time";
        public static final String KEY_CA_CONSUME_CALORIE = "ca_consume_calorie";
        public static final String KEY_CA_COUNT = "ca_count";
        public static final String KEY_CA_COUNT_PERCENT = "ca_count_percent";
        public static final String KEY_CA_PERFECT_COUNT = "ca_perfect_count";
        public static final String KEY_CA_MIN_ACCURACY = "ca_min_accuracy";
        public static final String KEY_CA_MAX_ACCURACY = "ca_max_accuracy";
        public static final String KEY_CA_AVG_ACCURACY = "ca_avg_accuracy";
        public static final String KEY_CA_MIN_HEARTRATE = "ca_min_heartrate";
        public static final String KEY_CA_MAX_HEARTRATE = "ca_max_heartrate";
        public static final String KEY_CA_AVG_HEARTRATE = "ca_avg_heartrate";
        public static final String KEY_CA_CMP_HEARTRATE = "ca_compared_heartrate";
        public static final String KEY_CA_POINT = "ca_point";
        public static final String KEY_CA_EXER_RESERVED_1 = "ca_reserved_1";
        public static final String KEY_CA_EXER_RESERVED_2 = "ca_reserved_2";
    }
    public static class Fitness {
        public static final Uri CONTENT_URI = Uri.parse("content://kr.co.greencomm.ibody24/fitness");

        public static final String PATH = "fitness";

        public static final String KEY_AD_INDEX = "cd_index";
        public static final String KEY_AD_LABEL = "cd_label";
        public static final String KEY_AD_CALORIE = "cd_calorie";
        public static final String KEY_AD_START_DATE = "cd_start_date";
        public static final String KEY_AD_END_DATE = "cd_end_date";
        public static final String KEY_AD_INTENSITY_L = "cd_intensity_L";
        public static final String KEY_AD_INTENSITY_M = "cd_intensity_M";
        public static final String KEY_AD_INTENSITY_H = "cd_intensity_H";
        public static final String KEY_AD_INTENSITY_D = "cd_intensity_D";
        public static final String KEY_AD_MAXHR = "cd_max_hr";
        public static final String KEY_AD_MINHR = "cd_min_hr";
        public static final String KEY_AD_AVGHR = "cd_avg_hr";
        public static final String KEY_AD_UPLOAD = "cd_upload";
    }
    public static class IndexTime {
        public static final Uri CONTENT_URI = Uri.parse("content://kr.co.greencomm.ibody24/index_time");

        public static final String PATH = "index_time";

        public static final String KEY_IS_INDEX = "is_index";
        public static final String KEY_IS_LABEL = "is_label";
        public static final String KEY_IS_START_TIME = "is_start_time";
        public static final String KEY_IS_END_TIME = "is_end_time";
    }
}
