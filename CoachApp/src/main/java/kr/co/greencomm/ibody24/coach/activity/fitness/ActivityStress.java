package kr.co.greencomm.ibody24.coach.activity.fitness;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.adapter.ImageAdapter;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.middleware.bluetooth.ConnectStatus;
import kr.co.greencomm.middleware.bluetooth.StatePeripheral;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver;
import kr.co.greencomm.ibody24.coach.utils.StressIdentifier;
import kr.co.greencomm.middleware.wrapper.MWControlCenter;

/**
 * Created by jeyang on 16. 9. 21..
 */
public class ActivityStress extends CoachBaseActivity implements View.OnClickListener {
    private static final String tag = ActivityStress.class.getSimpleName();

    public static ApplicationStatus select_mode = ApplicationStatus.HomeScreenFitness;

    private Button m_btn_start;
    private ImageView m_img_graph;
    private TextView m_txt_state_result, m_txt_state_desc, m_txt_warnning;
    //private AdapterViewFlipper m_img_counter;
    private ImageView m_img_counter;
    private TextView m_txt_counter;
    private FrameLayout m_layout_counter;

    //private ImageAdapter m_adapter;
    //private ArrayList<Integer> m_list;

    private int counter, limit = 120;

    private Runnable m_popup_runnable;
    private Handler m_popup_handler;

    private boolean success, isWait;
    private DialogInterface.OnDismissListener dismiss_listener;
    private boolean isClickButton;

    private Timer m_timer;

