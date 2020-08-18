package kr.co.greencomm.ibody24.coach.utils;

import android.os.Build;
import android.view.View;

/**
 * 시간이 경과 함에 따라 점차 표시되는 위젯 구현
 */
public abstract class FadeView {
    protected View m_view;
    // 시작하기 전에 대기할 시간 (밀리세크)
    private int m_wait_time;
    // 완전히 표시될때 까지 걸리는 시간 (밀리세크)
    private int m_period_time;
    // 표시가 시작된 시간
    private long m_start_time;
    // 총 표시 기간
    private int m_total_period;

    protected int m_displayColor;

    public FadeView(View containerView, int targetId, int waitTime, int periodTime, int colorId) {
        m_view = containerView.findViewById(targetId);
        m_wait_time = waitTime;
        m_period_time = periodTime;
        if (Build.VERSION.SDK_INT >= 23) {
            m_displayColor = containerView.getResources().getColor(colorId, null);
        } else {
            m_displayColor = containerView.getResources().getColor(colorId);
        }
        stop();
    }

    protected abstract void setColor(int color);

    public View getView() {
        return m_view;
    }

    public void start(int totalPeriod) {
        setColor(0);
        m_start_time = System.currentTimeMillis();
        m_total_period = totalPeriod;
    }

    public void stop() {
        m_start_time = 0;
    }

    public void update() {
        if (m_start_time == 0) return;

        int tick = (int) (System.currentTimeMillis() - m_start_time);
        tick = (tick >= m_total_period - m_period_time ? m_total_period - tick : tick - m_wait_time);
        if (tick < 0) tick = 0;
        if (tick > m_period_time) tick = m_period_time;

        int alpha = (int) (tick * 255 / m_period_time);
        setColor((alpha << 24) + (m_displayColor & 0xffffff));
    }
}
