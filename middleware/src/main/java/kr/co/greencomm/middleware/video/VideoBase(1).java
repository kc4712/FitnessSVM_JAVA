package kr.co.greencomm.middleware.video;

import android.content.Context;
import android.util.Log;

import com.KIST.kistAART.KIST_AART_output;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Locale;

import kr.co.greencomm.middleware.bluetooth.PeripheralBase;
import kr.co.greencomm.middleware.main.BluetoothLEManager;
import kr.co.greencomm.middleware.main.ConfigManager;
import kr.co.greencomm.middleware.service.MWBroadcastTop;
import kr.co.greencomm.middleware.tool.Action;
import kr.co.greencomm.middleware.tool.Course;
import kr.co.greencomm.middleware.tool.Message;
import kr.co.greencomm.middleware.tool.Program;
import kr.co.greencomm.middleware.tool.Translation;
import kr.co.greencomm.middleware.tool.UiDisplay;
import kr.co.greencomm.middleware.utils.ListQueueUtil;
import kr.co.greencomm.middleware.utils.MessageIndex;
import kr.co.greencomm.middleware.utils.container.CoachActivityData;
import kr.co.greencomm.middleware.utils.container.DataBase;

/**
 * Created by jeyang on 2016-08-26.
 */
public abstract class VideoBase extends TotalScoreBase {
    private static final String tag = VideoBase.class.getSimpleName();
    protected int currentPosition = 0;
    protected boolean isPlaying = false;
    protected boolean set_HRLock = false;
    protected boolean setPlay = true;
    protected boolean is_R_Video = false;
    protected boolean isSave = false;
    protected boolean set_AccuracyLock = false;

    private boolean isShowUI = false;

    protected final ListQueueUtil<Integer> HRQueue = new ListQueueUtil<>();
    protected final ListQueueUtil<Integer> accuracyQueue = new ListQueueUtil<>();
    protected final int[] out = new int[2];

    private static int THR_NOT_MOVE = 200; // 10초
    protected int idx_count = 0;
    protected float preCalorie = 0;
    protected float sumCalorie = 0;

    protected long save_start_time = 0;

    private static int MODE_NEW_START = 1;
    private static int MODE_RESUME = 2;

    protected BluetoothLEManager mBle;
    protected ConfigManager mConfig;
    protected DataBase mDatabase;
    protected PeripheralBase mPerip;

    private MWBroadcastTop mBrTop;

    protected abstract void setTotalScore(int point, int count_percent, int accuracy_percent, String comment);

    protected abstract void initInstance();

    protected VideoBase(Context context) {
        mBle = BluetoothLEManager.getInstance(context);
        mConfig = ConfigManager.getInstance(context);
        mDatabase = DataBase.getInstance();
        mPerip = new PeripheralBase(context);

        mBrTop = new MWBroadcastTop(context);
    }

    //--- 재생 모드 설정.
    //--- @param mode resume : 변수 보존, new : 변수 초기화.
    protected void setPlayMode(int mode) {
        if (mode == VideoBase.MODE_NEW_START) {
            initInstance();
        }
    }

    //--- 재생을 담당하며 자동으로 Raw 데이터를 기록. 재생 시, 설정된 파일명으로 Raw 데이터를 기록한다. Kist 엔진과의 연동
    //---시작을 담당.
    //--- @return true:성공, false:실패(이미 재생중. end function을 실행 시켜주세요.)
    protected boolean play() {
        //Log.i(VideoManager.tag, msg:"플레이 함수시작")
        if (isPlaying) {
            return false;
        } else {
            isPlaying = true;
            mDatabase.setProfile(mConfig.getUserProfile());
            return true;
        }
    }

    //--- 동영상 종료. 재생이 끝나면 항상 실행해야 함.
    protected void end() {
        //log(tag, "end()");
        mDatabase.resetVideoVariable();
        isPlaying = false;
        initInstance();
        set_HRLock = false;
        //mPerip.stopVibrate(); // 밴드에서 자동으로 울리므로 막음.
    }

    //--- 사용자가 직접 동영상을 종료할 경우 수행.
    protected void userExit() {
        //    for (long startTime = 0 ; startTime <= arrayStartTime ; startTime++)
        //        mConfig.deleteUserExerciseData(startTime);
        //    arrayStartTime= 0;//.clear();
    }

    //--- 현재 동영상 재생을 알리는 flag setting
    //--- @param setPlay 현재 동영상의 재생 상태. (true:재생중, false:중지)
    protected void setPlaying(boolean setPlay) {
        this.setPlay = setPlay;
    }

