package kr.co.greencomm.ibody24.coach.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabSetting;


/**
 * Created by young on 2015-08-19.
 */
public class ActivityConnectionFailed
        extends CoachBaseActivity {
    private Button m_btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentScale1440(R.layout.coachplus_connection_failed, R.id.screen_layout_main);
        setContentView(R.layout.coachplus_connection_failed);

        setApplicationStatus(ApplicationStatus.ConnectionFailedScreen);

        m_btn_next = (Button) findViewById(R.id.connectionfailed_btn_next);
        m_btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_popup_mode) {
                    ActivityTabSetting.popActivity();
                    return;
                }
                finish();
            }
        });
    }

    @Override
    public void run(Intent intent) {

    }
}
