package kr.co.greencomm.middleware.wrapper;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.co.greencomm.middleware.bluetooth.BluetoothCommand;
import kr.co.greencomm.middleware.bluetooth.CommSender;
import kr.co.greencomm.middleware.bluetooth.ConnectStatus;
import kr.co.greencomm.middleware.bluetooth.LeCallback;
import kr.co.greencomm.middleware.bluetooth.RequestAction;
import kr.co.greencomm.middleware.bluetooth.ScanMode;
import kr.co.greencomm.middleware.bluetooth.StatePeripheral;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.main.BluetoothLEManager;
import kr.co.greencomm.middleware.main.ConfigManager;
import kr.co.greencomm.middleware.main.StressNManager;
import kr.co.greencomm.middleware.main.VideoManager;
import kr.co.greencomm.middleware.provider.CoachContract;
import kr.co.greencomm.middleware.provider.SQLHelper;
import kr.co.greencomm.middleware.service.MWBroadcastTop;
import kr.co.greencomm.middleware.utils.FileManager;
import kr.co.greencomm.middleware.utils.FirmVersion;
import kr.co.greencomm.middleware.utils.ProductCode;
import kr.co.greencomm.middleware.utils.StateApp;
import kr.co.greencomm.middleware.utils.container.ActivityData;
import kr.co.greencomm.middleware.utils.container.Battery;
import kr.co.greencomm.middleware.utils.container.CoachActivityData;
import kr.co.greencomm.middleware.utils.container.CommQueue;
import kr.co.greencomm.middleware.utils.container.DataBase;
import kr.co.greencomm.middleware.utils.container.SleepData;
import kr.co.greencomm.middleware.utils.container.TotalScoreData;
import kr.co.greencomm.middleware.utils.container.UserProfile;
import kr.co.greencomm.middleware.video.IViewComment;

/**
 * Created by jeyang on 2016-08-29.
 */
public class MWControlCenter {
    private static final String tag = MWControlCenter.class.getSimpleName();
    private static MWControlCenter ourInstance;

    /**
     * Variable
     **/
    public static ProductCode m_productCode = ProductCode.Fitness;
    private int programCode;

    private static String ROOT_PATH;

    /**
     * Callback interface
     **/
    private LeCallback leCB;
    private IViewComment viewCB;
    private FirmVersion firmCB;

    /**
     * Instance
     **/
    public BluetoothLEManager mBleManager;
    public VideoManager mVideoManager;
    public DataBase mDatabase;
    public ConfigManager mConfig;
    public SQLHelper mSQLHelper;
    private WeakReference<Context> WContext;

    private MWBroadcastTop mBrTop;


    //*** 규창
//************************* 코치 노말 스트레스 용 ***********************
    public StressNManager mStressNManager; // = StressNManager.getInstance()
//************************* 코치 노말 스트레스 용 ***********************


