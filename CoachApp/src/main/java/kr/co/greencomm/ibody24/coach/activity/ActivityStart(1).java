package kr.co.greencomm.ibody24.coach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.register.ActivityLogin;
import kr.co.greencomm.ibody24.coach.activity.register.ActivityRegisterMember;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;


public class ActivityStart
        extends CoachBaseActivity
        implements View.OnClickListener {

    private Button m_btn_JoinUs;
    private Button m_btn_Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentScale1440(R.layout.activity_start, R.id.screen_layout_main);
        setContentView(R.layout.coachplus_start);

        setApplicationStatus(ApplicationStatus.StartScreen);

        // 회원등록 버튼의 이벤트 처리기 등록
        m_btn_JoinUs = (Button) findViewById(R.id.start_btn_join);
        m_btn_JoinUs.setOnClickListener(this);

        // 로그인 버튼의 이벤트 처리기 등록
        m_btn_Login = (Button) findViewById(R.id.start_btn_login);
        m_btn_Login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 사용자 등록 버튼
            case R.id.start_btn_join:
                changeActivity(ActivityRegisterMember.class);
                break;
            // 로그인 버튼
            case R.id.start_btn_login:
                changeActivity(ActivityLogin.class);
                break;
        }
    }

    @Override
    public void run(Intent intent) {

    }
}
