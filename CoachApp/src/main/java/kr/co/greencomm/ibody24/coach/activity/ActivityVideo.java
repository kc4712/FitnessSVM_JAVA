package kr.co.greencomm.ibody24.coach.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.utils.SetInfoView;
import kr.co.greencomm.ibody24.coach.utils.TimerTextView;
import kr.co.greencomm.ibody24.coach.data.DataTransfer;
import kr.co.greencomm.ibody24.coach.utils.ProgramCode;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver.Action;
import kr.co.greencomm.middleware.utils.FileManager;


/**
 * Created by young on 2015-08-21.
 */
public class ActivityVideo
        extends CoachBaseActivity {
    private static final String tag = ActivityVideo.class.getSimpleName();
    // 블루투스 장치의 연결을 대기하는 최대 시간 (msec)
    private static final int BLUETOOTH_CONNECT_WAIT_LIMIT = 6000;
    private static final int READY_VIDEO_MEDIA = 0x01;
    private static final int READY_BLUETOOTH_DEVICE = 0x02;
    private static final int READY_ALL = 0x03;

    // 상단 하단 좌측, 최하단에 표시되는 각종 텍스트 박스의 표시될때의 백그라운드 컬러
    private static int Information_Text_Back_Color = Color.rgb(0x33, 0x33, 0x33);

    // 비디오 재생 뷰
    private VideoView videoView;
    // 비디오 파일 플레이 준비 또는 블루투스 장치 연결 대기
    private ProgressBar progressBar;

    // 하단에 표시되는 각종 정보 표시 영역이 모두 포함된 레이아웃 한꺼번에 안보이게 할때 사용
    private LinearLayout m_infoView;

    // 세트 종합 정보 표시 관련
    private SetInfoView m_setInfo;

    // 포인트, 칼로리, 심박수, 운동횟수 등 텍스트 표시 뷰
    private TextView m_txt_Point;
    private TextView m_txt_Calorie;
    private TextView m_txt_HeartRate;
    private TextView m_txt_Count;
    private TimerTextView m_timer_warning;
    // 정확도 관련 바 그래프 표시를 위한 이미지 뷰
    private ImageView m_bar_graph;

    // 상단 운동 제목, 하단 좌측의 정확도, 최하단의 각종 멘트 표시 텍스트 뷰
    private TimerTextView m_timer_title;
    private TimerTextView m_timer_info;
    private TimerTextView m_timer_Acc;

    // 현재 진행중인 재생 시간 (초)
    private int m_sec = 0;

    // 비디오 화면 터치 주기를 확보하기 위한 플래그
    private boolean m_videoBeginTouch = false;
    // 비디오 재생 쓰레드 지속 컨디션 플레그
    private boolean m_loopFlag = false;
    // 비디오 재생, 중지 상태 플래그
    private boolean m_isPlaying = false;
    // 블루투스 장치 연결 요청 시간 (msec)
    private long m_request_time = 0;
    // 각종 상태를 종합하여 비디오 재생 관련 상황 판다.
    private int m_status;

    private boolean isShow;

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video);

        // 비디오 재생 중에 화면이 항상 켜져 있도록 한다.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.Bm.ACTION_MW_CONNECTION_STATE);
        filter.addAction(Action.Vm.ACTION_MW_TOTAL_SCORE);
        filter.addAction(Action.Vm.ACTION_MW_WARNNING);
        filter.addAction(Action.Vm.ACTION_MW_TOP_COMMENT);
        filter.addAction(Action.Vm.ACTION_MW_BOTTOM_COMMENT);
        filter.addAction(Action.Vm.ACTION_MW_MAINUI);
        filter.addAction(Action.Vm.ACTION_MW_SHOWUI);
        filter.addAction(Action.Am.ACTION_MW_GENERATE_COACH_EXER_DATA);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                run(intent);
            }
        };
        registerReceiver(mReceiver, filter);

        SendReceive.getConnectionState(this);

        // 필요한 모든 위젯을 확보하고 값을 초기화 한다.
        getWidgets();

        // 비디오 파일 명칭
        String fileName = getVideoFileName();
        Log("FileName: " + fileName);

        // 준비운동 이라면 비디오 뷰에 인터넷 주소를 제공하고
        if (program == ProgramCode.Basic) {
            Uri uri = Uri.parse("http://ibody24.com/video/" + fileName);
            Log("URI: " + uri.toString());
            videoView.setVideoURI(uri);
        }
        // 준비운동이 아니면 비디오 뷰에 다운로드된 파일을 제공한다.
        else {
            File path = new File(FileManager.getMainPath(this));
            File file = new File(path, fileName);
            videoView.setVideoPath(file.getAbsolutePath());
        }

        // 종합 상태값을 초기화 한다.
        m_status = 0;
        // 준비하는 과정을 미리함
        videoView.requestFocus();

        // 동영상이 재생준비가 완료되엇을떄를 알수있는 리스너 (실제 웹에서 영상을 다운받아 출력할때 많이 사용됨)
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // 동영상 재생준비가 완료된후 호출되는 메서드
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 동영상 재생 준비가 완료 되었다면 블루투스 장치 연결 상태를 확인하여
                // 비디오 재생을 시작할수 있도록 시작 메서드를 호출한다.
                m_status |= READY_VIDEO_MEDIA;
                checkStart();
            }
        });

        // 비디오 화면 클릭 이벤트 처리
        // 재생중 클릭하면 일시정지, 재생을 반복한다.
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (m_videoBeginTouch == false) {
                    m_videoBeginTouch = true;
                    if (m_isPlaying) {
                        m_isPlaying = false;
                        videoView.pause();
                        m_infoView.setVisibility(View.GONE);
                        // 비디오 메니져에게 일시 중지 상황을 알림
//                        if (m_video != null) {
//                            m_video.setPlaying(m_isPlaying);
//                        }
                    } else {
                        m_isPlaying = true;
                        videoView.start();
                        // 비디오 메니져에게 재생 상황을 알림
//                        if (m_video != null) {
//                            m_video.setPlaying(m_isPlaying);
//                        }
                    }
                }
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        m_videoBeginTouch = false;
                    }
                }, 100);
                return true;
            }
        });

        // 동영상 재생이 완료된걸 알수있는 리스너
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            // 동영상 재생이 완료된후 호출되는 메서드
            public void onCompletion(MediaPlayer player) {
                // 동영상이 끝에 도달한 경우 정상으로 종료 처리
                closeActivity(RESULT_OK);
            }
        });

        if (program != ProgramCode.Basic) {
//            if (BluetoothLEManager.getCountGattClient() == 0) {
//                BluetoothLEManager.registCallback(m_bluetooth_callback);
            // 블루투스 메니져의 인스턴스를 얻는다.
//            m_bluetooth = BluetoothLEManager.getInstance(this);
            // 밴드 연결을 요청한다.
//            m_bluetooth.connect(DB_User.getDeviceAddress());
            m_request_time = System.currentTimeMillis();
//            }
        }

        if (program == ProgramCode.Basic) {
            startVideo();
        } else {
            loopThread.start();
        }
    }

