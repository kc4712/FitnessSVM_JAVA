package kr.co.greencomm.middleware.utils;

/**
 * Created by jeyang on 2016-10-31.
 */
public enum MessageClass {
    Phone_Idle(1), Phone_Ringing(2), SMS(3), Missed_Call(4);

    MessageClass(int cls) {
        this.cls = cls;
    }

    private final int cls;

    public int getCls() {
        return cls;
    }
}