    //--- 현 동영상의 재생 위치를 알림.
    //--- @param currentPosition 현재 동영상의 재생 위치. (sec 단위)
    public void setCurrentTimePosition(int position) {
        currentPosition = position;
    }

    protected boolean isRVideo() {
        return is_R_Video;
    }

    public String getCourseCodeForQuery(int programCode) {
        int code = programCode + 320000;
        return String.valueOf(code);
    }

    // 이하 내용은 xml파일이 입력됨을 가정하고(stream?) 읽어서 동작시키는 로직의 구현내용.
    public void setProgram(Element xml) {
        pro = getProgram(xml);
        setMessageField(pro);
    }

    // 임시로 넣도록 하자. 스트림을 어떻게 받아올것인지는 이사님과 논의 필요..
    protected Program pro;

    private String m_languageCode = "KO";

    protected String getLanguageCode() {
        return m_languageCode;
    }

    public void setLanguageCode() {
        m_languageCode = Locale.getDefault().getLanguage().toUpperCase();
        Log.i(tag,"m_languageCode:" + m_languageCode);
        if (m_languageCode.equals("JA")) {
            m_languageCode = "JP";
        }
    }

    protected int v_code = 0;

    private int getProgramTotalCount() {
        if (pro.CourseList.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Course c : pro.CourseList) {
            count += c.TotalCount;
        }

        return count;
    }

    protected int getExerCount(Program pro, int v_code) {
        for (Course c : pro.CourseList) {
            if (c.Code == v_code) {
                return c.TotalCount;
            }
        }

        return 15;
    }

    private String findLocalizeMessage(ArrayList<Translation> list) {
        for (Translation t : list) {
            if (t.Language.equals(m_languageCode)) {
                return t.Display;
            }
        }

        return "";
    }

    private void setMessageField(Program pro) {
        if (pro.MessageList == null) {
            return;
        }

        for (Message message : pro.MessageList) {
            TotalScoreDic.put(message.Code, findLocalizeMessage(message.List));
        }
    }

    private Program getProgram(Element xml) {
        return new Program(xml);
    }

    private int getMsg(int grade, Action action) {
        int msg = 0;

        switch (grade) {
            case 5:
                if (action.Accuracy.OutFlag5) {
                    msg = action.Accuracy.MsgNum5;
                }
                break;
            case 4:
                if (action.Accuracy.OutFlag4) {
                    msg = action.Accuracy.MsgNum4;
                }
                break;
            case 3:
                if (action.Accuracy.OutFlag3) {
                    msg = action.Accuracy.MsgNum3;
                }
                break;
            case 2:
                if (action.Accuracy.OutFlag2) {
                    msg = action.Accuracy.MsgNum2;
                }
                break;
            case 1:
                if (action.Accuracy.OutFlag1) {
                    msg = action.Accuracy.MsgNum1;
                }
                break;
            default:
                msg = 0;
        }

        return msg;
    }

    private String getComment(Program pro, int index) {
        if (pro.MessageList.isEmpty()) {
            return null;
        }

        for (Message m : pro.MessageList) {
            if (m.Code == index) {
                // 언어 값은??? 프로퍼티를 셋만 쓸까?
                return findLocalizeMessage(m.List);
            }
        }

        return null;
    }

    protected UiDisplay onUiDisplay(Program pro, int currentPosition) {
        return pro.getUiDisplay(currentPosition);
    }

    protected void switchVideo(boolean isUiDisPlay) {
        if (isShowUI != isUiDisPlay) {
            isShowUI = isUiDisPlay;

            mBrTop.sendBroadcastShowUI(isShowUI);
            //MWNotification.postNotification(MWNotification.Video.ShowUI, info: [MWNotification.Video.ShowUI : isShowUI])
        }
    }

    private void reset() {
        // 리셋은 총점이 표시된 다음 실시해야함.
        // 총점의 값 세팅은 코스 하나가 끝나자마자 실시.
        // 총점의 표시는 코스끝+총점시작시간에서 총점표시시간만큼 표시.

    }

    private String getRandom(int idx) {
        switch (idx) {
            case 0:
                return TotalScoreDic.get(MessageIndex.Random1.getIndex());
            case 1:
                return TotalScoreDic.get(MessageIndex.Random2.getIndex());
            case 2:
                return TotalScoreDic.get(MessageIndex.Random3.getIndex());
            case 3:
                return TotalScoreDic.get(MessageIndex.Random4.getIndex());
            default:
                return TotalScoreDic.get(MessageIndex.Random1.getIndex());
        }
    }

