package kr.co.greencomm.ibody24.coach.tab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import kr.co.greencomm.ibody24.coach.activity.ActivityCourse;
import kr.co.greencomm.ibody24.coach.activity.ActivityTrainer;
import kr.co.greencomm.ibody24.coach.activity.ActivityTrainerProgram;
import kr.co.greencomm.ibody24.coach.activity.coach.ActivityHome;
import kr.co.greencomm.ibody24.coach.activity.fitness.ActivityHomeFitness;
import kr.co.greencomm.ibody24.coach.base.TabScreen;
import kr.co.greencomm.ibody24.coach.base.TabServer;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.utils.ProductCode;


/**
 * Created by young on 2015-08-24.
 */
public class ActivityTabHome extends TabServer {
    private final String TAG = "TAB_Home";

    private static ActivityTabHome m_host_home;

    public static ProductCode m_app_use_product;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        m_host_home = this;

        loadFirstScreen();
    }

    public static void loadFirstScreen() {
        m_host_home.loadScreen();
    }

    private void loadScreen() {
        super.initStack();

        String name = Preference.getBluetoothName(this);
        //changeActivity(this, ActivityHomeFitness.class);
        if (name != null && !name.isEmpty()) {
            String prefix = name.substring(0, ProductCode.Fitness.getBluetoothDeviceName().length());
            if (prefix.equals(ProductCode.Fitness.getBluetoothDeviceName())) {
                m_app_use_product = ProductCode.Fitness;
                m_host_home.changeActivity(this, ActivityHomeFitness.class);
                return;
            } else if (prefix.equals(ProductCode.Coach.getBluetoothDeviceName())) {
                m_app_use_product = ProductCode.Coach;
                m_host_home.changeActivity(this, ActivityHome.class);
                return;
            }
        }
        m_app_use_product = ProductCode.Coach;
        m_host_home.changeActivity(this, ActivityHome.class);
    }

    public static void pushActivity(Context context, Class<?> cls) {
        m_host_home.changeActivity(context, cls);
    }

    public static void popActivity() {
        m_host_home.backView();
    }

    public static void pushActivity(Intent intent) {
        m_host_home.changeActivity(intent);
    }

    public static void startTrainer(Context context, int trainerCode) {
        Class<?> cls = ActivityTrainer.class;
        Intent intent = new Intent(context, cls);
        intent.putExtra("TrainerCode", trainerCode);
        m_host_home.changeActivity(intent);
    }

    public static void startTrainerProgram(Context context, int trainerCode) {
        Class<?> cls = ActivityTrainerProgram.class;
        Intent intent = new Intent(context, cls);
        intent.putExtra("TrainerCode", trainerCode);
        m_host_home.changeActivity(intent);
    }

    public static void startFitnessProgram(Context context, int progCode) {
        Class<?> cls = ActivityCourse.class;
        Intent intent = new Intent(context, cls);
        intent.putExtra("ProgCode", progCode);
        m_host_home.changeActivity(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d(TAG, "onActivityResult requestCode:" + requestCode + ",resultCode:" + resultCode);
        //super.onActivityResult(requestCode, resultCode, data);
//        Activity act = getManagerActivity();
//        if (act != null) {
//            if (act instanceof ActivityCourse) {
//                ((ActivityCourse) act).onActivityResult(requestCode, resultCode, data);
//            }
//        }
    }
}
