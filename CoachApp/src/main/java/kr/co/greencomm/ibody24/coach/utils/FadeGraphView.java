package kr.co.greencomm.ibody24.coach.utils;

import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

/**
 * 시간이 경과 함에 따라 점차 표시되는 GraphView 구현
 */
public class FadeGraphView extends FadeImageView
{
	private int m_width = 0;
	private int m_graph_value = 0;
	private boolean m_get_size_flag = true;
	private boolean m_change_flag = false;

    public FadeGraphView(View containerView, int targetId, int waitTime, int periodTime, int colorId) {
		super(containerView, targetId, waitTime, periodTime, colorId);
		m_get_size_flag = true;
		m_change_flag = false;
	}

	@Override
	public void start(int totalPeriod) {
		super.start(totalPeriod);
		if (m_get_size_flag == false) return;
		m_view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				m_width = m_view.getWidth();
				Log.d("QQQ", String.format("Width:%d", m_width));
				if (m_width > 0) {
					m_view.getViewTreeObserver().removeOnPreDrawListener(this);
					m_get_size_flag = false;
					changeLayout();
				}
				return false;
			}
		});
	}

	public void setGraph(int value) {
		m_graph_value = value;
		if (m_graph_value < 0) m_graph_value = 0;
		if (m_graph_value > 100) m_graph_value = 100;
		m_change_flag = true;
		changeLayout();
	}

	private void changeLayout() {
		if (m_get_size_flag == true) return;
		if (m_change_flag == false) return;
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) m_view.getLayoutParams();
		params.width = m_width * m_graph_value / 100;
		m_view.setLayoutParams(params);
		m_change_flag = true;
	}
}
