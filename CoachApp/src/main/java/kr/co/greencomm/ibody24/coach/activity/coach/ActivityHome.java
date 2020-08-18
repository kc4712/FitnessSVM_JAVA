package kr.co.greencomm.ibody24.coach.activity.coach;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.ActivityCoachBasic;
import kr.co.greencomm.ibody24.coach.activity.ActivityFitnessProgram;
import kr.co.greencomm.ibody24.coach.activity.fitness.ActivityStress;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.data.TodayInfo;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.ibody24.coach.utils.StressIdentifier;
import kr.co.greencomm.middleware.bluetooth.ConnectStatus;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver;
import kr.co.greencomm.middleware.wrapper.MWControlCenter;


/**
 * Created by young on 2015-08-20.
 */
public class ActivityHome
        extends CoachBaseActivity
        implements View.OnClickListener {
    private static final String tag = ActivityHome.class.getSimpleName();

    private Button m_btn_basic_1, m_btn_basic_2;
    private Button m_btn_trainer_1, m_btn_trainer_2;

    //규창 16.12.26 상태 버튼 변수들
    private Button m_btn_connect;
    //규창 16.12.26 버튼을 이용한 메인화면 커넥트 플래그
    private boolean m_connect;
    //규창 16.12.26 버튼의 백그라운드 리소스 상태 플래그 true = 버튼prs, false = 버튼 nor
    private boolean m_btn_resource;

    //규창 16.12.26 애니메이션 스타트 플래그
    private static int animCounter = 0;

    //규창 16.12.26 버튼 애니메이션을 위한 타이머 변수
    private Timer m_timer;

    private TextView m_text_stress_desc;
    private TextView m_text_stress;

    private TextView m_text_acc;
    private TextView m_text_min;
    private TextView m_text_cal;

    private TextView m_text_level;

    private LinearLayout m_layout_stress;


    public static ActivityHome Home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.coachplus_main_coach);

        setApplicationStatus(ApplicationStatus.HomeScreen);
        ActivityStress.select_mode = ApplicationStatus.HomeScreen;

        Home = this;

        TodayInfo today = CoachBaseActivity.DB_Today;
        today.setUser(CoachBaseActivity.DB_User.getCode());
        today.runUserQueryToday(this);

        m_popup_mode = false;

        m_layout_stress = (LinearLayout) findViewById(R.id.main_coach_layout_stress);
        m_layout_stress.setOnClickListener(this);

        m_btn_basic_1 = (Button) findViewById(R.id.main_coach_basicProgram_1);
        m_btn_basic_2 = (Button) findViewById(R.id.main_coach_basicProgram_2);
        m_btn_basic_1.setOnClickListener(this);
        m_btn_basic_2.setOnClickListener(this);

        m_btn_trainer_1 = (Button) findViewById(R.id.main_coach_trainerProgram_1);
        m_btn_trainer_2 = (Button) findViewById(R.id.main_coach_trainerProgram_2);
        m_btn_trainer_1.setOnClickListener(this);
        m_btn_trainer_2.setOnClickListener(this);

        //규창 16.12.24 버튼 연결
        m_btn_connect = (Button) findViewById(R.id.button_bt_connect_change);
        m_btn_connect.setOnClickListener(this);

        m_text_stress_desc = (TextView) findViewById(R.id.main_coach_txt_stress_desc);
        m_text_stress = (TextView) findViewById(R.id.main_coach_txt_stress);

        m_text_acc = (TextView) findViewById(R.id.main_coach_txt_acc);
        m_text_min = (TextView) findViewById(R.id.main_coach_txt_min);
        m_text_cal = (TextView) findViewById(R.id.main_coach_txt_cal);

        m_text_level = (TextView) findViewById(R.id.main_coach_txt_level);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(receiver, filter);

        reloadStress();

        //규창 16.12.25 메인화면이 처음 생성될시에 연결 상태를 물어봐 연결상태 버튼에 대한 행동을 전달받아야한다
        SendReceive.getConnectionState(this);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log("날자가 바뀌어 데이터를 초기화 합니다.!!");
                    //getExerciseData();
                    //updateBroadcast();
                }
            });
        }
    };

    @Override
    public void onShow() {
        reloadStress();
        //규창 16.12.25 메인화면이 나타나게 되면 연결 상태를 물어봐 연결상태 버튼에 대한 행동을 전달받아야한다
        SendReceive.getConnectionState(this);
        //SendReceive.sendPeripheralState(this);
        Log.i(tag,"test1");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void reloadStress() {
        int lvl = Preference.getStressState(this);

        if (lvl == StressIdentifier.VeryGood.getID()) {
            m_text_stress.setText(StressIdentifier.VeryGood.toString());
            m_text_stress.setVisibility(View.VISIBLE);
            m_text_stress_desc.setVisibility(View.GONE);
        } else if (lvl == StressIdentifier.Good.getID()) {
            m_text_stress.setText(StressIdentifier.Good.toString());
            m_text_stress.setVisibility(View.VISIBLE);
            m_text_stress_desc.setVisibility(View.GONE);
        } else if (lvl == StressIdentifier.Normal.getID()) {
            m_text_stress.setText(StressIdentifier.Normal.toString());
            m_text_stress.setVisibility(View.VISIBLE);
            m_text_stress_desc.setVisibility(View.GONE);
        } else if (lvl == StressIdentifier.Bad.getID()) {
            m_text_stress.setText(StressIdentifier.Bad.toString());
            m_text_stress.setVisibility(View.VISIBLE);
            m_text_stress_desc.setVisibility(View.GONE);
        } else {
            m_text_stress.setVisibility(View.GONE);
            m_text_stress_desc.setVisibility(View.VISIBLE);
        }
        //규창 16.12.25 메인화면이 나타나게 되면 연결 상태를 물어봐 연결상태 버튼에 대한 행동을 전달받아야한다
        SendReceive.getConnectionState(this);
        //SendReceive.sendPeripheralState(this);
    }

    public void updateGraph() {
        m_text_acc.setText(String.valueOf(DB_Today.getPoint()));
        m_text_min.setText(String.valueOf(DB_Today.getRunSec() / 60));
        m_text_cal.setText(String.valueOf(DB_Today.getCalorie()));

        int lev = DB_Today.getUserLevel();
        if (lev <= 50) {
            m_text_level.setText("1");
        } else if (lev <= 90) {
            m_text_level.setText("2");
        } else if (lev <= 160) {
            m_text_level.setText("3");
        } else {
            m_text_level.setText("4");
        }

        Log("오늘 정보 갱신!!!!!");
    }

    private void setImageAnimation() {

    }

    //규창 16.12.24 미들웨어가 던져 주는 최종 연결상태를 보고 버튼의 이미지를 정함
    private void updateConnect(int conn) {
        // 연결상태 확인
        if (conn == ConnectStatus.STATE_CONNECTED.ordinal()) {
            m_connect = true;
            m_btn_connect.setBackgroundResource(R.drawable.button_bt_connect_prs);
            m_btn_resource = true;
        } else if (conn == ConnectStatus.STATE_CONNECTING.ordinal()) {
            m_connect = false;
        } else if (conn == ConnectStatus.STATE_DISCONNECTED.ordinal()){
            m_connect = false;
            m_btn_connect.setBackgroundResource(R.drawable.button_bt_connect_nor);
            m_btn_resource = false;
        } else if (conn == ConnectStatus.STATE_DISCONNECTED.ordinal() && CoachBaseActivity.DB_User.getDeviceAddress() == null && m_btn_resource){
            m_connect = false;
            m_btn_connect.setBackgroundResource(R.drawable.button_bt_connect_nor);
            m_btn_resource = false;
        }
    }

    //규창 16.12.26 버튼 깜빡이는 애니메이션
    private void startTimer() {
        if (m_connect) {
            return;
        }
        if(animCounter > 0)
            return;

        m_timer = new Timer();
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ++animCounter;
                if (animCounter > 6){
                    m_timer.cancel();
                    //Log.i(tag, "끄자");
                    animCounter = 0;
                    SendReceive.getConnectionState(getApplicationContext());
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.i(tag, "블링크 블링크 블링크");
                        if (m_btn_resource) {
                            m_btn_connect.setBackgroundResource(R.drawable.button_bt_connect_nor);
                            m_btn_resource = false;
                        } else if (!m_btn_resource) {
                            m_btn_connect.setBackgroundResource(R.drawable.button_bt_connect_prs);
                            m_btn_resource = true;
                        }
                    }
                });
            }
        }, 0, 800);
    }
    /* 임시 주석
    private void cancelTimer() {
        if (m_timer != null) {
            m_timer.cancel();
            m_timer = null;
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_coach_basicProgram_1:
            case R.id.main_coach_basicProgram_2:
                ActivityTabHome.pushActivity(this, ActivityCoachBasic.class);
                break;
            case R.id.main_coach_trainerProgram_1:
            case R.id.main_coach_trainerProgram_2:
                ActivityTabHome.pushActivity(this, ActivityFitnessProgram.class);
                break;
            case R.id.main_coach_layout_stress:
                ActivityTabHome.pushActivity(this, ActivityStress.class);
                break;
            // 규창 16.12.24 버튼 액션 연결
            case R.id.button_bt_connect_change:
                if (m_connect == false && m_btn_connect.isPressed() ){
                    Log.i(tag, "눌렀을때 미연결 상태"+ m_btn_connect.getId());
                    m_connect = false;
                    m_btn_connect.setBackgroundResource(R.drawable.button_bt_connect_prs);
                    m_btn_resource = true;
                    if (animCounter == 0) {
                        if (MWControlCenter.getInstance(getApplicationContext()).getConnectionState() != ConnectStatus.STATE_CONNECTED
                                && (CoachBaseActivity.DB_User.getDeviceAddress() != "" || CoachBaseActivity.DB_User.getDeviceAddress() != null)) {
                            Log.i(tag,"연결시작");
                            //SendReceive.sendStartScan(this);
                            SendReceive.sendConnect(this, CoachBaseActivity.DB_User.getDeviceAddress());
                            startTimer();
                        }
                    }

                }else if (m_connect == true && m_btn_connect.isPressed()){
                    if(MWControlCenter.getInstance(getApplicationContext()).getConnectionState() == ConnectStatus.STATE_CONNECTED) {
                        Log.i(tag, "버튼은 눌렀으나 연결 중일 때" + m_btn_connect.getId());
                        m_btn_connect.setBackgroundResource(R.drawable.button_bt_connect_prs);
                        m_btn_resource = true;
                        MWControlCenter.getInstance(getApplicationContext()).sendForceRefresh();
                    }else if(CoachBaseActivity.DB_User.getDeviceAddress() != "" || CoachBaseActivity.DB_User.getDeviceAddress() != null){
                        if (animCounter == 0) {
                            Log.i(tag, "극히 예외적으로 연결 재시작");
                            //SendReceive.sendStartScan(this);
                            SendReceive.sendConnect(this, CoachBaseActivity.DB_User.getDeviceAddress());
                            startTimer();
                        }
                    }
                }
                SendReceive.getConnectionState(this);
                break;
        }
    }

    @Override
    public void run(Intent intent) {
        String action = intent.getAction();
        if (action.equals(MWBroadcastReceiver.Action.Am.ACTION_MW_STRESS_DATA)) {
            short stress = intent.getShortExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_1, (short) 0);
            Preference.putStressState(this, stress);
            reloadStress();
        }else if (action.equals(MWBroadcastReceiver.Action.Bm.ACTION_MW_CONNECTION_STATE)) {
            int state = intent.getIntExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_1, 0);
            //규창 16.12.24 연결상태에 대한 브로드캐스트를 버튼 상태 결정에도 쓰도록 메서드 호출
            updateConnect(state);
            //본 메서드는 미들웨어가 스캔할때 버튼 리소스를 On, Off 반복하는 타이머 활성화 메서드를 종료
        }
    }
}