    public static MWControlCenter getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new MWControlCenter(context);
        }
        return ourInstance;
    }

    private MWControlCenter(Context context) {
        WContext = new WeakReference<>(context);
        mConfig = ConfigManager.getInstance(getContext());
        mDatabase = DataBase.getInstance();
        mSQLHelper = SQLHelper.getInstance(getContext());
        mBleManager = BluetoothLEManager.getInstance(getContext());
        mVideoManager = VideoManager.getInstance(getContext());

        //*** 규창
        //*************************코치 노말 스트레스 용 ***********************
        mStressNManager = StressNManager.getInstance(getContext());
        //*************************코치 노말 스트레스 용 ***********************

        mBrTop = new MWBroadcastTop(getContext());

        getRootPath();
        initCallback();

        int code = Preference.getProductCode(getContext());
        switch (code) {
            case 200003:
                m_productCode = ProductCode.Coach;
                break;
            case 220004:
                m_productCode = ProductCode.Fitness;
                break;
            default:
                m_productCode = ProductCode.Fitness;
        }

        if (Preference.getBluetoothMac(getContext()) == null) {
            mBleManager.setScanMode(ScanMode.MANUAL);
        }

        mBleManager.setTimeZoneOffsetForInit(getTzOffset());
        mVideoManager.setLanguageCode();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        WContext = null;
        mBleManager = null;
        mVideoManager = null;
        mDatabase = null;
        //*** 규창
        //*************************코치 노말 스트레스 용 ***********************
        mStressNManager = null;
        //*************************코치 노말 스트레스 용 ***********************
        mConfig = null;
        leCB = null;
        firmCB = null;
        mSQLHelper = null;
        viewCB = null;
    }

    private Context getContext() {
        return WContext.get();
    }

    private void initCallback() {
        if (leCB == null) {
            leCB = new LeCallback() {
                @Override
                public void onConnectionState(ConnectStatus state) {
                    if (state == ConnectStatus.STATE_CONNECTED) {
                        mBleManager.mBrTop.sendBroadcastConnectionSuccess();
                    }
                    mBleManager.mBrTop.sendBroadcastConnectionState(state);
                    Log.d(tag, "onConnectionState:" + state);
                }

                @Override
                public void onConnectionFailed() {
                    mBleManager.mBrTop.sendBroadcastConnectionFailed();
                    Log.d(tag, "onConnectionFailed");
                }
            };

            mBleManager.registerLeCallback(leCB);
        }

        if (viewCB == null) {
            viewCB = new IViewComment() {
                @Override
                public void onStressInform() {
                    // xxxxx
                }

                @Override
                public void onMainUi() {
                    mBrTop.sendBroadcastMainUI(mDatabase.getAccuracy(), mDatabase.getPoint(),
                            mDatabase.getCount()[0], mDatabase.getCalorie(), mDatabase.getHRCmp());
                }

                @Override
                public void onTopComment() {
                    mBrTop.sendBroadcastTopComment(mDatabase.getActivityName());
                }

                @Override
                public void onBottomComment() {
                    mBrTop.sendBroadcastBottomComment(mDatabase.getBottomComment());
                }

                @Override
                public void onWarnning() {
                    mBrTop.sendBroadcastWarnning(mDatabase.getHRWarnning());
                }

                @Override
                public void onTotalScore() {
                    TotalScoreData data = mDatabase.getTotalScore();
                    mBrTop.sendBroadcastTotalScore(data.getDuration(), data.getPoint(), data.getCount_percent(),
                            data.getAccuracy_percent(), data.getComment());
                }

                @Override
                public void onExerData() {
                    //provider
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            SQLHelper sql = SQLHelper.getInstance(getContext());
                            ContentResolver res = getContext().getContentResolver();
                            res.insert(CoachContract.Coach.CONTENT_URI,
                                    sql.addCoachActivityDataProvider(mDatabase.getCoachActivityData()).getValues());
                            mBrTop.sendBroadcastGenerateCoachExerData();
                        }
                    });
                }

                @Override
                public void onShowUI() {
                    mBrTop.sendBroadcastShowUI(mDatabase.getShowUi());
                }
            };

            DataBase.registCallback(viewCB);
        }

        if (firmCB == null) {
            firmCB = new FirmVersion() {
                @Override
                public void onFirmVersion() {

                }
            };

            DataBase.registFirmCallback(firmCB);
        }
    }

    public static short getTzOffset() {
        short offset;
        TimeZone tz = TimeZone.getDefault();
        if (tz.useDaylightTime())
            offset = (short) (tz.getRawOffset() / 1000 / 60 + 60);
        else
            offset = (short) (tz.getRawOffset() / 1000 / 60);
        return offset;
    }

    private void getRootPath() {
        ROOT_PATH = FileManager.getMainPath(getContext());
    }

    private void setProgram(String name) {
        String fullPath = ROOT_PATH + name;
        Log.d(tag, "xml name->"+name + " full path->"+fullPath);
        File fXmlFile = null;
        InputStream in = null;
        if (!FileManager.isExistFile(getContext(), name)) {
            Log.d(tag,"isExistFile not");
            // 리소스에서 선택해야함. 서비스는 앱의 리소스에 접근 가능한가?. 하겠지?
            int id = getContext().getResources().getIdentifier(name, "raw", "kr.co.greencomm.coachplus");
            in = getContext().getResources().openRawResource(id);
        } else {
            Log.d(tag,"isExistFile true");
            fXmlFile = new File(fullPath);
        }

        Document doc = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            if (fXmlFile != null) {
                doc = dBuilder.parse(fXmlFile);
            } else {
                doc = dBuilder.parse(in);
            }
            doc.getDocumentElement().normalize();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        mVideoManager.setProgram(doc.getDocumentElement());

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return :
     * @Parameter :
     * @Description : Sender Thread busy flag.
     */
    public boolean isBusySender() {
        return CommSender.isBusy();
    }

    /**
     * @return :
     * @Parameter :
     * @Description : Sender Thread data append!!.
     */
    public void appendSender(CommQueue data) {
        mBleManager.sender.append(data);
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 현재 장치의 User 밴드로 전달.
     */
    public void sendUserData() {
        mBleManager.sender.append(new CommQueue(BluetoothCommand.UserData, 0L, RequestAction.Start, false));
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 현재 입력된 프로그램의 쿼리 코스 코드 번호.
     */
    public String getCourseCodeForQuery() {
        return mVideoManager.getCourseCodeForQuery(programCode);
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 프로그램의 쿼리 코스 코드 번호 입력.
     */
    public void setProgramCode(int programCode) {
        this.programCode = programCode;
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 미들웨어에서 현재 사용할 제품 코드 입력 받음.
     */
    public void selectProduct(ProductCode code) {
        m_productCode = code;
        Preference.putProductCode(getContext(), code.getProductCode());
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 미들웨어에서 현재 사용할 제품 코드 입력 받음.
     */
    public ProductCode getSelectedProduct() {
        return m_productCode;
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 블루투스 베터리 정보 요청 전달
     */
    public void sendBatteryRequest() {
        mBleManager.sender.append(new CommQueue(BluetoothCommand.Battery, 0L, RequestAction.Start, true));
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 블루투스 활동 측정 명령 전달
     */
    public void sendActivityMeasure(boolean send, long time) {
        if (send) {
            mBleManager.sender.append(new CommQueue(BluetoothCommand.Activity, time, RequestAction.Start, false));
        } else {
            mBleManager.sender.append(new CommQueue(BluetoothCommand.Activity, 0L, RequestAction.End, false));
        }
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 블루투스 활동 측정 정보 요청
     */
    public void sendActivityInfo(long time) {
        mBleManager.sender.append(new CommQueue(BluetoothCommand.Activity, time, RequestAction.Inform, true));
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 블루투스 수면 측정 명령 전달
     */
    public void sendSleepMeasure(boolean send, long time) {
        if (send) {
            mBleManager.sender.append(new CommQueue(BluetoothCommand.Sleep, time, RequestAction.Start, false));
        } else {
            mBleManager.sender.append(new CommQueue(BluetoothCommand.Sleep, 0L, RequestAction.End, false));
        }
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 블루투스 수면 측정 정보 요청
     */
    public void sendSleepInfo(long time) {
        mBleManager.sender.append(new CommQueue(BluetoothCommand.Sleep, time, RequestAction.Inform, true));
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 블루투스 스트레스 측정 명령 전달
     */
    /*public void sendStressMeasure(boolean send, long time) {
        if (send) {
            mBleManager.sender.append(new CommQueue(BluetoothCommand.Stress, time, RequestAction.Start, false));
        } else {
            mBleManager.sender.append(new CommQueue(BluetoothCommand.Stress, 0L, RequestAction.End, false));
        }
    }*/
    public void sendStressMeasure(boolean send) {
        if (send) {
            mStressNManager.play();
            mBleManager.sender.append(new CommQueue(BluetoothCommand.Stress, 0L, RequestAction.Start, false));
        } else {
            mBleManager.sender.append(new CommQueue(BluetoothCommand.Stress, 0L, RequestAction.End, false));
            mStressNManager.end();
        }
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 블루투스 스트레스 측정 정보 요청
     */
    public void sendStressInfo(long time) {
        mBleManager.sender.append(new CommQueue(BluetoothCommand.Stress, time, RequestAction.Inform, true));
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 영상 play!!!
     */
    public void play() {
        mVideoManager.play();
        mBleManager.sender.append(new CommQueue(BluetoothCommand.Acc, 0L, RequestAction.Start, false));
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 영상 end!!!. play를 했으면 꼭 해줘야한다.
     */
    public void end() {
        mBleManager.sender.append(new CommQueue(BluetoothCommand.Acc, 0L, RequestAction.End, false));
        mVideoManager.end();
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 영상의 현재 포지션. (sec)
     */
    public void setCurrentTimePosition(int position) {
        mVideoManager.setCurrentTimePosition(position);
    }

    /**
     * @return :
     * @Parameter :
     * @Description : xml 운동 데이터 설정.
     */
    public void setXmlProgram(String name) {
        mVideoManager.setLanguageCode();
        setProgram(name);
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 앱의 Logout을 미들웨어에 알림. MW의 DB를 초기화하고, 블루투스 접속을 종료한다.
     */
    public void logout() {
        mBleManager.setIsLiveApp(false);
        mBleManager.stopBluetooth();
        Preference.putAutoLogin(getContext(), false);
    }

    /**
     * @return :
     * @Parameter : enum  StateApp (0:무응답, 1:앱 준비중, 2:앱 정상 시작, 3:앱 종료)
     * @Description : 앱의 생존 상태를 미들웨어로 입력한다.
     */
    public void setIsLiveApplication(StateApp state) {
        mBleManager.setIsLiveApp(state == StateApp.STATE_NORMAL);
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 앱의 블루투스 접속 시도 요청. 회원 가입 절차에서 밴드를 찾는 동작에서 수행.
     * 5초간 근처의 밴드를 찾고 연결을 수행하며, 연결에 성공하면 블루투스 장치 정보를 저장한다.
     * 기본 사용자 정보가 입력되어 있지 않다면 시작을 실패한다. 최초 연결 시, 주변에 같은 종류의 밴드가 복수 존재하면 시작을 실패한다.
     */
    public void tryConnectionBluetooth() {
        mBleManager.tryBluetooth();
    }

    /**
     * @return :
     * @Parameter :
     * @Description : 블루투스 접속 종료.
     */
    public void stopBluetooth() {
        mBleManager.stopBluetooth();
    }

    /**
     * @return : enum ConnectStatus
     * @Parameter :
     * @Description : MW의 블루투스 연결 상태 정보를 얻는다.
     */
    public ConnectStatus getConnectionState() {
        return mBleManager.getConnectionState();
    }

    /**
     * @return :
     * @Parameter : scanMode (enum ScanMode)
     * @Description : 블루투스의 스캔 모드를 설정한다. Auto: 자동 스캔(이미 생성된 DB를 바탕으로 자동 접속. 리스트 자동 삭제)
     * , MANUAL: 수동 접속(사용자의 접속할 기기를 선택. 리스트를 삭제하지 않음). 기본은 AUTO.
     */
    public void setScanMode(ScanMode scanMode) {
        mBleManager.setScanMode(scanMode);
    }

    /**
     * @return : 성공 여부.
     * @Parameter : String name (블루투스 장치명)
     * @Description : 블루투스 장치에 접속한다. 없는 장치 이거나, 접속할수 없는 경우 false 반환.
     */
    public boolean connect(String name) {
        return mBleManager.requestConnect(name);
    }

    /***************
     * Video
     ***************/
    public String getActivityName() {
        return mDatabase.getActivityName();
    }

    public String getBottomComment() {
        return mDatabase.getBottomComment();
    }

    public TotalScoreData getTotalScore() {
        return mDatabase.getTotalScore();
    }

    public int getAccuracy() {
        return mDatabase.getAccuracy();
    }

    public int getPoint() {
        return mDatabase.getPoint();
    }

    public Integer[] getCount() {
        return mDatabase.getCount();
    }

    public float getVideoCalorie() {
        return mDatabase.getCalorie();
    }

    public int getHRCmp() {
        return mDatabase.getHRCmp();
    }

    public String getHRWarnning() {
        return mDatabase.getHRWarnning();
    }

    public CoachActivityData getExerData() {
        return mDatabase.getCoachActivityData();
    }

    /***************
     * Bluetooth
     ***************/
    public Battery getBattery() {
        return mDatabase.getBattery();
    }

    public Short getStep() {
        return mDatabase.getStep();
    }

    public Double getTotalActivityCalorie() {
        return mDatabase.getTotal_activity_calorie();
    }

    public Double getTotalSleepCalorie() {
        return mDatabase.getTotal_sleep_calorie();
    }

    public Double getTotalDailyCalorie() {
        return mDatabase.getTotal_daily_calorie();
    }

    public Double getTotalCoachCalorie() {
        return mDatabase.getTotal_coach_calorie();
    }

    public ActivityData getActivityData() {
        return mDatabase.getActivityData();
    }

    public SleepData getSleepData() {
        return mDatabase.getSleepData();
    }

    public Short getStress() {
        return mDatabase.getStress();
    }

    public StatePeripheral getStatePeripheral() {
        Short state = mDatabase.getState();
        switch (state) {
            case 1:
                return StatePeripheral.IDLE;
            case 2:
                return StatePeripheral.Activity;
            case 3:
                return StatePeripheral.Stress;
            case 4:
                return StatePeripheral.Sleep;
            default:
                return StatePeripheral.IDLE;
        }
    }

    public String getVersion() {
        return mDatabase.getVersion();
    }

    /***************
     * User
     ***************/
    public UserProfile getProfile() {
        return mConfig.getUserProfile();
    }

    public void setProfile(UserProfile profile) {
        mConfig.setUserProfile(profile.getAge(), profile.getSex(), profile.getHeight(), profile.getWeight(), profile.getGoalWeight());
        mDatabase.setProfile(profile);
    }

    public int getDietPeriod() {
        return mConfig.getUserDietPeriod();
    }

    public void setDietPeriod(int period) {
        mConfig.setUserDietPeriod(period);
        mDatabase.setDietPeriod(period);
    }


    //*** 규창
    //*************************코치 노말 스트레스 용 ***********************

    /**
     * @return :
     * @Parameter :
     * @Description : 블루투스 코치 노말 스트레스 측정 명령 전달
     */
    public void sendNomalCoachStressMeasure(boolean send) {
        Log.i(tag, "sendNomalCoachStressMeasure=" + send);
        if (send) {
            mStressNManager.play();
            mBleManager.sender.append(new CommQueue(BluetoothCommand.Acc, 0L, RequestAction.Start, false));
        } else {
            mBleManager.sender.append(new CommQueue(BluetoothCommand.Acc, 0L, RequestAction.End, false));
            mStressNManager.end();
        }
    }

    public Short StressNResult() {
        //print("노티옴!!!! \(mDatabase.StressNResult)")
        return mDatabase.getStressResult();
    }
    //*************************코치 노말 스트레스 용 ***********************

    //규창 16.12.27 강제 동기화 메서드 개방
    public void sendForceRefresh(){
        if (!isBusySender()) {
            CommSender.getInstance(getContext()).notifySync(true);
            mBleManager.sender.append(new CommQueue(BluetoothCommand.StepCount_Calorie, System.currentTimeMillis(), RequestAction.Start, true));
        }
    }

    //규창 17.01.09 android.os.DeadObjectException 방지 위해 OS로 인한 블루투스 상태변화시 객체 주기적으로 리셋
    public void refreshBLEObject(){
        Log.i(tag, "BLE RefreshBLEObject");
        mBleManager.emergencyReconnect();
    }

}
