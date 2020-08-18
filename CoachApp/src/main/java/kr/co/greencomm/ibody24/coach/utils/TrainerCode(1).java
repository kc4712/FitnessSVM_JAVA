package kr.co.greencomm.ibody24.coach.utils;

import android.content.Context;

import kr.co.greencomm.ibody24.coach.R;

/**
 * Created by jeyang on 2016. 10. 4..
 */
public enum TrainerCode {
    Basic, Lee_Joo_young, Hong_Doo_Han, Kwon_Do_Ye;

    public int getPhotoNumber() {
        int n = 0;
        switch (this) {
            case Basic:
                n = 0;
                break;
            case Lee_Joo_young:
                n = 2;
                break;
            case Hong_Doo_Han:
                n = 2;
                break;
            case Kwon_Do_Ye:
                n = 3;
                break;
        }

        return n;
    }


    public int getTrainerNumber() {
        return 3;
    }

    public ProgramCode[] getProgramList() {
        ProgramCode[] ret = new ProgramCode[]{};
        switch (this) {
            case Basic:
                ret = new ProgramCode[]{};
                break;
            case Lee_Joo_young:
                ret = new ProgramCode[]{ProgramCode.Abs, ProgramCode.Back};
                break;
            case Hong_Doo_Han:
                ret = new ProgramCode[]{ProgramCode.TBT1, ProgramCode.TBT2};
                break;
            case Kwon_Do_Ye:
                ret = new ProgramCode[]{ProgramCode.Baby1, ProgramCode.Baby2};
                break;
        }

        return ret;
    }

    public String getProfileTrainerHtml(Context context) {
        String name = "";
        switch (this) {
            case Basic:
                name = "";
                break;
            case Lee_Joo_young:
                name = context.getString(R.string.html_lee_joo_young_profile_name);
                break;
            case Hong_Doo_Han:
                name = context.getString(R.string.html_hong_doo_han_profile_name);
                break;
            case Kwon_Do_Ye:
                name = context.getString(R.string.html_kwon_do_ye_profile_name);
                break;
        }

        return name;
    }
}