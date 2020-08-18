package kr.co.greencomm.ibody24.coach.activity.fitness;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.adapter.ImageAdapter;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.utils.ActivityIdentifier;
import kr.co.greencomm.ibody24.coach.data.ActivityInfo;
import kr.co.greencomm.ibody24.coach.data.DataTransfer;
import kr.co.greencomm.ibody24.coach.provider.CoachResolver;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.middleware.bluetooth.BluetoothCommand;
import kr.co.greencomm.middleware.bluetooth.ConnectStatus;
import kr.co.greencomm.middleware.bluetooth.RequestAction;
import kr.co.greencomm.middleware.bluetooth.StatePeripheral;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver.Action;
import kr.co.greencomm.middleware.utils.CourseLabel;
import kr.co.greencomm.middleware.utils.container.ActivityData;
import kr.co.greencomm.middleware.utils.container.IndexTimeData;

/**
 * Created by jeyang on 16. 9. 21..
 */
public class ActivityActMeasure extends CoachBaseActivity implements View.OnClickListener {
    private static final String tag = ActivityActMeasure.class.getSimpleName();

    private ActivityInfo datas = new ActivityInfo();

    private ImageView m_img_main;
    private Button m_btn_start_end;
    private Button m_btn_list;

    private Runnable m_popup_runnable;
    private Handler m_popup_handler;

    private boolean success, isWait;
    private DialogInterface.OnDismissListener dismiss_listener;
    private boolean isClickButton, isFirst;

    private boolean isStart;
    private long now;

    private AdapterViewFlipper m_img_loading;
    private ImageAdapter m_adapter;
    private ArrayList<Integer> m_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SendReceive.sendPeripheralState(this);

        setContentView(R.layout.coachplus_activity_measure);
        setApplicationStatus(ApplicationStatus.ActMeasureScreen);

        m_list = new ArrayList<>();

        m_img_main = (ImageView) findViewById(R.id.activity_measure_img_main);
        m_img_loading = (AdapterViewFlipper) findViewById(R.id.activity_measure_img_loading);
        m_btn_start_end = (Button) findViewById(R.id.activity_measure_btn_start_end);
        m_btn_list = (Button) findViewById(R.id.actvivity_measure_btn_list);
        m_btn_start_end.setOnClickListener(this);
        m_btn_list.setOnClickListener(this);

        m_adapter = new ImageAdapter(this);
        m_img_loading.setAdapter(m_adapter);

        m_list.add(R.drawable.coachplus_activity_icon_n_01);
        m_list.add(R.drawable.coachplus_activity_icon_n_02);
        m_list.add(R.drawable.coachplus_activity_icon_n_03);
        m_list.add(R.drawable.coachplus_activity_icon_n_04);
        m_adapter.bindData(m_list);

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

