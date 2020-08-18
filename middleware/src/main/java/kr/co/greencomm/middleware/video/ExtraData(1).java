package kr.co.greencomm.middleware.video;

/**
 * Created by jeyang on 2016-08-26.
 */
public class ExtraData {
    private int avgHR;
    private int count_percent;
    private int minAccuracy;
    private int maxAccuracy;
    private int avgAccuracy;
    private int minHeartRate;
    private int maxHeartRate;
    private int avgHeartRate;

    public ExtraData() {

    }

    public ExtraData(int avgHR, int count_percent, int minAccuracy, int maxAccuracy, int avgAccuracy, int minHeartRate, int maxHeartRate, int avgHeartRate) {
        this.avgHR = avgHR;
        this.count_percent = count_percent;
        this.minAccuracy = minAccuracy;
        this.maxAccuracy = maxAccuracy;
        this.avgAccuracy = avgAccuracy;
        this.minHeartRate = minHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.avgHeartRate = avgHeartRate;
    }

    public int getAvgHR() {
        return avgHR;
    }

    public void setAvgHR(int avgHR) {
        this.avgHR = avgHR;
    }

    public int getCount_percent() {
        return count_percent;
    }

    public void setCount_percent(int count_percent) {
        this.count_percent = count_percent;
    }

    public int getMinAccuracy() {
        return minAccuracy;
    }

    public void setMinAccuracy(int minAccuracy) {
        this.minAccuracy = minAccuracy;
    }

    public int getMaxAccuracy() {
        return maxAccuracy;
    }

    public void setMaxAccuracy(int maxAccuracy) {
        this.maxAccuracy = maxAccuracy;
    }

    public int getAvgAccuracy() {
        return avgAccuracy;
    }

    public void setAvgAccuracy(int avgAccuracy) {
        this.avgAccuracy = avgAccuracy;
    }

    public int getMinHeartRate() {
        return minHeartRate;
    }

    public void setMinHeartRate(int minHeartRate) {
        this.minHeartRate = minHeartRate;
    }

    public int getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(int maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    public int getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(int avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }
}
