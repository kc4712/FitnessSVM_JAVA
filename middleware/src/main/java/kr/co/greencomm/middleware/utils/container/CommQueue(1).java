package kr.co.greencomm.middleware.utils.container;

import android.os.Parcel;
import android.os.Parcelable;

import kr.co.greencomm.middleware.bluetooth.BluetoothCommand;
import kr.co.greencomm.middleware.bluetooth.RequestAction;
import kr.co.greencomm.middleware.utils.MessageClass;
import kr.co.greencomm.middleware.utils.NoticeIndex;

/**
 * Created by jeyang on 2016-08-25.
 */
public class CommQueue implements Parcelable {
    private BluetoothCommand cmd;
    private Long time;
    private RequestAction action;
    private Boolean resp;
    private MessageClass cls;
    private Integer count;
    private NoticeIndex index;

    public CommQueue(BluetoothCommand cmd, Long time, RequestAction action, Boolean resp, MessageClass cls, Integer count, NoticeIndex index) {
        this.cmd = cmd;
        this.time = time;
        this.action = action;
        this.resp = resp;
        this.cls = cls;
        this.count = count;
        this.index = index;
    }

    public CommQueue(BluetoothCommand cmd, Long time, RequestAction action, Boolean resp) {
        this.cmd = cmd;
        this.time = time;
        this.action = action;
        this.resp = resp;
        this.cls = MessageClass.Phone_Idle;
        this.count = 0;
        this.index = NoticeIndex.Empty;
    }

    public static BluetoothCommand readCmd(int cmd) {
        switch (cmd) {
            case 0x06:
                return BluetoothCommand.StepCount_Calorie;
            case 0x0b:
                return BluetoothCommand.Activity;
            case 0x0c:
                return BluetoothCommand.Sleep;
            case 0x13:
                return BluetoothCommand.Stress;
            case 0x14:
                return BluetoothCommand.State;
            case 0x04:
                return BluetoothCommand.Battery;
            case 0x02:
                return BluetoothCommand.RTC;
            case 0x10:
                return BluetoothCommand.Acc;
            case 0x03:
                return BluetoothCommand.UserData;
            case 0x07:
                return BluetoothCommand.Vibrate;
            case 0x11:
                return BluetoothCommand.DataClear;
            case 0x0d:
                return BluetoothCommand.Version;
            case 0x12:
                return BluetoothCommand.CoachCalorie;
            case 0x15:
                return BluetoothCommand.ProductID;
            case 0x16:
                return BluetoothCommand.NoticeONOFF;
            case 0x17:
                return BluetoothCommand.NoticeMessage;
            case 0x50:
                return BluetoothCommand.CoachReceiveAcc;
            default:
                return BluetoothCommand.Acc;
        }
    }

    private RequestAction readAction(int action) {
        switch (action) {
            case 1:
                return RequestAction.Start;
            case 2:
                return RequestAction.End;
            case 3:
                return RequestAction.Inform;
            default:
                return RequestAction.Start;
        }
    }

    private MessageClass readMessageClass(int cls) {
        switch (cls) {
            case 1:
                return MessageClass.Phone_Idle;
            case 2:
                return MessageClass.Phone_Ringing;
            case 3:
                return MessageClass.SMS;
            default:
                return MessageClass.Phone_Idle;
        }
    }

    private NoticeIndex readNoticeIndex(int index) {
        switch (index) {
            case 1:
                return NoticeIndex.Phone;
            case 2:
                return NoticeIndex.SMS;
            default:
                return NoticeIndex.Empty;
        }
    }

    protected CommQueue(Parcel in) {
        cmd = readCmd(in.readInt());
        time = in.readLong();
        action = readAction(in.readInt());
        int bool = in.readInt();
        resp = getBoolean(bool);
        cls = readMessageClass(in.readInt());
        count = in.readInt();
        index = readNoticeIndex(in.readInt());
    }

    private boolean getBoolean(int bool) {
        return bool == 0 ? false : true;
    }

    public BluetoothCommand getCmd() {
        return cmd;
    }

    public void setCmd(BluetoothCommand cmd) {
        this.cmd = cmd;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public RequestAction getAction() {
        return action;
    }

    public void setAction(RequestAction action) {
        this.action = action;
    }

    public Boolean getResp() {
        return resp;
    }

    public void setResp(Boolean resp) {
        this.resp = resp;
    }

    public MessageClass getCls() {
        return cls;
    }

    public void setCls(MessageClass cls) {
        this.cls = cls;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public NoticeIndex getIndex() {
        return index;
    }

    public void setIndex(NoticeIndex index) {
        this.index = index;
    }

    public static final Creator<CommQueue> CREATOR = new Creator<CommQueue>() {
        @Override
        public CommQueue createFromParcel(Parcel in) {
            return new CommQueue(in);
        }

        @Override
        public CommQueue[] newArray(int size) {
            return new CommQueue[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cmd.getCommand());
        dest.writeLong(time);
        dest.writeInt(action.getAction());
        dest.writeInt(resp ? 1 : 0);
        dest.writeInt(cls.getCls());
        dest.writeInt(count);
        dest.writeInt(index.ordinal());
    }
}
