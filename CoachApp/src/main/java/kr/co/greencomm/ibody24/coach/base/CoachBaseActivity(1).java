package kr.co.greencomm.ibody24.coach.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.ActivityChartMain;
import kr.co.greencomm.ibody24.coach.activity.ActivityChartWeek;
import kr.co.greencomm.ibody24.coach.activity.ActivityChartYear;
import kr.co.greencomm.ibody24.coach.activity.ActivityMain;
import kr.co.greencomm.ibody24.coach.activity.ActivitySetting;
import kr.co.greencomm.ibody24.coach.activity.ActivityStart;
import kr.co.greencomm.ibody24.coach.activity.coach.ActivityHome;
import kr.co.greencomm.ibody24.coach.activity.fitness.ActivityStepCount;
import kr.co.greencomm.ibody24.coach.activity.register.ActivityProductSelect;
import kr.co.greencomm.ibody24.coach.activity.register.ActivityRegisterComplete;
import kr.co.greencomm.ibody24.coach.activity.register.ActivitySettingProfile;
import kr.co.greencomm.ibody24.coach.activity.register.ActivitySettingWeight;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.data.UserRecord;
import kr.co.greencomm.ibody24.coach.dialog.MessageDialog;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabMycoach;
import kr.co.greencomm.ibody24.coach.utils.TrainerCode;
import kr.co.greencomm.ibody24.coach.utils.Weight;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.webs.QueryStatus;
import kr.co.greencomm.ibody24.coach.data.TodayInfo;
import kr.co.greencomm.ibody24.coach.dialog.MessageDialogConfirm;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabSetting;
import kr.co.greencomm.ibody24.coach.utils.ProgramCode;
import kr.co.greencomm.ibody24.coach.webs.QueryListener;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.utils.ProductCode;
import kr.co.greencomm.ibody24.coach.utils.SleepIdentifier;


/**
 * Created by young on 2015-08-27.
 */
