package kr.co.greencomm.middleware.bluetooth;

/**
 * Created by jeyang on 2016-08-25.
 */
public enum BluetoothCommand {
    StepCount_Calorie((short)0x06),
    Activity((short)0x0b),
    Sleep((short)0x0c),
    Stress((short)0x13),
    State((short)0x14),
    Battery((short)0x04),
    RTC((short)0x02),
    Acc((short)0x10),
    UserData((short)0x03),
    Vibrate((short)0x07),
    DataClear((short)0x11),
    Version((short)0x0d),
    CoachCalorie((short)0x12),
    ProductID((short)0x15),
    NoticeONOFF((short)0x16),
    NoticeMessage((short)0x17),
    CoachReceiveAcc((short)0x50),

    //규창 171108 피쳐 전달기능
    FeatureSet1((short)0xa1),
    FeatureSet2((short)0xa2),
    FeatureSet3((short)0xa3),

    //규창 171108 이전 피처 덩어리를 raw데이터로 전송받는 루틴 흉내
    pastFeatureRequest((short)0xb1),
    pastFeatureSet1((short)0xba),
    pastFeatureSet2((short)0xbb),
    pastFeatureSet3((short)0xbc),

    //규창 171108 과거 피쳐데이터 전송완료
    pastFeatureSetComplete((short)0xbf),
    featureError((short)0xaf);


    private final short command;

    BluetoothCommand(short command) {
        this.command = command;
    }

    public short getCommand() {
        return this.command;
    }

    public static BluetoothCommand getCommand(short cmd) {
        BluetoothCommand result = null;
        switch (cmd) {
            case 0x06:
                result = StepCount_Calorie;
                break;
            case 0x0b:
                result = Activity;
                break;
            case 0x0c:
                result = Sleep;
                break;
            case 0x13:
                result = Stress;
                break;
            case 0x14:
                result = State;
                break;
            case 0x04:
                result = Battery;
                break;
            case 0x02:
                result = RTC;
                break;
            case 0x10:
                result = Acc;
                break;
            case 0x03:
                result = UserData;
                break;
            case 0x07:
                result = Vibrate;
                break;
            case 0x11:
                result = DataClear;
                break;
            case 0x0d:
                result = Version;
                break;
            case 0x12:
                result = CoachCalorie;
                break;
            case 0x15:
                result = ProductID;
                break;
            case 0x16:
                result = NoticeONOFF;
                break;
            case 0x17:
                result = NoticeMessage;
                break;
            case 0x50:
                result = CoachReceiveAcc;
                break;
            case 0xa1:
                result = FeatureSet1;
                break;
            case 0xa2:
                result = FeatureSet2;
                break;
            case 0xa3:
                result = FeatureSet3;
                break;
            case 0xb1:
                result = pastFeatureRequest;
                break;
            case 0xba:
                result = pastFeatureSet1;
                break;
            case 0xbb:
                result = pastFeatureSet2;
                break;
            case 0xbc:
                result = pastFeatureSet3;
                break;
            case 0xaf:
                result = featureError;
                break;
            case 0xbf:
                result = pastFeatureSetComplete;
                break;
        }
        return result;
    }
}
