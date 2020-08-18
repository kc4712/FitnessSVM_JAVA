package kr.co.greencomm.middleware.utils;

/**
 * Created by jeyang on 2016-11-03.
 */
public enum NoticeIndex {
    Empty, Phone, SMS;

    public static NoticeIndex getIndex(short index) {
        NoticeIndex ret = Empty;
        switch (index) {
            case 0:
                ret = Empty;
                break;
            case 1:
                ret = Phone;
                break;
            case 2:
                ret = SMS;
                break;
        }

        return ret;
    }
}
