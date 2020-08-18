package kr.co.greencomm.middleware.utils.container;

/**
 * Created by jeyang on 2016-08-26.
 */
public class UserExerciseData {
    private Integer video_idx;
    private Integer video_full_count;
    private Integer exer_idx;
    private Integer exer_count;
    private Long start_time;
    private Long end_time;
    private Integer consume_calorie;
    private Integer count;
    private Integer count_percent;
    private Integer perfect_count;
    private Integer min_accuracy;
    private Integer max_accuracy;
    private Integer avg_accuracy;
    private Integer cmp_accuracy;
    private Integer min_heartrate;
    private Integer max_heartrate;
    private Integer avg_heartrate;
    private Integer point;
    private Integer reserved_1;
    private Integer reserved_2;


    public void setUserExerciseData(Integer video_idx, Integer video_full_count, Integer exer_idx, Integer exer_count, Long start_time,
                                    Long end_time, Integer consume_calorie, Integer count, Integer count_percent, Integer perfect_count,
                                    Integer min_accuracy, Integer max_accuracy, Integer avg_accuracy, Integer min_heartrate, Integer max_heartrate,
                                    Integer avg_heartrate, Integer cmp_accuracy, Integer point, Integer reserved_1, Integer reserved_2) {
        this.video_idx = video_idx;
        this.video_full_count = video_full_count;
        this.exer_idx = exer_idx;
        this.exer_count = exer_count;
        this.start_time = start_time;
        this.end_time = end_time;
        this.consume_calorie = consume_calorie;
        this.count = count;
        this.count_percent = count_percent;
        this.perfect_count = perfect_count;
        this.min_accuracy = min_accuracy;
        this.max_accuracy = max_accuracy;
        this.avg_accuracy = avg_accuracy;
        this.min_heartrate = min_heartrate;
        this.max_heartrate = max_heartrate;
        this.avg_heartrate = avg_heartrate;
        this.cmp_accuracy = cmp_accuracy;
        this.point = point;
        this.reserved_1 = reserved_1;
        this.reserved_2 = reserved_2;
    }


    public Integer getVideo_idx() {
        return video_idx;
    }

    public void setVideo_idx(Integer video_idx) {
        this.video_idx = video_idx;
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

    public Integer getMin_accuracy() {
        return min_accuracy;
    }

    public void setMin_accuracy(Integer min_accuracy) {
        this.min_accuracy = min_accuracy;
    }

    public Integer getMax_accuracy() {
        return max_accuracy;
    }

    public void setMax_accuracy(Integer max_accuracy) {
        this.max_accuracy = max_accuracy;
    }

    public Integer getAvg_accuracy() {
        return avg_accuracy;
    }

    public void setAvg_accuracy(Integer avg_accuracy) {
        this.avg_accuracy = avg_accuracy;
    }

    public Integer getMin_heartrate() {
        return min_heartrate;
    }

    public void setMin_heartrate(Integer min_heartrate) {
        this.min_heartrate = min_heartrate;
    }

    public Integer getMax_heartrate() {
        return max_heartrate;
    }

    public void setMax_heartrate(Integer max_heartrate) {
        this.max_heartrate = max_heartrate;
    }

    public Integer getAvg_heartrate() {
        return avg_heartrate;
    }

    public void setAvg_heartrate(Integer avg_heartrate) {
        this.avg_heartrate = avg_heartrate;
    }

    public Integer getExer_count() {
        return exer_count;
    }

    public void setExer_count(Integer exer_count) {
        this.exer_count = exer_count;
    }

    public Integer getCmp_accuracy() {
        return cmp_accuracy;
    }

    public void setCmp_accuracy(Integer cmp_accuracy) {
        this.cmp_accuracy = cmp_accuracy;
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
