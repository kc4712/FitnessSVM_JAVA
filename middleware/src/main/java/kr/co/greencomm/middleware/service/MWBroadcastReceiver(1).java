package kr.co.greencomm.middleware.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import kr.co.greencomm.middleware.bluetooth.BluetoothCommand;
import kr.co.greencomm.middleware.bluetooth.ConnectStatus;
import kr.co.greencomm.middleware.bluetooth.RequestAction;
import kr.co.greencomm.middleware.bluetooth.ScanMode;
import kr.co.greencomm.middleware.main.BluetoothLEManager;
import kr.co.greencomm.middleware.utils.MessageClass;
import kr.co.greencomm.middleware.utils.NoticeIndex;
import kr.co.greencomm.middleware.utils.PhoneHelper;
import kr.co.greencomm.middleware.utils.ProductCode;
import kr.co.greencomm.middleware.utils.SMSHelper;
import kr.co.greencomm.middleware.utils.StateApp;
import kr.co.greencomm.middleware.utils.container.ActivityData;
import kr.co.greencomm.middleware.utils.container.Battery;
import kr.co.greencomm.middleware.utils.container.CommQueue;
import kr.co.greencomm.middleware.utils.container.SleepData;
import kr.co.greencomm.middleware.utils.container.UserProfile;
import kr.co.greencomm.middleware.wrapper.MWControlCenter;
import no.nordicsemi.android.dfu.DfuBaseService;

public class MWBroadcastReceiver extends BroadcastReceiver {
    private static final String tag = MWBroadcastReceiver.class.getSimpleName();

    /**
     * actions list
     **/
    public static final class Action {
        public static final String AUTHORITY = "kr.co.greencomm.ibody24.coach";
        public static final String EXTRA_NAME_INT_1 = "value_int_1";
        public static final String EXTRA_NAME_INT_2 = "value_int_2";
        public static final String EXTRA_NAME_INT_3 = "value_int_3";
        public static final String EXTRA_NAME_INT_4 = "value_int_4";
        public static final String EXTRA_NAME_INT_ARRAY = "value_int_array";
        public static final String EXTRA_NAME_SHORT_1 = "value_short_1";
        public static final String EXTRA_NAME_SHORT_2 = "value_short_2";
        public static final String EXTRA_NAME_SHORT_3 = "value_short_3";
        public static final String EXTRA_NAME_FLOAT_1 = "value_float_1";
        public static final String EXTRA_NAME_FLOAT_2 = "value_float_2";
        public static final String EXTRA_NAME_FLOAT_3 = "value_float_3";
        public static final String EXTRA_NAME_FLOAT_4 = "value_float_4";
        public static final String EXTRA_NAME_DOUBLE_1 = "value_double_1";
        public static final String EXTRA_NAME_DOUBLE_2 = "value_double_2";
        public static final String EXTRA_NAME_LONG_1 = "value_long_1";
        public static final String EXTRA_NAME_BOOL_1 = "value_bool_1";
        public static final String EXTRA_NAME_FLOAT_ARRAY = "value_float_array";
        public static final String EXTRA_NAME_DOUBLE_ARRAY = "value_double_array";
        public static final String EXTRA_NAME_STRING_1 = "value_string_1";
        public static final String EXTRA_NAME_STRING_ARRAY = "value_string_array";
        public static final String EXTRA_NAME_BYTE_ARRAY = "value_byte_array";
        public static final String EXTRA_NAME_SHORT_ARRAY = "value_short_array";
        public static final String EXTRA_NAME_LONG_ARRAY = "value_long_array";
        public static final String EXTRA_NAME_OBJECT = "value_object";
        /**
         * ACTION_START_SERVICE
         *
         * @Extra_Name :
         * @Extra_Data :
         * @Description : 앱의 MW Service 시작 요청 방송.
         */
        public static final String ACTION_START_SERVICE = "kr.co.greencomm.ibody24.coach.Mw.startService";

        public static final class Vm {
            public static final String MID = Vm.class.getSimpleName();

            public static final String ACTION_APP_PLAY = AUTHORITY + "." + MID + ".appPlay";
            public static final String ACTION_APP_END = AUTHORITY + "." + MID + ".appEnd";

            public static final String ACTION_APP_SET_PROGRAM_CODE = AUTHORITY + "." + MID + ".appSetProgramCode";

            public static final String ACTION_APP_SET_CURRENT_TIME_POSITION = AUTHORITY + "." + MID + ".appSetCurrentTimePosition";

            public static final String ACTION_APP_DOWNLOAD_XML = AUTHORITY + "." + MID + ".appDownloadXml";

            public static final String ACTION_APP_GET_QUERY_CODE = AUTHORITY + "." + MID + ".appGetQueryCode";
            public static final String ACTION_MW_GET_QUERY_CODE = AUTHORITY + "." + MID + ".mwGetQueryCode";

            public static final String ACTION_MW_MAINUI = AUTHORITY + "." + MID + ".mwMainUI";
            public static final String ACTION_MW_TOP_COMMENT = AUTHORITY + "." + MID + ".mwTopComment";
            public static final String ACTION_MW_BOTTOM_COMMENT = AUTHORITY + "." + MID + ".mwBottomComment";
            public static final String ACTION_MW_WARNNING = AUTHORITY + "." + MID + ".mwWarnning";
            public static final String ACTION_MW_TOTAL_SCORE = AUTHORITY + "." + MID + ".mwTotalScore";
            public static final String ACTION_MW_SHOWUI = AUTHORITY + "." + MID + ".mwShowUI";
        }

        public static final class Am {
            public static final String MID = Am.class.getSimpleName();

