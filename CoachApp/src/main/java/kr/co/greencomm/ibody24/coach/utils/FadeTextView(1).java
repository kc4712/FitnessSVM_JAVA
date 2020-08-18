package kr.co.greencomm.ibody24.coach.utils;

import android.view.View;
import android.widget.TextView;

/**
 * 시간이 경과 함에 따라 점차 표시되는 TextView 구현
 */
public class FadeTextView extends FadeView
{
	/**
	 * 시간에 따라 점차 표시되는 TextView
	 * @param containerView 컨테이너 뷰
	 * @param targetId 사용될 뷰의 아이디
	 * @param waitTime 표시가 시작되기 전에 대기할 시간 (밀리세크)
	 * @param periodTime 표시에 소요되는 기간 (밀리세크)
	 * @param colorId 표시에 사용될 컬러의 아이디
	 */
	public FadeTextView(View containerView, int targetId, int waitTime, int periodTime, int colorId) {
		super(containerView, targetId, waitTime, periodTime, colorId);
	}

	@Override
	protected void setColor(int color) {
		((TextView)m_view).setTextColor(color);
	}

	public void setText(CharSequence text) {
		((TextView)m_view).setText(text);
	}
}