    protected boolean getResult(Program pro, KIST_AART_output out, ExtraData extra) {
        // 엔진 구동 시간으로 결정. UI 표시 시간은 다름. switchUI 기능 포함.
        double doublePosition = (double) currentPosition;
        Course course = pro.find(doublePosition);
        if (course != null) {
            isSave = false;

            mDatabase.resetTotalScore();

            is_R_Video = false;
            if (save_start_time == 0) {
                save_start_time = System.currentTimeMillis();
            }

            v_code = course.Code;
            Log.d(tag, "v_code->" + v_code);
            Action action = course.find(doublePosition);

            if (action != null) {
                Log.d(tag, "action->" + action + " " + action.Start);
                double duration = 0;
                for (Action act : course.ActionList) {
                    duration += act.getDuration();
                }

                action.SubmitFunctions(out);
                if (action.GetCheckResult(out)) {
                    action.Accuracy.ResetFrequency();

                    double acc = action.Accuracy.GetResult(out);
                    int grade = action.Accuracy.GetGrade(out);
                    Log.d(tag, "GETRESULT 1. code:" + v_code + " acc:" + acc + " grade:" + grade);

                    if (accuracyQueue.empty()) {
                        if (acc > 0) {
                            accuracyQueue.insert((int) acc);

                            this.out[1] = (int) acc;
                            this.out[0] += 1;
                        }

                        int msgIdx = getMsg(grade, action);
                        if (set_AccuracyLock) {
                            String msg = getComment(pro, msgIdx);
                            if (msg != null) {
                                if (msg.equals("RANDOM")) {
                                    mDatabase.setBottomComment(getRandom((int) (acc % 4)));
                                } else {
                                    mDatabase.setBottomComment(msg);
                                }
                            }
                            idx_count = 0;
                        }

                        Log.d(tag, "GETRESULT 2. code : " + (course.Code) + " currentPosition : " + (currentPosition) + " acc : " + (acc) + " grade : " + (grade) + " msgIdx : " + (msgIdx) + " action : " + (action));
                    }
                    out.Reset();
                }

                idx_count += 1;
                if (idx_count > THR_NOT_MOVE) {
                    idx_count = 0;

                    int hr_percent = CalculateBase.getHeartRateCompared(extra.getAvgHR(), mDatabase.getProfile());
                    if (hr_percent <= 20) {
                        mDatabase.setBottomComment(TotalScoreDic.get(MessageIndex.BAD.getIndex()));
                    } else if (hr_percent >= 60) {
                        mDatabase.setBottomComment(TotalScoreDic.get(MessageIndex.REST.getIndex()));
                    }
                }
            }
        } else {
            is_R_Video = true;
            out.Reset();
            out.ResetGlobal();
            if (v_code != 0 && !isSave) {
                // 총점 값 세팅.
                // 전역변수로 잡아놔야함
                // 쓰는놈하고 표시하려는놈하고 다름.
                // 이때, 운동 하나 끝나므로, 데이터 저장.

                // 밑의 코멘트의 선택 로직은 동일하나, 코멘트의 저장필요.(XML)
                isSave = true;
                int point = CalculateBase.getPoint(extra.getCount_percent(), extra.getAvgAccuracy());
                setTotalScore(point, extra.getCount_percent(), extra.getAvgAccuracy(), getCommentSection(extra.getCount_percent(), extra.getAvgAccuracy()));

                int cmpHeartRate = CalculateBase.getHeartRateCompared(extra.getAvgHeartRate(), mDatabase.getProfile());

                // max 심박수 자리에, 안정심박수를 넣어서 서버로 전달.(카르보넨 사용)
                int stable = mDatabase.getHeartrate_stable();
                if (stable == 0)
                    stable = 60;

                long save_end_time = System.currentTimeMillis();

                int exer_count = getExerCount(pro, v_code);

                if (extra.getMinHeartRate() == 0 || extra.getAvgHeartRate() == 0) {
                    Log.d(tag, "break save exer data.. reason: heartrate == 0");
                    return false;
                }
                if (save_start_time <= 0 || save_end_time <= 0) {
                    Log.d(tag, "break save exer data.. reason: savetime <= 0");
                    return false;
                }

                float consume_calorie = sumCalorie - preCalorie;
                mDatabase.setCoachActivityData(new CoachActivityData(0, pro.Code, getProgramTotalCount(), v_code, exer_count, save_start_time, save_end_time, Math.round(consume_calorie),
                        this.out[0], extra.getCount_percent(), 0, extra.getMinAccuracy(), extra.getMaxAccuracy(), extra.getAvgAccuracy(), extra.getMinHeartRate(),
                        stable, extra.getAvgHeartRate(), cmpHeartRate, point, 0, 0));

                return false;
            }
        }

        return true;
    }
}
