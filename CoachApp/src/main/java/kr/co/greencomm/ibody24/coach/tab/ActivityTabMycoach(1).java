package kr.co.greencomm.ibody24.coach.tab;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import kr.co.greencomm.ibody24.coach.activity.ActivityMycoach;
import kr.co.greencomm.ibody24.coach.activity.fitness.ActivityToday;
import kr.co.greencomm.ibody24.coach.base.TabScreen;
import kr.co.greencomm.ibody24.coach.base.TabServer;
import kr.co.greencomm.middleware.utils.ProductCode;

/**
 * Created by jeyang on 16. 9. 20..
 */
public class ActivityTabMycoach extends TabServer {
    private static ActivityTabMycoach m_host_mycoach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_host_mycoach = this;

        loadFirstScreen();
    }

    public static void loadFirstScreen() {
        // 한번도 들어오지 않을수 있기 때문에, null 처리 필요.
        if (m_host_mycoach != null)
            m_host_mycoach.loadScreen();
    }

    private void loadScreen() {
        super.initStack(); // 스택이 쌓이는 것도 없는 화면이므로, 안해도 될듯.
        if (ActivityTabHome.m_app_use_product == ProductCode.Coach) {
            changeActivity(this, ActivityMycoach.class);
            return;
        } else if (ActivityTabHome.m_app_use_product == ProductCode.Fitness) {
            changeActivity(this, ActivityToday.class);
            return;
        }
        changeActivity(this, ActivityMycoach.class);
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
