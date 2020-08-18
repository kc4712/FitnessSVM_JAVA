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
 * Created by young on 2015-08-26.
 */
public class PeriodGraph
    extends LinearLayout
{
    private static final String TAG = "PeriodGraph";

    private final int LEFT_ROUND = 30;
    private final int RIGHT_ROUND = 34;

    //private Context mContext;

    private TextView m_minText;
    private TextView m_maxText;

    private ImageView m_backImage;
    private ImageView m_graphImage;

    private int m_minValue;
    private int m_maxValue;

    private int m_lowValue;
    private int m_highValue;
    private int m_rangeValue;

    private int m_length = 0;
    private int m_value = 0;
    //타겟 웨이트 "주"를 일본어로 만들기위한 string//
    public String week = "";
    ///까지


    public PeriodGraph(Context context, AttributeSet attrs) {
        super(context, attrs);

        //mContext = context;

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_period_graph, this, true);

        m_minText = (TextView) findViewById(R.id.minText);
        m_maxText = (TextView) findViewById(R.id.maxText);

        m_backImage = (ImageView) findViewById(R.id.backImage);
        m_graphImage = (ImageView) findViewById(R.id.graphImage);

        /// 일본어 입히기위한 Context받아서 getString처리///
        week = context.getString(R.string.week);
        ///까지

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

    public void setRange(float current, float target) {
        //int start = (int)((double)(current - target) / 0.3D * 4.0D);
        int enough = (int)((double)(current - target) / 0.5D * 4.0D);
        int normal = (int)((double)(current - target) / 1.2D * 4.0D);

        m_minValue = Math.round(enough);
        m_maxValue = Math.round(normal);

        String str1 = String.valueOf(m_maxValue) + week;
        String str2 = String.valueOf(m_minValue) + week;
        m_minText.setText(str1);
        m_maxText.setText(str2);
        Log.d(TAG, String.format("String: Min=%s, Max=%s", str1, str2));

        int gap = Math.abs(m_maxValue - m_minValue);
        m_lowValue = m_maxValue - gap;
        m_highValue = m_minValue + gap;
        //if (m_lowValue < 0) m_lowValue = 0;
        m_rangeValue = m_highValue - m_lowValue;
        if (m_rangeValue < 1) m_rangeValue = 1;
        Log.d(TAG, "========= Min: " + m_minValue + ", Max: " + m_maxValue);
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

        Log.d(TAG, "Low: " + m_lowValue + ", High: " + m_highValue + ", Percent: " + pst);

        //pst += 0.02f;
        if (pst > 1) pst = 1f;
        if (pst < 0) pst = 0f;

        int cal = (int)((m_length - LEFT_ROUND * 2) * pst);
        int pos = cal + LEFT_ROUND + RIGHT_ROUND;
        if (pos < LEFT_ROUND + RIGHT_ROUND){
            pos = LEFT_ROUND + RIGHT_ROUND;
        }

        Log.d(TAG, "value = " + m_value + ", Pos: " + pos);

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
