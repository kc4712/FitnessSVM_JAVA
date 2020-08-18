package kr.co.greencomm.ibody24.coach.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.co.greencomm.ibody24.coach.R;


/**
 * Created by young on 2015-08-20.
 */
public class WeightGraph
    extends LinearLayout
{
    private static final String TAG = "WeightGraph";

    public static final int TYPE_RED = 1;
    public static final int TYPE_GREEN = 2;

    private final int LEFT_ROUND = 30;
    private final int RIGHT_ROUND = 34;

    private Context mContext;

    private TextView m_minText;
    private TextView m_maxText;

    private ImageView m_backImage;
    private ImageView m_graphImage;

    public int m_minValue;
    public int m_maxValue;

    private int m_lowValue;
    private int m_highValue;
    private int m_rangeValue;

    private int m_length = 0;
    private int m_value = 0;

    public WeightGraph(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_weight_graph, this, true);

        m_minText = (TextView) findViewById(R.id.minText);
        m_maxText = (TextView) findViewById(R.id.maxText);

        m_backImage = (ImageView) findViewById(R.id.backImage);
        m_graphImage = (ImageView) findViewById(R.id.graphImage);

        m_graphImage.setLayoutParams(new RelativeLayout.LayoutParams(600, ViewGroup.LayoutParams.WRAP_CONTENT));

        m_backImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                m_backImage.getViewTreeObserver().removeOnPreDrawListener(this);
                m_length = m_backImage.getWidth();
                Log.d("Widget", "Get Len: " + m_length);
                updateGraph();
                return true;
            }
        });
    }

    public void setRange(int height) {
        // 한국인 BMI 22.6 ~ 27.5 사망위험 가장 낮다...
        final float BMI_LOW_WEIGHT = 18.5F;
        final float BMI_HIGH_WEIGHT = 24.9F;
        float rate = height * height / 10000F;
        float lowWeight = BMI_LOW_WEIGHT * rate;
        float highWeight = BMI_HIGH_WEIGHT * rate;
        m_minValue = Math.round(lowWeight);
        m_maxValue = Math.round(highWeight);
        m_minText.setText(m_minValue + "kg");
        m_maxText.setText(m_maxValue + "kg");
        int gap = m_maxValue - m_minValue;
        m_lowValue = m_minValue - gap;
        m_highValue = m_maxValue + gap;
        //if (m_lowValue < 0) m_lowValue = 0;
        m_rangeValue = m_highValue - m_lowValue;
        if (m_rangeValue < 1) m_rangeValue = 1;
    }

    public float getCalorieConsumeDaily(float current, float target, int period) {
        return (float)((int)((current - target) * 1000.0F * 7.7F / (float)(period * 7)));
    }

    public void setValue(int value) {
        m_value = value;
        updateGraph();
    }

    private void updateGraph() {
        if (m_value == 0 || m_length == 0) {
            return;
        }
        float pst = (m_value - m_lowValue) / (float)m_rangeValue;

        if (pst > 1) pst = 1f;
        if (pst < 0) pst = 0f;

        Log.d(TAG, String.format("Length: %d, Low: %d, High: %d, Percent: %f", m_length, m_lowValue, m_highValue, pst));

        int cal = (int)((m_length - LEFT_ROUND * 2) * pst);
        int pos = cal + LEFT_ROUND + RIGHT_ROUND;
        if (pos < LEFT_ROUND + RIGHT_ROUND){
            pos = LEFT_ROUND + RIGHT_ROUND;
        }

        Log.d(TAG, String.format("value = %d, Cal = %d, Pos = %d", m_value, cal, pos));

        if (m_value >= m_minValue && m_value <= m_maxValue) {
            m_graphImage.setBackgroundResource(R.drawable.widget_weight_green);
        }
        else {
            m_graphImage.setBackgroundResource(R.drawable.widget_weight_red);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pos, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        m_graphImage.setLayoutParams(params);
    }
}
