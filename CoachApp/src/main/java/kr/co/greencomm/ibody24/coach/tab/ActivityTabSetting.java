package kr.co.greencomm.ibody24.coach.tab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import kr.co.greencomm.ibody24.coach.activity.ActivitySetting;
import kr.co.greencomm.ibody24.coach.base.TabScreen;
import kr.co.greencomm.ibody24.coach.base.TabServer;


/**
 * Created by young on 2015-08-24.
 */
public class ActivityTabSetting extends TabServer {
    private final String TAG = "TAB_Setting";

    private static ActivityTabSetting m_host_setting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        m_host_setting = this;
        changeActivity(this, ActivitySetting.class);
    }

    public static void pushActivity(Intent intent) {
        m_host_setting.changeActivity(intent);
    }

    public static void pushActivity(Context context, Class<?> cls) {
        m_host_setting.changeActivity(context, cls);
    }

    public static void pushActivityNoStack(Context context, Class<?> cls) {
        m_host_setting.changeActivityNoStack(context, cls);
    }

    public static void popActivity() {
        m_host_setting.backView();
    }

    public static void popActivityMulti(int count) {
        for (int i = 0; i < count; i++) {
            m_host_setting.backView();
        }
    }

    @Override
    public Activity getCurrentActivity() {
        TabScreen scr = getCurrentScreen();
        if (scr == null) {
            return null;
        }
        Context context = scr.getContext();
        if (context instanceof Activity) {
            return (Activity) context;
        }
        return null;
    }
}