        if (Preference.getPeripheralState(this) == StatePeripheral.Activity.getState()) {
            setView(StatePeripheral.Activity.getState());
        }
    }

    private void dismissCallback() {
        if (success) {
            showMessage(getString(R.string.success_connect));
        } else {
            showMessage(getString(R.string.fail_connect_device_confirm));
        }
        isWait = false;
    }

    private void setView(int state) {
        if (state == StatePeripheral.Activity.getState()) {
            m_img_main.setImageResource(R.drawable.coachplus_activity_02);
            m_img_loading.startFlipping();
            m_img_loading.setVisibility(View.VISIBLE);
            m_btn_start_end.setText(getString(R.string.measure_end));
            m_btn_list.setVisibility(View.INVISIBLE);
            isStart = true;
        } else {
            m_img_main.setImageResource(R.drawable.coachplus_activity_01);
            m_img_loading.stopFlipping();
            m_img_loading.setVisibility(View.INVISIBLE);
            m_btn_start_end.setText(getString(R.string.measure_start));
            m_btn_list.setVisibility(View.VISIBLE);
            isStart = false;
        }
//        m_btn_start_end.setEnabled(true);
    }

    private void loadData(int state) {
        if (state == StatePeripheral.Activity.getState()) {
            CoachResolver res = new CoachResolver();

            IndexTimeData idx = res.getIndexTimeProvider(BluetoothCommand.Activity.getCommand());
            if (idx == null) {
                return;
            }
            long save_time = idx.getStart_time();

            Preference.putPeripheralState(this, StatePeripheral.Activity.getState());

            now = save_time;
        }
    }

    @Override
    public void run(Intent intent) {
        String action = intent.getAction();

        if (action.equals(Action.Bm.ACTION_MW_PERIPHERAL_STATE)) {
            short state = intent.getShortExtra(Action.EXTRA_NAME_SHORT_1, (short) 0);
            if (!isFirst) {
                setView(state);
                if (state == StatePeripheral.Activity.getState()) {
                    loadData(state);
                }
                isFirst = true;
            }

            if (!isClickButton)
                return;
            isClickButton = false;

            if (state != StatePeripheral.IDLE.getState() && state != StatePeripheral.Activity.getState()) {
                showMessage(getString(R.string.state_not_idle));
                return;
            }

            CoachResolver res = new CoachResolver();

            if (!isStart) {
                //m_btn_start_end.setText(getString(R.string.measure_end));

                Preference.putPeripheralState(this, StatePeripheral.Activity.getState());

                now = System.currentTimeMillis();
                Log.d(tag,"measure start time -> "+now);
                res.addIndexTimeProvider(BluetoothCommand.Activity.getCommand(), now);

                SendReceive.appendBluetoothMessage(this, BluetoothCommand.Activity, now, RequestAction.Start, false);
                isStart = true;
                setView(StatePeripheral.Activity.getState());
            } else {
                //m_btn_start_end.setText(getString(R.string.measure_start));

                Preference.putPeripheralState(this, StatePeripheral.IDLE.getState());

                SendReceive.appendBluetoothMessage(this, BluetoothCommand.Activity, 0L, RequestAction.End, false);

                long end_time = System.currentTimeMillis();
                Log.d(tag,"measure end time -> "+end_time);

                res.updateIndexTimeProvider(now, end_time);

                SendReceive.appendBluetoothMessage(this, BluetoothCommand.Activity, now, RequestAction.Inform, true);
                isStart = false;
                setView(StatePeripheral.IDLE.getState());
            }
        } else if (action.equals(Action.Bm.ACTION_MW_CONNECTION_STATE)) {
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
                isClickButton = false;
                m_popup_handler.postDelayed(m_popup_runnable, 4000);
                return;
            }

            SendReceive.isBusySender(this);
        } else if (action.equals(Action.Bm.ACTION_MW_IS_BUSY_SENDER)) {
            boolean isBusy = intent.getBooleanExtra(Action.EXTRA_NAME_BOOL_1, false);
            if (isBusy) {
                showMessage(getString(R.string.device_initial_waiting));
                return;
            }

            SendReceive.sendPeripheralState(this);
        } else if (action.equals(Action.Am.ACTION_MW_GENERATE_ACTIVITY_DATA)) {
            long start_time = intent.getLongExtra(Action.EXTRA_NAME_LONG_1, 0L);
            if (start_time == 0) {
                Log.d(tag, "start time = 0...");
                showMessage(getString(R.string.error_measure));
                return;
            }
            CoachResolver res = new CoachResolver();

            ActivityData data = res.getActivityDataProvider(start_time);
            if (data == null) {
                Log.d(tag, "act data null... start time " + start_time);
                showMessage(getString(R.string.error_measure));
                return;
            }

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
            Preference.putPeripheralState(this, StatePeripheral.IDLE.getState());

            DataTransfer transfer = new DataTransfer();
            transfer.start();

            //setView(StatePeripheral.IDLE.getState());

            intent = new Intent(this, ActivityActResult.class);
            intent.putExtra("start_time", start_time);
            ActivityTabHome.pushActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_measure_btn_start_end:
                isClickButton = true;
                SendReceive.getConnectionState(this);
                break;
            case R.id.actvivity_measure_btn_list:
                ActivityInfo dat = new ActivityInfo();
                dat.setLabel(CourseLabel.Activity.getLabel());
                dat.runQuery(QueryCode.ListExercise, null);

                ActivityTabHome.pushActivity(this, ActivityActList.class);
                break;
        }
    }
}
