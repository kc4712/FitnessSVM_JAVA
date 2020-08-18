package kr.co.greencomm.middleware.bluetooth;

/**
 * Created by jeyang on 2016-08-25.
 */
public enum ConnectStatus {
    /**
     * 연결 끊김.
     */
    STATE_DISCONNECTED,
    /**
     * 연결 중.
     */
    STATE_CONNECTING,
    /**
     * 연결 완료.
     */
    STATE_CONNECTED,
    /**
     * 완벽한 종료. 내부 변수로만 활용.
     */
    STATE_EXIT
}