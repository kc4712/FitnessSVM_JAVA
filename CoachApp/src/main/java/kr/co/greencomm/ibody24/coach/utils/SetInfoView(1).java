package kr.co.greencomm.ibody24.coach.utils;

import android.view.View;
import kr.co.greencomm.ibody24.coach.R;

/**
 * Created by young on 2016-02-19.
 */
public class SetInfoView
{
	private final int FADEIN_PERIOD = 1500;

	private View m_view;

	private long m_setInfo_start = 0;
	private double m_setInfo_duration = 0;

	private FadeImageView m_line1;
	private FadeImageView m_line2;
	private FadeImageView m_line3;

	private FadeTextView m_set_title_count;
	private FadeTextView m_set_title_percent;
	private FadeGraphView m_set_image_count;
	private FadeGraphView m_set_image_percent;

	private FadeTextView m_set_title_info21;
	private FadeTextView m_set_text_point;
	private FadeTextView m_set_title_info22;

	private FadeTextView m_set_text_desc;

	public void setVisibility(int visibility) {
		m_view.setVisibility(visibility);
	}

	void start(int totalPeriod) {
		// 1차
		m_line1.start(totalPeriod);
		m_line2.start(totalPeriod);
		m_line3.start(totalPeriod);
		// 2차
		m_set_title_count.start(totalPeriod);
		m_set_title_percent.start(totalPeriod);
		m_set_image_count.start(totalPeriod);
		m_set_image_percent.start(totalPeriod);
		// 3차
		m_set_title_info21.start(totalPeriod);
		m_set_text_point.start(totalPeriod);
		m_set_title_info22.start(totalPeriod);
		// 4차
		m_set_text_desc.start(totalPeriod);
	}

	void update() {
		// 1차
		m_line1.update();
		m_line2.update();
		m_line3.update();
		// Fade in 2차
		m_set_title_count.update();
		m_set_title_percent.update();
		m_set_image_count.update();
		m_set_image_percent.update();
		// Fade in 3차
		m_set_title_info21.update();
		m_set_text_point.update();
		m_set_title_info22.update();
		// Fade in 4차
		m_set_text_desc.update();
	}

	void stop() {
		// 1차
		m_line1.stop();
		m_line2.stop();
		m_line3.stop();
		// Fade in 2차
		m_set_title_count.stop();
		m_set_title_percent.stop();
		m_set_image_count.stop();
		m_set_image_percent.stop();
		// Fade in 3차
		m_set_title_info21.stop();
		m_set_text_point.stop();
		m_set_title_info22.stop();
		// Fade in 4차
		m_set_text_desc.stop();
	}

	public void show(double displayTime, int point, int count_percent, int accuracy_percent, String desc) {
		m_set_text_point.setText(String.valueOf(point));
		m_set_text_desc.setText(desc);
		m_set_image_count.setGraph(count_percent);
		m_set_image_percent.setGraph(accuracy_percent);

		// FadeIn 적용되야 하는 모든 위젯을 시작시킨다.
		start((int)displayTime);

		m_setInfo_start = System.currentTimeMillis();
		m_setInfo_duration = displayTime;

		m_view.setVisibility(View.VISIBLE);
	}

	public void check() {
		if (m_setInfo_start != 0) {
			// FadeIn 적용되야 하는 모든 위젯을 갱신한다.
			update();
			if (m_setInfo_start + m_setInfo_duration < System.currentTimeMillis()) {
				stop();
				m_view.setVisibility(View.GONE);
				m_setInfo_start = 0;
			}
		}
	}

	public SetInfoView(View view) {
		m_view = view;
		// 1차 페이드 인
		m_line1 = new FadeImageView(view, R.id.line_1, 0, FADEIN_PERIOD, R.color.set_title_color1);
		m_line2 = new FadeImageView(view, R.id.line_2, 0, FADEIN_PERIOD, R.color.set_title_color1);
		m_line3 = new FadeImageView(view, R.id.line_3, 0, FADEIN_PERIOD, R.color.set_title_color1);
		// 2차 페이드 인
		m_set_title_count = new FadeTextView(view, R.id.set_title_count, FADEIN_PERIOD, FADEIN_PERIOD, R.color.set_title_color1);
		m_set_title_percent = new FadeTextView(view, R.id.set_title_percent, FADEIN_PERIOD, FADEIN_PERIOD, R.color.set_title_color1);
		m_set_image_count = new FadeGraphView(view, R.id.set_image_count, FADEIN_PERIOD, FADEIN_PERIOD, R.color.set_image_count_color);
		m_set_image_percent = new FadeGraphView(view, R.id.set_image_percent, FADEIN_PERIOD, FADEIN_PERIOD, R.color.set_image_percent_color);
		// 3차 페이드 인
		m_set_title_info21 = new FadeTextView(view, R.id.set_text_info21, FADEIN_PERIOD * 2, FADEIN_PERIOD, R.color.set_title_color2);
		m_set_text_point = new FadeTextView(view, R.id.set_text_point, FADEIN_PERIOD * 2, FADEIN_PERIOD, R.color.set_text_point_color);
		m_set_title_info22 = new FadeTextView(view, R.id.set_text_info22, FADEIN_PERIOD * 2, FADEIN_PERIOD, R.color.set_title_color2);
		// 4차 페이드 인
		m_set_text_desc = new FadeTextView(view, R.id.set_text_desc, FADEIN_PERIOD * 3, FADEIN_PERIOD, R.color.set_text_desc_color);
	}
}
