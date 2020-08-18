package kr.co.greencomm.middleware.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by young on 2015-09-04.
 */
public class Convert {

    private final static String TAG = "Convert";

    public static String getJsonValue(JSONObject obj, String key) {
        String result = null;
        try {
            result = obj.getString(key);
//            Log.d(TAG, "getJson " + key + ": " + result);
            if (result.equals("null")) {
                result = null;
            }
        } catch (Exception e) {
            Log.d(TAG, "getJson Exception: " + e);
            result = null;
        }
        return result;
    }

    public static int getInt(JSONObject json, String key) {
        if (getJsonValue(json, key) == null) {
            return 0;
        }
        return Integer.parseInt(getJsonValue(json, key));
    }

    public static long getLong(JSONObject json, String key) {
        if (getJsonValue(json, key) == null) {
            return 0;
        }
        return Long.parseLong(getJsonValue(json, key));
    }

    public static double getDouble(JSONObject json, String key) {
        if (getJsonValue(json, key) == null) {
            return 0;
        }
        return Double.parseDouble(getJsonValue(json, key));
    }

    private String DatetoString(Date d) {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        return sdFormat.format(d);
    }

    private Date StringtoDate(String szDate) {
        try {
            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat sdFormat = new SimpleDateFormat(dateFormat);
            Date d = sdFormat.parse(szDate);
            return d;
        } catch (Exception e) {
        }
        return null;
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static boolean isEmpty(String...  strs) {
        for(String str : strs) {
            if (str == null || str.length() == 0) {
                return true;
            }
        }
        return false;
    }

    public static String sha1(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(input.getBytes("UTF-8"), 0, input.length());
        byte[] sha1hash = messageDigest.digest();
        return convertToHex(sha1hash);
    }

    private static final String EMAIL_PATTERN = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

    public static boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{8,16}$";

    public static boolean isValidPassword(final String hex) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public static int CONNECT_TYPE_WIFI = 1;
    public static int CONNECT_TYPE_MOBILE = 2;
    public static int CONNECT_TYPE_NOT_CONNECTED = 0;

    public static int getConnectivityStatus(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(null != activeNetwork){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                return CONNECT_TYPE_WIFI;
            }
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return CONNECT_TYPE_MOBILE;
        }
        return CONNECT_TYPE_NOT_CONNECTED;
    }
}
