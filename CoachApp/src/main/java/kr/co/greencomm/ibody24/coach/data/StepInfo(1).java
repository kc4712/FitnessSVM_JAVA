package kr.co.greencomm.ibody24.coach.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.activity.ActivityMain;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.webs.QueryParser;
import kr.co.greencomm.ibody24.coach.webs.QueryStatus;
import kr.co.greencomm.ibody24.coach.webs.QueryThread;
import kr.co.greencomm.ibody24.coach.webs.WebResponse;
import kr.co.greencomm.ibody24.coach.utils.Utils;
import kr.co.greencomm.ibody24.coach.webs.QueryListener;
import kr.co.greencomm.middleware.bluetooth.BluetoothCommand;
import kr.co.greencomm.middleware.bluetooth.RequestAction;
import kr.co.greencomm.middleware.utils.Convert;

/**
 * Created by jeyang on 16. 9. 22..
 */
public class StepInfo implements QueryParser {
    private static final String tag = StepInfo.class.getSimpleName();
    private Integer m_index;
    private Long m_reg;
    private Integer m_step;

    public ArrayList<Integer> m_stepArray = new ArrayList<>();

    public Integer getIndex() {
        return m_index;
    }

    public void setIndex(Integer m_index) {
        this.m_index = m_index;
    }

    public Long getReg() {
        return m_reg;
    }

    public void setReg(Long m_reg) {
        this.m_reg = m_reg;
    }

    public Integer getStep() {
        return m_step;
    }

    public void setStep(Integer m_step) {
        this.m_step = m_step;
    }

    public static String makeRequest(QueryCode queryCode, StepInfo info) {
        StringBuilder sb = new StringBuilder(QueryThread.SERVER_URL);
        sb.append(queryCode.name());

        switch (queryCode) {
            case ListStep:
                sb.append("?User=").append(CoachBaseActivity.DB_User.getCode().toString());
                break;
            case InsertStep:
                sb.append("?User=").append(CoachBaseActivity.DB_User.getCode().toString());
                sb.append("&Reg=").append(info.m_reg);
                sb.append("&Step=").append(info.m_step);
                break;
        }

        return sb.toString();
    }

    public synchronized void runQuery(QueryCode queryCode, QueryListener listener) {
        String request = makeRequest(queryCode, this);
        QueryThread thread = new QueryThread(queryCode, request, this, listener);
        thread.start();
    }


    @Override
    public QueryStatus onParse(QueryCode queryCode, String request, String result, QueryListener listener) {
        String str;
        try {
            JSONObject json = new JSONObject(result);
            Log.d(tag, "웹 반환 값 파싱");
            Log.d(tag, "=====================================================");
            Log.d(tag, "Query Result Json: " + json);

            if (queryCode == QueryCode.ListStep) {
                JSONArray arr = json.getJSONArray("Items");
                if (arr != null) {
                    m_stepArray.clear();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject dat = arr.getJSONObject(i);
                        m_reg = Utils.convertDate(Convert.getJsonValue(dat, "RegDate")).getTimeInMillis();
                        m_step = Convert.getInt(dat, "Step");

                        m_stepArray.add(m_step);
                    }
                }

                return QueryStatus.Success;
            }

            if ((str = Convert.getJsonValue(json, "Result")) == null) {
                return QueryStatus.ERROR_Result_Parse;
            }
            if ("0".equals(str)) {
                return QueryStatus.Service_Error;
            }

            if (queryCode == QueryCode.InsertStep) {
                String date = Convert.getJsonValue(json, "LastStepDate");
                if (date != null) {
                    long nowDate = System.currentTimeMillis();
                    long getDate = Utils.convertDate(date).getTimeInMillis();

                    long diff = nowDate - getDate;
                    long milli_today = 86400000L;
                    if (diff / milli_today > 1) {
                        long len = diff / milli_today;
                        Log.d(tag, "len :: " + len);
                        for (int i = 0; i < len; i++) {
                            SendReceive.appendBluetoothMessage(ActivityMain.MainContext, BluetoothCommand.StepCount_Calorie,
                                    getDate + (i * milli_today), RequestAction.Start, true);
                        }
                    }
                }
            }

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
}