            //추가분... coach plus
            public static final String ACTION_MW_STEP_CALORIE = AUTHORITY + "." + MID + ".mwStepCalorie";
            public static final String ACTION_APP_STEP_CALORIE = AUTHORITY + "." + MID + ".appStepCalorie";

            public static final String ACTION_MW_ACTIVITY_DATA = AUTHORITY + "." + MID + ".mwActivityData";
            public static final String ACTION_APP_ACTIVITY_DATA = AUTHORITY + "." + MID + ".appActivityData";

            public static final String ACTION_MW_SLEEP_DATA = AUTHORITY + "." + MID + ".mwSleepData";
            public static final String ACTION_APP_SLEEP_DATA = AUTHORITY + "." + MID + ".appSleepData";

            public static final String ACTION_MW_STRESS_DATA = AUTHORITY + "." + MID + ".mwStressData";
            public static final String ACTION_APP_STRESS_DATA = AUTHORITY + "." + MID + ".appStressData";

            public static final String ACTION_MW_STRESS_ERR = AUTHORITY + "." + MID + ".mwStressErr";

            public static final String ACTION_MW_GENERATE_COACH_EXER_DATA = AUTHORITY + "." + MID + ".mwGenerateCoachExerData";

            public static final String ACTION_MW_GENERATE_ACTIVITY_DATA = AUTHORITY + "." + MID + ".mwGenerateActivityData";
        }

        public static final class Ec {
            public static final String MID = Ec.class.getSimpleName();
            /**
             * ACTION_APP_BATTERY
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : 앱의 베터리 정보 요청 방송.
             */
            public static final String ACTION_APP_BATTERY = AUTHORITY + "." + MID + ".appBattery";
            /**
             * ACTION_MW_BATTERY
             *
             * @Extra_Name : EXTRA_NAME_BYTE_ARRAY
             * @Extra_Data : byte[] battery (battery[0]= 0:미연결, 1:미충전,2:충전중,3:충전완료. battery[1]=남은 용량(%).)
             * @Description : MW의 베터리 정보 응답 방송. 정보가 갱신되는 경우 자동 방송 수행.
             */
            public static final String ACTION_MW_BATTERY = AUTHORITY + "." + MID + ".mwBattery";

            /**
             * ACTION_APP_USER_PROFILE
             *
             * @Extra_Name : EXTRA_NAME_INT_1
             * EXTRA_NAME_INT_2
             * EXTRA_NAME_INT_3
             * EXTRA_NAME_INT_4
             * EXTRA_NAME_FLOAT_1
             * EXTRA_NAME_FLOAT_2
             * @Extra_Data : int age
             * int sex
             * int job
             * int height
             * float weight
             * float goal_weight
             * @Description : 앱의 사용자 정보 전달 방송. 조건에 맞는 값이 들어오지 않으면 에러를 반환한다.(성공조건:나이는 2이상, 성별은 1 혹은 2, job은 1~4)
             */
            public static final String ACTION_APP_USER_PROFILE = AUTHORITY + "." + MID + ".appUserProfile";
            /**
             * ACTION_MW_USER_PROFILE
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : MW의 사용자 정보 요청 방송.
             */
            public static final String ACTION_MW_USER_PROFILE = AUTHORITY + "." + MID + ".mwUserProfile";

            /**
             * ACTION_APP_USER_DIETPERIOD
             *
             * @Extra_Name : EXTRA_NAME_INT_1
             * @Extra_Data : int diet_period (주 단위 감량기간)
             * @Description : 앱의 감량기간 정보 전달 방송. diet_period는 양수만 허용한다.
             */
            public static final String ACTION_APP_USER_DIETPERIOD = AUTHORITY + "." + MID + ".appUserDietPeriod";
            /**
             * ACTION_MW_USER_DIETPERIOD
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : MW의 감량기간 정보 요청 방송.
             */
            public static final String ACTION_MW_USER_DIETPERIOD = AUTHORITY + "." + MID + ".mwUserDietPeriod";

            /**
             * ACTION_APP_LOGOUT
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : 앱의 Logout의 알림 방송. MW의 DB를 초기화하고, 블루투스 접속을 종료한다.
             */
            public static final String ACTION_APP_LOGOUT = AUTHORITY + "." + MID + ".appLogout";

            /**
             * ACTION_APP_LOGIN
             *
             * @Extra_Name : EXTRA_NAME_STRING_ARRAY
             * @Extra_Data : String info[] (info[0] : UserCode, info[1] : Mac, info[2] : email, info[3] : password)
             * @Description : 앱에서 유저가 로그인 하는 경우. 로그인이 성공한 뒤, 호출되어야 한다. [로그인 정보가 정확해야한다. (UserCode, Mac, email, password)]
             */
            public static final String ACTION_APP_LOGIN = AUTHORITY + "." + MID + ".appLogin";

            public static final String ACTION_APP_SELECT_PRODUCT = AUTHORITY + "." + MID + ".appSelectProduct";
            public static final String ACTION_APP_GET_SELECTED_PRODUCT = AUTHORITY + "." + MID + ".appGetSelectedProduct";
            public static final String ACTION_MW_GET_SELECTED_PRODUCT = AUTHORITY + "." + MID + ".mwGetSelectedProduct";
        }

        public static final class Bm {
            public static final String MID = Bm.class.getSimpleName();
            /**
             * ACTION_APP_TRY_CONNECTION_BLUETOOTH
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : 앱의 블루투스 접속 시도 요청 방송. 회원 가입 절차에서 밴드를 찾는 동작에서 수행.
             * 5초간 근처의 밴드를 찾고 연결을 수행하며, 연결에 성공하면 블루투스 장치 정보를 저장한다.
             * 기본 사용자 정보가 입력되어 있지 않다면 시작을 실패한다. 최초 연결 시, 주변에 같은 종류의 밴드가 복수 존재하면 시작을 실패한다.
             */
            public static final String ACTION_APP_TRY_CONNECTION_BLUETOOTH = AUTHORITY + "." + MID + ".appTryConnectionBluetooth";

