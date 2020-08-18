package kr.co.greencomm.ibody24.coach.base;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Stack;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.ActivityMain;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.middleware.utils.StateApp;

/**
 * Created by young on 2016-09-20.
 */

public abstract class TabServer extends ActivityGroup {
    private final String TAG = "TabServer";

    private LocalActivityManager m_manager;
    private Stack<TabScreen> m_screens;

    public void show(TabScreen scr) {
        Context context = scr.getContext();
        if (context instanceof CoachBaseActivity) {
            ((CoachBaseActivity) context).onShow();
        }
        setContentView(scr.getView());
    }

    public TabScreen createView(Intent intent) {
        TabScreen ret = new TabScreen();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ret.setId(intent.getClass().getSimpleName());
        ret.setWindow(m_manager.startActivity(ret.getId(), intent));
        return ret;
    }

    public TabScreen createView(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        return createView(intent);
    }

    public void changeActivity(Intent intent) {
        TabScreen scr = createView(intent);
        m_screens.push(scr);
        show(scr);
    }

    public void changeActivityNoStack(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        TabScreen scr = createView(intent);
        show(scr);
    }

    public void changeActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        changeActivity(intent);
    }

    protected TabScreen getCurrentScreen() {
        if (m_screens.isEmpty()) {
            return null;
        }
        return m_screens.peek();
    }

    public abstract Activity getCurrentActivity();

    private boolean m_closeFlag = false;

    private Handler m_handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 110:
                    m_closeFlag = false;
                    break;
                default:
                    break;
            }
        }
    };

    private void closeDouble() {
        if (m_closeFlag == false) {
            Toast.makeText(this, R.string.confirm_back_close, Toast.LENGTH_SHORT).show();
            m_closeFlag = true;
            m_handler.sendEmptyMessageDelayed(110, 2000);
        } else {
            //super.onBackPressed();
            //CoachBaseActivity.closeDevice(this);
            SendReceive.sendIsLiveApplication(getApplicationContext(), StateApp.STATE_EXIT);
            finish();
        }
    }

    public void backView() {
        int cnt = m_screens.size();
        if (cnt >= 2) {
            Log.d(TAG, "==> backScreen");
            //m_screens.remove(cnt - 1);
            m_screens.pop();
            //TabScreen scr = m_screens.get(cnt - 2);
            TabScreen scr = m_screens.peek();
            show(scr);
        } else {
            if (this instanceof ActivityTabHome) {
                Log.d(TAG, "==> closeDouble");
                closeDouble();
            } else {
                Activity activity = this.getParent();
                if (activity instanceof ActivityMain) {
                    Log.d(TAG, "==> switchHome");
                    ((ActivityMain) activity).switchHome();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //m_this = this;
        m_manager = getLocalActivityManager();
        initStack();
    }

    public void initStack() {
        m_screens = new Stack<>();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        StringBuilder sb = new StringBuilder();
        sb.append("onBackPressed");

        Activity act = getParent();
        if (act != null) {
            sb.append("\nParent: " + act.getLocalClassName());
            if (act instanceof ActivityMain) {
                ((ActivityMain) act).getCurrentActivity();
            }
        }
        sb.append("\nViewCount: " + m_screens.size());
        sb.append("\nHost: " + this.getLocalClassName());
        Log.d(TAG, sb.toString());
//        TabScreen scr = m_screens.peek();
//        if (scr != null) {
//            Context context = scr.getContext();
//            if (context instanceof ActivityCourse) {
//                ActivityCourse act_course = ((ActivityCourse) context);
//                if (act_course.isDownloading()) {
//                    act_course.cancelDownload();
//                    return;
//                }
//            }
//        }
        backView();
    }
}
