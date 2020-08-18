package kr.co.greencomm.middleware.tool;

/**
 * Created by jeyang on 2016-08-26.
 */
public class TotalScoreDisplay {
    private Boolean showScore;
    private Double duration;

    public TotalScoreDisplay() {

    }

    public TotalScoreDisplay(Boolean showScore, Double duration) {
        this.showScore = showScore;
        this.duration = duration;
    }

    public Boolean getShowScore() {
        return showScore;
    }

    public void setShowScore(Boolean showScore) {
        this.showScore = showScore;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }
}
