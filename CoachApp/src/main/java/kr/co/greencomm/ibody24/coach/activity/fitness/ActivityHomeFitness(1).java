package kr.co.greencomm.ibody24.coach.activity.fitness;

import android.animation.TimeInterpolator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.ActivityCoachBasic;
import kr.co.greencomm.ibody24.coach.activity.ActivityFitnessProgram;
import kr.co.greencomm.ibody24.coach.activity.ActivityMain;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.utils.ActivityIdentifier;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.data.DataTransfer;
import kr.co.greencomm.ibody24.coach.data.StepInfo;
import kr.co.greencomm.ibody24.coach.data.TodayInfo;
import kr.co.greencomm.ibody24.coach.provider.CoachResolver;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.middleware.bluetooth.BluetoothCommand;
import kr.co.greencomm.middleware.bluetooth.ConnectStatus;
import kr.co.greencomm.middleware.bluetooth.RequestAction;
import kr.co.greencomm.middleware.bluetooth.ScanMode;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver.Action;
import kr.co.greencomm.ibody24.coach.utils.SleepIdentifier;
import kr.co.greencomm.ibody24.coach.utils.StressIdentifier;
import kr.co.greencomm.middleware.utils.container.ActivityData;
import kr.co.greencomm.middleware.utils.container.IndexTimeData;
import kr.co.greencomm.middleware.wrapper.MWControlCenter;

/**
 * Created by jeyang on 16. 9. 21..
 */
public class ActivityHomeFitness extends CoachBaseActivity implements View.OnClickListener {
    private static final String tag = ActivityHomeFitness.class.getSimpleName();

    private TextView m_txt_stepcount, m_txt_calorie, m_txt_act, m_txt_act_desc,
            m_txt_sleep, m_txt_sleep_desc, m_txt_stress, m_txt_stress_desc;
    private LinearLayout m_layout_stepcount, m_layout_calorie, m_layout_act, m_layout_sleep,
            m_layout_stress, m_layout_sync;
    private ImageView m_img_line;

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

    public static ActivityHomeFitness Home;

    private StepInfo step_datas = new StepInfo();

    private Handler m_handler;
    private Runnable m_stop_runnable;
    private long now;
    private long delay = 500;
    private long duration = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.coachplus_main_fitness);

        setApplicationStatus(ApplicationStatus.HomeScreenFitness);
        ActivityStress.select_mode = ApplicationStatus.HomeScreenFitness;

        Home = this;

        TodayInfo today = CoachBaseActivity.DB_Today;
        today.setUser(CoachBaseActivity.DB_User.getCode());
        today.runUserQueryToday(this);

        m_popup_mode = false;

        m_btn_basic_1 = (Button) findViewById(R.id.main_fitness_basicProgram_1);
        m_btn_basic_2 = (Button) findViewById(R.id.main_fitness_basicProgram_2);
        m_btn_basic_1.setOnClickListener(this);
        m_btn_basic_2.setOnClickListener(this);

        m_btn_trainer_1 = (Button) findViewById(R.id.main_fitness_trainerProgram_1);
        m_btn_trainer_2 = (Button) findViewById(R.id.main_fitness_trainerProgram_2);
        m_btn_trainer_1.setOnClickListener(this);
        m_btn_trainer_2.setOnClickListener(this);

        //규창 16.12.24 버튼 연결
        m_btn_connect = (Button) findViewById(R.id.button_bt_connect_change);
        m_btn_connect.setOnClickListener(this);


        m_txt_stepcount = (TextView) findViewById(R.id.main_fitness_txt_stepcount);
        m_txt_calorie = (TextView) findViewById(R.id.main_fitness_txt_calorie);
        m_txt_act = (TextView) findViewById(R.id.main_fitness_txt_act);
        m_txt_act_desc = (TextView) findViewById(R.id.main_fitness_txt_act_desc);
        m_txt_sleep = (TextView) findViewById(R.id.main_fitness_txt_sleep);
        m_txt_sleep_desc = (TextView) findViewById(R.id.main_fitness_txt_sleep_desc);
        m_txt_stress = (TextView) findViewById(R.id.main_fitness_txt_stress);
        m_txt_stress_desc = (TextView) findViewById(R.id.main_fitness_txt_stress_desc);

        m_layout_stepcount = (LinearLayout) findViewById(R.id.main_fitness_layout_stepcount);
        m_layout_calorie = (LinearLayout) findViewById(R.id.main_fitness_layout_calorie);
        m_layout_act = (LinearLayout) findViewById(R.id.main_fitness_layout_act);
        m_layout_sleep = (LinearLayout) findViewById(R.id.main_fitness_layout_sleep);
        m_layout_stress = (LinearLayout) findViewById(R.id.main_fitness_layout_stress);
        m_layout_sync = (LinearLayout) findViewById(R.id.main_fitness_layout_sync);

        m_img_line = (ImageView) findViewById(R.id.main_fitness_img_line);

        m_handler = new Handler();
        m_stop_runnable = new Runnable() {
            @Override
            public void run() {
                translateSyncView(false);
            }
        };

        updateData();

        //규창 17.01.04 임시로 Commsender부하 낮추기 위함
        //규창 16.12.25 메인화면이 처음 생성될시에 연결 상태를 물어봐 연결상태 버튼에 대한 행동을 전달받아야한다
        //SendReceive.getConnectionState(this);

        //updateBroadcast();
