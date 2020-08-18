package kr.co.greencomm.ibody24.coach.data;

import android.util.Log;

import org.json.JSONObject;

import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.provider.CoachResolver;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.webs.QueryParser;
import kr.co.greencomm.ibody24.coach.webs.QueryStatus;
import kr.co.greencomm.ibody24.coach.webs.QueryThread;
import kr.co.greencomm.ibody24.coach.webs.WebResponse;
import kr.co.greencomm.middleware.utils.Convert;
import kr.co.greencomm.ibody24.coach.webs.QueryListener;
import kr.co.greencomm.middleware.utils.container.ActivityData;
import kr.co.greencomm.middleware.utils.container.CoachActivityData;


/**
 * Created by young on 2015-09-05.
 */
public class ExerciseInfo implements QueryParser {
    private static final String TAG = "ExerciseInfo";

    private Integer m_index;
    private Integer m_course;
    private Long m_begTime;
    private Long m_endTime;
    private Integer m_totalCount;
    private Integer m_matchCount;
    private Integer m_accuracy;
    private Integer m_point;
    private Integer m_calorie;
    private Integer m_maxHeart;
    private Integer m_avgHeart;
    private Integer m_minHeart;
    private Integer m_intensityL;
    private Integer m_intensityM;
    private Integer m_intensityH;
    private Integer m_intensityD;

    public ExerciseInfo() {

    }

    public ExerciseInfo(CoachActivityData info) {
        m_index = info.getIndex();
        m_course = info.getExer_idx();
        m_begTime = info.getStart_time();
        m_endTime = info.getEnd_time();
        m_totalCount = info.getExer_count();
        m_matchCount = info.getCount();
        m_accuracy = info.getAvgAccuracy();
        m_point = info.getPoint();
        m_calorie = info.getConsume_calorie();
        m_maxHeart = info.getMaxHeartRate();
        m_avgHeart = info.getAvgHeartRate();
    }

    public ExerciseInfo(ActivityData info) {
        m_index = info.getIndex();
        m_course = info.getLabel();
        m_begTime = info.getStart_time();
        m_endTime = info.getEnd_time();
        m_calorie = (int) (info.getAct_calorie() * 1000);
        m_maxHeart = (int) info.getMaxHR();
        m_minHeart = (int) info.getMinHR();
        m_avgHeart = (int) info.getAvgHR();
        m_intensityL = (int) info.getIntensityL();
        m_intensityM = (int) info.getIntensityM();
        m_intensityH = (int) info.getIntensityH();
        m_intensityD = (int) info.getIntensityD();
    }

    public void setExerciseInfo(CoachActivityData info) {
        m_index = info.getIndex();
        m_course = info.getExer_idx();
        m_begTime = info.getStart_time();
        m_endTime = info.getEnd_time();
        m_totalCount = info.getExer_count();
        m_matchCount = info.getCount();
        m_accuracy = info.getAvgAccuracy();
        m_point = info.getPoint();
        m_calorie = info.getConsume_calorie();
        m_maxHeart = info.getMaxHeartRate();
        m_avgHeart = info.getAvgHeartRate();
    }

    public void setActivityInfo(ActivityData info) {
        m_index = info.getIndex();
        m_course = info.getLabel();
        m_begTime = info.getStart_time();
        m_endTime = info.getEnd_time();
        m_calorie = (int) (info.getAct_calorie() * 1000);
        m_maxHeart = (int) info.getMaxHR();
        m_minHeart = (int) info.getMinHR();
        m_avgHeart = (int) info.getAvgHR();
        m_intensityL = (int) info.getIntensityL();
        m_intensityM = (int) info.getIntensityM();
        m_intensityH = (int) info.getIntensityH();
        m_intensityD = (int) info.getIntensityD();
    }

    public Integer getIndex() {
        return m_index;
    }

    public void setIndex(Integer m_index) {
        this.m_index = m_index;
    }

    public Integer getCourse() {
        return m_course;
    }

    public void setCourse(Integer m_course) {
        this.m_course = m_course;
    }

    public Long getBegTime() {
        return m_begTime;
    }

    public void setBegTimee(Long m_begTime) {
        this.m_begTime = m_begTime;
    }

    public Long getEndTime() {
        return m_endTime;
    }

    public void setEndTime(Long m_endTime) {
        this.m_endTime = m_endTime;
    }

    public Integer getTotalCount() {
        return m_totalCount;
    }

    public void setTotalCount(Integer m_totalCount) {
        this.m_totalCount = m_totalCount;
    }

    public Integer getMatchCount() {
        return m_matchCount;
    }

