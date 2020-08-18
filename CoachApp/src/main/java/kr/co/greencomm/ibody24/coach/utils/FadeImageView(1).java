package kr.co.greencomm.ibody24.coach.utils;

import android.view.View;

/**
 * 시간이 경과 함에 따라 점차 표시되는 ImageView 구현
 */
public class FadeImageView extends FadeView
{
	public FadeImageView(View containerView, int targetId, int waitTime, int periodTime, int colorId) {
		super(containerView, targetId, waitTime, periodTime, colorId);
	}

	@Override
	protected void setColor(int color) {
		m_view.setBackgroundColor(color);
	}
}
