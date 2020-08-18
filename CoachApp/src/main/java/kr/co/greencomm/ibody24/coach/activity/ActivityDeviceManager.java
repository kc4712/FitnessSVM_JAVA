package kr.co.greencomm.ibody24.coach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.middleware.bluetooth.BluetoothCommand;
import kr.co.greencomm.middleware.bluetooth.ConnectStatus;
import kr.co.greencomm.middleware.bluetooth.RequestAction;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver.Action;
import kr.co.greencomm.middleware.utils.NoticeIndex;
import kr.co.greencomm.middleware.utils.ProductCode;
import kr.co.greencomm.middleware.utils.container.Battery;

/**
 * Created by jeyang on 16. 9. 21..
 */
public class ActivityDeviceManager extends CoachBaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String tag = ActivityDeviceManager.class.getSimpleName();

    private ImageView m_img_battery;
    private ImageView m_img_connect;
    private TextView m_txt_conn_state;
    private TextView m_txt_voltage;
    private TextView m_txt_dev_name; // xml에 없음
    private LinearLayout m_layout_connect, m_layout_phone_sw, m_layout_sms_sw;
    private ImageView m_img_line_1, m_img_line_2;
    private Switch m_sw_phone, m_sw_sms;

    private RotateAnimation anim;

    private boolean m_connect;

    private boolean m_notice_phone, m_notice_sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coachplus_device_manager);

        m_sw_phone = (Switch) findViewById(R.id.device_manager_switch_phone);
        m_sw_phone.setOnCheckedChangeListener(this);
        m_sw_sms = (Switch) findViewById(R.id.device_manager_switch_sms);
        m_sw_sms.setOnCheckedChangeListener(this);
        m_img_battery = (ImageView) findViewById(R.id.device_manager_img_battery);
        m_img_connect = (ImageView) findViewById(R.id.device_manager_img_connect);
        m_txt_conn_state = (TextView) findViewById(R.id.device_manager_txt_conn_state);
        m_txt_voltage = (TextView) findViewById(R.id.device_manager_txt_voltage);
        m_txt_dev_name = (TextView) findViewById(R.id.device_manager_txt_dev_name);
        m_layout_connect = (LinearLayout) findViewById(R.id.device_manager_layout_connect);
        m_layout_connect.setOnClickListener(this);
        m_layout_phone_sw = (LinearLayout) findViewById(R.id.device_manager_layout_phone_sw);
        m_layout_sms_sw = (LinearLayout) findViewById(R.id.device_manager_layout_sms_sw);
        m_img_line_1 = (ImageView) findViewById(R.id.device_manager_layout_line_1);
        m_img_line_2 = (ImageView) findViewById(R.id.device_manager_layout_line_2);

        if (ActivityTabHome.m_app_use_product == ProductCode.Coach) {
            setNoticeView(false);
        } else if (ActivityTabHome.m_app_use_product == ProductCode.Fitness) {
            setNoticeView(true);
        }

        m_notice_phone = Preference.getNoticePhoneONOFF(this);
        m_notice_sms = Preference.getNoticeSmsONOFF(this);
        m_sw_phone.setChecked(m_notice_phone);
        m_sw_sms.setChecked(m_notice_sms);

        setImageAnimation();
        SendReceive.getConnectionState(this);
    }

    @Override
    public void run(Intent intent) {
        String action = intent.getAction();
        if (action.equals(Action.Bm.ACTION_MW_CONNECTION_STATE)) {
            int state = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);
            updateConnect(state);
            if (state == ConnectStatus.STATE_CONNECTED.ordinal()) {
                SendReceive.appendBluetoothMessage(this, BluetoothCommand.Battery, 0L, RequestAction.Start, true);
            }
        } else if (action.equals(Action.Ec.ACTION_MW_BATTERY)) {
            short[] batt = intent.getShortArrayExtra(Action.EXTRA_NAME_SHORT_ARRAY);
            updateBattery(batt);
        }
    }

    private void setNoticeView(boolean set) {
        if (set) {
            m_layout_phone_sw.setVisibility(View.VISIBLE);
            m_layout_sms_sw.setVisibility(View.VISIBLE);
            m_img_line_1.setVisibility(View.VISIBLE);
            m_img_line_2.setVisibility(View.VISIBLE);
        } else {
            m_layout_phone_sw.setVisibility(View.INVISIBLE);
            m_layout_sms_sw.setVisibility(View.INVISIBLE);
            m_img_line_1.setVisibility(View.INVISIBLE);
            m_img_line_2.setVisibility(View.INVISIBLE);
        }
    }

    private void updateBattery(short[] batt) {
        // 미연결
        if (!m_connect) {
            m_img_battery.setImageResource(R.drawable.coachplus_battery_none);
            m_txt_voltage.setText(getString(R.string.device_disconnect));
            return;
        }

        // status= 0:미연결, 1:미충전, 2:충전중, 3:충전완료. voltage=남은 용량(%)
        Battery m_battery = new Battery(batt[0], batt[1]);
        Log.d(tag, "밧데리 [상태: " + m_battery.getStatus() + "], [충전: " + m_battery.getVoltage() + "]");

        // 충전중
        if (m_battery.getStatus() == 2) {
            m_img_battery.setImageResource(R.drawable.coachplus_battery_charging);
            m_txt_voltage.setText(getString(R.string.device_charging));
            return;
        } else if (m_battery.getStatus() == 3) {
            // 충전완료
            m_img_battery.setImageResource(R.drawable.coachplus_battery_complete);
            m_txt_voltage.setText("100%");
            return;
        }

        int value = m_battery.getVoltage();
        m_txt_voltage.setText(String.valueOf(value + "%"));
        //규창 16.12.27 배터리 잔량 표기 수정
        if (value < 25) {
            m_img_battery.setImageResource(R.drawable.coachplus_battery_01);
        } else if (value < 50) {
            m_img_battery.setImageResource(R.drawable.coachplus_battery_02);
        } else if (value < 75) {
            m_img_battery.setImageResource(R.drawable.coachplus_battery_03);
        } else {
            m_img_battery.setImageResource(R.drawable.coachplus_battery_04);
        }

        /*
        if (value < 15) {
            m_img_battery.setImageResource(R.drawable.coachplus_battery_none);
        } else if (value < 37) {
            m_img_battery.setImageResource(R.drawable.coachplus_battery_01);
        } else if (value < 59) {
            m_img_battery.setImageResource(R.drawable.coachplus_battery_02);
        } else if (value < 81) {
            m_img_battery.setImageResource(R.drawable.coachplus_battery_03);
        } else {
            m_img_battery.setImageResource(R.drawable.coachplus_battery_04);
        }
        */
    }

    private void updateConnect(int conn) {
        // 연결상태 확인
        if (conn == ConnectStatus.STATE_CONNECTED.ordinal()) {
            m_img_connect.clearAnimation();
            m_connect = true;
            m_txt_conn_state.setText(getString(R.string.device_connected));
            String devName = Preference.getBluetoothName(this);
            if (devName != null) {
                m_txt_dev_name.setText(devName);
                Log.d(tag, "dev name : " + devName);
                m_img_connect.setImageResource(R.drawable.coachplus_connect_01);
            } else {
                m_txt_dev_name.setText("");
                m_img_connect.setImageResource(R.drawable.coachplus_connect_02);
            }
            m_img_connect.setVisibility(View.GONE);
            m_txt_dev_name.setVisibility(View.VISIBLE);

            m_sw_phone.setEnabled(true);
            m_sw_sms.setEnabled(true);
        } else {
            m_connect = false;
            if (conn == ConnectStatus.STATE_CONNECTING.ordinal()) {
                m_txt_conn_state.setText(getString(R.string.device_connecting));
            } else {
                m_txt_conn_state.setText(getString(R.string.device_connect));
            }
            m_img_connect.setVisibility(View.VISIBLE);
            m_txt_dev_name.setVisibility(View.GONE);

            m_sw_phone.setEnabled(false);
            m_sw_sms.setEnabled(false);
        }
    }

    private void setImageAnimation() {
        anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(800);
    }

    private void saveSwitch() {
        Preference.putNoticePhoneONOFF(this, m_notice_phone);
        Preference.putNoticeSmsONOFF(this, m_notice_sms);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSwitch();
        m_img_connect.clearAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SendReceive.getConnectionState(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_manager_layout_connect:
                if (CoachBaseActivity.DB_User.getDeviceAddress() != null) {
                    if (!m_connect) {
                        m_img_connect.startAnimation(anim);
                        SendReceive.sendConnect(this, CoachBaseActivity.DB_User.getDeviceAddress());
                    }
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
        switch (compoundButton.getId()) {
            case R.id.device_manager_switch_phone:
                m_notice_phone = isCheck;
                if (isCheck) {
                    SendReceive.appendBluetoothMessage(this, BluetoothCommand.NoticeONOFF, 0L, RequestAction.Start, false, NoticeIndex.Phone);
                } else {
                    SendReceive.appendBluetoothMessage(this, BluetoothCommand.NoticeONOFF, 0L, RequestAction.End, false, NoticeIndex.Phone);
                }
                break;
            case R.id.device_manager_switch_sms:
                m_notice_sms = isCheck;
                if (isCheck) {
                    SendReceive.appendBluetoothMessage(this, BluetoothCommand.NoticeONOFF, 0L, RequestAction.Start, false, NoticeIndex.SMS);
                } else {
                    SendReceive.appendBluetoothMessage(this, BluetoothCommand.NoticeONOFF, 0L, RequestAction.End, false, NoticeIndex.SMS);
                }
                break;
        }
    }
}
