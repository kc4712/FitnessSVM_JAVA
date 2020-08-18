package kr.co.greencomm.ibody24.coach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.data.UserRecord;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabChart;
import kr.co.greencomm.middleware.video.CalculateBase;


/**
 * Created by young on 2015-08-27.
 */
public class ActivityChartMain
        extends CoachBaseActivity implements View.OnClickListener {

    private static ActivityChartMain m_this;

    //private BarChart m_chart;
    private RadarChart m_chart;

    private Button m_btn_acc;
    private Button m_btn_hrt;
    private Button m_btn_cnt;
    private Button m_btn_cal;
    private Button m_btn_pts;

    private TextView m_txt_pts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chart);

        //m_chart = (BarChart) findViewById(R.id.bar_chart);
        m_chart = (RadarChart) findViewById(R.id.chartmain_chart_view);

        m_btn_acc = (Button) findViewById(R.id.chartmain_btn_acc);
        m_btn_hrt = (Button) findViewById(R.id.chartmain_btn_hrt);
        m_btn_cnt = (Button) findViewById(R.id.chartmain_btn_cnt);
        m_btn_cal = (Button) findViewById(R.id.chartmain_btn_cal);
        m_btn_pts = (Button) findViewById(R.id.chartmain_btn_pts);

        m_txt_pts = (TextView) findViewById(R.id.chartmain_txt_pts);

        m_btn_acc.setOnClickListener(this);
        m_btn_hrt.setOnClickListener(this);
        m_btn_cnt.setOnClickListener(this);
        m_btn_cal.setOnClickListener(this);
        m_btn_pts.setOnClickListener(this);

        Log("Chart Main Start!!");
        //ActivityTabChart.Main = this;
        m_this = this;

        m_xDatas = new String[]{getString(R.string.chart_title_accuracy), getString(R.string.chart_title_exercise_number),
                getString(R.string.chart_title_consume_calorie), getString(R.string.chart_title_maximum_heartrate)};

        setChartProperties();
        setGraphData();

        m_txt_pts.setText(String.valueOf(DB_Today.Week.getPoint()) + "pts");
    }

    @Override
    public void onShow() {
        Log.d("jeyang", "onshow");
        m_chart.clear();
        setGraphData();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //if (ActivityTabChart.m_data_complete == false) return;
        ActivityMain act = (ActivityMain) this.getParent();
        act.switchHome();
    }

    public static void onGraphData() {
        if (m_this != null) {
            m_this.onRecvComplete();
        }
    }

    private void onRecvComplete() {
        Log("거미줄 챠트 데이터 갱신 !!");
        setGraphData();
        m_txt_pts.setText(String.valueOf(DB_Today.Week.getPoint()) + "pts");
        // 데이터 로딩 시작
        //ActivityTabChart.m_data_complete = true;
    }

    private void setChartProperties() {
        m_chart.setRotationEnabled(false);
        m_chart.setHighlightEnabled(false);

        XAxis xaxis = m_chart.getXAxis();
        //xaxis.setTextSize(1f);
        //xaxis.setDrawLabels(false);
        xaxis.setEnabled(false);

        YAxis yaxis = m_chart.getYAxis();
        yaxis.setDrawLabels(false);
        yaxis.setLabelCount(5, false);
        yaxis.setAxisMaxValue(100f);
        yaxis.setEnabled(false);

        Legend legend = m_chart.getLegend();
        legend.setEnabled(false);

        m_chart.setDescription(null);
        m_chart.setDrawMarkerViews(false);
    }

    protected String[] m_xDatas;

    public void setGraphData() {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < m_xDatas.length; i++) {
            xVals.add(m_xDatas[i]);
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        // 정확도
        int dat0 = DB_Today.Week.getAccuracy();
        // 운동횟수
        int dat1 = DB_Today.Week.getTotalCount() == 0 ? 0 : DB_Today.Week.getMatchCount() * 100 / DB_Today.Week.getTotalCount();
        // 소모칼로리
        //int dat2 = getDailyConsumeCalorie() == 0 ? 0 : DB_Today.Week.getCalorie() * 100 / getDailyConsumeCalorie();
        //규창 16.12.10 iOS와 같은 계산식을 쓰도록 임시 커밋
        //규창 17.05.30 BaseActivity의 UserRecord사용
        final double dailyCal = getDailyConsumeCalorie(DB_User.getWeight(),DB_User.getGoalWeight(), DB_User.getDietPeriod());
        double dat2 = dailyCal == 0.0 ? 0 : DB_Today.Week.getCalorie() * 100 / dailyCal;

        // 최대심박수
        int dat3 = CalculateBase.getHeartRateCompared(DB_Today.Week.getHeartRateAvg(), DB_Today.Week.getHeartRateMax(), DB_User.getAge());
//        Log.d(tag,)

        if (dat0 > 100) dat0 = 100;
        if (dat1 > 100) dat1 = 100;
        if (dat2 > 100) dat2 = 100;
        if (dat3 > 100) dat3 = 100;
        if (dat3 < 0) dat3 = 0;

        yVals.add(new Entry(dat0, 0));
        yVals.add(new Entry(dat1, 1));
        yVals.add(new Entry((float)dat2, 2));
        yVals.add(new Entry(dat3, 3));

        RadarDataSet set1 = new RadarDataSet(yVals, null);
        set1.setLineWidth(2f);
        set1.setDrawValues(false);

        ArrayList<RadarDataSet> dataSets = new ArrayList<RadarDataSet>();
        dataSets.add(set1);

        RadarData data = new RadarData(xVals, dataSets);
        data.setValueTextSize(10f);
        m_chart.setData(data);
        m_chart.animateXY(1000, 1000);
    }

    @Override
    public void onClick(View v) {
        // 그래프 데이터가 로딩 되지 않았으면 다음으로 진행 불가
        //if (ActivityTabChart.m_data_complete == false) return;
        switch (v.getId()) {
            case R.id.chartmain_btn_acc:
                ActivityTabChart.selectChart(1, 1);
                break;
            case R.id.chartmain_btn_hrt:
                ActivityTabChart.selectChart(1, 2);
                break;
            case R.id.chartmain_btn_cnt:
                ActivityTabChart.selectChart(1, 3);
                break;
            case R.id.chartmain_btn_cal:
                ActivityTabChart.selectChart(1, 4);
                break;
            case R.id.chartmain_btn_pts:
                ActivityTabChart.selectChart(1, 5);
                break;
        }
    }

    @Override
    public void run(Intent intent) {

    }
}
