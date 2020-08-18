package kr.co.greencomm.middleware.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.KIST.kistAART.KIST_AART;
import com.KIST.kistAART.KIST_AART_output;

import java.lang.ref.WeakReference;
import java.util.Locale;

import kr.co.greencomm.middleware.tool.Program;
import kr.co.greencomm.middleware.tool.TotalScoreDisplay;
import kr.co.greencomm.middleware.tool.UiDisplay;
import kr.co.greencomm.middleware.utils.MessageIndex;
import kr.co.greencomm.middleware.utils.container.TotalScoreData;
import kr.co.greencomm.middleware.video.CalculateBase;
import kr.co.greencomm.middleware.video.ExtraData;
import kr.co.greencomm.middleware.video.INordicFormat;
import kr.co.greencomm.middleware.video.VideoBase;


/**
 * 필라테스 비디오 재생과 관련하여 Surface를 제어하는 Manager. 재생되는 video에 따라 엔진의 구동이 달라질것으로 생각되며,
 * 그 제어를 하는 Manager. 즉, Kist 엔진과 App간의 연동.
 *
 * @author user
 */
public final class VideoManager extends VideoBase {
    private static final String tag = "VideoManager";

    private static final String version = "1.1.8.13";
    /**
     * Variable
     **/
    private int t_point = 0, t_count_percent = 0, t_accuracy_percent;
    private String t_comment = "";
    /**
     * 출산부 변수 end
     **/

    private int sumAccuracy = 0;
    private int avgAccuracy = 0;
    private int minAccuracy = 0;
    private int maxAccuracy = 0;
    private int maxHeartRate = 0;
    private int minHeartRate = 0;
    private int avgHeartRate = 0;
    private int sumHeartRate = 0;
    private int count_percent = 0;
    private int size_hr_queue = 0;

    private boolean setFormula = false;

    private WeakReference<Context> WContext;

    private float preCount = 0;

    private int interval_AccuracyLock = 2;// 정확도 1초뒤에 지움

    private AsyncTask<Integer, Void, Void> mTask;

    private static VideoManager mVManager = null;

    private final static int LEE_TRAINER_NUMBER = 1;
    private final static int HONG_TRAINER_NUMBER = 2;
    private final static int CHILDBIRTH_NUMBER = 3;

    public final static int SET_VIDEO_ID_1 = 3;
    public final static int SET_VIDEO_ID_2 = 4;
    public final static int SET_VIDEO_ID_3 = 2;
    public final static int SET_VIDEO_ID_4 = HONG_TRAINER_NUMBER * 100 + 1;
    public final static int SET_VIDEO_ID_5 = HONG_TRAINER_NUMBER * 100 + 2;
    public final static int SET_VIDEO_ID_6 = LEE_TRAINER_NUMBER * 100 + 1;
    public final static int SET_VIDEO_ID_7 = LEE_TRAINER_NUMBER * 100 + 2;
    public final static int SET_VIDEO_ID_8 = CHILDBIRTH_NUMBER * 100 + 1; // 1~4주차
    public final static int SET_VIDEO_ID_9 = CHILDBIRTH_NUMBER * 100 + 2; // full

    /* AART Engine */
    private KIST_AART m_KIST = new KIST_AART();
    private KIST_AART_output mOut = new KIST_AART_output();

    /**
     * Handler
     **/
    private Handler mHandler, mHandlerD;
    private Handler mHandler_AccuracyLock;
    private Handler mHandler_avgHR;

    /**
     * Runnable
     **/
    private Runnable mRunnable_AccuracyLock;
    private Runnable mRunnable_avgHR;

    public static VideoManager getInstance(Context context) {
        if (mVManager == null) {
            mVManager = new VideoManager(context);
        }
        return mVManager;
    }

