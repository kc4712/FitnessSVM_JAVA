package kr.co.greencomm.middleware.tool;

/**
 * Created by jeyang on 2016-08-26.
 */
public class UiDisplay {
    private Boolean showUi;
    private String name;

    public UiDisplay() {

    }

    public UiDisplay(Boolean showUi, String name) {
        this.showUi = showUi;
        this.name = name;
    }

    public Boolean getShowUi() {
        return showUi;
    }

    public void setShowUi(Boolean showUi) {
        this.showUi = showUi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