//        translateSyncView();
        //getExerciseData();
    }

    private void translateSyncView(boolean start) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (start) {
                    m_img_line.setVisibility(View.VISIBLE);
                    m_layout_sync.setVisibility(View.VISIBLE);
                    m_layout_sync.setAlpha(0f);
                    m_layout_sync.setTranslationX(2000);

                    m_layout_sync.animate().translationX(0).alpha(1f).setDuration(duration);
                } else {
                    if (m_layout_sync.getVisibility() != View.VISIBLE) {
                        return;
                    }

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) m_layout_sync.getLayoutParams();
                    int ori_height = params.height;

                    TimeInterpolator interpolator = new TimeInterpolator() {
                        @Override
                        public float getInterpolation(float v) {
                            float new_height = ori_height - (ori_height * v);
                            params.height = (int) new_height;
                            m_layout_sync.setLayoutParams(params);
                            return v;
                        }
                    };

                    m_layout_sync.animate().setInterpolator(interpolator).setDuration(duration).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            m_img_line.setVisibility(View.GONE);
                            m_layout_sync.setVisibility(View.GONE);
                            params.height = ori_height;
                            m_layout_sync.setLayoutParams(params);
                            m_layout_sync.animate().setInterpolator(null).withEndAction(null);
                        }
                    }).start(); // pending된 애니메이션을 바로 실행하기 위해.
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        onShow();

    }

    @Override
    public void onShow() {
        translateSyncView(false);
        updateData();
    }

    private void updateData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int main_Step = Preference.getMainStep(ActivityHomeFitness.this);
                int main_Calorie = Preference.getMainCalorieActivity(ActivityHomeFitness.this) + Preference.getMainCalorieCoach(ActivityHomeFitness.this) +
                        Preference.getMainCalorieSleep(ActivityHomeFitness.this) + Preference.getMainCalorieDaily(ActivityHomeFitness.this);

                m_txt_stepcount.setText(String.valueOf(main_Step));
                m_txt_calorie.setText(String.valueOf(main_Calorie));

                reloadActivity();
                reloadSleep();
                reloadStress();

                //규창 17.01.04 상태정보로 인한 부하 경감
                //if(!MWControlCenter.getInstance(getApplicationContext()).isBusySender()) {
                    //규창 16.12.26 메인화면에서 연결상태버튼의 갱신을 위함
                    SendReceive.getConnectionState(getApplicationContext());
                //}
            }
        });
    }

    public void run(Intent intent) {
        String action = intent.getAction();
        if (action.equals(Action.Am.ACTION_MW_STEP_CALORIE)) {
            short step = intent.getShortExtra(Action.EXTRA_NAME_SHORT_1, (short) 0);
            double[] calorie_arr = intent.getDoubleArrayExtra(Action.EXTRA_NAME_DOUBLE_ARRAY);

            reloadStepNCalorie((int) step, calorie_arr);
        } else if (action.equals(Action.Am.ACTION_MW_STRESS_DATA)) {
            short stress = intent.getShortExtra(Action.EXTRA_NAME_SHORT_1, (short) 0);
            Preference.putStressState(this, stress);
            reloadStress();
        } else if (action.equals(Action.Am.ACTION_MW_SLEEP_DATA)) {
            short[] sleep = intent.getShortArrayExtra(Action.EXTRA_NAME_SHORT_ARRAY);
            if (sleep == null ) {
                return;
            }

            CoachResolver res = new CoachResolver();
            //규창 17.01.04 극히 예외 상황이겠지만 내장DB의 수면시간 아이템이 없는 경우가 존재하여 앱크러시 헌데 왜 메인화면에서 수면상태 제외한 나머지를 RWD?...
            if(res.getIndexTimeProvider(BluetoothCommand.Sleep.getCommand()) == null) {
                return;
            }
            IndexTimeData save_time = res.getIndexTimeProvider(BluetoothCommand.Sleep.getCommand());
            if (save_time.getStart_time() == null || save_time.getEnd_time() == null || save_time.getStart_time() == 0 || save_time.getEnd_time() == 0) {
                return;
            }
            now = save_time.getStart_time();

            //규창 17.01.04
            res.deleteIndexTimeProvider(now);

            long interval = ((save_time.getEnd_time()) - (save_time.getStart_time())) / 60000;
            Log.d(tag, "main page sleep 2 end:"+save_time.getEnd_time()+" start:"+save_time.getStart_time());

            SleepIdentifier idfier = getSleepCalc(interval);
            Preference.putSleepState(this, idfier.getID()); // 어떻게 계산할지 정해지지 않음.
            Preference.putSleepTime(this, (int) interval);
            Preference.putSleepRolled(this, sleep[0]);
            Preference.putSleepAwaken(this, sleep[1]);
            Preference.putSleepStabilityHR(this, sleep[2]);

            reloadSleep();
        } else if (action.equals(Action.Am.ACTION_MW_ACTIVITY_DATA)) {
            long start_time = intent.getLongExtra(Action.EXTRA_NAME_LONG_1, 0);
            if (start_time == 0) {
                return;
            }

            CoachResolver res = new CoachResolver();
            ActivityData data = res.getActivityDataProvider(start_time);

            res.deleteIndexTimeProvider(start_time); // resolver 교체 필요

            int flag = 0;
            short getData = data.getIntensityL();
            short[] intensity_arr = new short[]{data.getIntensityL(), data.getIntensityM(),
                    data.getIntensityH(), data.getIntensityD()};
//            Arrays.sort(intensity_arr);

            for (short s : intensity_arr) {
                if (getData < s) {
                    getData = s;
                    flag++;
                }
            }

            Preference.putActivityState(this, ActivityIdentifier.getActivityIdentifier(flag).getID());

            DataTransfer transfer = new DataTransfer();
            transfer.start();

            reloadActivity();
        } else if (action.equals(Action.Bm.ACTION_MW_CONNECTION_STATE)) {
            int conn = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);
            //규창 16.12.24 연결상태에 대한 브로드캐스트를 버튼 상태 결정에도 쓰도록 메서드 호출
            updateConnect(conn);

            if (conn == ConnectStatus.STATE_CONNECTED.ordinal()) {
                CoachResolver res = new CoachResolver();

                ArrayList<IndexTimeData> arr = res.getIndexTimeProvider();
                if (arr == null) {
                    return;
                }

                for (IndexTimeData data : arr) {
                    if (data.getEnd_time() == 0) {
                        continue;
                    }

                    int label = data.getLabel();
                    if (label != BluetoothCommand.Activity.getCommand()) {
                        return;
                    }

                    SendReceive.appendBluetoothMessage(this, BluetoothCommand.getCommand((short) label),
                            data.getStart_time(), RequestAction.Inform, true);
                }
            }
        } else if (action.equals(Action.Bm.ACTION_MW_DATASYNC)) {
            boolean isSync = intent.getBooleanExtra(Action.EXTRA_NAME_BOOL_1, false);
            if (isSync) {
                now = System.currentTimeMillis();

                translateSyncView(true);
            } else {
                long interval = System.currentTimeMillis() - now;

                m_handler.removeCallbacks(m_stop_runnable);

                if (interval < delay) {
                    m_handler.postDelayed(m_stop_runnable, delay);
                    return;
                }

                translateSyncView(false);
            }
        }
    }

    public void updateBroadcast() {
        SendReceive.getActivityData(this);
        SendReceive.getSleepData(this);
        SendReceive.getStepNCalorie(this);
        SendReceive.getStressData(this);
//        reloadActivity();
//        reloadSleep();
//        reloadStepNCalorie();
//        reloadStress();
    }

    private void reloadStress() {
        int lvl = Preference.getStressState(this);

        if (lvl == StressIdentifier.VeryGood.getID()) {
            m_txt_stress.setText(StressIdentifier.VeryGood.toString());
            m_txt_stress.setVisibility(View.VISIBLE);
            m_txt_stress_desc.setVisibility(View.GONE);
        } else if (lvl == StressIdentifier.Good.getID()) {
            m_txt_stress.setText(StressIdentifier.Good.toString());
            m_txt_stress.setVisibility(View.VISIBLE);
            m_txt_stress_desc.setVisibility(View.GONE);
        } else if (lvl == StressIdentifier.Normal.getID()) {
            m_txt_stress.setText(StressIdentifier.Normal.toString());
            m_txt_stress.setVisibility(View.VISIBLE);
            m_txt_stress_desc.setVisibility(View.GONE);
        } else if (lvl == StressIdentifier.Bad.getID()) {
            m_txt_stress.setText(StressIdentifier.Bad.toString());
            m_txt_stress.setVisibility(View.VISIBLE);
            m_txt_stress_desc.setVisibility(View.GONE);
        } else {
            m_txt_stress.setVisibility(View.GONE);
            m_txt_stress_desc.setVisibility(View.VISIBLE);
        }
    }

    private void reloadSleep() {
        int lvl = Preference.getSleepState(this);

        if (lvl == SleepIdentifier.Enough.getID()) {
            m_txt_sleep.setText(SleepIdentifier.Enough.toString());
            m_txt_sleep.setVisibility(View.VISIBLE);
            m_txt_sleep_desc.setVisibility(View.GONE);
        } else if (lvl == SleepIdentifier.Normal.getID()) {
            m_txt_sleep.setText(SleepIdentifier.Normal.toString());
            m_txt_sleep.setVisibility(View.VISIBLE);
            m_txt_sleep_desc.setVisibility(View.GONE);
        } else if (lvl == SleepIdentifier.Few.getID()) {
            m_txt_sleep.setText(SleepIdentifier.Few.toString());
            m_txt_sleep.setVisibility(View.VISIBLE);
            m_txt_sleep_desc.setVisibility(View.GONE);
        } else if (lvl == SleepIdentifier.Lack.getID()) {
            m_txt_sleep.setText(SleepIdentifier.Lack.toString());
            m_txt_sleep.setVisibility(View.VISIBLE);
            m_txt_sleep_desc.setVisibility(View.GONE);
        } else {
            m_txt_sleep.setVisibility(View.GONE);
            m_txt_sleep_desc.setVisibility(View.VISIBLE);
        }
    }

    private void reloadStepNCalorie(int step, double[] calorie) {
        m_txt_stepcount.setText(String.valueOf(step));

        if (calorie == null)
            calorie = new double[]{0, 0, 0, 0};
        m_txt_calorie.setText(String.valueOf((int) calorie[0] + (int) calorie[1] + (int) calorie[2] + (int) calorie[3]));

        //queryStep(step);
    }

    private void queryStep(int step) {
        if (step == Preference.getMainStep(this))
            return;
        long current = System.currentTimeMillis();

        step_datas.setReg(current);
        step_datas.setStep(step);

        step_datas.runQuery(QueryCode.InsertStep, this);
    }

    private void reloadActivity() {
        int lvl = Preference.getActivityState(this);

        if (lvl == ActivityIdentifier.Low.getID()) {
            m_txt_act.setText(ActivityIdentifier.Low.toString());
            m_txt_act.setVisibility(View.VISIBLE);
            m_txt_act_desc.setVisibility(View.GONE);
        } else if (lvl == ActivityIdentifier.Mid.getID()) {
            m_txt_act.setText(ActivityIdentifier.Mid.toString());
            m_txt_act.setVisibility(View.VISIBLE);
            m_txt_act_desc.setVisibility(View.GONE);
        } else if (lvl == ActivityIdentifier.High.getID()) {
            m_txt_act.setText(ActivityIdentifier.High.toString());
            m_txt_act.setVisibility(View.VISIBLE);
            m_txt_act_desc.setVisibility(View.GONE);
        } else if (lvl == ActivityIdentifier.Danger.getID()) {
            m_txt_act.setText(ActivityIdentifier.Danger.toString());
            m_txt_act.setVisibility(View.VISIBLE);
            m_txt_act_desc.setVisibility(View.GONE);
        } else {
            m_txt_act.setVisibility(View.GONE);
            m_txt_act_desc.setVisibility(View.VISIBLE);
        }
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
        if(animCounter > 0) {
            return;
        }

        m_timer = new Timer();
        m_timer.schedule(new TimerTask() {

            @Override
            public void run() {
                animCounter++;
                if (animCounter > 6){
                    m_timer.cancel();
                    Log.i(tag, "끄자");
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


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_fitness_basicProgram_1:
            case R.id.main_fitness_basicProgram_2:
                ActivityTabHome.pushActivity(this, ActivityCoachBasic.class);
                break;
            case R.id.main_fitness_trainerProgram_1:
            case R.id.main_fitness_trainerProgram_2:
                ActivityTabHome.pushActivity(this, ActivityFitnessProgram.class);
                break;
            case R.id.main_fitness_layout_stepcount:
                ActivityTabHome.pushActivity(this, ActivityStepCount.class);
                break;
            case R.id.main_fitness_layout_calorie:
                ActivityTabHome.pushActivity(this, ActivityToday.class);
                break;
            case R.id.main_fitness_layout_act:
                ActivityTabHome.pushActivity(this, ActivityActMeasure.class);
                break;
            case R.id.main_fitness_layout_sleep:
                ActivityTabHome.pushActivity(this, ActivitySleep.class);
                break;
            case R.id.main_fitness_layout_stress:
                ActivityTabHome.pushActivity(this, ActivityStress.class);
                break;
            // 규창 16.12.24 버튼 액션 연결
            case R.id.button_bt_connect_change:
                if (m_connect == false && m_btn_connect.isPressed() ){
                    SendReceive.getConnectionState(this);
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
                        translateSyncView(false);
                        Log.i(tag, "버튼은 눌렀으나 연결 중일 때" + m_btn_connect.getId());
                        m_btn_connect.setBackgroundResource(R.drawable.button_bt_connect_prs);
                        m_btn_resource = true;
                        MWControlCenter.getInstance(getApplicationContext()).sendForceRefresh();
                    }else if(MWControlCenter.getInstance(getApplicationContext()).getConnectionState() != ConnectStatus.STATE_CONNECTED &&
                            CoachBaseActivity.DB_User.getDeviceAddress() != "" || CoachBaseActivity.DB_User.getDeviceAddress() != null){
                        if (animCounter == 0) {
                            Log.i(tag, "극히 예외적으로 연결 재시작");
                            //SendReceive.sendStartScan(this);
                            SendReceive.sendConnect(this, CoachBaseActivity.DB_User.getDeviceAddress());
                            startTimer();
                        }
                    }
                }
                //규창 17.01.04 상태정보로 인한 부하 경감
                if(!MWControlCenter.getInstance(getApplicationContext()).isBusySender()) {
                    SendReceive.getConnectionState(this);
                }
            break;
        }
    }
}
