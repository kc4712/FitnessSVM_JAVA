package kr.co.greencomm.ibody24.coach.data;

/**
 * Created by young on 2015-08-24.
 */
public class TrainerInfo {
    public int recNo;
    public int photoResId;
    public int specResId;
    public int nameResId;
    public int titleResId;

    public TrainerInfo(int no, int iconId, int nameId, int titleId, int specId) {
        this.recNo = no;
        this.photoResId = iconId;
        this.specResId = specId;
        this.nameResId = nameId;
        this.titleResId = titleId;
    }
}
