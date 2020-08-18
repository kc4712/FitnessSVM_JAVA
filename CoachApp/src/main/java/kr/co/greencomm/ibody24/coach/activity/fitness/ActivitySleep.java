package kr.co.greencomm.ibody24.coach.activity.fitness;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.provider.CoachResolver;
import kr.co.greencomm.middleware.bluetooth.BluetoothCommand;
import kr.co.greencomm.middleware.bluetooth.ConnectStatus;
import kr.co.greencomm.middleware.bluetooth.RequestAction;
import kr.co.greencomm.middleware.bluetooth.StatePeripheral;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver.Action;
import kr.co.greencomm.ibody24.coach.utils.SleepIdentifier;
import kr.co.greencomm.middleware.utils.container.IndexTimeData;

/**
 * Created by jeyang on 16. 9. 21..
 */
public class ActivitySleep extends CoachBaseActivity implements View.OnClickListener {
    private static final String tag = ActivitySleep.class.getSimpleName();

    private Button m_btn_start;
    private TextView m_txt_state, m_txt_time, m_txt_rolled, m_txt_awaken, m_txt_stabilityHR;

    private boolean isStart;
    private long now;

    private Runnable m_popup_runnable;
    private Handler m_popup_handler;

    private boolean success, isWait;
    private DialogInterface.OnDismissListener dismiss_listener;
    private boolean isClickButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coachplus_sleep);
        setApplicationStatus(ApplicationStatus.SleepScreen);

        m_btn_start = (Button) findViewById(R.id.sleep_btn_start);

        m_txt_state = (TextView) findViewById(R.id.sleep_txt_state);
        m_txt_time = (TextView) findViewById(R.id.sleep_txt_time);
        m_txt_rolled = (TextView) findViewById(R.id.sleep_txt_rolled);
        m_txt_awaken = (TextView) findViewById(R.id.sleep_txt_awaken);
        m_txt_stabilityHR = (TextView) findViewById(R.id.sleep_txt_stability_hr);

        if (Preference.getPeripheralState(this) == StatePeripheral.Sleep.getState()) {
            loadData();
        }


        SleepIdentifier id = SleepIdentifier.getSleepIdentifier(Preference.getSleepState(this));
        if (id != null) {
            m_txt_state.setText(id.toString());
            m_txt_time.setText(String.valueOf(Preference.getSleepTime(this)));
            m_txt_rolled.setText(String.valueOf(Preference.getSleepRolled(this)));
            m_txt_awaken.setText(String.valueOf(Preference.getSleepAwaken(this)));
            m_txt_stabilityHR.setText(String.valueOf(Preference.getSleepStabilityHR(this)));
        }

        m_btn_start.setOnClickListener(this);

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

    private void dismissCallback() {
        if (success) {
            showMessage(getString(R.string.success_connect));
        } else {
            showMessage(getString(R.string.fail_connect_device_confirm));
        }
        isWait = false;
    }

    @Override
    public void run(Intent intent) {
        String action = intent.getAction();

        if (action.equals(Action.Bm.ACTION_MW_CONNECTION_STATE)) {
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

            SendReceive.sendPeripheralState(this);
        } else if (action.equals(Action.Bm.ACTION_MW_PERIPHERAL_STATE)) {
            short state = intent.getShortExtra(Action.EXTRA_NAME_SHORT_1, (short) 0);
            if (state == StatePeripheral.Sleep.getState() && Preference.getPeripheralState(this) != StatePeripheral.Sleep.getState()) {
                loadData();
            }

            if (!isClickButton)
                return;
            isClickButton = false;

            if (state != StatePeripheral.IDLE.getState() && state != StatePeripheral.Sleep.getState()) {
                showMessage(getString(R.string.state_not_idle));
                return;
            }

            CoachResolver res = new CoachResolver();

            if (!isStart) {
                m_btn_start.setText(getString(R.string.measure_end));

                Preference.putPeripheralState(this, StatePeripheral.Sleep.getState());

                now = System.currentTimeMillis();
                res.addIndexTimeProvider(BluetoothCommand.Sleep.getCommand(), now);

                SendReceive.appendBluetoothMessage(this, BluetoothCommand.Sleep, now, RequestAction.Start, false);
                isStart = true;
            } else {
                m_btn_start.setText(getString(R.string.measure_start));

                Preference.putPeripheralState(this, StatePeripheral.IDLE.getState());

                SendReceive.appendBluetoothMessage(this, BluetoothCommand.Sleep, 0L, RequestAction.End, false);

                long end_time = System.currentTimeMillis();

                res.updateIndexTimeProvider(now, end_time);

                SendReceive.appendBluetoothMessage(this, BluetoothCommand.Sleep, now, RequestAction.Inform, true);
                isStart = false;
            }
        } else if (action.equals(Action.Am.ACTION_MW_SLEEP_DATA)) {
            short[] sleep = intent.getShortArrayExtra(Action.EXTRA_NAME_SHORT_ARRAY);
            if (sleep == null) {
                return;
            }

            if (!isStart) {
                CoachResolver res = new CoachResolver();

                IndexTimeData save_time = res.getIndexTimeProvider(BluetoothCommand.Sleep.getCommand());
                now = save_time.getStart_time();
                res.deleteIndexTimeProvider(now);

                if (sleep[2] == 0) {
                    showMessage(getString(R.string.error_measure));
                    return;
                }

                long interval = ((save_time.getEnd_time()) - (save_time.getStart_time())) / 60000;
                Log.d(tag, "sleep 2 end:"+save_time.getEnd_time()+" start:"+save_time.getStart_time());

                SleepIdentifier idfier = getSleepCalc(interval);
                m_txt_state.setText(idfier.toString());
                m_txt_state.setTextColor(idfier.getColor());
                m_txt_time.setText(String.valueOf(interval));
                m_txt_rolled.setText(String.valueOf(sleep[0]));
                m_txt_awaken.setText(String.valueOf(sleep[1]));
                m_txt_stabilityHR.setText(String.valueOf(sleep[2]));

                Preference.putSleepState(this, idfier.getID()); // 어떻게 계산할지 정해지지 않음.
                Preference.putSleepTime(this, (int) interval);
                Preference.putSleepRolled(this, sleep[0]);
                Preference.putSleepAwaken(this, sleep[1]);
                Preference.putSleepStabilityHR(this, sleep[2]);
            }
        }
    }

    private void loadData() {
        CoachResolver res = new CoachResolver();
        IndexTimeData save_time = res.getIndexTimeProvider(BluetoothCommand.Sleep.getCommand());

        if (save_time == null)
            return;
        now = save_time.getStart_time();

        m_btn_start.setText(getString(R.string.measure_end));

        Preference.putPeripheralState(this, StatePeripheral.Sleep.getState());

        isStart = true;
    }

    @Override
    public void onClick(View v) {
        isClickButton = true;
        SendReceive.getConnectionState(this);
    }
}
