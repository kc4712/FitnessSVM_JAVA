package kr.co.greencomm.ibody24.coach.utils;

import android.content.Context;

import kr.co.greencomm.ibody24.coach.R;

/**
 * Created by jeyang on 2016. 10. 4..
 */
public enum ProgramCode {
    Basic(1), Full_Body(2), Mat(3), Active(4), Abs(101), Back(102), TBT1(201), TBT2(202), Baby1(301), Baby2(302);

    private final int code;

    ProgramCode(int code) {
        this.code = code;
    }

    public int getProgramCode() {
        return code;
    }

    public String getProgramName(Context context) {
        String name = "";
        switch (this) {
            case Abs:
                name = context.getString(R.string.program_name_abdominal_muscles);
                break;
            case Back:
                name = context.getString(R.string.program_name_backside);
                break;
            case TBT1:
                name = context.getString(R.string.program_name_tbt1);
                break;
            case TBT2:
                name = context.getString(R.string.program_name_tbt2);
                break;
            case Baby1:
                name = context.getString(R.string.program_name_baby_1);
                break;
            case Baby2:
                name = context.getString(R.string.program_name_baby_2);
                break;
        }

        return name;
    }

    public enum BasicCode {
        Basic1, Basic2, Basic3, Basic4, Basic5, Basic6, Basic7, Basic8;

        public static BasicCode getBasicCode(int code) {
            BasicCode ret = Basic1;
            switch (code) {
                case 1:
                    ret = Basic1;
                    break;
                case 2:
                    ret = Basic2;
                    break;
                case 3:
                    ret = Basic3;
                    break;
                case 4:
                    ret = Basic4;
                    break;
                case 5:
                    ret = Basic5;
                    break;
                case 6:
                    ret = Basic6;
                    break;
                case 7:
                    ret = Basic7;
                    break;
                case 8:
                    ret = Basic8;
                    break;
            }

            return ret;
        }
    }
}
