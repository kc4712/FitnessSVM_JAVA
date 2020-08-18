package kr.co.greencomm.middleware.bluetooth;

/**
 * Created by jeyang on 2016-08-25.
 */
public enum StatePeripheral {
    IDLE((short)1), Activity((short)2), Stress((short)3), Sleep((short)4);

    private final short state;

    StatePeripheral(short state) {
        this.state = state;
    }

    public short getState() {
        return state;
    }
}
