package kr.co.greencomm.middleware.bluetooth;

import java.util.UUID;

public class DeviceUUID {
	public static final UUID DFU_SERVICE_UUID = UUID.fromString("00001530-1212-EFDE-1523-785FEABCD123");
	public static final UUID DFU_CONTROL_POINT_UUID = UUID.fromString("00001531-1212-EFDE-1523-785FEABCD123");
	public static final UUID DFU_PACKET_UUID = UUID.fromString("00001532-1212-EFDE-1523-785FEABCD123");
	public static final UUID DFU_VERSION = UUID.fromString("00001534-1212-EFDE-1523-785FEABCD123");
	public static final UUID CLIENT_CHARACTERISTIC_CONFIG = new UUID(0x0000290200001000l, 0x800000805f9b34fbl);
	public static final UUID SERVICE_CHANGED_UUID = new UUID(0x00002A0500001000l, 0x800000805F9B34FBl);
	
	
	public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
	public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
	public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
}
