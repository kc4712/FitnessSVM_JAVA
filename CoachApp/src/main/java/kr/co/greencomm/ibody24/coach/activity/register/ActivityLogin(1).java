package kr.co.greencomm.ibody24.coach.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.ActivityInformation;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.utils.Convert;


public class ActivityLogin
        extends CoachBaseActivity
        implements View.OnClickListener {
    private EditText m_edit_email;
    private EditText m_edit_password;
    private Button m_btn_Login;
    private Button m_btn_find_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentScale1440(R.layout.coachplus_login, R.id.screen_layout_main);
        setContentView(R.layout.coachplus_login);

        setApplicationStatus(ApplicationStatus.LoginScreen);

        // 이메일 입력
        m_edit_email = (EditText) findViewById(R.id.login_edit_email);
        // 암호 입력
        m_edit_password = (EditText) findViewById(R.id.login_edit_password);

        // 로그인 버튼
        m_btn_Login = (Button) findViewById(R.id.login_btn_login);
        m_btn_Login.setOnClickListener(this);

        // 아이디 찾기 버튼
        m_btn_find_id = (Button) findViewById(R.id.login_btn_find_id);
        m_btn_find_id.setOnClickListener(this);


        String email = Preference.getEmail(this);
        if (email != null) {
            m_edit_email.setText(email);
        }

        String password = Preference.getPassword(this);
        if (password != null) {
            m_edit_password.setText(password);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_login:
                String m_email = m_edit_email.getText().toString();
                String m_pass = m_edit_password.getText().toString();
                if (Convert.isEmpty(m_email) == true) {
                    displayMsg(R.string.enter_email);
                    m_edit_email.requestFocus();
                    return;
                }
                if (Convert.isEmpty(m_pass) == true) {
                    displayMsg(R.string.enter_pass);
                    m_edit_password.requestFocus();
                    return;
                }
                Log("로그인 시작");
                m_popup_mode = false;
                DB_User.setEmail(m_email);
                DB_User.setPassword(m_pass);
                DB_User.runQuery(QueryCode.LoginUser, this);
                break;
            case R.id.login_btn_find_id:
                Intent intent = new Intent(this, ActivityInformation.class);
                intent.putExtra("title", getString(R.string.find_account_title));
                intent.putExtra("address", getString(R.string.find_account_url));
                startActivity(intent);
                break;

        }
    }

    @Override
    public void run(Intent intent) {

    }
}
