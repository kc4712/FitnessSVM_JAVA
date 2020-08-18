package kr.co.greencomm.ibody24.coach.utils;

import android.widget.TextView;

/**
 * Created by young on 2015-09-05.
 */
public class TimerTextView
{

    private TextView m_textView;
    private int m_displayTime;
    private int m_backColor;
    private String m_nowText;
    private long m_startTime;

    public TimerTextView(TextView textView, int displayTime, int backColor) {
        m_textView = textView;
        m_displayTime = displayTime;
        m_backColor = backColor;
        resetDisplay();
    }

    private void resetDisplay() {
        m_nowText = "";
        m_textView.setText(m_nowText);
        m_textView.setBackgroundColor(0);
        m_startTime = 0;
    }

    public void show(String msg) {
        if (msg == null) msg = "";
        msg = msg.trim();
        if (msg.equals(m_nowText)) return;
        m_nowText = msg;
        if (m_nowText != null && m_nowText.length() > 0) {
            m_textView.setText(m_nowText);
            m_textView.setBackgroundColor(m_backColor);
            m_startTime = System.currentTimeMillis();
        }
    }

    public void hideCheck() {
        if (m_startTime == 0) return;
        if (m_startTime + m_displayTime < System.currentTimeMillis()) {
            resetDisplay();
        }
    }
}