            /**
             * ACTION_MW_CONNECTION_FAILED
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : MW의 블루투스 연결 성공 알림 방송.
             */
            public static final String ACTION_MW_CONNECTION_SUCCESS = AUTHORITY + "." + MID + ".mwConnectionSuccess";

            /**
             * ACTION_MW_CONNECTION_FAILED
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : MW의 블루투스 연결 실패 알림 방송.
             */
            public static final String ACTION_MW_CONNECTION_FAILED = AUTHORITY + "." + MID + ".mwConnectionFailed";

            /**
             * ACTION_APP_CONNECTION_STATE
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : 앱의 블루투스 연결 상태 정보 요청 방송.
             */
            public static final String ACTION_APP_CONNECTION_STATE = AUTHORITY + "." + MID + ".appConnectionState";
            /**
             * ACTION_MW_CONNECTION_STATE
             *
             * @Extra_Name : EXTRA_NAME_INT_1
             * @Extra_Data : int state (0:연결종료, 1:연결중, 2:연결완료)
             * @Description : MW의 블루투스 연결 상태 정보 응답 방송. 앱에서 요청이 오는 경우 방송 수행.
             */
            public static final String ACTION_MW_CONNECTION_STATE = AUTHORITY + "." + MID + ".mwConnectionState";

            /**
             * ACTION_APP_DEVICE_INFORMATION
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : 앱의 블루투스 장치 정보 요청 방송.
             */
            public static final String ACTION_APP_DEVICE_INFORMATION = AUTHORITY + "." + MID + ".appDeviceInformation";
            /**
             * ACTION_MW_DEVICE_INFORMATION
             *
             * @Extra_Name : EXTRA_NAME_STRING_ARRAY
             * @Extra_Data : String[] info (info[0]=name(장치 이름), info[1]=mac(장치 Mac정보))
             * @Description : MW의 블루투스 장치 정보 응답 방송. 연결이 이루어지지 않은 경우 null 반환.
             */
            public static final String ACTION_MW_DEVICE_INFORMATION = AUTHORITY + "." + MID + ".mwDeviceInformation";

            /**
             * ACTION_APP_START_FIRMUP
             *
             * @Extra_Name : EXTRA_NAME_STRING_1
             * @Extra_Data : String path (외부저장소의 펌웨어 파일 경로)
             * @Description : 앱의 펌웨어 업데이트 요청 방송. 펌웨어 업데이트를 수행하며, 업데이트 결과 code를 전달. *(ANR 동작의 위험...탈출 구문 필요함.)*
             */
            public static final String ACTION_APP_START_FIRMUP = AUTHORITY + "." + MID + ".appStartFirmUp";
            /**
             * ACTION_MW_START_FIRMUP
             *
             * @Extra_Name : EXTRA_NAME_INT_1
             * @Extra_Data : int code (업데이트 결과를 알려주는 반환 code. 성공:1000, 실패:그외)
             * @Description : MW의 펌웨어 업데이트 결과code 응답 방송.
             */
            public static final String ACTION_MW_START_FIRMUP = AUTHORITY + "." + MID + ".mwStartFirmUp";

            /**
             * ACTION_APP_FIRM_VERSION
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : 앱의 펌웨어 버전 요청 방송.
             */
            public static final String ACTION_APP_FIRM_VERSION = AUTHORITY + "." + MID + ".appFirmVersion";
            /**
             * ACTION_MW_FIRM_VERSION
             *
             * @Extra_Name : EXTRA_NAME_INT_ARRAY
             * @Extra_Data : int[] version (펌웨어 버전 정보. version[0]=Major, version[1]=Minor, version[2]=Build, version[3]=Rev)
             * @Description : MW의 펌웨어 버전 응답 방송.
             */
            public static final String ACTION_MW_FIRM_VERSION = AUTHORITY + "." + MID + ".mwFirmVersion";

            /**
             * ACTION_MW_FIRM_PROGRESS
             *
             * @Extra_Name : EXTRA_NAME_INT_1
             * @Extra_Data : int progress (업데이트 진행 상태(%))
             * @Description : MW의 펌웨어 업데이트 진행 상태(%) 응답 방송. 일반적인 경우 자동으로 방송.
             */
            public static final String ACTION_MW_FIRM_PROGRESS = AUTHORITY + "." + MID + ".mwFirmProgress";

            /**
             * ACTION_MW_GENERATE_SCANLIST
             *
             * @Extra_Name : EXTRA_NAME_OBJECT
             * @Extra_Data : HashMap<String, DeviceRecord> arList (스캔 리스트)
             * @Description : MW의 스캔 리스트 방송. 리스트가 갱신될 때마다 방송한다. ScanMode가 MANUAL일 경우만 동작한다.
             */
            public static final String ACTION_MW_GENERATE_SCANLIST = AUTHORITY + "." + MID + ".mwGenerateScanList";

            /**
             * ACTION_APP_SET_SCANMODE
             *
             * @Extra_Name : EXTRA_NAME_INT_1
             * @Extra_Data : int scanMode (스캔 모드)
             * @Description : 앱의 블루투스 스캔 모드 설정을 한다.
             * Auto(0): 자동 스캔(이미 생성된 DB를 바탕으로 자동 접속. 리스트 자동 삭제)
             * MANUAL(1): 수동 접속(사용자의 접속할 기기를 선택. 리스트를 삭제하지 않음). 기본은 AUTO.
             */
            public static final String ACTION_APP_SET_SCANMODE = AUTHORITY + "." + MID + ".appSetScanMode";