//    private IBluetooth m_bluetooth_callback = new IBluetooth() {
//        @Override
//        public void connectionState(int i) {
//            if (i == BluetoothManager.CONNECTION_COMPLETE) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        m_status |= READY_BLUETOOTH_DEVICE;
//                        String devName = DB_User.getDeviceName();
//                        if (devName == null || devName.isEmpty()) {
//                            // 여기에서 정보 업데이트
//                            m_bluetooth = BluetoothManager.getInstance(ActivityVideo.this);
//                            String[] infos = m_bluetooth.getDeviceInformation();
//                            if (infos.length > 1) {
//                                DB_User.setDeviceAddress(infos[1]);
//                            }
//                            DB_User.setDeviceName(infos[0]);
//                            DB_User.runUserQuery(QueryCode.SetDevice, null);
//                        }
//                        checkStart();
//                    }
//                });
//            }
//        }
//        @Override
//        public void changedScanList(ArrayList<DeviceInformation> arrayList) {}
//    };

    private void startVideo() {
        Log.d(tag, "startVideo");
        progressBar.setVisibility(View.GONE);
        videoView.seekTo(0);
        videoView.start();
        m_isPlaying = true;
    }

    private void checkStart() {
        if (m_status == READY_ALL) {
            // 블루투스 운동 모드 시작
//            m_bluetooth = BluetoothLEManager.getInstance(this);
//            m_bluetooth.play();
            // 비디오 메니져의 인스턴스를 얻는다.
//            m_video = VideoManager.getInstance(getApplicationContext());
//            VideoManager.registCallback(this);
            //언어 설정
//            m_video.setLocale(Locale.getDefault().getLanguage());
            //까지
//            m_video.setPlayMode(VideoManager.MODE_NEW_START);
//            m_video.setVideoID(m_progCode);
//            m_video.play();
            if (program != ProgramCode.Basic) {
                SendReceive.setPlay(this, getXmlFileName());
                startVideo();
            }
        }
    }

    private void closeActivity(int resultCode) {
        try {
            m_loopFlag = false;
            loopThread.join();
        } catch (Exception e) {
        }

        if (program != ProgramCode.Basic) {
            SendReceive.setEnd(this);
//            VideoManager.unregistCallback();
//            if (m_video != null) {
//                m_video.end();
//            }
//            BluetoothLEManager.unregistCallback();
//            if (m_bluetooth != null) {
//                if (BluetoothLEManager.getCountGattClient() > 0) {
//                    m_bluetooth.stop();
//                m_bluetooth.disconnect();
//                }
//            }

        }

        if (resultCode == RESULT_OK) {
            DataTransfer transfer = new DataTransfer();
            transfer.start();
        }

        m_status = 0;

        m_isPlaying = false;
        finish();
    }

    /**
     * 화면 표시 등을 위해 각 위젯들을 가져오고
     * 내용을 초기화 한다.
     */
    private void getWidgets() {
        videoView = (VideoView) findViewById(R.id.video_VideoView);
        progressBar = (ProgressBar) findViewById(R.id.video_ProgressBar);

        m_infoView = (LinearLayout) findViewById(R.id.video_info_view);
        m_setInfo = new SetInfoView(findViewById(R.id.video_set_view));

        m_txt_Point = (TextView) findViewById(R.id.video_txt_pts);
        m_txt_Calorie = (TextView) findViewById(R.id.video_txt_cal);
        m_txt_HeartRate = (TextView) findViewById(R.id.video_txt_heart);
        m_txt_Count = (TextView) findViewById(R.id.video_txt_cnt);

        m_timer_warning = new TimerTextView((TextView) findViewById(R.id.video_txt_acc_warning), 2000, Color.WHITE);

        m_timer_title = new TimerTextView((TextView) findViewById(R.id.video_txt_title), 2000, Information_Text_Back_Color);
        m_timer_info = new TimerTextView((TextView) findViewById(R.id.video_txt_info), 2000, Information_Text_Back_Color);
        m_timer_Acc = new TimerTextView((TextView) findViewById(R.id.video_txt_acc), 2000, Information_Text_Back_Color);

        m_bar_graph = (ImageView) findViewById(R.id.video_bar_graph);

        m_txt_Point.setText("0");
        m_txt_Calorie.setText("0");
        m_txt_HeartRate.setText("0");
        m_txt_Count.setText("0");

        m_infoView.setVisibility(View.GONE);
        m_setInfo.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        closeActivity(RESULT_OK);
    }

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            // 비디오 시작이 되지 않고 일정 시간 이상 지나면 중단 처리
            if (m_loopFlag == true && m_status != 0x03) {
                if (m_request_time + BLUETOOTH_CONNECT_WAIT_LIMIT < System.currentTimeMillis()) {
                    // 종료시킨다.
                    m_loopFlag = false;
                    try {
                        loopThread.join();
                    } catch (Exception e) {
                    }
                    if (m_status == 0x01) {
                        displayMsg(R.string.not_connect_band, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                closeActivity(RESULT_CANCELED);
                            }
                        });
                    } else {
                        displayMsg(R.string.not_prepare_video, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                closeActivity(RESULT_CANCELED);
                            }
                        });
                    }
                }
            } else {
                // 비디오 재생 시간을 1초 간격으로 미들웨어에 제공
                if (m_isPlaying) {
                    int pos = videoView.getCurrentPosition() / 1000;
                    if (m_sec != pos) {
                        m_sec = pos;
//                        m_video.setCurrentPosition(m_sec);
                        Log("SetPosition: " + m_sec);
                        // UI가 표시되지 않아도 되는 시간인지 확인하여 끈다.
                        if (program != ProgramCode.Basic) {
                            SendReceive.setCurrentPosition(ActivityVideo.this, pos);
                            m_infoView.setVisibility(!isShow ? View.GONE : View.VISIBLE);
                        }
                    }
                }
            }

            // 화면에 표시되는 텍스트 정보들이 일정 시간 이후 자동으로 꺼짐 처리
            m_timer_title.hideCheck();
            m_timer_info.hideCheck();
            m_timer_Acc.hideCheck();

            // 세트 종합 정보 표시 처리
            m_setInfo.check();

        }
    };

    Thread loopThread = new Thread(new Runnable() {
        public void run() {
            try {
                m_loopFlag = true;
                while (m_loopFlag) {
                    myHandler.sendMessage(myHandler.obtainMessage());
                    Thread.sleep(50);
                }
            } catch (Throwable t) {
                // Exit Thread
            }
        }
    });

    private float m_org_Point = 0;
    private float m_org_Calorie = 0;
    private int m_org_HeartRate = 0;
    private int m_org_Count = 0;
    private int m_org_Accuracy = 0;

    void displayPoint(int value) {
        if (m_org_Point == value) return;
        m_org_Point = value;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String arg = String.valueOf(m_org_Point);
                Log("포인트: " + arg);
                m_txt_Point.setText(arg);
            }
        });
    }

    void displayCalorie(float value) {
        if (m_org_Calorie == value) return;
        m_org_Calorie = value;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String arg = String.valueOf(m_org_Calorie);
                Log("칼로리소비: " + arg);
                m_txt_Calorie.setText(arg);
            }
        });
    }

    void displayHeartRate(int value) {
        if (m_org_HeartRate == value) return;
        m_org_HeartRate = value;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String arg = String.valueOf(m_org_HeartRate);
                Log("심박수: " + arg);
                m_txt_HeartRate.setText(arg);
            }
        });
    }

    void displayWarnning(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_timer_warning.show("Warning!!");
                m_timer_info.show(msg);
            }
        });
    }

    void displayCount(int value) {
        if (m_org_Count == value) return;
        m_org_Count = value;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String arg = String.valueOf(m_org_Count);
                Log("수행횟수: " + arg);
                m_txt_Count.setText(arg);
            }
        });
    }

    void displayAccuracyBar(int value) {
        if (m_org_Accuracy == value) return;
        m_org_Accuracy = value;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String arg = String.valueOf(m_org_Accuracy);
                Log("정확도: " + arg);
                if (1 <= m_org_Accuracy && m_org_Accuracy < 20) {
                    m_bar_graph.setImageResource(R.drawable.video_precision_01);
                } else if (20 <= m_org_Accuracy && m_org_Accuracy < 40) {
                    m_bar_graph.setImageResource(R.drawable.video_precision_02);
                } else if (40 <= m_org_Accuracy && m_org_Accuracy < 60) {
                    m_bar_graph.setImageResource(R.drawable.video_precision_03);
                } else if (60 <= m_org_Accuracy && m_org_Accuracy < 80) {
                    m_bar_graph.setImageResource(R.drawable.video_precision_04);
                } else if (80 <= m_org_Accuracy && m_org_Accuracy <= 100) {
                    m_bar_graph.setImageResource(R.drawable.video_precision_05);
                } else {
                    m_bar_graph.setImageResource(R.drawable.video_precision_00);
                }
            }
        });
    }

    void displayDescription(String msg) {
        Log("표시정보: " + msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_timer_info.show(msg);
            }
        });
    }

    void displayTitle(String msg) {
        Log("제목표시: " + msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_timer_title.show(msg);
            }
        });
    }

    void displayAccuracy(int value) {
        if (m_org_Accuracy == value) return;

        if (value < 1) {
            return;
        }
        Log("정확도텍스트표시: " + value);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_timer_Acc.show(String.valueOf(value));
            }
        });
    }

    void displayTotalScore(double displayTime, int point, int count_percent, int accuracy_percent, String desc) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(tag, "displayTotalScore -> "+displayTime+" "+point+" "+count_percent+" "+accuracy_percent+ " "+desc);
                m_setInfo.show(displayTime, point, count_percent, accuracy_percent, desc);
            }
        });
    }

    @Override
    public void run(Intent intent) {
        String action = intent.getAction();

        Log.d(tag, "Video SendReceive onReceive-> action : " + action);

        if (action.equals(Action.Bm.ACTION_MW_CONNECTION_STATE)) {
            int state = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);
            if (state == 2) {
                m_status |= READY_BLUETOOTH_DEVICE;
                checkStart();
            }
        } else if (action.equals(Action.Vm.ACTION_MW_TOTAL_SCORE)) {
            double duration = intent.getDoubleExtra(Action.EXTRA_NAME_DOUBLE_1, 0);
            int point = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);
            int count_percent = intent.getIntExtra(Action.EXTRA_NAME_INT_2, 0);
            int accuracy_percent = intent.getIntExtra(Action.EXTRA_NAME_INT_3, 0);
            String comment = intent.getStringExtra(Action.EXTRA_NAME_STRING_1);

            displayTotalScore(duration, point, count_percent, accuracy_percent, comment);
        } else if (action.equals(Action.Vm.ACTION_MW_WARNNING)) {
            String warnning = intent.getStringExtra(Action.EXTRA_NAME_STRING_1);

            displayWarnning(warnning);
        } else if (action.equals(Action.Vm.ACTION_MW_TOP_COMMENT)) {
            String comment = intent.getStringExtra(Action.EXTRA_NAME_STRING_1);

            displayTitle(comment);
        } else if (action.equals(Action.Vm.ACTION_MW_BOTTOM_COMMENT)) {
            String comment = intent.getStringExtra(Action.EXTRA_NAME_STRING_1);

            displayDescription(comment);
        } else if (action.equals(Action.Vm.ACTION_MW_MAINUI)) {
            int accuracy = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);
            int point = intent.getIntExtra(Action.EXTRA_NAME_INT_2, 0);
            int count = intent.getIntExtra(Action.EXTRA_NAME_INT_3, 0);
            float calorie = intent.getFloatExtra(Action.EXTRA_NAME_FLOAT_1, 0);
            int HRcmp = intent.getIntExtra(Action.EXTRA_NAME_INT_4, 0);

            //displayAccuracy(accuracy);
            displayAccuracyBar(accuracy);
            displayPoint(point);
            displayCount(count);
            displayCalorie(calorie);
            displayHeartRate(HRcmp);
        } else if (action.equals(Action.Vm.ACTION_MW_SHOWUI)) {
            boolean showUI = intent.getBooleanExtra(Action.EXTRA_NAME_BOOL_1, false);

            isShow = showUI;
        } else if (action.equals(Action.Am.ACTION_MW_GENERATE_COACH_EXER_DATA)) {
            // 영상 끝나면 자동으로 업로드 중. 이거 필요헌가?
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
