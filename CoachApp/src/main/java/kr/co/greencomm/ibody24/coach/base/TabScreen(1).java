package kr.co.greencomm.ibody24.coach.base;

import android.content.Context;
import android.view.View;
import android.view.Window;

/**
 * Created by young on 2016-09-22.
 */

public class TabScreen
{
    public String m_id;
    public Window m_window;
    public View m_view;

    public void setId(String id) {
        m_id = id;
    }

    public String getId() {
        return m_id;
    }

    public void setWindow(Window window) {
        m_window = window;
        if (window != null) {
            m_view = window.getDecorView();
        }
    }

    public Window getWindow() {
        return m_window;
    }

    public View getView() {
        return m_view;
    }

    public Context getContext() {
        return m_window.getContext();
    }

    public  TabScreen() {
    }
}
