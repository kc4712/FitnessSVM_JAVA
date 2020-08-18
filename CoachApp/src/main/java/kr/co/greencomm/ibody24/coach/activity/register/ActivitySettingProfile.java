package kr.co.greencomm.ibody24.coach.activity.register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.ActivityMain;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.dialog.GenderDialog;
import kr.co.greencomm.ibody24.coach.dialog.NumberPickerDialog;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.utils.Height;
import kr.co.greencomm.ibody24.coach.utils.Weight;

/**
 * Created by young on 2015-08-19.
 */
public class ActivitySettingProfile
        extends CoachBaseActivity
        implements View.OnClickListener {
    private EditText m_edit_name;
    private EditText m_edit_birth;
    private EditText m_edit_gender;
    private EditText m_edit_height;
    private EditText m_edit_weight;

    private Button m_btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentScale1440(R.layout.activity_user_info, R.id.screen_layout_main);
        setContentView(R.layout.coachplus_setting_profile);

        setApplicationStatus(ApplicationStatus.UserProfileScreen);

        m_edit_name = (EditText) findViewById(R.id.setting_profile_edit_name);

        m_edit_birth = (EditText) findViewById(R.id.setting_profile_edit_birth);
        m_edit_gender = (EditText) findViewById(R.id.setting_profile_edit_gender);
        m_edit_height = (EditText) findViewById(R.id.setting_profile_edit_height);
        m_edit_weight = (EditText) findViewById(R.id.setting_profile_edit_weight);

        m_edit_birth.setOnClickListener(this);
        m_edit_gender.setOnClickListener(this);
        m_edit_height.setOnClickListener(this);
        m_edit_weight.setOnClickListener(this);

        m_btn_next = (Button) findViewById(R.id.setting_profile_btn_next);
        m_btn_next.setOnClickListener(this);
        if (m_popup_mode) {
            m_btn_next.setText(R.string.setting);
            updateInfo();
        }
    }

    private void updateInfo() {
        String name = m_edit_name.getText().toString().trim();
        if (name != null && name.length() > 0) {
            DB_User.setName(name);
        }
        if (DB_User.getName() != null) {
            m_edit_name.setText(DB_User.getName());
        }
        if (DB_User.getBirthday() != null) {
            m_edit_birth.setText(String.valueOf(DB_User.getBirthString()));
        } else {
            m_edit_birth.setText("");
        }

        if (DB_User.getGender() == 1) {
            m_edit_gender.setText(R.string.male);
        } else if (DB_User.getGender() == 2) {
            m_edit_gender.setText(R.string.female);
        } else {
            m_edit_gender.setText("");
        }

        if (DB_User.getWeight() > 0) {
            m_edit_weight.setText(String.valueOf((int)DB_User.getWeight()));
        } else {
            m_edit_weight.setText("");
        }

        if (DB_User.getHeight() > 0) {
            m_edit_height.setText(String.valueOf(DB_User.getHeight()));
        } else {
            m_edit_height.setText("");
        }
        m_edit_name.requestFocus();
    }

    private boolean checkProcess() {
        DB_User.setName(m_edit_name.getText().toString().trim());
        DB_User.setHeight(Integer.parseInt(m_edit_height.getText().toString()));
        if (DB_User.isProfileValid() == false) {
            displayMsg(R.string.please_all_information);
            m_edit_name.requestFocus();
            return false;
        }
        return true;
    }

    private void saveProcess() {
        DB_User.runQuery(QueryCode.SetProfile, null);
        DB_User.runQuery(QueryCode.SetTarget, this);
    }

    private void inputBirth() {
        DatePickerDialog.OnDateSetListener m_listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DB_User.setBirthday(year, monthOfYear, dayOfMonth);
                updateInfo();
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(ActivityMain.MainContext == null ? this : ActivityMain.MainContext, m_listener, DB_User.getBirthYear(), DB_User.getBirthMonth() - 1, DB_User.getBirthDay());
        dialog.show();
    }

    private void inputGender() {
        GenderDialog.OnDateSetListener m_listener = new GenderDialog.OnDateSetListener() {
            @Override
            public void onDateSet(View view, int gender) {
                DB_User.setGender(gender);
                updateInfo();
            }
        };
        GenderDialog dialog = new GenderDialog(ActivityMain.MainContext == null ? this : ActivityMain.MainContext, getString(R.string.select_gender), DB_User.getGender(), m_listener);
        dialog.show();
    }

    private void inputHeight() {
        NumberPickerDialog.OnDateSetListener listener = new NumberPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(NumberPicker view, int value) {
                DB_User.setHeight(value);
                updateInfo();
            }
        };
        NumberPickerDialog dialog = new NumberPickerDialog(ActivityMain.MainContext == null ? this : ActivityMain.MainContext, getString(R.string.height_input), DB_User.getHeight() == 0 ? Height.Default : DB_User.getHeight(), 260, 90, listener);
        dialog.show();
    }

    private void inputWeight() {
        NumberPickerDialog.OnDateSetListener listener = new NumberPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(NumberPicker view, int value) {
                DB_User.setWeight(value);
                updateInfo();
            }
        };
        NumberPickerDialog dialog = new NumberPickerDialog(ActivityMain.MainContext == null ? this : ActivityMain.MainContext, getString(R.string.weight_input), (int) (DB_User.getWeight() == 0 ? Weight.Default : DB_User.getWeight()), 180, 10, listener);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_profile_edit_birth:
                inputBirth();
                break;
            case R.id.setting_profile_edit_gender:
                inputGender();
                break;
            case R.id.setting_profile_edit_height:
                inputHeight();
                break;
            case R.id.setting_profile_edit_weight:
                inputWeight();
                break;
            case R.id.setting_profile_btn_next:
                if (checkProcess() == true) {
                    saveProcess();
                }
                break;
        }
    }

    @Override
    public void run(Intent intent) {

    }
}
