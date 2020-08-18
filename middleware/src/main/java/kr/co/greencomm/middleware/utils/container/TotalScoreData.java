package kr.co.greencomm.middleware.utils.container;

/**
 * Created by jeyang on 2016-08-29.
 */
public class TotalScoreData {
    private Double duration;
    private Integer point;
    private Integer count_percent;
    private Integer accuracy_percent;
    private String comment;

    public TotalScoreData() {

    }

    public TotalScoreData(Double duration, Integer point, Integer count_percent, Integer accuracy_percent, String comment) {
        this.duration = duration;
        this.point = point;
        this.count_percent = count_percent;
        this.accuracy_percent = accuracy_percent;
        this.comment = comment;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getCount_percent() {
        return count_percent;
    }

    public void setCount_percent(Integer count_percent) {
        this.count_percent = count_percent;
    }

    public Integer getAccuracy_percent() {
        return accuracy_percent;
    }

    public void setAccuracy_percent(Integer accuracy_percent) {
        this.accuracy_percent = accuracy_percent;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