public abstract class CoachBaseActivity
        extends Activity
        implements QueryListener {
    private static final String tag = CoachBaseActivity.class.getSimpleName();

    protected static final int REQUEST_ENABLE_BT = 300;

    public static UserRecord DB_User = new UserRecord();
    public static TodayInfo DB_Today = new TodayInfo();

    public static TrainerCode trainer = TrainerCode.Basic;
    public static ProgramCode program = ProgramCode.Basic;
    public static ProgramCode.BasicCode basic = ProgramCode.BasicCode.Basic1;

    public String getVideoFileName() {
        String fName =
            "prog_" +
            String.format("%03d", program.getProgramCode()) +
            (program == ProgramCode.Basic ? String.format("_%02d", basic.ordinal() + 1) : "") +
            ".mp4";

        Log.d(tag, "Video File Name: " + fName);
        return fName;
    }

    public String getLanguageVideoFileName() {
        Locale locale = Locale.getDefault();
        String language =  locale.getLanguage();
        if (language.equals("ja")) language = "jp";

        String fName = language + "/" + getVideoFileName();

        Log.d(tag, "Video File Name: " + fName);
        return fName;
    }

    public static String getXmlFileName() {
        return String.format("video_%03d.xml", program.getProgramCode());
    }

    public abstract void run(Intent intent);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Date addingDay(Date date, int add_day) {
        long now = date.getTime();
        date.setTime(now + add_day * 24 * 60 * 60 * 1000);

        return date;
    }

    /**
     * 블루투스 기능이 켜져 있는지 확인한다.
     */
    protected boolean isBluetoothEnabled() {
        BluetoothAdapter m_adapter = BluetoothAdapter.getDefaultAdapter();
        return (m_adapter != null && m_adapter.isEnabled());
    }

    /**
     * 블루투스 기능을 켜는 액티비티 실행
     */
    protected void requestBluetoothEnable() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log("Base - onActivityResult requestCode:" + requestCode + ",resultCode:" + resultCode);
        //super.onActivityResult(requestCode, resultCode, data);
        Log("Request: " + requestCode + ", Result: " + resultCode);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
//                ConfigManager.initInstance(this);
                nextProcess();
            } else {
                displayMsg(R.string.error_bluetooth, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // 시간 지연 후 액티비티 시작 하는 딜레이 처리
    //----------------------------------------------------------------------------------------------

    private static final int START_DELAY_TIME = 3000;
    private Handler m_start_handler = null;

    Runnable run_Intro = new Runnable() {
        @Override
        public void run() {
            // 화면을 전환 : ActivityStart
            nextProcess();
            // fade in 으로 시작하여 fade out 으로 인트로 화면이 꺼지게 에니메이션 추가
            //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            m_start_handler = null;
        }
    };

    //----------------------------------------------------------------------------------------------
    // 현재 액티비티를 종료하고 다른 액티비티를 시작한다.
    //----------------------------------------------------------------------------------------------

    protected void changeActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        this.startActivity(intent);
        this.finish();
    }

    protected void changeActivityClear(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        this.finish();
    }

    private static ApplicationStatus m_app_stat = ApplicationStatus.IntroScreen;

    protected void setApplicationStatus(ApplicationStatus stat) {
        m_app_stat = stat;
        Log("Set Status: " + stat);
    }

    @Override
    public void onBackPressed() {
        Log("onBackPressed");
        if (m_start_handler != null) {
            m_start_handler.removeCallbacks(run_Intro);
            m_start_handler = null;
        }
        Activity act = getParent();
        if (act != null) {
            Log("Parent: " + act.getLocalClassName());
            if (act instanceof TabServer) {
                ((TabServer) act).backView();
            }
        } else {
            if (m_app_stat.compareTo(ApplicationStatus.HomeScreen) < 0) {
                super.onBackPressed();
            }
        }
    }

    private void setAutoLogin() {
        Preference.putAutoLogin(this, true);
        Preference.putEmail(this, DB_User.getEmail());
        Preference.putPassword(this, DB_User.getPassword());
    }

    protected void setManualLogin() {
        Preference.putAutoLogin(this, false);
        Preference.putEmail(this, "");
        Preference.putPassword(this, "");
    }

    private void changeScreenUserDataValid() {
        Log.d(tag, "changeScreenUserDataValid");
        if (DB_User.isDeviceValid() == false) {
//            changeActivity(ActivitySelectDevice.class);
            changeActivity(ActivityProductSelect.class);
        } else if (DB_User.isProfileValid() == false) {
            changeActivity(ActivitySettingProfile.class);
        } else if (DB_User.isTargetValid() == false) {
            changeActivity(ActivitySettingWeight.class);
        } else {
            setAutoLogin();
            changeActivityClear(ActivityMain.class);
        }
    }

    protected static boolean m_popup_mode = false;
    protected static boolean m_popup_mode_home = false;
    protected static boolean m_error_flag = false;

    protected void nextProcess() {
        switch (m_app_stat) {
            case IntroScreen:
                m_app_stat = ApplicationStatus.WaitStartActivity;
                m_start_handler = new Handler();
                m_start_handler.postDelayed(run_Intro, START_DELAY_TIME);
                break;
            case WaitStartActivity:
//                ConfigManager.initInstance(this);

                if (Preference.getAutoLogin(this)) {
                    m_popup_mode = false;
                    DB_User.setEmail(Preference.getEmail(this));
                    DB_User.setPassword(Preference.getPassword(this));
                    m_app_stat = ApplicationStatus.AutoLoginStart;
                    DB_User.runQuery(QueryCode.LoginUser, this);
                } else {
                    changeActivity(ActivityStart.class);
                }
                break;
            case StartScreen:
                break;
            case DeviceEditScreen:
                break;
            case AutoLoginStart:
                if (m_error_flag == true) {
                    // 자동 로그인에 실패한 경우 환경설정에서 자동을 취소한다.
                    setManualLogin();
                    m_error_flag = false;
                    changeActivity(ActivityStart.class);
                    return;
                }
                changeScreenUserDataValid();
                break;
            case RegisterMemberScreen:
                // 사용자 등록에 오류가 없는 경우에만 완료 화면으로
                if (m_error_flag == false) {
                    changeActivity(ActivityRegisterComplete.class);
                }
                break;
            case ConnectionFailedScreen:
            case SelectDeviceScreen:
                if (m_popup_mode) {
                    m_popup_mode = false;
                    //finish();
//                    ActivityTabSetting.popActivity();
                    ActivityTabSetting.popActivityMulti(4);
                    ActivityTabHome.loadFirstScreen();
                    ActivityTabMycoach.loadFirstScreen();
                    ActivitySetting.loadFirstScreen();
                    return;
                }
                changeScreenUserDataValid();
//                changeActivity(ActivitySettingProfile.class);
                break;
            case UserProfileScreen:
                if (m_popup_mode) {
                    m_popup_mode = false;
                    //finish();
                    ActivityTabSetting.popActivity();
                    return;
                }
                changeScreenUserDataValid();
//                changeActivity(ActivitySettingWeight.class);
                break;
            case TargetWeightScreen:
                if (m_popup_mode) {
                    m_popup_mode = false;
                    setMiddleWareData(this);
                    //finish();
                    ActivityTabSetting.popActivity();
                    return;
                }
                setAutoLogin();
                changeActivity(ActivityMain.class);
                break;
            case LoginScreen:
                if (m_error_flag == false) {
                    changeScreenUserDataValid();
                }
                break;
        }
    }

    private void errDisplayAndNext(final int msgId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayMsg(msgId, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_error_flag = true;
                        nextProcess();
                    }
                });
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // 밴드의 연결과 해제
    //----------------------------------------------------------------------------------------------

    public static void connectDevice(Context context, String mac) {
        SendReceive.sendConnect(context, mac);
    }

    //----------------------------------------------------------------------------------------------
    // 현재의 사용자 정보 미들웨어에 설정
    //----------------------------------------------------------------------------------------------

    public static void setMiddleWareData(Context context) {
//        ConfigManager.initInstance(context);
//        ConfigManager conf = ConfigManager.getInstance();
//
//        conf.setUserProfile(
//                DB_User.getGender(),
//                DB_User.getAge(),
//                DB_User.getHeight(),
//                DB_User.getLanguage());
//
//        conf.setUserWeightProfile(
//                (int) DB_User.getCurrentWeight(),
//                (int) DB_User.getTargetWeight(),
//                DB_User.getDietPeriod());
    }

    //----------------------------------------------------------------------------------------------
    // 운동 데이터의 표시와 저장
    //----------------------------------------------------------------------------------------------

    protected int getDailyConsumeCalorie() {
//        ContentsManager content = new ContentsManager(this);
//        return content.getCalorieConsumeDaily();
        return 0;
    }

    protected double getDailyConsumeCalorie(float weight,float goalWeight,int dietPeriod) {
        return Weight.getCalorieConsumeDaily(weight, goalWeight, dietPeriod);
    }


    //----------------------------------------------------------------------------------------------
    // 웹 서비스 결과 처리
    //----------------------------------------------------------------------------------------------
    protected void onRecvTodayInfo() {
    }

    @Override
    public void onQueryResult(final QueryCode queryCode, String request, String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_error_flag = false;
                switch (queryCode) {
                    case ListStep:
                        ActivityStepCount.instance.setData();
                        break;
                    case InsertStep:
                        break;
                    case InsertExercise:
                        break;
                    case ExerciseToday:
                        //onRecvTodayInfo();
                        if (ActivityHome.Home != null)
                            ActivityHome.Home.updateGraph();
                        ActivityChartMain.onGraphData();
                        break;
                    case WeekData:
                        //ActivityTabChart.Week.onRecvComplete();
                        ActivityChartWeek.onGraphData();
                        break;
                    case YearData:
                        //ActivityTabChart.Year.onRecvComplete();
                        ActivityChartYear.onGraphData();
                        break;
                    case LoginUser:
                        Preference.putAge(getApplicationContext(), DB_User.getAge());
                        Preference.putSex(getApplicationContext(), DB_User.getGender());
                        Preference.putHeight(getApplicationContext(), DB_User.getHeight());
                        Preference.putWeight(getApplicationContext(), DB_User.getWeight());
                        Preference.putGoalWeight(getApplicationContext(), DB_User.getGoalWeight());
                        Preference.putDietPeriod(getApplicationContext(), DB_User.getDietPeriod());
                        Preference.putUrlUsercode(getApplicationContext(), DB_User.getCode().toString());
                        Preference.putBluetoothName(getApplicationContext(), DB_User.getDeviceName());
                        Preference.putBluetoothMac(getApplicationContext(), DB_User.getDeviceAddress());

                        Log.d(tag, "DB dev name : " + DB_User.getDeviceName());
                        Log.d(tag, "DB dev addr : " + DB_User.getDeviceAddress());
                        String name = DB_User.getDeviceName();
                        if (name != null && !name.isEmpty()) {
                            String prefix = name.substring(0, ProductCode.Fitness.getBluetoothDeviceName().length());
                            if (prefix.equals(ProductCode.Fitness.getBluetoothDeviceName())) {
                                SendReceive.sendProductCode(getApplicationContext(), ProductCode.Fitness);
                            } else if (prefix.equals(ProductCode.Coach.getBluetoothDeviceName())) {
                                SendReceive.sendProductCode(getApplicationContext(), ProductCode.Coach);
                            }
                        } else {
                            SendReceive.sendProductCode(getApplicationContext(), ProductCode.Coach);
                        }
                        nextProcess();
                        break;
                    case SetDevice:
                        Preference.putBluetoothName(getApplicationContext(), DB_User.getDeviceName());
                        Preference.putBluetoothMac(getApplicationContext(), DB_User.getDeviceAddress());
                        nextProcess();
                        break;
                    default:
                        nextProcess();
                        break;
                }
            }
        });
    }

    @Override
    public void onQueryError(QueryCode queryCode, QueryStatus nErrorCode, String szErrMessage) {
        switch (nErrorCode) {
            case ERROR_Query:
            case ERROR_Web_Read:
            case ERROR_Result_Parse:
            case ERROR_Non_Information:
                errDisplayAndNext(R.string.error_program);
                break;
            case ERROR_Exists_Email:
                errDisplayAndNext(R.string.error_exists_email);
                break;
            case ERROR_Account_Not_Match:
                errDisplayAndNext(R.string.error_not_account);
                break;
        }
    }

    /*
        //객체 이름으로 객체 생성하기
		try {
			Constructor con = Class.forName("kr.co.greencomm.data.UserInfo").getConstructor();
			Object obj = con.newInstance();
			SpectrumActivity.Log("객체생성: " + obj);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		SpectrumActivity.Log("========== Folders ==========");
		SpectrumActivity.Log("RootDirectory: " + Environment.getRootDirectory());
		SpectrumActivity.Log("ExternalStorageState: " + Environment.getExternalStorageState());
		SpectrumActivity.Log("DataDirectory: " + Environment.getDataDirectory());
		SpectrumActivity.Log("DownloadCacheDirectory: " + Environment.getDownloadCacheDirectory());
		SpectrumActivity.Log("ExternalStorageDirectory: " + Environment.getExternalStorageDirectory());
		SpectrumActivity.Log("ExternalStoragePublicDirectory(DOWNLOADS): " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
		SpectrumActivity.Log("-------------------------------");
		SpectrumActivity.Log("FilesDir: " + this.getFilesDir());
     */

    //----------------------------------------------------------------------------------------------
    // 로그 메시지 표시
    //----------------------------------------------------------------------------------------------

    private boolean m_enable = true;

    private int printLog(int priority, String msg) {
        if (m_enable) {
            String className = this.getClass().getSimpleName();
            if (className.startsWith("Activity") == true) {
                className = className.replaceFirst("^Activity", "화면_");
            }
            String tag = "[" + className + "] ";
            return Log.println(priority, tag, msg);
        }
        return 0;
    }

    public void Enable() {
        m_enable = true;
    }

    public void Disable() {
        m_enable = false;
    }

    public void Log(String msg) {
        printLog(Log.DEBUG, msg);
    }

    public void Log(String title, String msg) {
        printLog(Log.DEBUG, title + ": " + msg);
    }

    //----------------------------------------------------------------------------------------------
    // 다이아로그 페시지 창 표시
    //----------------------------------------------------------------------------------------------

    /**
     * 간단한 메세지 출력을 위한 Alert
     */
    protected void showMessage(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.MainContext);
                builder.setMessage(msg)
                        .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    protected void showMessageConfirm(final String msg, DialogInterface.OnClickListener listener) {
        MessageDialogConfirm confirm = new MessageDialogConfirm(ActivityMain.MainContext, msg, listener);
        confirm.show();
    }

    private AlertDialog alert;
    protected void showMessageNonBtn(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.MainContext);
                alert = builder.setMessage(msg).setCancelable(false)
                        .create();
                alert.show();
            }
        });
    }

    protected void cancelMessageNonBtn(DialogInterface.OnDismissListener listener) {
        if (alert != null && alert.isShowing()) {
            alert.setOnDismissListener(listener);
            alert.dismiss();
        }
    }

    protected void displayMsg(String msg, View.OnClickListener listener) {
        MessageDialog dialog = new MessageDialog(ActivityMain.MainContext == null ? this : ActivityMain.MainContext, msg, listener);
        if (!isFinishing())
            dialog.show();
    }

    protected void displayMsg(int resId) {
        displayMsg(getString(resId), null);
    }

    protected void displayMsg(int resId, View.OnClickListener listener) {
        displayMsg(getString(resId), listener);
    }

    protected void displayMsg(String msg) {
        displayMsg(msg, null);
    }

    public static String getUrlHtmlResources(String name) {
        String url = "file:///android_res/raw/" + name + ".html";
        return url;
    }

    public static Spanned getHtml(String source) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Html.fromHtml(source);
        }
        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
    }

    //----------------------------------------------------------------------------------------------
    // 파일 다운로드 관련
    //----------------------------------------------------------------------------------------------

    protected boolean m_isdown = false;

    protected void startDownVideo(int prog) {
        String fileName = String.format("prog_%03d", prog);
        String sourceFile = "http://222.236.47.55/video/" + fileName;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(sourceFile + ".mp4"));
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, fileName + ".green");
        request.setTitle("iBODY24 FITNESS Program");
        request.setDescription("작품번호: " + prog);
        this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).mkdir();
        DownloadManager m_down_manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        m_down_manager.enqueue(request);
    }

    protected boolean isExistsVideo(int prog) {
        String fileName = String.format("prog_%03d.mp4", prog);
        File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, fileName);
        return file.isFile();
    }

    protected boolean isDownComplete(int prog) {
        String fileName = String.format("prog_%03d.green", prog);
        File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, fileName);
        if (file.isFile() == false) return false;
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor cursor = manager.query(query);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (cursor.isLast() == false) {
                String name = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                Log("다운로드 커서 파일명: " + name);
                if (name.equals(file.getAbsolutePath())) {
                    cursor.close();
                    return true;
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return false;
    }

    protected void renameVideo(int prog) {
        String fileName = String.format("prog_%03d", prog);
        File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File source = new File(path, fileName + ".green");
        if (source.isFile()) {
            source.renameTo(new File(path, fileName + ".mp4"));
        }
    }

    public void onShow() {
        // 상속해서 사용중.
    }

    protected SleepIdentifier getSleepCalc(long time) {
        if (time > 420) {
            return SleepIdentifier.Enough;
        } else if (time > 300) {
            return SleepIdentifier.Normal;
        } else if (time > 180) {
            return SleepIdentifier.Few;
        } else {
            return SleepIdentifier.Lack;
        }
    }
}
