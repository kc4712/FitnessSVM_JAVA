package kr.co.greencomm.middleware.utils.container;

/**
 * Created by jeyang on 2016-08-29.
 */
public class CoachActivityData {
    private Integer index;
    private Integer videoIdx;
    private Integer video_full_count;
    private Integer exer_idx;
    private Integer exer_count;
    private Long start_time;
    private Long end_time;
    private Integer consume_calorie;
    private Integer count;
    private Integer count_percent;
    private Integer perfect_count;
    private Integer minAccuracy;
    private Integer maxAccuracy;
    private Integer avgAccuracy;
    private Integer minHeartRate;
    private Integer maxHeartRate;
    private Integer avgHeartRate;
    private Integer cmpHeartRate;
    private Integer point;
    private Integer reserved_1;
    private Integer reserved_2;

    public CoachActivityData() {

    }

    public CoachActivityData(Integer index, Integer videoIdx, Integer video_full_count, Integer exer_idx, Integer exer_count, Long start_time, Long end_time,
                             Integer consume_calorie, Integer count, Integer count_percent, Integer perfect_count, Integer minAccuracy, Integer maxAccuracy,
                             Integer avgAccuracy, Integer minHeartRate, Integer maxHeartRate, Integer avgHeartRate, Integer cmpHeartRate, Integer point, Integer reserved_1, Integer reserved_2) {
        this.index = index;
        this.videoIdx = videoIdx;
        this.video_full_count = video_full_count;
        this.exer_idx = exer_idx;
        this.exer_count = exer_count;
        this.start_time = start_time;
        this.end_time = end_time;
        this.consume_calorie = consume_calorie;
        this.count = count;
        this.count_percent = count_percent;
        this.perfect_count = perfect_count;
        this.minAccuracy = minAccuracy;
        this.maxAccuracy = maxAccuracy;
        this.avgAccuracy = avgAccuracy;
        this.minHeartRate = minHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.avgHeartRate = avgHeartRate;
        this.cmpHeartRate = cmpHeartRate;
        this.point = point;
        this.reserved_1 = reserved_1;
        this.reserved_2 = reserved_2;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getVideoIdx() {
        return videoIdx;
    }

    public void setVideoIdx(Integer videoIdx) {
        this.videoIdx = videoIdx;
    }

    public Integer getVideo_full_count() {
        return video_full_count;
    }

    public void setVideo_full_count(Integer video_full_count) {
        this.video_full_count = video_full_count;
    }

    public Integer getExer_idx() {
        return exer_idx;
    }

    public void setExer_idx(Integer exer_idx) {
        this.exer_idx = exer_idx;
    }

    public Integer getExer_count() {
        return exer_count;
    }

    public void setExer_count(Integer exer_count) {
        this.exer_count = exer_count;
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

    public Integer getConsume_calorie() {
        return consume_calorie;
    }

    public void setConsume_calorie(Integer consume_calorie) {
        this.consume_calorie = consume_calorie;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCount_percent() {
        return count_percent;
    }

    public void setCount_percent(Integer count_percent) {
        this.count_percent = count_percent;
    }

    public Integer getPerfect_count() {
        return perfect_count;
    }

    public void setPerfect_count(Integer perfect_count) {
        this.perfect_count = perfect_count;
    }

    public Integer getMinAccuracy() {
        return minAccuracy;
    }

    public void setMinAccuracy(Integer minAccuracy) {
        this.minAccuracy = minAccuracy;
    }

    public Integer getMaxAccuracy() {
        return maxAccuracy;
    }

    public void setMaxAccuracy(Integer maxAccuracy) {
        this.maxAccuracy = maxAccuracy;
    }

    public Integer getAvgAccuracy() {
        return avgAccuracy;
    }

    public void setAvgAccuracy(Integer avgAccuracy) {
        this.avgAccuracy = avgAccuracy;
    }

    public Integer getMinHeartRate() {
        return minHeartRate;
    }

    public void setMinHeartRate(Integer minHeartRate) {
        this.minHeartRate = minHeartRate;
    }

    public Integer getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(Integer maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    public Integer getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(Integer avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }

    public Integer getCmpHeartRate() {
        return cmpHeartRate;
    }

    public void setCmpHeartRate(Integer cmpHeartRate) {
        this.cmpHeartRate = cmpHeartRate;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getReserved_1() {
        return reserved_1;
    }

    public void setReserved_1(Integer reserved_1) {
        this.reserved_1 = reserved_1;
    }

    public Integer getReserved_2() {
        return reserved_2;
    }

    public void setReserved_2(Integer reserved_2) {
        this.reserved_2 = reserved_2;
    }
}