    private VideoManager(Context context) {
        super(context);
        WContext = new WeakReference<>(context);
        Log.d(tag, "coach version : " + version);

        mConfig = ConfigManager.getInstance(getContext());
        mBle = BluetoothLEManager.getInstance(getContext());
        if (mHandler == null)
            mHandler = new Handler();
        if (mHandlerD == null)
            mHandlerD = new Handler();
        if (mHandler_avgHR == null)
            mHandler_avgHR = new Handler();

        if (mRunnable_avgHR == null) {
            mRunnable_avgHR = new Runnable() {
                @Override
                public void run() {
                    set_HRLock = false;
                }
            };
        }
        if (mRunnable_AccuracyLock == null) {
            mRunnable_AccuracyLock = new Runnable() {
                @Override
                public void run() {
//                    set_AccuracyLock = false;
                    out[1] = 0;
                    mDatabase.setAccuracy(out[1]);
                    mDatabase.setPoint(CalculateBase.getPoint(count_percent, avgAccuracy));
                }
            };
        }
    }

    protected Context getContext() {
        return WContext.get();
    }

    @Override
    protected void initInstance() {
        m_KIST = new KIST_AART();
        mOut = new KIST_AART_output();

        try {
            HRQueue.clear();
            accuracyQueue.clear();
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        setFormula = false;
        is_R_Video = false;

        isPlaying = false;
        setPlay = true;

        releaseAccuracyLock();

        set_HRLock = false;
        mHandler_avgHR.removeCallbacks(mRunnable_avgHR);
        CalculateBase.reset();

        for (int i = 0; i < out.length; i++) {
            out[i] = 0;
        }

        currentPosition = 0;
        t_count_percent = t_accuracy_percent = t_point = 0;
        t_comment = "";

        sumAccuracy = 0;
        avgAccuracy = 0;
        minAccuracy = 0;
        maxAccuracy = 0;
        maxHeartRate = 0;
        minHeartRate = 0;
        avgHeartRate = 0;
        sumHeartRate = 0;
        count_percent = 0;
        size_hr_queue = 0;

        preCalorie = 0;

        sumCalorie = 0;
        save_start_time = 0;
    }

    /**
     * 재생을 담당하며 자동으로 Raw 데이터를 기록. 재생 시, 설정된 파일명으로 Raw 데이터를 기록한다. Kist 엔진과의 연동
     * 시작을 담당.
     *
     * @return true:성공, false:실패(이미 재생중. end function을 실행 시켜주세요.)
     */
    @Override
    public boolean play() {
        if (!super.play())
            return false;
        else {
            BluetoothLEManager.registDataCallback(new INordicFormat() {
                /**
                 * float[] sensor로 raw 데이터가 들어옴.
                 */
                @Override
                public void onSensor(double[] sensor) {
                    /**** float[] sensor 내용 ****/
                    /** 배열의 0,1,2 = 가속도 x,y,z **/
                    /** 배열의 3,4,5 = 자이로 x,y,z **/
                    /** 배열의 6 = 기압 **/
                    /** 배열의 7 = 심박 **/

                    if (!setPlay)
                        return;

                    if (pro.Code == 0)
                        return;

                    /**
                     * KIST 엔진 실행
                     */
                    mOut = m_KIST.fn_AART_Cal_parameter(sensor);

                    /** 심박수 **/
                    int hearteRate = (int) sensor[7];
                    UiDisplay uiDisplay = onUiDisplay(pro, currentPosition);
                    switchVideo(uiDisplay.getShowUi());

                    if (hearteRate != 0)
                        HRQueue.insert(hearteRate);

                    /**
                     * 최대 심박수 대비 현재 심박수 계산(%)
                     */
                    int avgHR = CalculateBase.avgHeartRate(hearteRate);
                    if (!set_HRLock) {
                        onHeartRateCompared(avgHR);
                        /**
                         * 심박수 감시.
                         */
                        onHeartRateWarnning(avgHR);
                        set_HRLock = true;
                        mHandler_avgHR.postDelayed(mRunnable_avgHR, 5 * 1000);
                    }

                    /**
                     * KIST 엔진에서 어느정도 간격으로 데이터를 갱신하려는지 모름. KIST 엔진의 갱신에 맞추어서
                     * 심박수를 계산해야 함. 현재는 임시로 5초 간격으로 구성해놓음. sumInterval = 5;
                     */
                    if (!setFormula) {
                        setFormula = true;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int sumHR = 0;
                                int HR = 0;

                                int size_hr_queue = HRQueue.size();
                                VideoManager.this.size_hr_queue += size_hr_queue;
                                int minus = 0;
                                if (size_hr_queue > 0) {
                                    for (int i = 0; i < size_hr_queue; i++) {
                                        try {
                                            HR = HRQueue.remove();
                                        } catch (ArrayIndexOutOfBoundsException e) {
                                            if (VideoManager.this.size_hr_queue > 0)
                                                VideoManager.this.size_hr_queue--;
                                            minus++;
                                            continue;
                                        }
                                        sumHR += HR;
                                        if (maxHeartRate < HR)
                                            maxHeartRate = HR;
                                        if (minHeartRate > HR)
                                            minHeartRate = HR;

                                        if (minHeartRate == 0)
                                            minHeartRate = HR;
                                    }
                                }
                                size_hr_queue -= minus;

                                /** 5초 딜레이를 주면서, 수집한 데이터를 계산함 **/
                                float retCal = 0;
                                if (size_hr_queue > 0)
                                    retCal = CalculateBase.formulaHeartRate(sumHR / size_hr_queue, mDatabase.getProfile());
                                if (retCal > 0)
                                    sumCalorie += retCal;

                                sumHeartRate += sumHR;
                                if (VideoManager.this.size_hr_queue != 0)
                                    avgHeartRate = sumHeartRate / VideoManager.this.size_hr_queue;

                                // speed
                                mDatabase.setCalorie(Float.parseFloat(String.format(Locale.getDefault(), "%.2f", sumCalorie)));
//                                if (mIView != null) {
//                                    mIView.onKISTOutput_Calorie(NumberFormatUtil.convertPoint(sumCalorie), videoN);
//                                }

                                setFormula = false;
                            }
                        }, CalculateBase.sumInterval * 1000);
                    }

                    mDatabase.setActivityName(seperateActivityNameForLanguage(uiDisplay.getName()));
                    onTotalScore(pro, currentPosition);
//                    onActivityName(videoID, currentPosition);
                    if (!getResult(pro, mOut, new ExtraData(avgHR, count_percent, minAccuracy, maxAccuracy, avgAccuracy, minHeartRate, maxHeartRate, avgHeartRate))) {
                        preCalorie = sumCalorie;
                        for (int i = 0; i < out.length; i++) {
                            out[i] = 0;
                        }
                        count_percent = 0;
                        minAccuracy = 0;
                        maxAccuracy = 0;
                        avgAccuracy = 0;
                        minHeartRate = 0;
                        maxHeartRate = 0;
                        avgHeartRate = 0;
                        sumHeartRate = 0;
                        sumAccuracy = 0;
                        size_hr_queue = 0;

                        save_start_time = 0;
                    }

                    int refCount = getExerCount(pro, v_code);
                    mDatabase.setCount(new Integer[]{out[0], refCount});

                    int acc = 0;
                    int size_accuracy_queue = accuracyQueue.size();
                    if (size_accuracy_queue > 0)
                        for (int i = 0; i < size_accuracy_queue; i++) {
                            acc = accuracyQueue.remove();
                            acc = acc > 100 ? 100 : acc;

                            sumAccuracy += acc;
                            if (maxAccuracy < acc)
                                maxAccuracy = acc;
                            if (minAccuracy > acc)
                                minAccuracy = acc;

                            if (minAccuracy == 0)
                                minAccuracy = acc;
                        }

                    if (out[0] != 0)
                        avgAccuracy = VideoManager.this.sumAccuracy / out[0] > 100 ? 100 : VideoManager.this.sumAccuracy / out[0];
                    else
                        avgAccuracy = 0;

                    count_percent = (int) (100f * out[0] / refCount > 100 ? 100 : 100f * out[0] / refCount);
                    if (out[0] != preCount) {
                        releaseAccuracyLock();
                        mDatabase.setAccuracy(out[1] > 100 ? 100 : out[1]);
                        mDatabase.setPoint(CalculateBase.getPoint(count_percent, avgAccuracy));

                        setAccuracyLock();
                    } /*else if (!getAccuracyLock()) {
                        out[1] = 0;
                        mDatabase.setAccuracy(out[1]);
                        mDatabase.setPoint(CalculateBase.getPoint(count_percent, avgAccuracy));
                    }*/
                    preCount = out[0];
                }
            });

