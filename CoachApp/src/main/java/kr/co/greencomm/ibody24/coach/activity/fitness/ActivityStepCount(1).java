package kr.co.greencomm.ibody24.coach.activity.fitness;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.data.StepInfo;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver.Action;
import kr.co.greencomm.middleware.wrapper.MWControlCenter;

/**
 * Created by jeyang on 16. 9. 21..
 */
public class ActivityStepCount extends CoachBaseActivity implements OnChartValueSelectedListener {
    private static final String tag = ActivityStepCount.class.getSimpleName();

    public static ActivityStepCount instance;

    private BarChart m_chart;
    private TextView m_txt_comment, m_txt_remain, m_txt_step;
    private TextView m_txt_calculateCal;    // hyeonjun
    private TextView m_txt_selected_date, m_txt_selected_step;

    private StepInfo m_datas = new StepInfo();

    private int Reference_Step = 10000;
    private int m_current_step = 0;
    private String m_current_txt_step = "";
    private String m_calculate_txt_cal = "";    // hyeonjun

    //규창 16.01.02 사용자의 현재 체중을 저장하는 변수
    private float m_current_Weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coachplus_stepcount);
        setApplicationStatus(ApplicationStatus.StepCountScreen);

        instance = this;

        m_chart = (BarChart) findViewById(R.id.stepcount_chart);
        m_txt_comment = (TextView) findViewById(R.id.stepcount_txt_comment);
        m_txt_remain = (TextView) findViewById(R.id.stepcount_txt_remain);
        m_txt_step = (TextView) findViewById(R.id.stepcount_txt_step);
        m_txt_selected_date = (TextView) findViewById(R.id.stepcount_txt_selected_date);
        m_txt_selected_step = (TextView) findViewById(R.id.stepcount_txt_selected_step);
        m_txt_calculateCal = (TextView)findViewById(R.id.stepcount_txt_calculateCal);  // hyeonjun

        m_datas.runQuery(QueryCode.ListStep, this);

        setChartProperties();
        setData();

        int main_Step = Preference.getMainStep(this);

        //규창 16.01.02 사용자 프로필의 체중이 없을 경우 0 처리
        if (Preference.getWeight(this) > 0) {
            m_current_Weight = Preference.getWeight(this);
        }else {
            m_current_Weight = 0;
        }
        setView(main_Step);
        //SendReceive.getStepNCalorie(this);
    }

    private void setView(int step) {
        setStep(step);
        setComment();
    }

    private String getCommonComment() {
        Random rnd = new Random();
        String comment = "";
        switch (rnd.nextInt(4)) {
            case 0:
                comment = getString(R.string.Step_Extra_1);
                break;
            case 1:
                comment = getString(R.string.Step_Extra_2);
                break;
            case 2:
                comment = getString(R.string.Step_Extra_3);
                break;
            case 3:
                comment = getString(R.string.Step_Extra_4);
                break;
        }

        return comment;
    }

    private void setComment() {
        if (m_current_step >= Reference_Step) {
            m_current_txt_step = getString(R.string.complete_step);
            if (Build.VERSION.SDK_INT >= 23) {
                m_txt_comment.setTextColor(getResources().getColor(R.color.colorStepComplete, null));
            } else {
                m_txt_comment.setTextColor(getResources().getColor(R.color.colorStepComplete));
            }
            m_txt_comment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        } else {
            String comment = "";
            Random rnd = new Random();
            if (m_current_step > 9000) {
                int n = rnd.nextInt(4);
                switch (n) {
                    case 0:
                        comment = getString(R.string.Step_9001_9999_1);
                        break;
                    case 1:
                        comment = getString(R.string.Step_9001_9999_2);
                        break;
                    case 2:
                        comment = getString(R.string.Step_9001_9999_3);
                        break;
                    case 3:
                        comment = getCommonComment();
                        break;
                }
            } else if (m_current_step > 8000) {
                comment = getString(R.string.Step_8001_9000_1);
            } else if (m_current_step > 7000) {
                comment = getString(R.string.Step_7001_8000_1);
            } else if (m_current_step > 5000) {
                comment = getString(R.string.Step_5001_7000_1);
            } else if (m_current_step > 3000) {
                comment = getString(R.string.Step_3001_5000_1);
            } else if (m_current_step > 2000) {
                int n = rnd.nextInt(3);
                switch (n) {
                    case 0:
                        comment = getString(R.string.Step_2001_3000_1);
                        break;
                    case 1:
                        comment = getString(R.string.Step_2001_3000_1);
                        break;
                    case 2:
                        comment = getCommonComment();
                        break;
                }
            } else {
                int n = rnd.nextInt(5);
                switch (n) {
                    case 0:
                        comment = getString(R.string.Step_0_2000_1);
                        break;
                    case 1:
                        comment = getString(R.string.Step_0_2000_2);
                        break;
                    case 2:
                        comment = getString(R.string.Step_0_2000_3);
                        break;
                    case 3:
                        comment = getString(R.string.Step_0_2000_4);
                        break;
                    case 4:
                        comment = getCommonComment();
                        break;
                }
            }

            m_current_txt_step = comment;
            m_txt_comment.setTextColor(Color.BLACK);
            m_txt_comment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }

        m_txt_comment.setText(m_current_txt_step);
    }

    private void setStep(int step) {
        m_current_step = step;
        int stepRemain;
        if (step > Reference_Step) {
            m_current_txt_step = String.format(Locale.getDefault(), "+ %d", step - Reference_Step);
            stepRemain = step - Reference_Step;
            m_txt_remain.setVisibility(View.GONE);
        } else {
            m_current_txt_step = String.valueOf(Math.abs(Reference_Step - step));
            stepRemain = Math.abs(Reference_Step - step);
            m_txt_remain.setVisibility(View.VISIBLE);
        }

        m_txt_step.setText(m_current_txt_step);
        // hyeonjun 기대 소모 칼로리
        m_txt_calculateCal.setText(calcluateCal(stepRemain));
    }
    
    // hyeonjun 기대 소모 칼로리 계산 메소드
    private String calcluateCal(int step) {
        int result = (int) (2.0*m_current_Weight*(step/83)*0.0175);

        return String.format("%d", result);
    }

    private void setChartProperties() {
        m_chart.setDrawBarShadow(false);
        m_chart.setDrawBorders(true);
        m_chart.setDrawValueAboveBar(true);
        m_chart.setDescription("");
        m_chart.setMaxVisibleValueCount(60);
        m_chart.setDrawGridBackground(false);
        m_chart.setPinchZoom(false);
        m_chart.setDoubleTapToZoomEnabled(false);
        m_chart.setScaleEnabled(false);
        m_chart.setScaleXEnabled(true);
        m_chart.setDrawMarkerViews(false);
        m_chart.setOnChartValueSelectedListener(this);

        XAxis xaxis = m_chart.getXAxis();
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xaxis.labelFont = UIFont.systemFont(ofSize: 11);
        xaxis.setSpaceBetweenLabels(5);
        xaxis.setDrawAxisLine(false);
        xaxis.setDrawGridLines(false);
        xaxis.setGridLineWidth(0.5f);
        xaxis.setDrawLabels(false);

        YAxis la = m_chart.getAxisLeft();
        la.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(" %d", (int) value);
            }
        });
        la.setDrawGridLines(true);
        la.setDrawAxisLine(true);
        la.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        la.setLabelCount(11, false);
        la.setStartAtZero(true);
        la.setAxisMinValue(0.0f);

        YAxis ra = m_chart.getAxisRight();
        ra.setEnabled(false);

        Legend legend = m_chart.getLegend();
        legend.setEnabled(false);
    }

    public void setData() {
//        DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd");

        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = 0; i < m_datas.m_stepArray.size(); i++) {
            Calendar cal = new GregorianCalendar();
            cal.add(Calendar.DATE, -6);
            cal.add(Calendar.DATE, i);
            String str = fmt.format(cal.getTime());
            xVals.add(str);

            int val = m_datas.m_stepArray.get(i);
            yVals1.add(new BarEntry(val, i));
            Log("Data: [" + i + "] " + val);
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(40f);
        if (Build.VERSION.SDK_INT >= 23) {
            set1.setColor(getResources().getColor(R.color.colorStep, null));
        } else {
            set1.setColor(getResources().getColor(R.color.colorStep));
        }

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setDrawValues(false);
        data.setHighlightEnabled(true);

        m_chart.setData(data);
        m_chart.animateXY(1000, 1000);
    }

    @Override
    public void run(Intent intent) {
        String action = intent.getAction();

        if (action.equals(Action.Am.ACTION_MW_STEP_CALORIE)) {
            //short step = intent.getShortExtra(Action.EXTRA_NAME_SHORT_1, (short) 0);
            setStep();
            /*if (step == Preference.getMainStep(this))
                return;

            long current = System.currentTimeMillis();

            m_datas.setReg(current);
            m_datas.setStep((int) step);

            m_datas.runQuery(QueryCode.InsertStep, this);*/
        }
    }

    public void setStep() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                short step = MWControlCenter.getInstance(getApplicationContext()).getStep();
                int len = m_datas.m_stepArray.size();
                m_datas.m_stepArray.set(len-1, (int) step);
                setData();
                setView(step);
            }
        });
    }

    @Override
    public void onQueryResult(QueryCode queryCode, String request, String result) {
        super.onQueryResult(queryCode, request, result);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        int index = h.getXIndex();
//        DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat. , Locale.getDefault());
        DateFormat fmt = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());

        String date = fmt.format(addingDay(new Date(), index - 6));
        m_txt_selected_date.setText(date);
        String stepFormat = getString(R.string.step_format);
        m_txt_selected_step.setText(String.format(Locale.KOREA, stepFormat, m_datas.m_stepArray.get(index)));
    }

    @Override
    public void onNothingSelected() {

    }
}
