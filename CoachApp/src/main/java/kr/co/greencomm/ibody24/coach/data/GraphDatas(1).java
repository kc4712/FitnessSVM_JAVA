package kr.co.greencomm.ibody24.coach.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.webs.QueryParser;
import kr.co.greencomm.middleware.utils.Convert;
import kr.co.greencomm.ibody24.coach.webs.QueryListener;
import kr.co.greencomm.ibody24.coach.webs.QueryStatus;
import kr.co.greencomm.ibody24.coach.webs.QueryThread;


/**
 * Created by young on 2015-09-06.
 */
public class GraphDatas implements QueryParser
{
    private static final String TAG = "GraphDatas";

    //private ArrayList<Integer> m_datas;
    private int[] m_week_datas = new int[7];
    private int[] m_year_datas = new int[12];

    //public ArrayList<Integer> getDatas() {return m_datas; }
    public int[] getWeekDatas() {return m_week_datas; }
    public int[] getYearDatas() {return m_year_datas; }

    public void parse(QueryCode queryCode, JSONArray arr) throws Exception {
        //m_datas = new ArrayList<>();
        int cnt = arr.length();
        for (int i=0; i<cnt; i++) {
            int dat = arr.getInt(i);
            //m_datas.add(dat);
            if (queryCode == QueryCode.WeekData) {
                m_week_datas[i] = dat;
            }
            else {
                m_year_datas[i] = dat;
            }
        }
        Log.d(TAG, "===================================================");
        if (queryCode == QueryCode.WeekData) {
            Log.d(TAG, "Week Info");
            Log.d(TAG, "---------------------------------------------------");
            Log.d(TAG, "Datas: " + m_week_datas.toString());
        }
        else {
            Log.d(TAG, "Year Info");
            Log.d(TAG, "---------------------------------------------------");
            Log.d(TAG, "Datas: " + m_year_datas.toString());
        }
        Log.d(TAG, "===================================================");
    }

    public String makeRequestToday(QueryCode queryCode, UUID user, int part) {
        StringBuilder sb = new StringBuilder(QueryThread.SERVER_URL);
        sb.append(queryCode.name());
        sb.append("?user=").append(user.toString());
        Calendar now = new GregorianCalendar();
        sb.append("&year=").append(now.get(Calendar.YEAR));
        sb.append("&month=").append(now.get(Calendar.MONTH) + 1);
        if (queryCode == QueryCode.WeekData) {
            sb.append("&day=").append(now.get(Calendar.DAY_OF_MONTH));
        }
        sb.append("&part=").append(part);
        return sb.toString();
    }

    public synchronized void runUserQuery(QueryCode queryCode, UUID user, int part, QueryListener listener) {
        QueryThread thread = new QueryThread(queryCode, makeRequestToday(queryCode, user, part), this, listener);
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

            JSONArray arr = json.getJSONArray("Datas");
            if (arr != null) {
                parse(queryCode, arr);
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
