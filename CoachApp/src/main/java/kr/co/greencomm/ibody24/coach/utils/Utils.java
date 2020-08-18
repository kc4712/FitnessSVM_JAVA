package kr.co.greencomm.ibody24.coach.utils;

import android.util.Log;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Calendar;

/**
 * Created by young on 2015-10-06.
 */
public class Utils {

    private final static String TAG = "Utils";

    /**
     * 입력된 스트링을 UTF-8로 엔코딩 하여 출력한다.
     * @param str 입력 문자열
     * @return 엔코딩된 문자열
     */
    public static String encodeString(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        }
        catch (Exception ex) {}
        return "";
    }

    /**
     * JSON 객체에서 특정 항목을 문자열로 반환 한다.
     * @param obj JSON 객체
     * @param key 원하는 항목 이름
     * @return 찾은 항목의 문자열 값
     */
    public static String getJsonValue(JSONObject obj, String key) {
        String result = null;
        try {
            result = obj.getString(key);
            if (result.equals("null")) {
                result = null;
            }
            //Log.d(TAG, "getJsonValue Key: " + key + ", Result: " + result);
        }
        catch (Exception e) {
            Log.d(TAG, "getJsonValue Exception: " + e);
            result = null;
        }
        return result;
    }

    /**
     * long 형식의 밀리세크를 날자 형식으로 변환
     */
    public static Calendar LongToDate(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal;
    }

    /**
     * 닷넷 서버에서 반환된 날자를 자바 날자 형식으로 변환 한다.
     * @param strValue 닷넷의 날자 값
     * @return 자바 날자 값
     */
    public static Calendar convertDate(String strValue) {
        String timeString = strValue.substring(strValue.indexOf("(") + 1, strValue.indexOf(")"));
        String[] timeSegments = timeString.split("\\+");
        int timeZoneOffSet = Integer.valueOf(timeSegments[1]) * 36000; // (("0100" / 100) * 3600 * 1000)
        long millis = Long.valueOf(timeSegments[0]);
        //millis += timeZoneOffSet; // +9시간되어서 막았음.
        return LongToDate(millis);
    }

}
