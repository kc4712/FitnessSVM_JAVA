package kr.co.greencomm.ibody24.coach.tab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import kr.co.greencomm.ibody24.coach.activity.ActivityChartMain;
import kr.co.greencomm.ibody24.coach.activity.ActivityChartWeek;
import kr.co.greencomm.ibody24.coach.activity.ActivityChartYear;
import kr.co.greencomm.ibody24.coach.activity.ActivityMain;
import kr.co.greencomm.ibody24.coach.base.TabScreen;
import kr.co.greencomm.ibody24.coach.base.TabServer;

/**
 * Created by young on 2015-08-24.
 */
public class ActivityTabChart extends TabServer
{
    private final String TAG = "TAB_Chart";

    private static TabScreen m_view_main;
    private static TabScreen m_view_week;
    private static TabScreen m_view_year;

    private static int m_screen;

    private static ActivityTabChart m_host_chart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        m_host_chart = this;

        m_view_main = createView(this, ActivityChartMain.class);
        m_view_week = createView(this, ActivityChartWeek.class);
        m_view_year = createView(this, ActivityChartYear.class);

        selectChart(0, 0);
    }

    @Override
    public Activity getCurrentActivity() {
        //return super.getCurrentActivity();
        TabScreen scr = null;
        switch (m_screen) {
            case 0:
                scr = m_view_main;
                break;
            case 1:
                scr = m_view_week;
                break;
            default:
                scr = m_view_year;
        }
        if (scr == null) return null;
        Context context = scr.getContext();
        return (Activity) context;
    }

    public static void selectChart(int level, int part) {
        //m_data_complete = false;
        switch (level) {
            case 0:
                m_host_chart.show(m_view_main);
                m_screen = 0;
                //m_data_complete = true;
                break;
            case 1:
                m_host_chart.show(m_view_week);
                if (part >= 1 && part <= 5) {
                    ActivityChartWeek.setGraphMode(part);
                }
                m_screen = 1;
                break;
            default:
                m_host_chart.show(m_view_year);
                if (part >= 1 && part <= 5) {
                    ActivityChartYear.setGraphMode(part);
                }
                m_screen = 2;
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //if (m_data_complete == false) return;
        if (m_screen > 0) {
            selectChart(m_screen - 1, 0);
        } else {
            ActivityMain act = (ActivityMain) this.getParent();
            act.switchHome();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Activity act = m_manager.getCurrentActivity();
//        if (act != null) {
//        }
    }
}
