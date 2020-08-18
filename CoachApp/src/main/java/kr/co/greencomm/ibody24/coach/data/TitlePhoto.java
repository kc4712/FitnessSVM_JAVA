package kr.co.greencomm.ibody24.coach.data;

/**
 * Created by young on 2015-08-21.
 */
public class TitlePhoto {
    public int iconResId;
    public int titleResId;
    public String titleText;

    public TitlePhoto(int icon, int title) {
        this.iconResId = icon;
        this.titleResId = title;
    }

    public TitlePhoto(int icon, String title) {
        this.iconResId = icon;
        this.titleText = title;
    }
}
