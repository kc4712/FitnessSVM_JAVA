package kr.co.greencomm.middleware.utils.container;


import android.util.Log;

import kr.co.greencomm.middleware.utils.FirmVersion;
import kr.co.greencomm.middleware.video.IViewComment;

public final class DataBase {
    private static DataBase mMainInstance = null;

    private static IViewComment mIView;
    private static FirmVersion mFirmVersion;

    public static void registFirmCallback(FirmVersion cb) {
        mFirmVersion = cb;
    }

    public static void unregistFirmCallback() {
        mFirmVersion = null;
    }

    public static void registCallback(IViewComment cb) {
        mIView = cb;
    }

    public static void unregistCallback() {
        mIView = null;
    }

    public void resetVideoVariable() {
        activityName = "";
        bottomComment = "";
        totalScore = new TotalScoreData();
        accuracy = 0;
        point = 0;
        count = new Integer[2];
        calorie = 0f;
        HRCmp = 0;
        HRWarnning = "";
        coachActivityData = new CoachActivityData();
    }

    public void resetTotalScore() {
        totalScore = new TotalScoreData();
    }

    private DataBase() {
        profile = new UserProfile();
        battery = new Battery();
        activityData = new ActivityData();
        sleepData = new SleepData();

        resetVideoVariable();
    }

    public static DataBase getInstance() {
        if (mMainInstance == null) {
            mMainInstance = new DataBase();
        }
        return mMainInstance;
    }

    /************ User ****************/
    private UserProfile profile;
    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    private Integer dietPeriod = 0;
    public Integer getDietPeriod() {
        return dietPeriod;
    }

    public void setDietPeriod(Integer dietPeriod) {
        this.dietPeriod = dietPeriod;
    }

    private Short heartrate_stable = 0;
    public Short getHeartrate_stable() {
        return heartrate_stable;
    }

    public void setHeartrate_stable(Short heartrate_stable) {
        this.heartrate_stable = heartrate_stable;
    }

    /************ Bluetooth ****************/
    private Battery battery = new Battery();
    public Battery getBattery() {
        return battery;
    }

    public void setBattery(Battery battery) {
        this.battery = battery;
    }

    private Short step = 0;
    public Short getStep() {
        return step;
    }

    public void setStep(Short step) {
        this.step = step;
    }

    private Double total_activity_calorie = 0.0, total_sleep_calorie = 0.0, total_daily_calorie = 0.0, total_coach_calorie = 0.0;
    public Double getTotal_activity_calorie() {
        return total_activity_calorie;
    }

    public void setTotal_activity_calorie(Double total_activity_calorie) {
        this.total_activity_calorie = total_activity_calorie;
    }

    public Double getTotal_sleep_calorie() {
        return total_sleep_calorie;
    }

    public void setTotal_sleep_calorie(Double total_sleep_calorie) {
        this.total_sleep_calorie = total_sleep_calorie;
    }

    public Double getTotal_daily_calorie() {
        return total_daily_calorie;
    }

    public void setTotal_daily_calorie(Double total_daily_calorie) {
        this.total_daily_calorie = total_daily_calorie;
    }

    public Double getTotal_coach_calorie() {
        return total_coach_calorie;
    }

    public void setTotal_coach_calorie(Double total_coach_calorie) {
        this.total_coach_calorie = total_coach_calorie;
    }

    private ActivityData activityData;
    public ActivityData getActivityData() {
        return activityData;
    }

    public void setActivityData(ActivityData activityData) {
        this.activityData = activityData;
    }

    private SleepData sleepData;
    public SleepData getSleepData() {
        return sleepData;
    }

    public void setSleepData(SleepData sleepData) {
        this.sleepData = sleepData;
    }

    private Short stress = 0;
    public Short getStress() {
        return stress;
    }

    public void setStress(Short stress) {
        this.stress = stress;
    }

    private Short state = 0;
    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    private String version = "";
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        mFirmVersion.onFirmVersion();
    }

    /**************** Video ****************/
    private Boolean showUi;
    public Boolean getShowUi() {
        return showUi;
    }

    public void setShowUi(Boolean showUi) {
        this.showUi = showUi;
        mIView.onShowUI();
    }

    private String activityName = "";
    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        if (this.activityName.equals(activityName) || activityName.equals("")) {
            return;
        }
        this.activityName = activityName;
        mIView.onTopComment();
    }

    private String bottomComment = "";
    public String getBottomComment() {
        return bottomComment;
    }

    public void setBottomComment(String bottomComment) {
        if (bottomComment.equals("")) {
            return;
        }
        this.bottomComment = bottomComment;
        mIView.onBottomComment();
    }

    private TotalScoreData totalScore;
    public TotalScoreData getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(TotalScoreData totalScore) {
        if (this.totalScore.equals(totalScore)) {
            return;
        }
        this.totalScore = totalScore;
        mIView.onTotalScore();
    }

    private Integer accuracy = 0;
    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        if (this.accuracy == accuracy) {
            return;
        }
        this.accuracy = accuracy;
        mIView.onMainUi();
    }

    private Integer point = 0;
    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        if (this.point == point) {
            return;
        }
        this.point = point;
        mIView.onMainUi();
    }

    private Integer[] count = new Integer[2];

    /**
     *
     * @return [count, refCount]
     */
    public Integer[] getCount() {
        return count;
    }

    public void setCount(Integer[] count) {
        if (this.count.equals(count)) {
            return;
        }
        this.count = count;
        mIView.onMainUi();
    }

    private Float calorie = 0f;
    public Float getCalorie() {
        return calorie;
    }

    public void setCalorie(Float calorie) {
        if (this.calorie.compareTo(calorie) == 0) {
            return;
        }
        this.calorie = calorie;
        mIView.onMainUi();
    }

    private Integer HRCmp = 0;
    public Integer getHRCmp() {
        return HRCmp;
    }

    public void setHRCmp(Integer HRCmp) {
        if (this.HRCmp != null && this.HRCmp == HRCmp) {
            return;
        }
        this.HRCmp = HRCmp;
        mIView.onMainUi();
    }

    private String HRWarnning = "";
    public String getHRWarnning() {
        return HRWarnning;
    }

    public void setHRWarnning(String HRWarnning) {
        if (HRWarnning.equals("")) {
            return;
        }
        this.HRWarnning = HRWarnning;
        mIView.onWarnning();
    }

    private CoachActivityData coachActivityData;
    public CoachActivityData getCoachActivityData() {
        return coachActivityData;
    }

    public void setCoachActivityData(CoachActivityData coachActivityData) {
        this.coachActivityData = coachActivityData;
        mIView.onExerData();
    }



    //*** 규창
    //*************************코치 노말 스트레스 용 ***********************
    /* 참고용 코드
    private Short stress;
    public Short getStress() {
        return stress;
    }

    public void setStress(Short stress) {
        this.stress = stress;
    }*/
    private Short mStressResult;
    public Short getStressResult() { return mStressResult; }

    public void setStressResult(Short stressResult) {
        this.mStressResult = stressResult;
        //mIView.onStressInform();
    }
    //*************************코치 노말 스트레스 용 ***********************
}