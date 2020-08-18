package kr.co.greencomm.ibody24.coach.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;


public class ActivityRegisterComplete
        extends CoachBaseActivity
        implements View.OnClickListener {
    private Button m_btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentScale1440(R.layout.coachplus_register_complete, R.id.screen_layout_main);
        setContentView(R.layout.coachplus_register_complete);

        setApplicationStatus(ApplicationStatus.RegisterCompleteScreen);

        // 버튼 필요함
        m_btn_next = (Button) findViewById(R.id.complete_btn_next);
        m_btn_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 시작하기 버튼
            case R.id.complete_btn_next:
                changeActivity(ActivityProductSelect.class);
                break;
        }
    }

    @Override
    public void run(Intent intent) {

    }
}
