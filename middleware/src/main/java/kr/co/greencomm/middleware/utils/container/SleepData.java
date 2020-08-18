package kr.co.greencomm.middleware.utils.container;

/**
 * Created by jeyang on 2016-08-26.
 */
public class SleepData {
    private Short rolled = 0;
    private Short awaken = 0;
    private Short stabilityHR = 0;
    private Long start_time = 0L;

    public SleepData() {

    }

    public SleepData(Short rolled, Short awaken, Short stabilityHR, Long start_time) {
        this.rolled = rolled;
        this.awaken = awaken;
        this.stabilityHR = stabilityHR;
        this.start_time = start_time;
    }

    public Short getRolled() {
        return rolled;
    }

    public void setRolled(Short rolled) {
        this.rolled = rolled;
    }

    public Short getAwaken() {
        return awaken;
    }

    public void setAwaken(Short awaken) {
        this.awaken = awaken;
    }

    public Short getStabilityHR() {
        return stabilityHR;
    }

    public void setStabilityHR(Short stabilityHR) {
        this.stabilityHR = stabilityHR;
    }

    public Long getStart_time() {
        return start_time;
    }

    public void setStart_time(Long start_time) {
        this.start_time = start_time;
    }
}
