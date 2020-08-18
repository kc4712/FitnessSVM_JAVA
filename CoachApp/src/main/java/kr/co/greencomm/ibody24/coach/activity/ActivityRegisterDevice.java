package kr.co.greencomm.ibody24.coach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.register.ActivityProductSelect;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabSetting;

/**
 * Created by jeyang on 16. 9. 21..
 */
public class ActivityRegisterDevice extends CoachBaseActivity implements View.OnClickListener {
    private static final String tag = ActivityRegisterDevice.class.getSimpleName();

    private Button m_btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coachplus_register_device);

        setApplicationStatus(ApplicationStatus.RegisterDeviceScreen);

        m_btn_register = (Button) findViewById(R.id.register_device_btn_register);
        m_btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        m_popup_mode = true;
        ActivityTabSetting.pushActivity(this, ActivityProductSelect.class);
    }

    @Override
    public void run(Intent intent) {

    }
}
