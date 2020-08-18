package kr.co.greencomm.middleware.utils.container;

/**
 * Created by jeyang on 2016-08-31.
 */
public class IndexTimeData {
    private Integer label;
    private Long start_time;
    private Long end_time;

    public IndexTimeData() {

    }

    public IndexTimeData(Integer label, Long start_time, Long end_time) {
        this.label = label;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public Integer getLabel() {
        return label;
    }

    public void setLabel(Integer label) {
        this.label = label;
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
}
