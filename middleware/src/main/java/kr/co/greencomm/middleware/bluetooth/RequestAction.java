package kr.co.greencomm.middleware.bluetooth;

/**
 * Created by jeyang on 2016-08-25.
 */
public enum RequestAction {
    Start((short) 1), End((short) 2), Inform((short) 3);

    private final short action;

    RequestAction(short action) {
        this.action = action;
    }

    public short getAction() {
        return this.action;
    }

    public static RequestAction getAction(short action) {
        RequestAction result = null;

        switch (action) {
            case 1:
                result = Start;
                break;
            case 2:
                result = End;
                break;
            case 3:
                result = Inform;
                break;
        }

        return result;
    }
}
