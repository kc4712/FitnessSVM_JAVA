package kr.co.greencomm.ibody24.coach.utils;

/**
 * Created by jeyang on 2016. 10. 12..
 */
public class ActListItem {
    private String date;
    private Long utc;

    public ActListItem(String date, Long utc) {
        this.date = date;
        this.utc = utc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getUtc() {
        return utc;
    }

    public void setUtc(Long utc) {
        this.utc = utc;
    }
}
