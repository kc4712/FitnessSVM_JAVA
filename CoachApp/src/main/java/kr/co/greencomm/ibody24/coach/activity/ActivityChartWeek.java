package kr.co.greencomm.ibody24.coach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.data.GraphDatas;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabChart;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;


/**
 * Created by young on 2015-08-31.
 */
public class ActivityChartWeek
        extends CoachBaseActivity
        implements View.OnClickListener {
    private static ActivityChartWeek m_this;

    private BarChart m_chart;
    private TextView m_txt_title;
    private TextView m_txt_comment;
    private Button m_btn_year;

    private int m_mode;
    private GraphDatas m_datas = new GraphDatas();
    private int m_max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chart_week);

        m_chart = (BarChart) findViewById(R.id.chartweek_bar_chart);

        m_txt_title = (TextView) findViewById(R.id.chartweek_txt_title);
        m_txt_comment = (TextView) findViewById(R.id.chartweek_txt_comment);

        m_btn_year = (Button) findViewById(R.id.chartweek_btn_year);
        m_btn_year.setOnClickListener(this);

        Log("Chart Week Start!!");
        m_this = this;

        setChartProperties();
    }

    private void setChartProperties() {
        m_chart.setDrawBarShadow(false);
        m_chart.setDrawValueAboveBar(true);
        m_chart.setDescription("");
        m_chart.setPinchZoom(false);
        m_chart.setDoubleTapToZoomEnabled(false);
        m_chart.setScaleEnabled(false);

        XAxis xaxis = m_chart.getXAxis();
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis la = m_chart.getAxisLeft();
        la.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(" %3.0f%%", value);
            }
        });
        la.setLabelCount(11, false);
        la.setStartAtZero(true);
        la.setAxisMinValue(0.0f);
        la.setAxisMaxValue(100.0f);
        la.setDrawLimitLinesBehindData(true);

        YAxis ra = m_chart.getAxisRight();
        ra.setEnabled(false);

        Legend legend = m_chart.getLegend();
        legend.setEnabled(false);

        m_chart.setDrawMarkerViews(false);
    }

    private void setLimitLine(float witdh) {
        LimitLine line1 = new LimitLine(100);
        line1.setLineWidth(witdh);
        LimitLine line2 = new LimitLine(80);
        line2.setLineWidth(witdh/2);

        YAxis la = m_chart.getAxisLeft();
        la.removeAllLimitLines();
        if (witdh == 0) {
            return;
        }
        la.addLimitLine(line1);
        la.addLimitLine(line2);
    }

    private void setData() {
        SimpleDateFormat fmt = new SimpleDateFormat("M/d");

        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            Calendar cal = new GregorianCalendar();
            cal.add(Calendar.DATE, -6);
            cal.add(Calendar.DATE, i);
            String str = fmt.format(cal.getTime());
            xVals.add(str);

            int val = m_datas.getWeekDatas()[i];
//            if (m_mode == 4) { // 소모 칼로리
//                val = m_datas.getWeekDatas()[i] / 100;
//            } else {
//                val = m_datas.getWeekDatas()[i];
//            }

            yVals1.add(new BarEntry(val, i));
            Log("Data: [" + i + "] " + val);
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(40f);

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        // 그래프 위에 값을 표시 하지 않는다.
        data.setDrawValues(false);
        // 그래프 바가 클릭 되지 않게 한다.
        data.setHighlightEnabled(false);

        m_chart.setData(data);
        m_chart.animateXY(1000, 1000);
    }

    public static void onGraphData() {
        if (m_this != null) {
            m_this.onRecvComplete();
        }
    }

    public static void setGraphMode(int mode) {
        if (m_this == null) return;
        m_this.setGraph(mode);
    }

    private void setGraph(int mode) {
        if (m_chart.getBarData() != null) {
            m_chart.clear();
        }

        m_mode = mode;
        switch (mode) {
            case 1:
                m_txt_title.setText(R.string.chart_title_accuracy);
                m_txt_comment.setText(R.string.comment_accuracy);
                setLimitLine(0);
                break;
            case 2:
                m_txt_title.setText(R.string.chart_title_maximum_heartrate);
                m_txt_comment.setText(R.string.comment_maximum_heartrate);
                setLimitLine(4);
                break;
            case 3:
                m_txt_title.setText(R.string.chart_title_exercise_number);
                m_txt_comment.setText(R.string.comment_exercise_number);
                setLimitLine(0);
                break;
            case 4:
                m_txt_title.setText(R.string.chart_title_consume_calorie);
                m_txt_comment.setText(R.string.comment_consume_calorie);
                setLimitLine(0);
                break;
            case 5:
                m_txt_title.setText(R.string.total_point);
                m_txt_comment.setText(R.string.comment_total_point);
                setLimitLine(0);
                break;
        }
        m_datas.runUserQuery(QueryCode.WeekData, DB_User.getCode(), mode, this);
    }

    private void onRecvComplete() {
        Log("주간 데이터 획득 완료 !!!");
        m_max = 0;
        int len = m_datas.getWeekDatas().length;
        for (int i = 0; i < len; i++) {
            int dat = m_datas.getWeekDatas()[i];
            if (dat > m_max) m_max = dat;
        }
        YAxis la = m_chart.getAxisLeft();
        if (m_mode == 4) {
            int cal = ((m_max / 100) + 1) * 100;
            Log("max: " + m_max + ", set: " + cal);
            la.setAxisMaxValue((float) cal);
            la.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format(" %5.0fkcal", value);
                }
            });
        } else {
            la.setAxisMaxValue(100f);
            la.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%4.0f%%", value);
                }
            });
        }
        setData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chartweek_btn_year) {
            ActivityTabChart.selectChart(2, m_mode);
        }
    }

    @Override
    public void run(Intent intent) {

    }
}
