package kr.co.greencomm.middleware.utils.container;

import org.json.JSONObject;

import kr.co.greencomm.middleware.provider.SQLHelper;
import kr.co.greencomm.middleware.utils.Convert;

/**
 * Created by jeyang on 2016-08-26.
 */
public class ActivityData {
    private Integer index = 0;
    private Integer label = 0;
    private Double act_calorie = 0.0;
    private Short intensityL = 0;
    private Short intensityM = 0;
    private Short intensityH = 0;
    private Short intensityD = 0;
    private Short minHR = 0;
    private Short maxHR = 0;
    private Short avgHR = 0;
    private Long start_time = 0L;
    private Long end_time = 0L;
    private Integer upload = 0;
    public ActivityData() {

    }

    public ActivityData(Integer index, Integer label, Double act_calorie, Short intensityL, Short intensityM, Short intensityH, Short intensityD, Short minHR, Short maxHR, Short avgHR, Long start_time, Long end_time, Integer upload) {
        this.index = index;
        this.label = label;
        this.act_calorie = act_calorie;
        this.intensityL = intensityL;
        this.intensityM = intensityM;
        this.intensityH = intensityH;
        this.intensityD = intensityD;
        this.minHR = minHR;
        this.maxHR = maxHR;
        this.avgHR = avgHR;
        this.start_time = start_time;
        this.end_time = end_time;
        this.upload = upload;
    }

    public ActivityData(JSONObject json) {
        label = Convert.getInt(json, "CourseCode");
        act_calorie = Convert.getDouble(json, "Calorie") / 1000;
        start_time = Convert.getLong(json, "BegTime");
        end_time = Convert.getLong(json, "EndTime");
        intensityL = (short) Convert.getInt(json, "Inten1");
        intensityM = (short) Convert.getInt(json, "Inten2");
        intensityH = (short) Convert.getInt(json, "Inten3");
        intensityD = (short) Convert.getInt(json, "Inten4");
        maxHR = (short) Convert.getInt(json, "HeartRateMax");
        minHR = (short) Convert.getInt(json, "HeartRateMin");
        avgHR = (short) Convert.getInt(json, "HeartRateAvg");
        upload = SQLHelper.SET_UPLOAD;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getLabel() {
        return label;
    }

    public void setLabel(Integer label) {
        this.label = label;
    }

    public Double getAct_calorie() {
        return act_calorie;
    }

    public void setAct_calorie(Double act_calorie) {
        this.act_calorie = act_calorie;
    }

    public Short getIntensityL() {
        return intensityL;
    }

    public void setIntensityL(Short intensityL) {
        this.intensityL = intensityL;
    }

    public Short getIntensityM() {
        return intensityM;
    }

    public void setIntensityM(Short intensityM) {
        this.intensityM = intensityM;
    }

    public Short getIntensityH() {
        return intensityH;
    }

    public void setIntensityH(Short intensityH) {
        this.intensityH = intensityH;
    }

    public Short getIntensityD() {
        return intensityD;
    }

    public void setIntensityD(Short intensityD) {
        this.intensityD = intensityD;
    }

    public Short getMinHR() {
        return minHR;
    }

    public void setMinHR(Short minHR) {
        this.minHR = minHR;
    }

    public Short getMaxHR() {
        return maxHR;
    }

    public void setMaxHR(Short maxHR) {
        this.maxHR = maxHR;
    }

    public Short getAvgHR() {
        return avgHR;
    }

    public void setAvgHR(Short avgHR) {
        this.avgHR = avgHR;
    }

    public Long getStart_time() {
        return start_time;
    }

    public void setStart_time(Long start_time) {
        this.start_time = start_time;
    }

    public Long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Long end_time) {
        this.end_time = end_time;
    }

    public Integer getUpload() {
        return upload;
    }

    public void setUpload(Integer upload) {
        this.upload = upload;
    }
}
