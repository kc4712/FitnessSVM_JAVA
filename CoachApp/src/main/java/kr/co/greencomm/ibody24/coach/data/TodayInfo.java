package kr.co.greencomm.ibody24.coach.data;

import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.webs.QueryParser;
import kr.co.greencomm.ibody24.coach.webs.QueryStatus;
import kr.co.greencomm.ibody24.coach.webs.QueryThread;
import kr.co.greencomm.middleware.utils.Convert;
import kr.co.greencomm.ibody24.coach.webs.QueryListener;


/**
 * Created by young on 2015-09-05.
 */
public class TodayInfo implements QueryParser
{
    private static final String TAG = "TodayInfo";

    private static final SimpleDateFormat dispFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private UUID m_user;
    private int m_point;
    private int m_runSec;
    private int m_calorie;
    private int m_userLevel;

    public WeekInfo Week = new WeekInfo();

    public void setUser(UUID value) { m_user = value; }
    public void setPoint(int value) { m_point = value; }
    public void setRunSec(int value) { m_runSec = value; }
    public void setCalorie(int value) { m_calorie = value; }
    public void setUserLevel(int value) { m_userLevel = value; }

    public UUID getUser() { return m_user; }
    public int getPoint() { return m_point; }
    public int getRunSec() { return m_runSec; }
    public int getCalorie() { return m_calorie; }
    public int getUserLevel() { return m_userLevel; }

    public void parse(JSONObject json) throws Exception {
        String str;
        if ((str = Convert.getJsonValue(json, "Point")) != null) this.m_point = Integer.parseInt(str);
        if ((str = Convert.getJsonValue(json, "RunSec")) != null) this.m_runSec = Integer.parseInt(str);
        if ((str = Convert.getJsonValue(json, "Calorie")) != null) this.m_calorie = Integer.parseInt(str);
        if ((str = Convert.getJsonValue(json, "UserLevel")) != null) this.m_userLevel = Integer.parseInt(str);
        Log.d(TAG, "===================================================");
        Log.d(TAG, "Today Info");
        Log.d(TAG, "---------------------------------------------------");
        Log.d(TAG, "UserCode: " + m_user.toString());
        Log.d(TAG, "Point: " + m_point);
        Log.d(TAG, "RunSec: " + m_runSec);
        Log.d(TAG, "Calorie: " + m_calorie);
        Log.d(TAG, "UserLevel: " + m_userLevel);
        Log.d(TAG, "===================================================");
    }

    public String makeRequestToday() {
        StringBuilder sb = new StringBuilder(QueryThread.SERVER_URL);
        sb.append(QueryCode.ExerciseToday.name());
        sb.append("?user=").append(m_user.toString());
        Calendar now = new GregorianCalendar();
        sb.append("&year=").append(now.get(Calendar.YEAR));
        sb.append("&month=").append(now.get(Calendar.MONTH) + 1);
        sb.append("&day=").append(now.get(Calendar.DAY_OF_MONTH));
        return sb.toString();
    }

    public synchronized void runUserQueryToday(QueryListener listener) {
        QueryThread thread = new QueryThread(QueryCode.ExerciseToday, makeRequestToday(), this, listener);
        thread.start();
    }

    @Override
    public QueryStatus onParse(QueryCode queryCode, String request, String result, QueryListener listener) {
        String str;
        try {
            JSONObject json = new JSONObject(result);
            Log.d(TAG, "웹 반환 값 파싱");
            Log.d(TAG, "=====================================================");
            Log.d(TAG, "Query Result Json: " + json);

            if ((str = Convert.getJsonValue(json, "Result")) == null) {
                return QueryStatus.ERROR_Result_Parse;
            }
            if ("0".equals(str)) {
                return QueryStatus.Service_Error;
            }

            if ((str = Convert.getJsonValue(json, "TodayInfo")) != null) {
                parse(json.getJSONObject("TodayInfo"));
            }
            if ((str = Convert.getJsonValue(json, "WeekInfo")) != null) {
                Week.parse(json.getJSONObject("WeekInfo"));
            }

            return QueryStatus.Success;
        }
        catch (Exception e) {
            Log.d(TAG, "onQueryParse catch error: " + e.toString());
            return QueryStatus.Catch_Error;
        }
    }

    @Override
    public void OnQuerySuccess(QueryCode queryCode) {

    }

    @Override
    public void OnQueryFail(QueryStatus queryStatus) {

    }
}
