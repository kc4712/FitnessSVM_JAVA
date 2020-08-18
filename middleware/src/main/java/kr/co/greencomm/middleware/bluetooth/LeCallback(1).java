package kr.co.greencomm.middleware.bluetooth;

/**
 * 블루투스의 연결 상태를 반환하는 callback interface.
 */
public interface LeCallback {
	//public void onScan(ArrayList<DeviceRecord> deviceArray);
	/**
	 * 블루투스 연결 상태를 반환한다. 연결 상태에 아무런 변화도 이루어지지 않았을 경우, 기본은 STATE_DISCONNECTED 이다.
	 * @param state 연결 상태.(STATE_DISCONNECTED, STATE_CONNECTING, STATE_CONNECTED)
	 */
	public void onConnectionState(ConnectStatus state);
	/**
	 * 블루투스 연결 시도 중 복수 기기 발견, 기기를 찾지 못했을 경우 callback 발생한다.
	 * stopBluetooth() 메서드를 호출하지 않으면 지속적으로 기기를 찾는다.
	 */
	public void onConnectionFailed();
}
