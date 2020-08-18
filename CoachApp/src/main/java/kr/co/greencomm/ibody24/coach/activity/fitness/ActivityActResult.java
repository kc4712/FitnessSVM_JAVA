package kr.co.greencomm.ibody24.coach.activity.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.ActivityInformation;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.provider.CoachResolver;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.middleware.utils.container.ActivityData;

/**
 * Created by jeyang on 16. 9. 21..
 */
public class ActivityActResult extends CoachBaseActivity implements View.OnClickListener {
    private static final String tag = ActivityActResult.class.getSimpleName();

    private TextView m_txt_calorie, m_txt_date, m_txt_time;
    private TextView m_txt_low, m_txt_mid, m_txt_high, m_txt_danger;
    private TextView m_txt_min_heartrate, m_txt_max_heartrate, m_txt_avg_heartrate;

    private ImageView m_img_bar_low, m_img_bar_mid, m_img_bar_high, m_img_bar_danger;

    private LinearLayout m_layout_bar;

    private Button m_btn_question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coachplus_activity_result);
        setApplicationStatus(ApplicationStatus.ActResultScreen);

        m_txt_calorie = (TextView) findViewById(R.id.activity_result_txt_calorie);
        m_txt_date = (TextView) findViewById(R.id.activity_result_txt_date);
        m_txt_time = (TextView) findViewById(R.id.activity_result_txt_time);
        m_txt_low = (TextView) findViewById(R.id.activity_result_txt_low);
        m_txt_mid = (TextView) findViewById(R.id.activity_result_txt_mid);
        m_txt_high = (TextView) findViewById(R.id.activity_result_txt_high);
        m_txt_danger = (TextView) findViewById(R.id.activity_result_txt_danger);
        m_txt_min_heartrate = (TextView) findViewById(R.id.activity_result_txt_min_heartrate);
        m_txt_max_heartrate = (TextView) findViewById(R.id.activity_result_txt_max_heartrate);
        m_txt_avg_heartrate = (TextView) findViewById(R.id.activity_result_txt_avg_heartrate);

        m_img_bar_low = (ImageView) findViewById(R.id.activity_result_img_bar_low);
        m_img_bar_mid = (ImageView) findViewById(R.id.activity_result_img_bar_mid);
        m_img_bar_high = (ImageView) findViewById(R.id.activity_result_img_bar_high);
        m_img_bar_danger = (ImageView) findViewById(R.id.activity_result_img_bar_danger);

        m_btn_question = (Button) findViewById(R.id.activity_result_question);
        m_btn_question.setOnClickListener(this);

        m_layout_bar = (LinearLayout) findViewById(R.id.activity_result_layout_bar);

        long start_time = getIntent().getLongExtra("start_time", 0L);

        setGraphData(start_time);
    }

    private void setGraphData(long start_time) {
        if (start_time == 0) {
            return;
        }

        CoachResolver res = new CoachResolver();
        ActivityData datas = res.getActivityDataProvider(start_time);

        Log.d(tag, "datas :: " + datas.getStart_time() + ", " + datas.getEnd_time() + "");

        if (datas == null) {
            m_layout_bar.setVisibility(View.INVISIBLE);

            m_txt_low.setText(String.format(Locale.getDefault(), "%s 0%", getString(R.string.workout_graph_low)));
            m_txt_mid.setText(String.format(Locale.getDefault(), "%s 0%", getString(R.string.workout_graph_mid)));
            m_txt_high.setText(String.format(Locale.getDefault(), "%s 0%", getString(R.string.workout_graph_high)));
            m_txt_danger.setText(String.format(Locale.getDefault(), "%s 0%", getString(R.string.workout_graph_danger)));
            return;
        }

        int[] inten_datas = new int[]{datas.getIntensityL(), datas.getIntensityM(),
                datas.getIntensityH(), datas.getIntensityD()};

        float sum = 0f;
        for (int val : inten_datas) {
            sum += (float) val;
        }

        float[] values = new float[4];
        for (int i = 0; i < inten_datas.length; i++) {
            values[i] = inten_datas[i] / sum * 100;
        }

        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) m_img_bar_low.getLayoutParams();
        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) m_img_bar_mid.getLayoutParams();
        LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) m_img_bar_high.getLayoutParams();
        LinearLayout.LayoutParams params4 = (LinearLayout.LayoutParams) m_img_bar_danger.getLayoutParams();

//        values[0] = 10;
//        values[1] = 20;
//        values[2] = 30;
//        values[3] = 40;

        params1.weight = values[0];
        params2.weight = values[1];
        params3.weight = values[2];
        params4.weight = values[3];

        m_img_bar_low.setLayoutParams(params1);
        m_img_bar_mid.setLayoutParams(params2);
        m_img_bar_high.setLayoutParams(params3);
        m_img_bar_danger.setLayoutParams(params4);

        m_txt_low.setText(String.format(Locale.getDefault(), "%s %d%%", getString(R.string.workout_graph_low), (int) values[0]));
        m_txt_mid.setText(String.format(Locale.getDefault(), "%s %d%%", getString(R.string.workout_graph_mid), (int) values[1]));
        m_txt_high.setText(String.format(Locale.getDefault(), "%s %d%%", getString(R.string.workout_graph_high), (int) values[2]));
        m_txt_danger.setText(String.format(Locale.getDefault(), "%s %d%%", getString(R.string.workout_graph_danger), (int) values[3]));

        m_txt_calorie.setText(String.valueOf(datas.getAct_calorie().intValue()));

        String start_TimeString = getTimeString(datas.getStart_time());
        String end_TimeString = getTimeString(datas.getEnd_time());

        String dateString = getDateString(datas.getStart_time());

        m_txt_date.setText(dateString);
        m_txt_time.setText(String.format(Locale.getDefault(), "%s ~ %s", start_TimeString, end_TimeString));

        m_txt_min_heartrate.setText(String.valueOf(datas.getMinHR()));
        m_txt_max_heartrate.setText(String.valueOf(datas.getMaxHR()));
        m_txt_avg_heartrate.setText(String.valueOf(datas.getAvgHR()));
    }

    private String getDateString(long date) {
        DateFormat fmt = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String ret = fmt.format(new Date(date));

        return ret;
    }

    private String getTimeString(long date) {
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String ret = fmt.format(new Date(date));

        return ret;
    }

    @Override
    public void run(Intent intent) {

    }

    @Override
    public void onClick(View view) {
        m_popup_mode_home = true;
        Intent intent = new Intent(this, ActivityInformation.class);
        //intent.putExtra("title", String.format(Locale.getDefault(), getString(R.string.setting_menu1), ActivityTabHome.m_app_use_product.getProductName()));
        intent.putExtra("address", getUrlHtmlResources(getString(R.string.html_name_workout_measure)));
        ActivityTabHome.pushActivity(intent);
    }
}
