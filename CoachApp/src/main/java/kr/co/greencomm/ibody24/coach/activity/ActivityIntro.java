package kr.co.greencomm.ibody24.coach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import java.util.Locale;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.service.MWService;
import kr.co.greencomm.middleware.utils.Convert;
import kr.co.greencomm.middleware.utils.FileManager;

/**
 * 어플리케이션 시작화면으로 처음에 보여진후 필요한
 * 장치 활성화 자동 로그인 등의 상황을 파악하여 처리하고
 * 일정 시간이 지나면 다음 화면으로 넘어간다.
 */
public class ActivityIntro
        extends CoachBaseActivity {
    private boolean isForeground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentScale1440(R.layout.activity_intro, R.id.screen_layout_main);
        setContentView(R.layout.coachplus_intro);

        setApplicationStatus(ApplicationStatus.IntroScreen);

        String lang = Locale.getDefault().getLanguage().toUpperCase();

        if (Preference.getLanguage(this) == null) {
            Preference.putLanguage(this, lang);
        } else {
            if (!Preference.getLanguage(this).equals(lang)) {
                FileManager.deleteAllFile(FileManager.getMainPath(this));
                Preference.removeXmlStore(this);
                Preference.putLanguage(this, lang);
            }
        }

        //Intent intent = new Intent(MWService.ACTION_START_SERVICE);
        //startService(intent);
        Intent intent = new Intent(getApplicationContext(), MWService.class);
        startService(intent);

        // 블루투스 장치를 지원하는지 확인하여 꺼져 있으면 켜기 작업을 요청한다.
        if (isBluetoothEnabled() == false) {
            requestBluetoothEnable();
            return;
        }
        if (Convert.getConnectivityStatus(getApplicationContext()) == Convert.CONNECT_TYPE_NOT_CONNECTED) {
            if (!isForeground)
                return;
            displayMsg(R.string.not_prepare_wifi, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            nextProcess();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
    }

    @Override
    public void run(Intent intent) {

    }
}
