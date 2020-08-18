package kr.co.greencomm.ibody24.coach.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.utils.Weight;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.widget.PeriodGraph;
import kr.co.greencomm.ibody24.coach.widget.WeightGraph;


/**
 * Created by young on 2015-08-20.
 */
public class ActivitySettingWeight
        extends CoachBaseActivity
        implements View.OnClickListener {
    private WeightGraph m_currentWeightGraph;
    private WeightGraph m_targetWeightGraph;
    private PeriodGraph m_periodGraph;

    private EditText m_edit_currentWeight;
    private EditText m_edit_targetWeight;
    private EditText m_edit_dietPeriod;
    private EditText m_edit_dayCalorie;

    private TextView m_txt_weightComment;
    private TextView m_txt_periodComment;

    private Button m_btn_next;

    private int m_valueCurrent;
    private int m_valueTarget;
    private int m_valuePeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentScale1440(R.layout.activity_target_weight, R.id.screen_layout_main);
        setContentView(R.layout.activity_target_weight);

        setApplicationStatus(ApplicationStatus.TargetWeightScreen);

        m_currentWeightGraph = (WeightGraph) findViewById(R.id.setting_weight_current_weight_graph);
        m_targetWeightGraph = (WeightGraph) findViewById(R.id.setting_weight_target_weight_graph);
        m_periodGraph = (PeriodGraph) findViewById(R.id.setting_weight_target_dietPeriod);

        m_edit_currentWeight = (EditText) findViewById(R.id.setting_weight_current_weight);
        m_edit_targetWeight = (EditText) findViewById(R.id.setting_weight_target_weight);
        m_edit_dietPeriod = (EditText) findViewById(R.id.setting_weight_diet_period);
        m_edit_dayCalorie = (EditText) findViewById(R.id.setting_weight_day_calorie);

        m_txt_weightComment = (TextView) findViewById(R.id.setting_weight_weight_comment);
        m_txt_periodComment = (TextView) findViewById(R.id.setting_weight_period_comment);

        m_btn_next = (Button) findViewById(R.id.setting_weight_btn_next);
        m_btn_next.setOnClickListener(this);

        // 키를 설정
        int height = DB_User.getHeight();
        m_valueCurrent = (int) DB_User.getWeight();
        m_valueTarget = (int) DB_User.getGoalWeight();
        m_valuePeriod = DB_User.getDietPeriod();

        m_currentWeightGraph.setRange(height);
        m_targetWeightGraph.setRange(height);

        m_currentWeightGraph.setValue(m_valueCurrent);
        m_targetWeightGraph.setValue(m_valueTarget);

        m_periodGraph.setRange(m_valueCurrent, m_valueTarget);
        m_periodGraph.setValue(m_valuePeriod);

        m_edit_currentWeight.setText(m_valueCurrent == 0 ? "" : String.valueOf(m_valueCurrent));
        m_edit_targetWeight.setText(m_valueTarget == 0 ? "" : String.valueOf(m_valueTarget));
        m_edit_dietPeriod.setText(m_valuePeriod == 0 ? "" : String.valueOf(m_valuePeriod));

        m_edit_currentWeight.addTextChangedListener(watcherCurrentWeight);
        m_edit_targetWeight.addTextChangedListener(watcherTargetWeight);
        m_edit_dietPeriod.addTextChangedListener(watcherDietPeriod);

        String msg1 = String.format(getString(R.string.BMI_WHO), m_currentWeightGraph.m_minValue, m_currentWeightGraph.m_maxValue);
        m_txt_weightComment.setText(getHtml(msg1));

        updateGraph();
        /*
        <font color="#777777">WHO BMI 공식에 따르면 당신의 키를 기준으로한  적정 몸무게는</font>
	    <font color="#45c383">%1$d ~ %2$d kg </font>
	    <font color="#777777">입니다.</font>

        int start = (int)((double)(weight - goalWeight) / 0.3D * 4.0D);
        int enough = (int)((double)(weight - goalWeight) / 0.5D * 4.0D);
        int normal = (int)((double)(weight - goalWeight) / 1.2D * 4.0D);
        return (float)((int)((weight - goalWeight) * 1000.0F * 7.7F / (float)(dietPeriod * 7)));
        */
        if (m_popup_mode) {
            m_btn_next.setText(R.string.setting);
        }
    }

    private TextWatcher watcherCurrentWeight = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                m_valueCurrent = Integer.parseInt(s.toString());
                if (DB_User.getWeight() != m_valueCurrent) {
                    updateCurrentGraph();
                    updatePeriodGraph();
                }
            } catch (Exception e) {
            }
        }
    };

    private TextWatcher watcherTargetWeight = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                m_valueTarget = Integer.parseInt(s.toString());
                if (DB_User.getGoalWeight() != m_valueTarget) {
                    updateTargetGraph();
                    updatePeriodGraph();
                }
            } catch (Exception e) {
            }
        }
    };

    private TextWatcher watcherDietPeriod = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                m_valuePeriod = Integer.parseInt(s.toString());
                if (DB_User.getDietPeriod() != m_valuePeriod) {
                    updatePeriodGraph();
                }
            } catch (Exception e) {
            }
        }
    };

    private void updateCurrentGraph() {
        //weight
        if (m_valueCurrent < Weight.Minimum || m_valueCurrent > Weight.Maximum)
            m_valueCurrent = Weight.Default;
        if (m_valueTarget > m_valueCurrent)
            m_valueTarget = m_valueCurrent;
        if (m_valueTarget < Weight.Minimum)
            m_valueTarget = Weight.Default < m_valueCurrent ? Weight.Default : m_valueCurrent;
        m_currentWeightGraph.setValue(Math.round(m_valueCurrent));
    }

    private void updateTargetGraph() {
        if (m_valueTarget > m_valueCurrent)
            m_valueTarget = m_valueCurrent;
        if (m_valueTarget < Weight.Minimum)
            m_valueTarget = Weight.Default < m_valueCurrent ? Weight.Default : m_valueCurrent;
        m_targetWeightGraph.setValue(Math.round(m_valueTarget));
    }

    private void updatePeriodGraph() {
        if (m_valuePeriod < 1)
            m_valuePeriod = 1;
        if (m_valuePeriod > 199)
            m_valuePeriod = 199;
        m_periodGraph.setRange(m_valueCurrent, m_valueTarget);
        m_periodGraph.setValue(m_valuePeriod);
        int cal = (int) m_currentWeightGraph.getCalorieConsumeDaily(m_valueCurrent, m_valueTarget, m_valuePeriod);
        if (cal > 0) {
            String msg1 = String.format(getString(R.string.recommend_calorie_comment), cal);
            m_txt_periodComment.setText(getHtml(msg1));
            m_edit_dayCalorie.setText(String.valueOf(cal));
        }
    }

    private void updateGraph() {
        if (m_valueCurrent != 0 && m_valueTarget != 0 && m_valuePeriod != 0) {
            updateCurrentGraph();
            updateTargetGraph();
            updatePeriodGraph();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.setting_weight_btn_next) {
            if (m_valuePeriod <= 0) {
                displayMsg(R.string.caution_diet_period);
            } else if (m_valueTarget > m_valueCurrent) {
                displayMsg(R.string.caution_weight);
            } else {
                DB_User.setWeight(m_valueCurrent);
                DB_User.setGoalWeight(m_valueTarget);
                DB_User.setDietPeriod(m_valuePeriod);
                DB_User.runQuery(QueryCode.SetTarget, this);
            }
        }
    }

    @Override
    public void run(Intent intent) {

    }
}