            /**
             * ACTION_MW_END_OF_SCANLIST
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : MW의 블루투스 스캔의 종료를 의미한다.
             */
            public static final String ACTION_MW_END_OF_SCANLIST = AUTHORITY + "." + MID + ".mwEndOfScanList";

            /** 규창 16.12.25 미들웨어에서 자동스캔이 5회를 넘어갔을 시에 앱쪽에 스캔, BLE 중지요청을 한다
             * ACTION_MW_AUTO_SCAN_MAXIMUM
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description :
             */

            public static final String ACTION_MW_AUTO_SCAN_MAXIMUM = AUTHORITY + "." + MID + ".mwAutoScanMaximum";

            /**
             * ACTION_APP_BL_CONNECT
             *
             * @Extra_Name : EXTRA_NAME_STRING_1
             * @Extra_Data : String mac (블루투스 장치의 Mac 주소)
             * @Description : 앱에서 필요한 경우, 해당 Mac 주소의 장치에 접속한다. ScanMode가 MANUAL상태인 경우만 동작한다.
             */

            public static final String ACTION_APP_BL_CONNECT = AUTHORITY + "." + MID + ".appBlConnect";

            /**
             * ACTION_APP_START_SCAN
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : 앱에서 필요한 경우, 블루투스 장치 스캔을 시작한다. ScanMode가 MANUAL상태인 경우만 동작한다.
             */
            public static final String ACTION_APP_START_SCAN = AUTHORITY + "." + MID + ".appStartScan";

            /**
             * ACTION_APP_SET_DEVICE
             *
             * @Extra_Name : EXTRA_NAME_STRING_1
             * @Extra_Data : String mac (블루투스 장치의 Mac 주소)
             * @Description : 앱에서 블루투스 장치 설정 요청. null로 입력하는 경우, 장치정보를 초기화한다.
             */
            public static final String ACTION_APP_SET_DEVICE = AUTHORITY + "." + MID + ".appSetDevice";

            // 추가
            public static final String ACTION_APP_STOP_SCAN = AUTHORITY + "." + MID + ".appStopScan";

            public static final String ACTION_MW_PERIPHERAL_STATE = AUTHORITY + "." + MID + ".mwPeripheralState";
            public static final String ACTION_APP_PERIPHERAL_STATE = AUTHORITY + "." + MID + ".appPeripheralState";

            public static final String ACTION_APP_APPEND_BL_MESSAGE = AUTHORITY + "." + MID + ".appAppendBLMessage";

            public static final String ACTION_APP_SEND_USER_DATA = AUTHORITY + "." + MID + ".appSendUserData";

            public static final String ACTION_APP_STOP_BLUETOOTH = AUTHORITY + "." + MID + ".appStopBluetooth";

            public static final String ACTION_MW_DATASYNC = AUTHORITY + "." + MID + ".mwDataSync";

            public static final String ACTION_APP_IS_BUSY_SENDER = AUTHORITY + "." + MID + ".appIsBusySender";
            public static final String ACTION_MW_IS_BUSY_SENDER = AUTHORITY + "." + MID + ".mwIsBusySender";

            public static final String ACTION_APP_NORMAL_STRESS = AUTHORITY + "." + MID + ".appNormalStress";
            public static final String ACTION_APP_STRESS = AUTHORITY + "." + MID + ".appStress";
            //public static final String ACTION_APP_APPEND_SENDER = AUTHORITY + "." + MID + ".appAppendSender";
            // short, long, short, boolean
        }

        public static final class Sm {
            public static final String MID = Sm.class.getSimpleName();
            /**
             * ACTION_APP_IS_LIVE_APPLICATION
             *
             * @Extra_Name : EXTRA_NAME_INT_1
             * @Extra_Data : int state (0:무응답, 1:앱 준비중, 2:앱 정상 시작, 3:앱 종료)
             * @Description : 앱의 생존 상태를 전달하는 방송.
             */
            public static final String ACTION_APP_IS_LIVE_APPLICATION = AUTHORITY + "." + MID + ".appIsLiveApplication";
            /**
             * ACTION_MW_IS_LIVE_APPLICATION
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : MW의 앱 생존 상태 요청 방송.
             */
            public static final String ACTION_MW_IS_LIVE_APPLICATION = AUTHORITY + "." + MID + ".mwIsLiveApplication";

            /**
             * ACTION_APP_LOGIN_INFORMATION
             *
             * @Extra_Name : EXTRA_NAME_STRING_1
             * @Extra_Data : String userCode (String type 사용자 코드)
             * @Description : 앱의 로그인 사용자 코드 전달 방송.
             */
            public static final String ACTION_APP_LOGIN_INFORMATION = AUTHORITY + "." + MID + ".appLoginInformation";
            /**
             * ACTION_MW_LOGIN_INFORMATION
             *
             * @Extra_Name :
             * @Extra_Data :
             * @Description : MW의 로그인 사용자 코드 요청 방송.
             */
            public static final String ACTION_MW_LOGIN_INFORMATION = AUTHORITY + "." + MID + ".mwLoginInformation";
        }
    }

    public static final String ACTION_CHECK_FIRM_VERSION = "kr.co.greencomm.ibody24.coach.Mw.checkFirmVersion";
    public static final String ACTION_START_FIRM_VERSION = "kr.co.greencomm.ibody24.coach.Mw.startFirmVersion";
    public static final String ACTION_RESTART_SERVICE = "kr.co.greencomm.ibody24.coach.Mw.restartService";
    public static final String ACTION_WAKELOCK_SERVICE = "kr.co.greencomm.ibody24.coach.Mw.wakeLockService";
    public static final String ACTION_START_FIRMUP = "kr.co.greencomm.ibody24.coach.Mw.startFirmUp";

