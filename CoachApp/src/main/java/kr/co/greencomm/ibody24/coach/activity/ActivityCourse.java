package kr.co.greencomm.ibody24.coach.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.adapter.TitlePhotoAdapter;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.data.VersionService;
import kr.co.greencomm.ibody24.coach.dialog.DownProgressDialog;
import kr.co.greencomm.ibody24.coach.dialog.ProgressListener;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.ibody24.coach.utils.ProgramCode;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.webs.WebDownListener;
import kr.co.greencomm.ibody24.coach.webs.WebDownLoad;
import kr.co.greencomm.ibody24.coach.data.TitlePhoto;
import kr.co.greencomm.ibody24.coach.widget.ExpandableHeightGridView;
import kr.co.greencomm.middleware.bluetooth.ConnectStatus;
import kr.co.greencomm.middleware.bluetooth.StatePeripheral;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver.Action;
import kr.co.greencomm.middleware.utils.FileManager;
import kr.co.greencomm.middleware.utils.ProductCode;


/**
 * Created by young on 2015-08-24.
 */
public class ActivityCourse
        extends CoachBaseActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener, ProgressListener, WebDownListener {
    private static final String tag = ActivityCourse.class.getSimpleName();
    public static final int REQUEST_CODE_PLAY_VIDEO = 501;

    private ArrayList<TitlePhoto> m_array = new ArrayList<TitlePhoto>();
    private GridView m_view;
    private TitlePhotoAdapter m_adapter;

    private WebDownLoad downloader;
    private ProgressDialog m_dialog;

    private int m_query_code;

    private Button m_btn_start;
    private WebView m_webView;

    private Runnable m_popup_runnable;
    private Handler m_popup_handler;

    private boolean success, isWait;
    private DialogInterface.OnDismissListener dismiss_listener;
    private boolean isClickButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentScale1440(R.layout.activity_coach_basic, R.id.screen_layout_main);
        setApplicationStatus(ApplicationStatus.CourseScreen);

        SendReceive.getConnectionState(this);
        SendReceive.getCourseQueryCode(this, program.getProgramCode());

        dismiss_listener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dismissCallback();
            }
        };

        m_popup_handler = new Handler();
        m_popup_runnable = new Runnable() {
            @Override
            public void run() {
                cancelMessageNonBtn(dismiss_listener);
            }
        };

        if (program == ProgramCode.Basic) {
            setContentView(R.layout.activity_course_one);

            m_webView = (WebView) findViewById(R.id.course_one_webView);

            m_array.add(new TitlePhoto(R.drawable.basic_001_01, getString(R.string.exer1)));
            m_array.add(new TitlePhoto(R.drawable.basic_001_02, getString(R.string.exer2)));
            m_array.add(new TitlePhoto(R.drawable.basic_001_03, getString(R.string.exer3)));
            m_array.add(new TitlePhoto(R.drawable.basic_001_04, getString(R.string.exer4)));
            m_array.add(new TitlePhoto(R.drawable.basic_001_05, getString(R.string.exer5)));
            m_array.add(new TitlePhoto(R.drawable.basic_001_06, getString(R.string.exer6)));
            m_array.add(new TitlePhoto(R.drawable.basic_001_07, getString(R.string.exer7)));
            m_array.add(new TitlePhoto(R.drawable.basic_001_08, getString(R.string.exer8)));

            String url = getUrlHtmlResources(getString(R.string.html_prepare_exer));
            m_webView.loadUrl(url);
        } else {
            setContentView(R.layout.activity_course_all);
            m_webView = (WebView) findViewById(R.id.course_all_webView);

            m_btn_start = (Button) findViewById(R.id.course_all_startButton);
            m_btn_start.setOnClickListener(this);
            String url = null;

            if (!FileManager.isExistFile(this, getVideoFileName())) {
                m_btn_start.setText("DOWNLOAD");
            }

            TextView m_title = (TextView) findViewById(R.id.course_all_titleText);

            switch (program) {
                case Full_Body:
                    m_title.setText(R.string.coach_basic_title_002);
                    url = getUrlHtmlResources(getString(R.string.html_15min));
                    m_array.add(new TitlePhoto(R.drawable.basic_002_01, getString(R.string.exer9)));
                    m_array.add(new TitlePhoto(R.drawable.basic_002_02, getString(R.string.exer10)));
                    m_array.add(new TitlePhoto(R.drawable.basic_002_03, getString(R.string.exer11)));
                    m_array.add(new TitlePhoto(R.drawable.basic_002_04, getString(R.string.exer12)));
                    m_array.add(new TitlePhoto(R.drawable.basic_002_05, getString(R.string.exer13)));
                    m_array.add(new TitlePhoto(R.drawable.basic_002_06, getString(R.string.exer14)));
                    m_array.add(new TitlePhoto(R.drawable.basic_002_07, getString(R.string.exer15)));
                    m_array.add(new TitlePhoto(R.drawable.basic_002_08, getString(R.string.exer16)));
                    m_array.add(new TitlePhoto(R.drawable.basic_002_09, getString(R.string.exer17)));
                    m_array.add(new TitlePhoto(R.drawable.basic_002_10, getString(R.string.exer18)));
                    break;
                case Mat:
                    m_title.setText(R.string.coach_basic_title_003);
                    url = getUrlHtmlResources(getString(R.string.html_mat));
                    m_array.add(new TitlePhoto(R.drawable.basic_003_01, "Rolling like a ball"));
                    m_array.add(new TitlePhoto(R.drawable.basic_003_02, "Open leg rocker"));
                    m_array.add(new TitlePhoto(R.drawable.basic_003_03, "Double leg pull"));
                    m_array.add(new TitlePhoto(R.drawable.basic_003_04, "Criss cross"));
                    m_array.add(new TitlePhoto(R.drawable.basic_003_05, "Swimming"));
                    break;
                case Active:
                    m_title.setText(R.string.coach_basic_title_004);
                    url = getUrlHtmlResources(getString(R.string.html_active));
                    m_array.add(new TitlePhoto(R.drawable.basic_004_01, "Side step jacks"));
                    m_array.add(new TitlePhoto(R.drawable.basic_004_02, "Criss Cross squat"));
                    m_array.add(new TitlePhoto(R.drawable.basic_004_03, "Curtsy lunge"));
                    m_array.add(new TitlePhoto(R.drawable.basic_004_04, "Alt toe tap squat"));
                    m_array.add(new TitlePhoto(R.drawable.basic_004_05, "Squat hops"));
                    break;
                case Abs:
                    m_title.setText(R.string.program_001_001);
                    url = getUrlHtmlResources(getString(R.string.html_abdominal_muscles));
                    m_array.add(new TitlePhoto(R.drawable.fitness_101_01, "Wide squat plie"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_101_02, "Roll up"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_101_03, "Double leg circle"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_101_04, "C Curved Crunch"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_101_05, "Heel Touch"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_101_06, "Criss Cross"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_101_07, "Oblique Side band"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_101_08, "O Balance"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_101_09, "Triangle Holding"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_101_10, "Plank Push Up"));
                    break;
                case Back:
                    m_title.setText(R.string.program_001_002);
                    url = getUrlHtmlResources(getString(R.string.html_backside));
                    m_array.add(new TitlePhoto(R.drawable.fitness_102_01, "Ballerina Wide Squat"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_102_02, "Hip Lifting"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_102_03, "Single Leg Bridge"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_102_04, "Back Extension"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_102_05, "Swimming"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_102_06, "Single Leg Circle"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_102_07, "Double Leg Kick"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_102_08, "Stand Hip Extension"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_102_09, "Lunge"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_102_10, "Hug A Tree Squat"));
                    break;
                case TBT1:
                    m_title.setText(R.string.program_002_001);
                    url = getUrlHtmlResources(getString(R.string.html_tbt1));
                    m_array.add(new TitlePhoto(R.drawable.fitness_201_01, "Jumping Jack"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_201_02, "Arm Walking"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_201_03, "High Knee Freeze"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_201_04, "Hip Bridge"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_201_05, "Squat"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_201_06, "Push-up"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_201_07, "Heel Touch"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_201_08, "Reverse Lunge"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_201_09, "Sky Reach"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_201_10, "Sumo Walk"));
                    break;
                case TBT2:
                    m_title.setText(R.string.program_002_002);
                    url = getUrlHtmlResources(getString(R.string.html_tbt2));
                    m_array.add(new TitlePhoto(R.drawable.fitness_202_01, "Jumping Jack"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_202_02, "SC with Reach"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_202_03, "Oblique Crunch"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_202_04, "Push-up Combo"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_202_05, "Double Deep Squat"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_202_06, "T Push-up"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_202_07, "Russian Twist"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_202_08, "Front Lunge"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_202_09, "Back Extension"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_202_10, "Side Crunch"));
                    break;
                case Baby1:
                    m_title.setText(R.string.program_003_001);
                    url = getUrlHtmlResources(getString(R.string.html_baby_1));
                    m_array.add(new TitlePhoto(R.drawable.fitness_301_01, "Side streching"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_301_02, "Spine twist"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_301_03, "Chin up"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_301_04, "Side Crunch"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_301_05, "Roll up down"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_301_06, "Cat pose"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_301_07, "Lift twist"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_301_08, "Wide squat"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_301_09, "Body balance"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_301_10, "Flying"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_301_11, "Bridge"));
                    break;
                case Baby2:
                    m_title.setText(R.string.program_003_002);
                    url = getUrlHtmlResources(getString(R.string.html_baby_2));
                    m_array.add(new TitlePhoto(R.drawable.fitness_302_01, "Roll up"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_302_02, "Hip kick"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_302_03, "Hack squat"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_302_04, "Deep lunge"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_302_05, "Lunge kick"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_302_06, "Jumping squat"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_302_07, "Boat"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_302_08, "Flying"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_302_09, "Lunge press"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_302_10, "Hyper extension"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_302_11, "Step squat"));
                    m_array.add(new TitlePhoto(R.drawable.fitness_302_12, "Lift&twist"));
                    break;
                default:
                    return;
            }

            m_webView.loadUrl(url);
        }

        m_adapter = new TitlePhotoAdapter(this, program == ProgramCode.Basic ? true : false);
        m_adapter.bindData(m_array);

        if (program == ProgramCode.Basic) {
            m_view = (GridView) findViewById(R.id.course_one_item_list);
        } else {
            m_view = (GridView) findViewById(R.id.course_all_item_list);
            ((ExpandableHeightGridView) m_view).setExpanded(true);
        }

        m_view.setOnItemClickListener(this);
        m_view.setAdapter(m_adapter);
    }

    private void dismissCallback() {
        if (success) {
            showMessage(getString(R.string.success_connect));
        } else {
            showMessage(getString(R.string.fail_connect_device_confirm));
        }
        isWait = false;
    }

    private BroadcastReceiver downComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            m_isdown = false;
            unregisterReceiver(downComplete);
        }
    };

    private void checkAndPlay() {
        callActivity();
        /*
        if (BluetoothManager.isConnectionCompleted()) {
            callActivity(subCode);
        }
        else {
            if (BluetoothManager.getCountGattClient() > 0) {
                try {
                    Thread.sleep(1000);
                    checkAndPlay(subCode);
                }
                catch (Exception e) {}
                return;
            }
            MessageDialog dialog = new MessageDialog(ActivityMain.MainContext, "iBODY24 Coach 장치와 연결되지 않았습니다.", null);
            dialog.show();
        }
        */
    }

    @Override
    protected void onStart() {
        Log("onStart");
        super.onStart();
        //connectDevice(this);
    }

    private void callActivity() {
        Intent intent = new Intent(this, ActivityVideo.class);
        startActivityForResult(intent, REQUEST_CODE_PLAY_VIDEO);
//        intent.putExtra("ProgCode", m_progCode);
//        intent.putExtra("SubCode", subCode);
//        Activity act = getParent();
//        if (act == null) {
//            startActivityForResult(intent, REQUEST_CODE_PLAY_VIDEO);
//        } else {
//            act.startActivityForResult(intent, REQUEST_CODE_PLAY_VIDEO);
//        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log("선택: " + position);
        if (program == ProgramCode.Basic) {
            basic = ProgramCode.BasicCode.getBasicCode(position + 1);
            checkAndPlay();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.course_all_startButton:
                if (!FileManager.isExistFile(this, getXmlFileName())) {
                    VersionService service = new VersionService(this, m_query_code);
                    service.runQuery(QueryCode.CheckVersion);
                }
                if (!FileManager.isExistFile(this, getVideoFileName())) {
                    showMessageConfirm(getString(R.string.warnning_download), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                return;
                            } else {
                                m_btn_start.setEnabled(false);
                                String url = "http://ibody24.com/down/" + getLanguageVideoFileName();
                                Log.d(tag, "Video File URL: " + url);
                                downloader = new WebDownLoad(url, FileManager.getMainPath(getApplicationContext()), ActivityCourse.this, ActivityCourse.this);
                                downloader.start();

                                m_dialog = new DownProgressDialog(ActivityMain.MainContext);
                                m_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                String title = "Fitness Program No. " + program.getProgramCode();
                                m_dialog.setMessage(title);
                                m_dialog.setMax(100);
                                m_dialog.setCancelable(true);
                                m_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        cancelDownload();
                                    }
                                });
                                m_dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            cancelDownload();
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                                m_dialog.show();
                            }
                        }
                    });
                } else {
                    isClickButton = true;

                    //규창 16.12.14 구형 코치 영상 재생 불가능 대응 코드
                    if(ActivityTabHome.m_app_use_product == ProductCode.Fitness){
                        SendReceive.getConnectionState(this);
                    }else if(ActivityTabHome.m_app_use_product == ProductCode.Coach){
                        isClickButton = false;
                        Log.i(tag,"코치일 경우 그냥 재생");
                        checkAndPlay();
                    }

                }
                break;
        }
    }

    public boolean isDownloading() {
        Log.d(tag, "isDownloading()");
        if (downloader == null || m_dialog == null) {
            return false;
        }

        if (downloader.isAlive() || m_dialog.isShowing()) {
            return true;
        }

        return false;
    }

    public void cancelDownload() {
        Log.d(tag, "cancelDownload()");
        if (downloader != null && downloader.isAlive())
            downloader.cancel();
        if (m_dialog != null && m_dialog.isShowing())
            m_dialog.dismiss();
        if (m_btn_start != null)
            m_btn_start.setEnabled(true);
    }

    @Override
    public void onProgress(int progress) {
        m_dialog.setProgress(progress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelDownload();
    }

    @Override
    public void run(Intent intent) {
        String action = intent.getAction();

        if (action.equals(Action.Vm.ACTION_MW_GET_QUERY_CODE)) {
            m_query_code = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);
        } else if(action.equals(Action.Bm.ACTION_MW_CONNECTION_STATE)) {
            int state = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);

            if (isWait) {
                if (state == ConnectStatus.STATE_CONNECTED.ordinal()) {
                    m_popup_handler.removeCallbacks(m_popup_runnable);
                    success = true;
                    cancelMessageNonBtn(dismiss_listener);
                    return;
                }
            }

            if (!isClickButton)
                return;

            if (state == ConnectStatus.STATE_DISCONNECTED.ordinal()) {
                //규창 17.03.15 메인화면 강제 리프레시에 있던 연결 브로드캐스트 사용
                //SendReceive.sendRequestConnection(this);
                SendReceive.sendConnect(this, CoachBaseActivity.DB_User.getDeviceAddress());
                showMessageNonBtn(getString(R.string.connecting_device));
                success = false;
                isWait = true;
                m_popup_handler.postDelayed(m_popup_runnable, 4000);
                isClickButton = false;
                return;
            }

            SendReceive.isBusySender(this);
        } else if (action.equals(Action.Bm.ACTION_MW_IS_BUSY_SENDER)) {
            boolean isBusy = intent.getBooleanExtra(Action.EXTRA_NAME_BOOL_1, false);
            if (isBusy) {
                showMessage(getString(R.string.device_initial_waiting));
                isClickButton = false;
                return;
            }
            //규창 16.12.14 구형 코치 영상 재생 불가능 대응 코드
            if (ActivityTabHome.m_app_use_product == ProductCode.Fitness) {
                SendReceive.sendPeripheralState(this);
            }
        } else if (action.equals(Action.Bm.ACTION_MW_PERIPHERAL_STATE) && ActivityTabHome.m_app_use_product == ProductCode.Fitness) {
            short state = intent.getShortExtra(Action.EXTRA_NAME_SHORT_1, (short) 0);

            if (!isClickButton)
                return;
            isClickButton = false;

            if (state != StatePeripheral.IDLE.getState()) {
                showMessage(getString(R.string.state_not_idle));
                return;
            }

            checkAndPlay();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SendReceive.getConnectionState(this);
    }

    @Override
    public void success(String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_btn_start.setText("START");
                m_btn_start.setEnabled(true);
                m_dialog.dismiss();
            }
        });
    }

    @Override
    public void fail() {
    }
}