    private long now;
    private boolean isStart, popup_exit;
    private boolean isCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coachplus_stress);
        setApplicationStatus(ApplicationStatus.StressScreen);

        //규창 16.12.27 화면꺼짐 방지 -- 일부 기기에서 안먹네?.....
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        m_btn_start = (Button) findViewById(R.id.stress_btn_start);
        m_img_graph = (ImageView) findViewById(R.id.stress_img_graph);
        m_txt_state_result = (TextView) findViewById(R.id.stress_txt_state);
        m_txt_state_desc = (TextView) findViewById(R.id.stress_txt_state_desc);
        m_txt_warnning = (TextView) findViewById(R.id.stress_txt_warnning);
        m_txt_counter = (TextView) findViewById(R.id.stress_txt_counter);
        //m_img_counter = (AdapterViewFlipper) findViewById(R.id.stress_img_counter);
        m_img_counter = (ImageView) findViewById(R.id.stress_img_counter);
        m_layout_counter = (FrameLayout) findViewById(R.id.stress_layout_counter);
        m_btn_start.setOnClickListener(this);

        //m_list = new ArrayList<>();
        //setAdapterViewFlipper();

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
    }
    /*
    private void setAdapterViewFlipper() {
        m_list.clear();
        m_list.add(R.drawable.coachplus_stress_count_00);//0   0
        m_list.add(R.drawable.coachplus_stress_count_00);//1   5
        m_list.add(R.drawable.coachplus_stress_count_05);//2   10
        m_list.add(R.drawable.coachplus_stress_count_05);//3   15
        m_list.add(R.drawable.coachplus_stress_count_10);//4   20
        m_list.add(R.drawable.coachplus_stress_count_10);//5   25
        m_list.add(R.drawable.coachplus_stress_count_15);//6   30
        m_list.add(R.drawable.coachplus_stress_count_15);//7   35
        m_list.add(R.drawable.coachplus_stress_count_20);//8   40
        m_list.add(R.drawable.coachplus_stress_count_20);//9   45
        m_list.add(R.drawable.coachplus_stress_count_25);//10  50
        m_list.add(R.drawable.coachplus_stress_count_25);//11  55
        m_list.add(R.drawable.coachplus_stress_count_30);//12  60
        m_list.add(R.drawable.coachplus_stress_count_30);//13  65
        m_list.add(R.drawable.coachplus_stress_count_35);//14  70
        m_list.add(R.drawable.coachplus_stress_count_35);//15  75
        m_list.add(R.drawable.coachplus_stress_count_40);//16  80
        m_list.add(R.drawable.coachplus_stress_count_40);//17  85
        m_list.add(R.drawable.coachplus_stress_count_45);//18  90
        m_list.add(R.drawable.coachplus_stress_count_45);//19  95
        m_list.add(R.drawable.coachplus_stress_count_50);//20  100
        m_list.add(R.drawable.coachplus_stress_count_50);//21  105
        m_list.add(R.drawable.coachplus_stress_count_55);//22  110
        m_list.add(R.drawable.coachplus_stress_count_55);//23  115
        m_list.add(R.drawable.coachplus_stress_count_60);//24  120
        //m_list.add(R.drawable.coachplus_stress_count_60);//25
        m_adapter = new ImageAdapter(this);
        m_adapter.bindData(m_list);
        //m_img_counter.setAdapter(m_adapter);
    }*/

    private void dismissCallback() {
        if (success) {
            showMessage(getString(R.string.success_connect));
        } else {
            showMessage(getString(R.string.fail_connect_device_confirm));
        }
        isWait = false;
    }

    private void process() {
        if (counter < limit) {
            setConstructCounter();
            counter++;
            m_txt_counter.setText(String.valueOf(counter));

            return;
        }

        if (popup_exit) {
            showMessage(getString(R.string.stress_measure_exit));
        }
        m_btn_start.setText(getString(R.string.measure_start));
        Log.i(tag, "select_mode"+ select_mode);
        if (select_mode == ApplicationStatus.HomeScreenFitness) {
            //SendReceive.appendBluetoothMessage(this, BluetoothCommand.Stress, 0L, RequestAction.End, false);
            SendReceive.sendStress(this, false);
//            SendReceive.appendBluetoothMessage(this, BluetoothCommand.Stress, now, RequestAction.Inform, true);
            //Log.i(tag, "ApplicationStatus.HomeScreenFitness");
        } else {
            //Log.i(tag, "SendReceive.sendNormalStress");
            SendReceive.sendNormalStress(this, false);
            //SendReceive.appendBluetoothMessage(this, BluetoothCommand.Acc, 0L, RequestAction.End, false);
        }

        Preference.putPeripheralState(ActivityStress.this, StatePeripheral.IDLE.getState());

        m_txt_warnning.setVisibility(View.GONE);
        m_img_graph.setImageResource(R.drawable.coachplus_s_img_02);
//        m_txt_state_desc.setText("stress_measure_empty");

        isStart = false;

        cancelTimer();
    }

    private void startTimer() {
        //if (!m_img_counter.isFlipping()) {
            Log.i(tag,"is Call?");
            //setAdapterViewFlipper();
            m_layout_counter.setVisibility(View.VISIBLE);
            //m_img_counter.startFlipping();
            m_timer = new Timer("Image_Timer");
            m_timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            process();
                        }
                    });
                }
            },1000,1000);
        //}
    }

    private void cancelTimer() {
        //if (m_img_counter.isFlipping()) {
            m_img_counter.setImageResource(R.drawable.coachplus_stress_count_00); //1 0
            m_layout_counter.setVisibility(View.INVISIBLE);
            //m_img_counter.stopFlipping();
            //setAdapterViewFlipper();
            m_timer.cancel();
        //}
    }

    /*
    @Override
    protected void onPause() {
        super.onPause();

        // 16.12.13 백그라운드에서 측정을 계속 하도록 요청있었음
        cancelTimer();

        if (isStart) {
            Preference.putPeripheralState(this, StatePeripheral.IDLE.getState());
            SendReceive.sendStress(this, false);
        }
    }*/
    // 규창 16.12.26 백그라운드에서 메인화면으로 돌아왔을 시에 스트레스 측정이 완료되고 결과 값이 있다면 결과 표시하도록 변경
    @Override
    protected void onResume(){
        super.onResume();
        //Log.i(tag,"OnResume()?");
        if (MWControlCenter.getInstance(getApplicationContext()).mDatabase.getStress() != 0 && !isCancel) {
            setScreen(MWControlCenter.getInstance(getApplicationContext()).mDatabase.getStress());
            MWControlCenter.getInstance(getApplicationContext()).mDatabase.setStress((short)0);
        }
        //Log.i(tag,"OnResume()?"+ counter + " " + limit);
        // 규창 17.02.03 백그라운드,탭 할시 타이머 카운트에 맞춰 이미지 플립아이템 재구성
        //setConstructCounter();

    }

    private void setConstructCounter(){
        /*if (counter > 0 && counter < limit) {
            //m_img_counter.stopFlipping();
            m_list.clear();
            m_adapter = null;
            //m_img_counter.clearAnimation();*/
            Log.i(tag,"OnResume()?"+ counter + " " + limit);
            if (counter >= 0 && counter < 10 ) {
                Log.i(tag,"OnResume 0~10초 구간"+ counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_00); //1 0
            } else if (counter >= 10 && counter < 20 ) {
                Log.i(tag,"OnResume 10~20초 구간"+ counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_05); //2 10
            } else if (counter >= 20 && counter < 30 ) {
                Log.i(tag,"OnResume 20~30초 구간"+ counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_10); //3 20
            } else if (counter >= 30 && counter < 40 ) {
                Log.i(tag,"OnResume 30~40초 구간"+ counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_15); //4 30
            } else if (counter >= 40 && counter < 50 ) {
                Log.i(tag, "OnResume 40~50초 구간" + counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_20); //5 40
            } else if (counter >= 50 && counter < 60 ) {
                Log.i(tag, "OnResume 50~60초 구간" + counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_25); //6 50
            } else if (counter >= 60 && counter < 70 ) {
                Log.i(tag, "OnResume 60~70초 구간"+ counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_30); //7 60
            } else if (counter >= 70 && counter < 80 ) {
                Log.i(tag," OnResume 70~80초 구간"+ counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_35); //8 70
            } else if (counter >= 80 && counter < 90 ) {
                Log.i(tag, "OnResume 80~90초 구간"+ counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_40); //9 80
            } else if (counter >= 90 && counter < 100 ) {
                Log.i(tag, "OnResume 90~100초 구간"+ counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_45); //10 90
            } else if (counter >= 100 && counter < 110 ) {
                Log.i(tag, "OnResume 100~110초 구간" + counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_50); //11 100
            } else if (counter >= 110 && counter < 119 ) {
                Log.i(tag, "OnResume 110~118초 구간" + counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_55); //12 110
            } else if (counter >= 119 && counter <= limit) {
                Log.i(tag, "OnResume 119~120초 구간"+ counter);
                m_img_counter.setImageResource(R.drawable.coachplus_stress_count_60); //13 118
            }
        //}
    }

    @Override
    public void run(Intent intent) {
        String action = intent.getAction();

        if (action.equals(MWBroadcastReceiver.Action.Bm.ACTION_MW_CONNECTION_STATE)) {
            int state = intent.getIntExtra(MWBroadcastReceiver.Action.EXTRA_NAME_INT_1, 0);

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
            isClickButton = false;

            if (state == ConnectStatus.STATE_DISCONNECTED.ordinal()) {
                //규창 17.03.15 메인화면 강제 리프레시에 있던 연결 브로드캐스트 사용
                //SendReceive.sendRequestConnection(this);
                SendReceive.sendConnect(this, CoachBaseActivity.DB_User.getDeviceAddress());
                showMessageNonBtn(getString(R.string.connecting_device));
                success = false;
                isWait = true;
                m_popup_handler.postDelayed(m_popup_runnable, 4000);
                return;
            }

            SendReceive.isBusySender(this);
        } else if (action.equals(MWBroadcastReceiver.Action.Bm.ACTION_MW_IS_BUSY_SENDER)) {
            boolean isBusy = intent.getBooleanExtra(MWBroadcastReceiver.Action.EXTRA_NAME_BOOL_1, false);
            if (isBusy) {
                showMessage(getString(R.string.device_initial_waiting));
                return;
            }

            if (select_mode == ApplicationStatus.HomeScreenFitness) {
                SendReceive.sendPeripheralState(this);
            } else {
                if (!isStart) {
                    isCancel = false;
                    popup_exit = true;
                    m_btn_start.setText(getString(R.string.measure_cancel));

                    m_txt_warnning.setVisibility(View.VISIBLE);
                    m_img_graph.setImageResource(R.drawable.coachplus_s_img_01);
                    Log.i(tag, "select_mode="+ select_mode);
                    if (select_mode == ApplicationStatus.HomeScreenFitness) {
                        //now = System.currentTimeMillis();
                        Log.e(tag, "측정 종료");
                        //SendReceive.appendBluetoothMessage(this, BluetoothCommand.Stress, now, RequestAction.Start, false);
                        SendReceive.sendStress(this, true);
                    } else {
                        //SendReceive.appendBluetoothMessage(this, BluetoothCommand.Acc, 0L, RequestAction.Start, false);
                        SendReceive.sendNormalStress(this, true);
                    }

                    Preference.putPeripheralState(this, StatePeripheral.Stress.getState());

                    isStart = true;

                    counter = 0;
                    startTimer();

                    m_txt_counter.setText(String.valueOf(counter));
                    m_txt_state_desc.setText(getString(R.string.loading_stress_index));
                } else {
                    isCancel = true;
                    popup_exit = false;
                    m_btn_start.setText(getString(R.string.measure_start));
                    Log.i(tag, "select_mode"+ select_mode);
                    if (select_mode == ApplicationStatus.HomeScreenFitness) {
                        //SendReceive.appendBluetoothMessage(this, BluetoothCommand.Stress, 0L, RequestAction.End, false);
                        SendReceive.sendStress(this, false);
                    } else {
                        //SendReceive.appendBluetoothMessage(this, BluetoothCommand.Acc, 0L, RequestAction.End, false);
                        SendReceive.sendNormalStress(this, false);
                    }

                    Preference.putPeripheralState(ActivityStress.this, StatePeripheral.IDLE.getState());

                    m_txt_warnning.setVisibility(View.GONE);
                    m_img_graph.setImageResource(R.drawable.coachplus_s_img_02);
                    m_txt_state_desc.setText(getString(R.string.empty_stress_index));

                    isStart = false;

                    cancelTimer();
                }
            }
        } else if (action.equals(MWBroadcastReceiver.Action.Bm.ACTION_MW_PERIPHERAL_STATE)) {
            short state = intent.getShortExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_1, (short) 0);
            if (select_mode == ApplicationStatus.HomeScreenFitness) {
                if (state != StatePeripheral.IDLE.getState() && state != StatePeripheral.Stress.getState()) {
                    showMessage(getString(R.string.state_not_idle));
                    return;
                }
            }

            if (!isStart) {
                isCancel = false;
                popup_exit = true;
                m_btn_start.setText(getString(R.string.measure_cancel));

                m_txt_warnning.setVisibility(View.VISIBLE);
                m_img_graph.setImageResource(R.drawable.coachplus_s_img_01);
                Log.i(tag, "select_mode="+ select_mode);
                if (select_mode == ApplicationStatus.HomeScreenFitness) {
                    //now = System.currentTimeMillis();
                    Log.e(tag, "측정 종료");
                    //SendReceive.appendBluetoothMessage(this, BluetoothCommand.Stress, now, RequestAction.Start, false);
                    SendReceive.sendStress(this, true);
                } else {
                    //SendReceive.appendBluetoothMessage(this, BluetoothCommand.Acc, 0L, RequestAction.Start, false);
                    SendReceive.sendNormalStress(this, true);
                }

                Preference.putPeripheralState(this, StatePeripheral.Stress.getState());

                isStart = true;

                counter = 0;
                startTimer();

                m_txt_counter.setText(String.valueOf(counter));
                m_txt_state_desc.setText(getString(R.string.loading_stress_index));
            } else {
                isCancel = true;
                popup_exit = false;
                m_btn_start.setText(getString(R.string.measure_start));
                Log.i(tag, "select_mode"+ select_mode);
                if (select_mode == ApplicationStatus.HomeScreenFitness) {
                    //SendReceive.appendBluetoothMessage(this, BluetoothCommand.Stress, 0L, RequestAction.End, false);
                    SendReceive.sendStress(this, false);
                } else {
                    //SendReceive.appendBluetoothMessage(this, BluetoothCommand.Acc, 0L, RequestAction.End, false);
                    SendReceive.sendNormalStress(this, false);
                }

                Preference.putPeripheralState(ActivityStress.this, StatePeripheral.IDLE.getState());

                m_txt_warnning.setVisibility(View.GONE);
                m_img_graph.setImageResource(R.drawable.coachplus_s_img_02);
                m_txt_state_desc.setText(getString(R.string.empty_stress_index));

                isStart = false;

                cancelTimer();
            }
        } else if (action.equals(MWBroadcastReceiver.Action.Am.ACTION_MW_STRESS_DATA)) {
            short stress = intent.getShortExtra(MWBroadcastReceiver.Action.EXTRA_NAME_SHORT_1, (short) 0);
            if (stress != 0 && !isCancel) {
                setScreen(stress);
            }
        }
    }

    private void setScreen(short stress) {
        m_txt_state_result.setVisibility(View.VISIBLE);
        m_txt_state_desc.setVisibility(View.GONE);

        StressIdentifier id;
        if (stress == StressIdentifier.VeryGood.getID()) {
            id = StressIdentifier.VeryGood;
        } else if (stress == StressIdentifier.Good.getID()) {
            id = StressIdentifier.Good;
        } else if (stress == StressIdentifier.Normal.getID()) {
            id = StressIdentifier.Normal;
        } else if (stress == StressIdentifier.Bad.getID()) {
            id = StressIdentifier.Bad;
        } else {
            id = StressIdentifier.Normal;
        }
        m_txt_state_result.setText(id.toString());
        m_txt_state_result.setTextColor(id.getColor());
        Preference.putStressState(this, id.getID());
    }

    @Override
    public void onClick(View v) {
        isClickButton = true;
        SendReceive.getConnectionState(this);
    }
}
