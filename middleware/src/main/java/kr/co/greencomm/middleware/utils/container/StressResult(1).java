package kr.co.greencomm.middleware.utils.container;

/**
 * Created by jeyang on 2016-08-29.
 */
public class StressResult {
    private Double maxBPM;
    private Double minBPM;
    private Double avgBPM;
    private Double SDNN;
    private Double RMSSD;

    public StressResult() {

    }

    public StressResult(Double maxBPM, Double minBPM, Double avgBPM, Double SDNN, Double RMSSD) {
        this.maxBPM = maxBPM;
        this.minBPM = minBPM;
        this.avgBPM = avgBPM;
        this.SDNN = SDNN;
        this.RMSSD = RMSSD;
    }

    public Double getMaxBPM() {
        return maxBPM;
    }

    public void setMaxBPM(Double maxBPM) {
        this.maxBPM = maxBPM;
    }

    public Double getMinBPM() {
        return minBPM;
    }

    public void setMinBPM(Double minBPM) {
        this.minBPM = minBPM;
    }

    public Double getAvgBPM() {
        return avgBPM;
    }

    public void setAvgBPM(Double avgBPM) {
        this.avgBPM = avgBPM;
    }

    public Double getSDNN() {
        return SDNN;
    }

    public void setSDNN(Double SDNN) {
        this.SDNN = SDNN;
    }

    public Double getRMSSD() {
        return RMSSD;
    }

    public void setRMSSD(Double RMSSD) {
        this.RMSSD = RMSSD;
    }
}
