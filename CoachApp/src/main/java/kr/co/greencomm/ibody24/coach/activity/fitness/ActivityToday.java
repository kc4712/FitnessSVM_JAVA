package kr.co.greencomm.ibody24.coach.activity.fitness;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.data.UserRecord;
import kr.co.greencomm.ibody24.coach.utils.Weight;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver.Action;
import kr.co.greencomm.middleware.video.CalculateBase;

/**
 * Created by jeyang on 16. 9. 21..
 */
public class ActivityToday extends CoachBaseActivity {
    private static final String tag = ActivityToday.class.getSimpleName();

    private TextView m_txt_sum_calorie, m_txt_percent;
    private EditText m_txt_calorie;

    private LinearLayout m_layout_main;

    private PieChart m_chart;

    private double[] m_pie_data = new double[]{0, 0, 0, 0};

    private ArrayList<Double> values = new ArrayList<>();
    private String[] xVals;
    private int[] colors;

    private double m_sum_calorie_number;
    private int m_percent_number, m_today_calorie_number;

    private float MET_MAN = 1.5f;
    private float MET_WOMAN = 1.4f;

    private TextWatcher txt_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                setViewer(Integer.parseInt(s.toString()), m_pie_data[0], m_pie_data[1], m_pie_data[2], m_pie_data[3]);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coachplus_today);
        setApplicationStatus(ApplicationStatus.TodayScreen);

        m_layout_main = (LinearLayout) findViewById(R.id.today_layout_main);
        m_layout_main.setFocusable(true);
        m_layout_main.setFocusableInTouchMode(true);

        m_txt_calorie = (EditText) findViewById(R.id.today_txt_calorie);
        m_txt_sum_calorie = (TextView) findViewById(R.id.today_txt_sum_calorie);
        m_txt_percent = (TextView) findViewById(R.id.today_txt_percent);

        m_chart = (PieChart) findViewById(R.id.today_chart);

        m_txt_calorie.addTextChangedListener(txt_watcher);

        if (Build.VERSION.SDK_INT >= 23) {
            colors = new int[]{getResources().getColor(R.color.color_pie_act, null), getResources().getColor(R.color.color_pie_coach, null),
                    getResources().getColor(R.color.color_pie_sleep, null), getResources().getColor(R.color.color_pie_daily, null)};
        } else {
            colors = new int[]{getResources().getColor(R.color.color_pie_act), getResources().getColor(R.color.color_pie_coach),
                    getResources().getColor(R.color.color_pie_sleep), getResources().getColor(R.color.color_pie_daily)};
        }

        xVals = new String[]{getString(R.string.graph_legend_activity), getString(R.string.graph_legend_coach), getString(R.string.graph_legend_sleep), getString(R.string.graph_legend_daily)};

        setChartProperties();
        setData();

        //SendReceive.getStepNCalorie(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_layout_main.requestFocus();

        //SendReceive.getStepNCalorie(this);

        m_pie_data = new double[]{Preference.getMainCalorieActivity(this), Preference.getMainCalorieCoach(this),
                Preference.getMainCalorieSleep(this), Preference.getMainCalorieDaily(this)};

        int cal = getTodayCalorie();

        m_txt_calorie.setText(String.valueOf(cal));
        setViewer(cal, m_pie_data[0], m_pie_data[1], m_pie_data[2], m_pie_data[3]);
        setData();
    }

    private void setViewer(int today, double activity, double coach, double sleep, double daily) {
        double sum_cal = activity + coach + daily + sleep;

        if (m_sum_calorie_number == sum_cal && m_today_calorie_number == today) {
            return;
        }

        Preference.putTodayCalorie(this, today);
        m_today_calorie_number = today;
        m_sum_calorie_number = sum_cal;
        m_txt_sum_calorie.setText(String.valueOf((int) m_sum_calorie_number));
        // 데이터가 들어오면 달성도 계산 가능.
        Double percent = m_sum_calorie_number / m_today_calorie_number * 100;
        if (percent.isInfinite() || percent.isNaN()) {
            percent = 0.0;
        }

        if (percent > 100) {
            percent = 100.0;
        }

        // 누적 갈로리가 변경되던가, 오늘의 목표가 변하면 텍스트뷰를 새로 그려야함.
        if (m_percent_number != percent.intValue()) {
            m_percent_number = percent.intValue();
            m_txt_percent.setText(String.valueOf(m_percent_number));
        }

        getGraphData(activity, coach, sleep, daily);
    }

    private void getGraphData(double activity, double coach, double sleep, double daily) {
        // 5개 요소의 전체 사용 시간을 합산해서, 어느 정도의 비율을 차지하고 있는지 계산.
        // 1.활동, 2.코치, 3.수면, 4.일상
        double sum_cal = activity + coach + daily + sleep;

        double dat1 = activity / sum_cal * 100;
        double dat2 = coach / sum_cal * 100;
        double dat3 = sleep / sum_cal * 100;
        double dat4 = daily / sum_cal * 100;

        values.clear();
        values.add(dat1);
        values.add(dat2);
        values.add(dat3);
        values.add(dat4);
    }

    private void setChartProperties() {
        m_chart.setDrawHoleEnabled(true);
        m_chart.setDescription("");
        m_chart.setRotationAngle(0f);
        m_chart.setDrawMarkerViews(false);
        m_chart.setUsePercentValues(true);
//        m_chart.drawSlicesUnderHoleEnabled = false
        m_chart.setHoleRadius(0.58f);
        m_chart.setTransparentCircleRadius(0.61f);
        m_chart.setExtraLeftOffset(5f);
        m_chart.setExtraTopOffset(10f);
        m_chart.setExtraRightOffset(5f);
        m_chart.setExtraBottomOffset(5f);
        m_chart.setRotationEnabled(false);
        m_chart.setHighlightEnabled(true);
        m_chart.setDrawSliceText(false);

        Legend legend = m_chart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
//        legend.horizontalAlignment = ChartLegend.HorizontalAlignment.center
//        legend.verticalAlignment = ChartLegend.VerticalAlignment.bottom
//        legend.font = NSUIFont.systemFont(ofSize: 13)
    }

    public void setData() {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals1 = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            xVals.add(this.xVals[i]);

            yVals1.add(new Entry(values.get(i).floatValue(), i));
        }

        PieDataSet set1 = new PieDataSet(yVals1, null);
        set1.setColors(colors);
        set1.setDrawValues(false);
        set1.setHighlightEnabled(true);
        set1.setSliceSpace(2f);

        PieData data = new PieData(xVals, set1);

        m_chart.setData(data);
        m_chart.animateXY(1000, 1000);
    }

    private int getTodayCalorie() {
        int cal = Preference.getTodayCalorie(this);
        if (cal == 0) {
            double metabolic = 0;
            if (DB_User.getGender() == UserRecord.SEX_MAN)
                metabolic = CalculateBase.getConsumeCalorie(DB_User.getWeight(), MET_MAN);
            else
                metabolic = CalculateBase.getConsumeCalorie(DB_User.getWeight(), MET_WOMAN);
            double daily = Weight.getCalorieConsumeDaily(DB_User.getWeight(), DB_User.getGoalWeight(), DB_User.getDietPeriod());

            Log.i(tag, "metablic : " + metabolic + " daily : " + daily + " gender : " + DB_User.getGender());

            cal = (int) (metabolic + daily);
        }

        return cal;
    }

    @Override
    public void run(Intent intent) {
        String action = intent.getAction();

        if (action.equals(Action.Am.ACTION_MW_STEP_CALORIE)) {
            double[] data = intent.getDoubleArrayExtra(Action.EXTRA_NAME_DOUBLE_ARRAY);
            m_pie_data = data;

            int cal = getTodayCalorie();

            m_txt_calorie.setText(String.valueOf(cal));
            setViewer(cal, data[0], data[1], data[2], data[3]);
            setData();
        }
    }
}