    /**
     * Variable
     **/
    private static boolean isRinging;

    /**
     * Instance
     **/
    private WeakReference<Context> WContext;
    private PowerManager mPowerManager;
    private ActivityManager mActivityManager;

    private MWBroadcastTop mBrTop;
    private MWControlCenter mw;

    /**
     * Method
     **/
    public MWBroadcastReceiver() {
        Log.d(tag, "MWBroadcastReceiver construct");
    }

    private void initInstance(Context context) {
        if (mw == null) {
            Log.d(tag, "MWBroadcastReceiver control-center");
            mw = MWControlCenter.getInstance(context);
        }

        if (mBrTop == null)
            mBrTop = new MWBroadcastTop(getContext());
        if (mPowerManager == null)
            mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (mActivityManager == null)
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    private Context getContext() {
        return WContext.get();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (WContext == null) {
            WContext = new WeakReference<>(context);
        }
        initInstance(getContext());

//		if(!isInit)
//			new ActionInfo().start();


        String action = intent.getAction();
        Log.d(tag, "MWBroadcastReceiver onReceive-> action : " + action);
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent service = new Intent(context, MWService.class);
            context.startService(service);
        } else if (action.equals(ACTION_WAKELOCK_SERVICE)) {
            /** 서비스는 살아 있는데, 아무것도 않함. Timer들이 전부 중단된 상태같음
             * 앱 실행 플레그를 보고 입력해줘야할까. 다른것들과 중복되는 동작이 너무 많음.-> 서비스가 죽는다는 전제조건이 틀리기 때문에...왜 안죽냐...그냥 pending 상태인듯.
             * 알람 받으면 그때만 살아서 동작함. wake up 알람이라..그럼 wakelock 알람을 쓰고 안쓰고는 어떻게 결정할까...기기가 sleep인지 아닌지 알수 있나?
             **/
            // test vibrate 권한도 해제해야함.
            //Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            //vb.vibrate(1000);
            boolean isActive;
            if (Build.VERSION.SDK_INT < 20)
                isActive = mPowerManager.isScreenOn();
            else
                isActive = mPowerManager.isInteractive();
            Log.d(tag, "isActive : " + isActive);

            /** 서비스 확인 **/
            boolean isRun;
            List<RunningServiceInfo> serviceList = mActivityManager.getRunningServices(Integer.MAX_VALUE);

            for (RunningServiceInfo s : serviceList) {
                //Log.d(tag,s.service.getClassName() +" <::> "+ s.service.getPackageName());
                if (s.service.getClassName().equals(MWService.NAME)) {
                    isRun = true;
                    break;
                }
            }
//			Log.d(tag,"isRun : "+isRun +" isInit : " + isInit);
//			if(!isRun || !isInit) {
//				Intent service = new Intent(context, MWService.class);
//				context.startService(service);
//			}

            if (!isActive) {
                /**
                 * 서비스는 살아있지만, 나머지 timer들이 죽은 상태. 왜 죽어있지? 쓰레드는 살아있는거 같은디...타이머만?
                 * 죽어있다는건 앱도 죽었다는 소리인데, 앱 br 응답이 오나? 안오면 의미 없는 코드.
                 */
                if (BluetoothLEManager.isEnabled()) {
                    mw.tryConnectionBluetooth();
                }
            }
        } else if (action.equals(Action.Am.ACTION_APP_STEP_CALORIE)) {
            short step = mw.getStep();
            double activityCalorie = mw.getTotalActivityCalorie();
            double sleepCalorie = mw.getTotalSleepCalorie();
            double dailyCalorie = mw.getTotalDailyCalorie();
            double coachCalorie = mw.getTotalCoachCalorie();
            double[] value = new double[]{activityCalorie, coachCalorie, sleepCalorie, dailyCalorie};

            mBrTop.sendBroadcastStepNCalorie(step, value);
        } else if (action.equals(Action.Ec.ACTION_APP_SELECT_PRODUCT)) {
            int productCode = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);
            ProductCode code;

            switch (productCode) {
                case 200003:
                    code = ProductCode.Coach;
                    break;
                case 220004:
                    code = ProductCode.Fitness;
                    break;
                default:
                    code = ProductCode.Fitness;
            }

            mw.selectProduct(code);
        } else if (action.equals(Action.Ec.ACTION_APP_GET_SELECTED_PRODUCT)) {
            intent = new Intent();
            intent.setAction(Action.Ec.ACTION_MW_GET_SELECTED_PRODUCT);
            intent.putExtra(Action.EXTRA_NAME_INT_1, mw.getSelectedProduct().getProductCode());
            getContext().sendBroadcast(intent);
        } else if (action.equals(Action.Am.ACTION_APP_ACTIVITY_DATA)) {
//            long date = intent.getLongExtra(Action.EXTRA_NAME_LONG_1, 0);
            ActivityData data = mw.getActivityData();

            mBrTop.sendBroadcastActivityData(data);
        } else if (action.equals(Action.Am.ACTION_APP_SLEEP_DATA)) {
            SleepData data = mw.getSleepData();

            mBrTop.sendBroadcastSleepData(data);
        } else if (action.equals(Action.Am.ACTION_APP_STRESS_DATA)) {
            short stress = mw.getStress();

            mBrTop.sendBroadcastStressData(stress);
        } else if (action.equals(Action.Bm.ACTION_APP_SEND_USER_DATA)) {
            mw.sendUserData();
        } else if (action.equals(Action.Bm.ACTION_APP_PERIPHERAL_STATE)) {
            CommQueue Q = new CommQueue(BluetoothCommand.State, 0L, RequestAction.Start, true);
            mw.appendSender(Q);
            //mBrTop.sendBroadcastPeripheralState(state.getState());
        } else if (action.equals(Action.Vm.ACTION_APP_PLAY)) {
            String xml = intent.getStringExtra(Action.EXTRA_NAME_STRING_1);

            mw.setXmlProgram(xml);
            mw.play();
        } else if (action.equals(Action.Vm.ACTION_APP_END)) {
            mw.end();
        } else if (action.equals(Action.Vm.ACTION_APP_SET_PROGRAM_CODE)) {
            int programCode = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);
            mw.setProgramCode(programCode);
        } else if (action.equals(Action.Vm.ACTION_APP_SET_CURRENT_TIME_POSITION)) {
            int position = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);
            mw.setCurrentTimePosition(position);
        } else if (action.equals(Action.Vm.ACTION_APP_DOWNLOAD_XML)) {
            // xml download..
        } else if (action.equals(Action.Ec.ACTION_APP_LOGOUT)) {
            mw.logout();
        } else if (action.equals(Action.Ec.ACTION_APP_BATTERY)) {
            Battery battery = mw.getBattery();

            short[] data = new short[]{battery.getStatus(), battery.getVoltage()};
            mBrTop.sendBroadcastBattery(data);
        } else if (action.equals(Action.Bm.ACTION_APP_DEVICE_INFORMATION)) {
            String[] info = mw.mBleManager.getDeviceInformation();

            if (info != null)
                Log.d(tag, "name:" + info[0] + " mac:" + info[1]);
            intent = new Intent();
            intent.setAction(Action.Bm.ACTION_MW_DEVICE_INFORMATION);
            intent.putExtra(Action.EXTRA_NAME_STRING_ARRAY, info);
            context.sendBroadcast(intent);
        } else if (action.equals(Action.Ec.ACTION_APP_USER_PROFILE)) { // parcel로 받을까...아니면 그냥 extra로?? 6개니까 parcel 쓰자.
            int age = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);
            int sex = intent.getIntExtra(Action.EXTRA_NAME_INT_2, 0);
            int job = intent.getIntExtra(Action.EXTRA_NAME_INT_3, 0);
            int height = intent.getIntExtra(Action.EXTRA_NAME_INT_4, 0);
            float weight = intent.getFloatExtra(Action.EXTRA_NAME_FLOAT_1, 0);
            float goal_weight = intent.getFloatExtra(Action.EXTRA_NAME_FLOAT_2, 0);
            Log.d(tag, "age:" + age + " sex:" + sex + " job:" + job + " height:" + height + " weight:" + weight + " goalweight:" + goal_weight);

            UserProfile profile = new UserProfile();
            profile.setUserProfile(sex, age, height, weight, goal_weight);
            mw.setProfile(profile);
        } else if (action.equals(Action.Ec.ACTION_APP_USER_DIETPERIOD)) {
            int dietPeriod = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);

            mw.setDietPeriod(dietPeriod);
        } else if (action.equals(Action.Bm.ACTION_APP_TRY_CONNECTION_BLUETOOTH)) {
            mw.tryConnectionBluetooth();
        } else if (action.equals(Action.Bm.ACTION_APP_APPEND_BL_MESSAGE)) {
            BluetoothCommand cmd = BluetoothCommand.getCommand(intent.getShortExtra(Action.EXTRA_NAME_SHORT_1, (short) 0));
            long time = intent.getLongExtra(Action.EXTRA_NAME_LONG_1, 0);
            RequestAction requestAction = RequestAction.getAction(intent.getShortExtra(Action.EXTRA_NAME_SHORT_2, (short) 0));
            boolean resp = intent.getBooleanExtra(Action.EXTRA_NAME_BOOL_1, false);
            NoticeIndex index = NoticeIndex.getIndex(intent.getShortExtra(Action.EXTRA_NAME_SHORT_3, (short) 0));

            CommQueue Q = new CommQueue(cmd, time, requestAction, resp, MessageClass.Phone_Idle, 0, index);

            mw.appendSender(Q);
        } else if (action.equals(Action.Bm.ACTION_APP_CONNECTION_STATE)) {
            ConnectStatus state = mw.getConnectionState();

            Log.d(tag, "conn state:" + state);
            mBrTop.sendBroadcastConnectionState(state);

            // 규창 16.11.10 펌웨어 업데이트
            /**
             * kr.co.greencomm.ibody24.DFUCustomBroadcast.Progress
             *
             * @Extras : Progress
             * @Data : Int
             * @Description : 라이브러리에서 펌웨어 업데이트시 방송하는 펌웨어 업로드 Progress
             */
        } else if (action.equals(DfuBaseService.DFU_Progress)) {
            Bundle bundle = intent.getExtras();
            int Progress = bundle.getInt(DfuBaseService.DFU_Extra_Progress);
            //mw.mBleManager.writeReset(path);
            //BLE의 연결복구를 위해 Progress를 Ble에도 넣어둠
            BluetoothLEManager.Progress = Progress;
            Log.i(tag, "Progress= "+ Progress);

            /**
             * kr.co.greencomm.ibody24.DFUCustomBroadcast.Completed
             *
             * @Extras : 없음
             * @Data : 없음
             * @Description : 라이브러리에서 펌웨어 업데이트 진행중 성공시 방송하는 브로드캐스트
             */
        }else if (action.equals(DfuBaseService.DFU_Completed)) {
            Log.i(tag, DfuBaseService.DFU_Completed);
            BluetoothLEManager.DfuMode = false;
            BluetoothLEManager.Dfu_Force_Ble = false;
            BluetoothLEManager.FirmUpComplete = true;

            mw.mBleManager.recoveryConnect();

            /**
             * kr.co.greencomm.ibody24.DFUCustomBroadcast.Failed
             *
             * @Extras : 없음
             * @Data : 없음
             * @Description : 라이브러리에서 펌웨어 업데이트 진행중 실패시 방송하는 브로드캐스트
             */
        }else if (action.equals(DfuBaseService.DFU_Failed)) {
            //Bundle bundle = intent.getExtras();//("no.nordicsemi.android.dfu.extra.EXTRA_DATA");
            //int Progress = bundle.getInt("PROGRESS");
            //mw.mBleManager.writeReset(path);

            //실패 했을 경우에 밴드의 부트모드->재부팅을 염두에 두어 1분 후에 연결을 회복
            Log.i(tag, DfuBaseService.DFU_Failed);
            BluetoothLEManager.DfuMode = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BluetoothLEManager.Dfu_Force_Ble = false;
                    mw.mBleManager.recoveryConnect();
                }
            }, 60000);


        }else if (action.equals(Action.Bm.ACTION_APP_START_FIRMUP)) {
            String path = intent.getStringExtra(Action.EXTRA_NAME_STRING_1);
            mw.mBleManager.writeReset(path);
            Log.i(tag, "mBleManager"+ mw.mBleManager.isLiveApp());
            // 규창 16.11.17 밴드가 부트모드일때 접속을 못하거나 연결을 실패했을 경우 Service모드로 들어가게 하기 위함
            // 규창 16.12.09 기준은 180초 정도이나 밴드가 접속 거부 시 블루투스 강제 리셋 루틴으로 인해 240초 증감
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /*
                        프로그레스 브로드캐스트가 진행중에 연결끊기는 등 문제가 발생했다면 DFU라이브러리
                        에서 연결 실패 처리하도록 예외처리가 되어있다.
                        따라서 DFU라이브러리로 진입도 못한, 250초 후라면 연결복구 기능 작동
                    */
                    if(BluetoothLEManager.Progress == 0 && mw.mBleManager.DfuMode == true) {
                        mw.mBleManager.recoveryDFUMODE();
                    }
                 }
            }, 250000);

            /*Intent service = new Intent(context, MWService.class);
            service.setAction(ACTION_START_FIRMUP);
            service.putExtra(Action.EXTRA_NAME_STRING_1, path);
            context.startService(service);*/
        } else if (action.equals(Action.Bm.ACTION_APP_FIRM_VERSION)) {
            if (mw.mBleManager.getConnectionState() == ConnectStatus.STATE_CONNECTED)
                mw.mBleManager.sender.append(new CommQueue(BluetoothCommand.Version, 0L, RequestAction.Start, true));
            /*String version = mw.getVersion();

            mBrTop.sendBroadcastFirmVersion(version);*/
        } else if (action.equals(Action.Sm.ACTION_APP_IS_LIVE_APPLICATION)) {
            boolean isRun = false;
            List<RunningServiceInfo> serviceList = mActivityManager.getRunningServices(Integer.MAX_VALUE);

            for (RunningServiceInfo s : serviceList) {
                //Log.d(tag,s.service.getClassName() +" :: "+ s.service.getPackageName());
                if (s.service.getClassName().equals(MWService.NAME)) {
                    isRun = true;
                    break;
                }
            }
//			Log.d(tag,"isRun : "+isRun +" isInit : " + isInit);
//			if(!isRun || !isInit) {
//				Intent service = new Intent(context, MWService.class);
//				context.startService(service);
//			}

            int state = intent.getIntExtra(Action.EXTRA_NAME_INT_1, StateApp.STATE_NO_RESPONSE.ordinal());
            switch (state) {
                case 0:
                    MWService.STATE_APPLICATION = StateApp.STATE_NO_RESPONSE;
                    break;
                case 1:
                    MWService.STATE_APPLICATION = StateApp.STATE_READY;
                    break;
                case 2:
                    MWService.STATE_APPLICATION = StateApp.STATE_NORMAL;
                    break;
                case 3:
                    MWService.STATE_APPLICATION = StateApp.STATE_EXIT;
                    break;
                default:
                    MWService.STATE_APPLICATION = StateApp.STATE_NO_RESPONSE;
                    break;
            }

            mw.setIsLiveApplication(MWService.STATE_APPLICATION);
        } else if (action.equals(Action.Sm.ACTION_APP_LOGIN_INFORMATION)) {
            String userCode = intent.getStringExtra(Action.EXTRA_NAME_STRING_1);
            Log.d(tag, "userCode:" + userCode);
        } else if (action.equals(ACTION_RESTART_SERVICE)) {
            boolean isActive = false;
            if (Build.VERSION.SDK_INT < 20)
                isActive = mPowerManager.isScreenOn();
            else
                isActive = mPowerManager.isInteractive();
            if (!isActive)
                return;

            boolean isRun = false;
            List<RunningServiceInfo> serviceList = mActivityManager.getRunningServices(Integer.MAX_VALUE);

            for (RunningServiceInfo s : serviceList) {
                if (s.service.getClassName().equals(MWService.NAME)) {
                    isRun = true;
                    break;
                }
            }
//			Log.d(tag,"isRun : "+isRun +" isInit : " + isInit);
//			if(!isRun || !isInit) {
//				Intent service = new Intent(getContext(), MWService.class);
//				getContext().startService(service);
//			}
        } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            Log.d(tag, "bluetooth state change!!->" + state);
            // 먼진 모르지만 블루투스 어뎁터 상태가 변동이 있음. 꺼진다던가..켜진다던가..
            if (state == BluetoothAdapter.STATE_ON) {
                // 켜짐.-> 켜진다는건 꺼져있었다는 소리야. 혹은 무슨 일로 꺼졌다 다시 켜짐. 그럼 미들웨어를 재시작.
                // 그냥 키면 이상한데... 초기화에서 필요한 과정 전부 거쳐야함.
                // 프로필이 제대로 들어오지 않은 상태라면, 멈출 것.
                // 제대로 들어온 상태라면, 그냥 붙고 동작할 것이다. 장치를 못찾으면? 계속 찾는다...위험한가.
                mw.setIsLiveApplication(StateApp.STATE_NORMAL);
            } else if (state == BluetoothAdapter.STATE_OFF) {
                // 꺼짐-> 꺼지면 미들웨어 중단. 어짜피 블루투스 먹통이라 아무 동작도 못함.
                mw.stopBluetooth();


                //규창 17.01.09 android.os.DeadObjectException 방지 위해 OS로 인한 블루투스 상태변화시 객체 주기적으로 리셋
                mw.refreshBLEObject();
            }
        } else if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
            short offset = MWControlCenter.getTzOffset();
            Log.d(tag, "change time zone : " + offset);
            mw.mBleManager.setTimeZoneOffset(offset);
        } else if (action.equals(ACTION_CHECK_FIRM_VERSION)) {
            String version = mw.getVersion();
            if (version != null) {
                Intent service = new Intent(context, MWService.class);
                service.setAction(ACTION_START_FIRM_VERSION);
                service.putExtra(Action.EXTRA_NAME_STRING_1, version);
                context.startService(service);
            }
        } else if (action.equals(Action.Bm.ACTION_APP_SET_SCANMODE)) {
            int scanMode = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);

            mw.setScanMode(scanMode == 0 ? ScanMode.AUTO : ScanMode.MANUAL);
            mw.stopBluetooth();
        } else if (action.equals(Action.Bm.ACTION_APP_BL_CONNECT)) {
            String mac = intent.getStringExtra(Action.EXTRA_NAME_STRING_1);

//			if(mw.mBleManager.getScanMode() != ScanMode.MANUAL)
//				return;

            mw.mConfig.setMac(mac);
            mw.connect(mac);
        } else if (action.equals(Action.Bm.ACTION_APP_START_SCAN)) {
            mw.mBleManager.scanBluetooth(true);
        } else if (action.equals(Action.Bm.ACTION_APP_STOP_SCAN)) {
            mw.mBleManager.scanBluetooth(false);
        } else if (action.equals(Action.Ec.ACTION_APP_LOGIN)) {
            String[] info = intent.getStringArrayExtra(Action.EXTRA_NAME_STRING_ARRAY);
            if (info == null)
                return;

            String userCode = info[0];
            String mac = info[1];
            String email = info[2];
            String password = info[3];

            mw.mConfig.setUserCode(userCode);
            mw.mConfig.setMac(mac);
            mw.mConfig.setEmail(email);
            mw.mConfig.setPassword(password);

            mw.mBleManager.setIsLiveApp(true);

            // data upload 필요함. DataTransfer
        } else if (action.equals(Action.Bm.ACTION_APP_SET_DEVICE)) {
            String mac = intent.getStringExtra(Action.EXTRA_NAME_STRING_1);
            mw.mConfig.setMac(mac);
        } else if (action.equals(Action.Bm.ACTION_APP_STOP_BLUETOOTH)) {
            mw.stopBluetooth();
        } else if (action.equals(Action.Bm.ACTION_APP_IS_BUSY_SENDER)) {
            boolean isbusy = mw.isBusySender();
            mBrTop.sendBroadcastIsBusySender(isbusy);
        } else if (action.equals(Action.Bm.ACTION_APP_STRESS)) {
            boolean start = intent.getBooleanExtra(Action.EXTRA_NAME_BOOL_1, false);
            mw.sendStressMeasure(start);
        } else if (action.equals(Action.Bm.ACTION_APP_NORMAL_STRESS)) {
            boolean start = intent.getBooleanExtra(Action.EXTRA_NAME_BOOL_1, false);
            mw.sendNomalCoachStressMeasure(start);
        } else if (action.equals(Action.Vm.ACTION_APP_GET_QUERY_CODE)) {
            int programCode = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);
            String code = mw.mVideoManager.getCourseCodeForQuery(programCode);
            mBrTop.sendBroadcastQueryCode(Integer.parseInt(code));
        } else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d(tag, "state call : " + state + " isRinging : "+isRinging);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                if (!isRinging)
                    mw.mBleManager.sender.append(new CommQueue(BluetoothCommand.NoticeMessage, 0L, RequestAction.Start, false, MessageClass.Phone_Ringing, 0, NoticeIndex.Empty));
                isRinging = true;
            } else {
                if (isRinging) {
                    mw.mBleManager.sender.append(new CommQueue(BluetoothCommand.NoticeMessage, 0L, RequestAction.Start, false, MessageClass.Phone_Idle, 0, NoticeIndex.Empty));
                    int count = PhoneHelper.getMissedCount(PhoneHelper.read(getContext()));
                    Log.i(tag, "missed call log count:" + count);
                    if (count > 0) {
                        mw.mBleManager.sender.append(new CommQueue(BluetoothCommand.NoticeMessage, 0L, RequestAction.Start, false, MessageClass.Missed_Call, count, NoticeIndex.Empty));
                    }
                }
                isRinging = false;
            }
        } else if (action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            int count = SMSHelper.getNotReadCount(SMSHelper.read(getContext()));
            Log.i(tag, "SMS_RECEIVED_ACTION count:" + count);
            // 여기 들어온 것은 문자 메시지가 새로 도착했다는 것->밴드로 메시지 전달(읽지 않은 문자 개수)
            mw.mBleManager.sender.append(new CommQueue(BluetoothCommand.NoticeMessage, 0L, RequestAction.Start, false, MessageClass.SMS, count, NoticeIndex.Empty));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.d(tag, "BR 소멸자");
        WContext = null;
        mw = null;
    }
}