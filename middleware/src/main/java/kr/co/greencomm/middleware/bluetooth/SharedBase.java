package kr.co.greencomm.middleware.bluetooth;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import android.content.Context;

import kr.co.greencomm.middleware.main.ConfigManager;

public abstract class SharedBase implements ITimeZone {
    private static final String tag = SharedBase.class.getSimpleName();

    /**
     * Flag
     **/
    private static boolean DEBUG = false;

    /** Variable **/

    /**
     * Instance
     **/
    protected ConfigManager mDBHelper = null;
    protected Calendar Global_Calendar, Live_Calendar;

    /**
     * Abstract
     **/
    protected abstract void dispose();

    private final WeakReference<Context> m_context;

    public SharedBase(Context context) {
        m_context = new WeakReference<>(context);
        mDBHelper = ConfigManager.getInstance(getContext());
        Global_Calendar = Calendar.getInstance();
        Live_Calendar = Calendar.getInstance();
    }

    protected Context getContext() {
        return m_context.get();
    }

    /**
     * Method
     **/
    protected void baseDispose() {
        Global_Calendar = Live_Calendar = null;
        mDBHelper = null;
    }
}
