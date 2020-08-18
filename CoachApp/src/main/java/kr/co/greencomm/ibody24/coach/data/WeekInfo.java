package kr.co.greencomm.ibody24.coach.data;

import android.util.Log;

import org.json.JSONObject;

import kr.co.greencomm.middleware.utils.Convert;

/**
 * Created by young on 2015-09-06.
 */
public class WeekInfo
{
    private static final String TAG = "WeekInfo";

    private int m_accuracy;
    private int m_calorie;
    private int m_heartRateMax;
    private int m_heartRateAvg;
    private int m_totalCount;
    private int m_matchCount;
    private int m_point;

    public void setAccuracy(int value) { m_accuracy = value; }
    public void setCalorie(int value) { m_calorie = value; }
    public void setHeartRateMax(int value) { m_heartRateMax = value; }
    public void setHeartRateAvg(int value) { m_heartRateAvg = value; }
    public void setTotalCount(int value) { m_totalCount = value; }
    public void setMatchCount(int value) { m_matchCount = value; }
    public void setPoint(int value) { m_point = value; }

    public int getAccuracy() { return m_accuracy; }
    public int getCalorie() { return m_calorie; }
    public int getHeartRateMax() { return m_heartRateMax; }
    public int getHeartRateAvg() { return m_heartRateAvg; }
    public int getTotalCount() { return m_totalCount; }
    public int getMatchCount() { return m_matchCount; }
    public int getPoint() { return m_point; }

    public void parse(JSONObject json) throws Exception {
        String str;
        if ((str = Convert.getJsonValue(json, "Accuracy")) != null) this.m_accuracy = Integer.parseInt(str);
        if ((str = Convert.getJsonValue(json, "Calorie")) != null) this.m_calorie = Integer.parseInt(str);
        if ((str = Convert.getJsonValue(json, "HeartRateMax")) != null) this.m_heartRateMax = Integer.parseInt(str);
        if ((str = Convert.getJsonValue(json, "HeartRateAvg")) != null) this.m_heartRateAvg = Integer.parseInt(str);
        if ((str = Convert.getJsonValue(json, "TotalCount")) != null) this.m_totalCount = Integer.parseInt(str);
        if ((str = Convert.getJsonValue(json, "MatchCount")) != null) this.m_matchCount = Integer.parseInt(str);
        if ((str = Convert.getJsonValue(json, "Point")) != null) this.m_point = Integer.parseInt(str);
        Log.d(TAG, "===================================================");
        Log.d(TAG, "Week Info");
        Log.d(TAG, "---------------------------------------------------");
        Log.d(TAG, "Accuracy: " + m_accuracy);
        Log.d(TAG, "Calorie: " + m_calorie);
        Log.d(TAG, "HeartRateMax: " + m_heartRateMax);
        Log.d(TAG, "HeartRateAvg: " + m_heartRateAvg);
        Log.d(TAG, "TotalCount: " + m_totalCount);
        Log.d(TAG, "MatchCount: " + m_matchCount);
        Log.d(TAG, "Point: " + m_point);
        Log.d(TAG, "===================================================");
    }
}
