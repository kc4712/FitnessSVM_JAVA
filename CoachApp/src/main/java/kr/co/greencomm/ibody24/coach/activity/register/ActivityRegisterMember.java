package kr.co.greencomm.ibody24.coach.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.ActivityInformation;
import kr.co.greencomm.ibody24.coach.activity.ActivityStart;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.middleware.utils.Convert;


public class ActivityRegisterMember
        extends CoachBaseActivity
        implements View.OnClickListener {
    private EditText m_edit_email;
    private EditText m_edit_password;
    private EditText m_edit_confirm;

    private CheckBox m_check_agreement;

    private Button m_btn_read_agreement;
    private Button m_btn_register_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentScale1440(R.layout.coachplus_register_member, R.id.screen_layout_main);
        setContentView(R.layout.coachplus_register_member);

        setApplicationStatus(ApplicationStatus.RegisterMemberScreen);

        // 이메일 입력
        m_edit_email = (EditText) findViewById(R.id.register_edit_email);
        // 암호 입력
        m_edit_password = (EditText) findViewById(R.id.register_edit_password);
        // 암호 확인 입력
        m_edit_confirm = (EditText) findViewById(R.id.register_edit_confirm);

        // 약관 동의
        m_check_agreement = (CheckBox) findViewById(R.id.register_check_agreement);
        // 약관 읽기
        m_btn_read_agreement = (Button) findViewById(R.id.register_btn_read_agreement);
        m_btn_read_agreement.setOnClickListener(this);
        // 가입 완료
        m_btn_register_user = (Button) findViewById(R.id.register_btn_register_user);
        m_btn_register_user.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        changeActivity(ActivityStart.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn_read_agreement:
                Intent intent = new Intent(this, ActivityInformation.class);
                intent.putExtra("title", getString(R.string.terms_of_use));
                intent.putExtra("address", getUrlHtmlResources(getString(R.string.html_name_terms)));

                startActivity(intent);
                break;
            case R.id.register_btn_register_user:
                sub_registerUser();
                break;
        }
    }

    private void sub_registerUser() {
        String email = m_edit_email.getText().toString().trim();
        String pass = m_edit_password.getText().toString();
        String passConfirm = m_edit_confirm.getText().toString();

        if (Convert.getConnectivityStatus(getApplicationContext()) == Convert.CONNECT_TYPE_NOT_CONNECTED) {
            displayMsg(R.string.network_error);
            return;
        }

        // 약관 동의 체크
        if (!m_check_agreement.isChecked()) {
            displayMsg(R.string.agree_term);
            return;
        }

        // 이메일 입력 확인
        if (Convert.isEmpty(email)) {
            displayMsg(R.string.enter_email);
            m_edit_email.requestFocus();
            return;
        }

        // 이메일 validation
        if (Convert.isEmailValid(email) == false) {
            displayMsg(R.string.email_not_valid);
            m_edit_email.requestFocus();
            return;
        }

        // 비밀번호 입력 확인
        if (Convert.isEmpty(pass)) {
            displayMsg(R.string.enter_pass);
            m_edit_password.requestFocus();
            return;
        }

        // 비밀번호확인 입력 확인
        if (Convert.isEmpty(passConfirm)) {
            displayMsg(R.string.enter_pass_confirm);
            m_edit_confirm.requestFocus();
            return;
        }

        // 비밀번호 validation
        if (Convert.isValidPassword(pass) == false) {
            displayMsg(R.string.pass_not_valid);
            m_edit_password.requestFocus();
            return;
        }

        // 비밀번호와 비밀번호 확인이 같은지 체크
        if (pass.equals(passConfirm) == false) {
            displayMsg(R.string.pass_not_match);
            m_edit_password.requestFocus();
            return;
        }

        DB_User.setEmail(email);
        DB_User.setPassword(pass);
        DB_User.runQuery(QueryCode.CreateUser, this);
    }

    @Override
    public void run(Intent intent) {

    }
}
