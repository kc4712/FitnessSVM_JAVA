package kr.co.greencomm.middleware.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by young on 2016-02-19.
 */
public class DeviceRecord implements Parcelable
{
	private BluetoothDevice device;
	private String name;
	private String mac;
	private int rssi;

	public DeviceRecord(BluetoothDevice device, int rssi) {
		this.device = device;
		this.name = device.getName();
		this.mac = device.getAddress();
		this.rssi = rssi;
	}

	public DeviceRecord(BluetoothDevice device, String addr, String name, int rssi) {
		this.device = device;
		this.name = name;
		this.mac = addr;
		this.rssi = rssi;
	}

	protected DeviceRecord(Parcel in) {
		device = in.readParcelable(BluetoothDevice.class.getClassLoader());
		name = in.readString();
		mac = in.readString();
		rssi = in.readInt();
	}

	public static final Creator<DeviceRecord> CREATOR = new Creator<DeviceRecord>() {
		@Override
		public DeviceRecord createFromParcel(Parcel in) {
			return new DeviceRecord(in);
		}

		@Override
		public DeviceRecord[] newArray(int size) {
			return new DeviceRecord[size];
		}
	};

	public String getName() {
		return name;
	}
	public String getMac() {
		return mac;
	}
	public BluetoothDevice getDevice() {
		return device;
	}
	public int getRssi() {
		return rssi;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}
	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(device, flags);
		dest.writeString(name);
		dest.writeString(mac);
		dest.writeInt(rssi);
	}
}
