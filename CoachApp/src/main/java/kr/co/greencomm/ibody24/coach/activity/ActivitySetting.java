package kr.co.greencomm.ibody24.coach.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.Locale;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.register.ActivityLogin;
import kr.co.greencomm.ibody24.coach.activity.register.ActivitySettingProfile;
import kr.co.greencomm.ibody24.coach.activity.register.ActivitySettingWeight;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.dialog.MessageDialogConfirm;
import kr.co.greencomm.ibody24.coach.provider.CoachResolver;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabSetting;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.utils.ProductCode;
import kr.co.greencomm.middleware.utils.StateApp;


/**
 * Created by young on 2015-08-28.
 */
public class ActivitySetting
        extends CoachBaseActivity
        implements View.OnClickListener {
    private Button m_btn_glossary;
    private Button m_btn_manual;
    private Button m_btn_profile;
    private Button m_btn_weight;
    private Button m_btn_notice;
    private Button m_btn_device_manager;
    private Button m_btn_register_device;
    private Button m_btn_logout;

    private Intent intent;

    private static ActivitySetting m_setting_act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.coachplus_setting);

        m_setting_act = this;

        m_btn_glossary = (Button) findViewById(R.id.setting_glossary);
        m_btn_manual = (Button) findViewById(R.id.setting_manual);
        m_btn_profile = (Button) findViewById(R.id.setting_profile);
        m_btn_weight = (Button) findViewById(R.id.setting_weight);
        m_btn_notice = (Button) findViewById(R.id.setting_notice);
        m_btn_device_manager = (Button) findViewById(R.id.setting_device_manager);
        m_btn_register_device = (Button) findViewById(R.id.setting_register_device);
        m_btn_logout = (Button) findViewById(R.id.setting_logout);

        m_btn_glossary.setOnClickListener(this);
        m_btn_manual.setOnClickListener(this);
        m_btn_profile.setOnClickListener(this);
        m_btn_weight.setOnClickListener(this);
        m_btn_notice.setOnClickListener(this);
        m_btn_device_manager.setOnClickListener(this);
        m_btn_register_device.setOnClickListener(this);
        m_btn_logout.setOnClickListener(this);

        loadFirstScreen();

        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
//            m_txt_version.setText(version);
        } catch (Exception e) {
        }
    }

    public static void loadFirstScreen() {
        if (m_setting_act != null)
            m_setting_act.loadScreen();
    }

    private void loadScreen() {
        m_btn_glossary.setText(String.format(Locale.getDefault(), getString(R.string.setting_menu1), ActivityTabHome.m_app_use_product.getProductName()));
        m_btn_manual.setText(String.format(Locale.getDefault(), getString(R.string.setting_menu2), ActivityTabHome.m_app_use_product.getProductName()));
    }

    @Override
    public void onClick(View v) {
        m_popup_mode = true;
        switch (v.getId()) {
            case R.id.setting_glossary:
                intent = new Intent(this, ActivityInformation.class);
                intent.putExtra("title", String.format(Locale.getDefault(), getString(R.string.setting_menu1), ActivityTabHome.m_app_use_product.getProductName()));
                if (ActivityTabHome.m_app_use_product == ProductCode.Coach) {
                    intent.putExtra("address", getUrlHtmlResources(getString(R.string.html_name_glossary)));
                } else if (ActivityTabHome.m_app_use_product == ProductCode.Fitness) {
                    intent.putExtra("address", getUrlHtmlResources(getString(R.string.html_name_glossary_fit)));
                }
                ActivityTabSetting.pushActivity(intent);
                break;
            case R.id.setting_manual:
                intent = new Intent(this, ActivityInformation.class);
                intent.putExtra("title", String.format(Locale.getDefault(), getString(R.string.setting_menu2), ActivityTabHome.m_app_use_product.getProductName()));
                if (ActivityTabHome.m_app_use_product == ProductCode.Coach) {
                    intent.putExtra("address", getUrlHtmlResources(getString(R.string.html_name_manual)));
                } else if (ActivityTabHome.m_app_use_product == ProductCode.Fitness) {
                    intent.putExtra("address", getUrlHtmlResources(getString(R.string.html_name_manual_fit)));
                }
                ActivityTabSetting.pushActivity(intent);
                break;
            case R.id.setting_profile:
                intent = new Intent(this, ActivitySettingProfile.class);
                //startActivityForResult(intent, 702);
                ActivityTabSetting.pushActivity(intent);
                break;
            case R.id.setting_notice:
                intent = new Intent(this, ActivityInformation.class);
                intent.putExtra("title", getString(R.string.setting_menu4));
                intent.putExtra("address", getString(R.string.web_event));
                //startActivityForResult(intent, 703);
                ActivityTabSetting.pushActivity(intent);
                break;
            case R.id.setting_weight:
                intent = new Intent(this, ActivitySettingWeight.class);
                //startActivityForResult(intent, 704);
                ActivityTabSetting.pushActivity(intent);
                break;
            case R.id.setting_device_manager:
                intent = new Intent(this, ActivityDeviceManager.class);
                //startActivity(intent);
                ActivityTabSetting.pushActivity(intent);
                break;
            case R.id.setting_register_device:
                //startActivity(intent);
                if (CoachBaseActivity.DB_User.getDeviceName() == null || CoachBaseActivity.DB_User.getDeviceName().isEmpty() == true) {
                    intent = new Intent(this, ActivityRegisterDevice.class);
                } else {
                    intent = new Intent(this, ActivityDeviceEdit.class);

                }
                ActivityTabSetting.pushActivity(intent);
                break;
            case R.id.setting_logout:
                MessageDialogConfirm confirm = new MessageDialogConfirm(ActivityMain.MainContext, getString(R.string.confirm_logout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
//                            CoachBaseActivity.closeDevice(ActivitySetting.this);
                            //setManualLogin();
                            SendReceive.logout(getApplicationContext());
                            CoachResolver res = new CoachResolver();
                            res.deleteActivityDataProvider();
                            res.deleteCoachActivityDataProvider();
                            res.deleteIndexTimeProvider();
                            Preference.removeAll(getApplicationContext());
                            SendReceive.sendIsLiveApplication(getApplicationContext(), StateApp.STATE_EXIT);
                            intent = new Intent(ActivityMain.MainContext, ActivityLogin.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            ActivityMain.MainContext.startActivity(intent);
                            ActivityMain.MainContext = null;
                            finish();
                        }
                    }
                });
                confirm.show();
                break;
        }
    }

    @Override
    public void run(Intent intent) {

    }
}