    public void setMatchCount(Integer m_matchCount) {
        this.m_matchCount = m_matchCount;
    }

    public Integer getAccuracy() {
        return m_accuracy;
    }

    public void setAccuracy(Integer m_accuracy) {
        this.m_accuracy = m_accuracy;
    }

    public Integer getPoint() {
        return m_point;
    }

    public void setPoint(Integer m_point) {
        this.m_point = m_point;
    }

    public Integer getCalorie() {
        return m_calorie;
    }

    public void setCalorie(Integer m_calorie) {
        this.m_calorie = m_calorie;
    }

    public Integer getMaxHeart() {
        return m_maxHeart;
    }

    public void setMaxHeart(Integer m_maxHeart) {
        this.m_maxHeart = m_maxHeart;
    }

    public Integer getAvgHeart() {
        return m_avgHeart;
    }

    public void setAvgHeart(Integer m_avgHeart) {
        this.m_avgHeart = m_avgHeart;
    }

    public Integer getMinHeart() {
        return m_minHeart;
    }

    public void setMinHeart(Integer m_minHeart) {
        this.m_minHeart = m_minHeart;
    }

    public Integer getIntensityL() {
        return m_intensityL;
    }

    public void setIntensityL(Integer m_intensityL) {
        this.m_intensityL = m_intensityL;
    }

    public Integer getIntensityM() {
        return m_intensityM;
    }

    public void setIntensityM(Integer m_intensityM) {
        this.m_intensityM = m_intensityM;
    }

    public Integer getIntensityH() {
        return m_intensityH;
    }

    public void setIntensityH(Integer m_intensityH) {
        this.m_intensityH = m_intensityH;
    }

    public Integer getIntensityD() {
        return m_intensityD;
    }

    public void setIntensityD(Integer m_intensityD) {
        this.m_intensityD = m_intensityD;
    }

    public static String makeRequestInsert(QueryCode queryCode, ExerciseInfo info) {
        StringBuilder sb = new StringBuilder(QueryThread.SERVER_URL);

        switch (queryCode) {
            case InsertExercise:
                sb.append(QueryCode.InsertExercise.name());
                sb.append("?User=").append(CoachBaseActivity.DB_User.getCode().toString());
                sb.append("&Course=").append(info.m_course);
                sb.append("&Begtime=").append(info.m_begTime);
                sb.append("&Endtime=").append(info.m_endTime);
                sb.append("&Total=").append(info.m_totalCount);
                sb.append("&Match=").append(info.m_matchCount);
                sb.append("&Accuracy=").append(info.m_accuracy);
                sb.append("&Point=").append(info.m_point);
                sb.append("&Calorie=").append(info.m_calorie);
                sb.append("&Maxrate=").append(info.m_maxHeart);
                sb.append("&Avgrate=").append(info.m_avgHeart);
                break;
            case InsertFitness:
                sb.append(QueryCode.InsertExercise2.name());
                sb.append("?User=").append(CoachBaseActivity.DB_User.getCode().toString());
                sb.append("&Course=").append(info.m_course);
                sb.append("&Begtime=").append(info.m_begTime);
                sb.append("&Endtime=").append(info.m_endTime);
                sb.append("&Accuracy=").append(info.m_accuracy);
                sb.append("&Point=").append(info.m_point);
                sb.append("&Calorie=").append(info.m_calorie);
                sb.append("&Maxrate=").append(info.m_maxHeart);
                sb.append("&Avgrate=").append(info.m_avgHeart);
                sb.append("&Minrate=").append(info.m_minHeart);
                sb.append("&Inten1=").append(info.m_intensityL);
                sb.append("&Inten2=").append(info.m_intensityM);
                sb.append("&Inten3=").append(info.m_intensityH);
                sb.append("&Inten4=").append(info.m_intensityD);
                break;
        }

        return sb.toString();
    }

    public synchronized void runUserQueryInsert(QueryCode queryCode, QueryListener listener) {
        String request = makeRequestInsert(queryCode, this);
        QueryThread thread = new QueryThread(queryCode, request, this, listener);
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

            return QueryStatus.Success;
        } catch (Exception e) {
            Log.d(TAG, "onQueryParse catch error: " + e.toString());
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

        if (queryCode == QueryCode.InsertExercise) {
            CoachResolver res = new CoachResolver();
            res.deleteCoachActivityDataProvider(m_index);
        }
    }

    @Override
    public void OnQueryFail(QueryStatus queryStatus) {
        if (m_web_resp != null) {
            m_web_resp.onFail();
        }
    }
}
