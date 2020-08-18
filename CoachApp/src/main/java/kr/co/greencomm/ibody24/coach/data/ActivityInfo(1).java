package kr.co.greencomm.ibody24.coach.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.activity.ActivityMain;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.webs.QueryParser;
import kr.co.greencomm.ibody24.coach.webs.WebResponse;
import kr.co.greencomm.ibody24.coach.provider.CoachResolver;
import kr.co.greencomm.ibody24.coach.webs.QueryListener;
import kr.co.greencomm.ibody24.coach.webs.QueryStatus;
import kr.co.greencomm.ibody24.coach.webs.QueryThread;
import kr.co.greencomm.middleware.provider.CoachContract;
import kr.co.greencomm.middleware.utils.Convert;
import kr.co.greencomm.middleware.utils.CourseLabel;
import kr.co.greencomm.middleware.utils.container.ActivityData;

/**
 * Created by jeyang on 16. 9. 22..
 */
public class ActivityInfo implements QueryParser {
    private static final String tag = ActivityInfo.class.getSimpleName();
    private Integer m_index;
    private Integer m_label;

    private QueryThread thread;

    private ArrayList<ActivityData> list_activityInfo = new ArrayList<>();

    public Integer getIndex() {
        return m_index;
    }

    public void setIndex(Integer m_index) {
        this.m_index = m_index;
    }

    public Integer getLabel() {
        return m_label;
    }

    public void setLabel(Integer m_label) {
        this.m_label = m_label;
    }

    public static String makeRequest(QueryCode queryCode, ActivityInfo info) {
        StringBuilder sb = new StringBuilder(QueryThread.SERVER_URL);
        sb.append(queryCode.name());

        switch (queryCode) {
            case ListExercise:
                sb.append("?User=").append(CoachBaseActivity.DB_User.getCode().toString());
                sb.append("&Course=").append(info.m_label);
                break;
            case ListCalorieToday:
                sb.append("?User=").append(CoachBaseActivity.DB_User.getCode().toString());
                break;
        }

        return sb.toString();
    }

    @Override
    public QueryStatus onParse(QueryCode queryCode, String request, String result, QueryListener listener) {
        String str;
        try {
            JSONObject json = new JSONObject(result);
            Log.d(tag, "웹 반환 값 파싱");
            Log.d(tag, "=====================================================");
            Log.d(tag, "Query Result Json: " + json);

//            if ((str = Convert.getJsonValue(json, "Result")) == null) {
//                return QueryStatus.ERROR_Result_Parse;
//            }
//            if ("0".equals(str)) {
//                return QueryStatus.Service_Error;
//            }

            JSONArray arr = json.getJSONArray("Items");
            if (arr != null) {
                double data1 = 0, data2 = 0, data3 = 0, data4 = 0;

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject dat = arr.getJSONObject(i);
                    if (queryCode == QueryCode.ListCalorieToday) {
                        if (Integer.parseInt(Convert.getJsonValue(dat, "CourseCode")) == CourseLabel.Activity.getLabel()) {
                            data1 += Integer.parseInt(Convert.getJsonValue(dat, "Calorie"));
                        } else if (Integer.parseInt(Convert.getJsonValue(dat, "CourseCode")) == CourseLabel.Sleep.getLabel()) {
                            data3 += Integer.parseInt(Convert.getJsonValue(dat, "Calorie"));
                        } else if (Integer.parseInt(Convert.getJsonValue(dat, "CourseCode")) == CourseLabel.Daily.getLabel()) {
                            data4 += Integer.parseInt(Convert.getJsonValue(dat, "Calorie"));
                        }

                        for (int c : CourseLabel.CoachCourse) {
                            if (Integer.parseInt(Convert.getJsonValue(dat, "CourseCode")) == c) {
                                data2 += Integer.parseInt(Convert.getJsonValue(dat, "Calorie"));
                            }
                        }
                    } else if (queryCode == QueryCode.ListExercise) {
                        list_activityInfo.add(new ActivityData(dat));
                    }
                }
            }

            CoachResolver res = new CoachResolver();
            for (ActivityData act : list_activityInfo) {
                if (res.getActivityDataProvider(act.getStart_time()) == null) {
                    Log.d(tag, "query insert activity data start time -> "+act.getStart_time());
                    res.addActivityDataProvider(act);
                }
            }

            ActivityMain.MainContext.getContentResolver().notifyChange(CoachContract.Fitness.CONTENT_URI, null);

            list_activityInfo.clear();

            return QueryStatus.Success;
        } catch (Exception e) {
            Log.d(tag, "onQueryParse catch error: " + e.toString());
            return QueryStatus.Catch_Error;
        }
    }

    private WebResponse m_web_resp;

    public WebResponse getWeb_resp() {
        return m_web_resp;
    }

    public void setWeb_resp(WebResponse m_web_resp) {
        this.m_web_resp = m_web_resp;
    }

    @Override
    public void OnQuerySuccess(QueryCode queryCode) {
        if (m_web_resp != null) {
            m_web_resp.onSuccess();
        }
    }

    @Override
    public void OnQueryFail(QueryStatus queryStatus) {
        if (m_web_resp != null) {
            m_web_resp.onFail();
        }
    }

    public synchronized void runQuery(QueryCode queryCode, QueryListener listener) {
        try {
            thread = new QueryThread(queryCode, makeRequest(queryCode, this), this, listener);
            thread.start();
        }
        catch (Exception e) {
            thread.raiseError(QueryStatus.ERROR_Query, e.toString());
        }
    }
}