            return true;
        }
    }

    /**
     * 동영상 종료. 재생이 끝나면 항상 실행해야 함.
     */
    @Override
    public void end() {
        super.end();
        releaseAccuracyLock();

        mHandler_avgHR.removeCallbacks(mRunnable_avgHR);

//        if (mTask != null)
//            mTask.cancel(true);
        BluetoothLEManager.unregistDataCallback();
    }

    /**
     * 현재 동영상 재생을 알리는 flag setting
     *
     * @param setPlay 현재 동영상의 재생 상태. (true:재생중, false:중지)
     */
    public void setPlaying(boolean setPlay) {
        this.setPlay = setPlay;
    }

    /**
     * 현 동영상의 재생 위치를 알림.
     *
     * @param currentPosition 현재 동영상의 재생 위치. (sec 단위)
     */
    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public boolean isRVideo() {
        return is_R_Video;
    }

    private void onHeartRateCompared(int hr) {
        mDatabase.setHRCmp(CalculateBase.getHeartRateCompared(hr, mDatabase.getProfile()));
    }

    private String seperateActivityNameForLanguage(String name) {
        String[] seperate = name.split("\\/");
        if (seperate.length < 4) {
            return name;
        }

        String lang = getLanguageCode();
        if (lang.equals("KO")) {
            return seperate[0];
        } else if (lang.equals("JP")) {
            return seperate[1];
        } else if (lang.equals("EN")) {
            return seperate[2];
        } else if (lang.equals("ZH")) {
            return seperate[3];
        } else {
            return name;
        }
    }

    /**
     * 정확한 심박수가 들어온다는 가정하에 comment를 하기 위한 함수.
     *
     * @param hr
     */
    private void onHeartRateWarnning(int hr) {
        float[] hrZone = CalculateBase.getHeartRateDangerZone(mDatabase.getProfile());
        if (hrZone[1] < hr) {// 최대 심박수의 80%
            //mPerip.startVibrate(); // 밴드에서 자동으로 울리므로 막음.
            mDatabase.setHRWarnning(TotalScoreDic.get(MessageIndex.HEART.getIndex()));
        }
    }

    @Override
    protected void setTotalScore(int point, int count_percent, int accuracy_percent, String comment) {
        t_point = point;
        t_count_percent = count_percent;
        t_accuracy_percent = accuracy_percent;
        t_comment = comment;
    }

    private void onTotalScore(Program pro, int currentPosition) {
        TotalScoreDisplay score = pro.getToTalScoreDisplay(currentPosition);
        if (score.getShowScore()) {
            if (t_comment.equals("")) {
                return;
            }

            mDatabase.setTotalScore(new TotalScoreData(score.getDuration() * 1000, t_point, t_count_percent, t_accuracy_percent, t_comment));

            t_point = 0;
            t_count_percent = 0;
            t_accuracy_percent = 0;
            t_comment = "";
        }
    }

    /**
     * 정확도 lock 설정. 6초 안으로 0의 값이 들어오면 무시.
     */
    private void setAccuracyLock() {
        if (mHandler_AccuracyLock != null) {
            return;
        }

        mHandler_AccuracyLock = new Handler(Looper.getMainLooper());
        mHandler_AccuracyLock.postDelayed(mRunnable_AccuracyLock, interval_AccuracyLock * 1000);
        set_AccuracyLock = true;
    }

    /**
     * 정확도 lock 해제.
     */
    private void releaseAccuracyLock() {
        if (mHandler_AccuracyLock == null)
            return;

        mHandler_AccuracyLock.removeCallbacks(mRunnable_AccuracyLock);
        mHandler_AccuracyLock = null;
        set_AccuracyLock = false;
    }

    /**
     * 현재 정확도 lock 확인.
     *
     * @return 현재 정확도 lock flag.
     */
    private boolean getAccuracyLock() {
        return set_AccuracyLock;
    }
}