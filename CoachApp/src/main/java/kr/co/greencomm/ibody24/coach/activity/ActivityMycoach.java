package kr.co.greencomm.ibody24.coach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.provider.CoachResolver;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.ibody24.coach.utils.Weight;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.utils.container.ActivityData;

/**
 * Created by jeyang on 16. 9. 23..
 */
public class ActivityMycoach extends CoachBaseActivity {
    private static final String tag = ActivityMycoach.class.getSimpleName();

    private ImageView m_img_cup, m_img_back;
    private TextView m_txt_percent, m_txt_calorie;

    private int m_today_calorie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coach_mycoach);
        setApplicationStatus(ApplicationStatus.MycoachScreen);

        m_img_cup = (ImageView) findViewById(R.id.mycoach_img_cup);
        m_img_back = (ImageView) findViewById(R.id.mycoach_img_back);
        m_txt_percent = (TextView) findViewById(R.id.mycoach_txt_percent);
        m_txt_calorie = (TextView) findViewById(R.id.mycoach_txt_calorie);

        m_today_calorie = Preference.getTodayCalorie(this);

        m_img_cup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                m_img_cup.getViewTreeObserver().removeOnPreDrawListener(this);
                setPercent(getPercent());
                return true;
            }
        });

//        setPercent(50);
    }

    @Override
    public void onShow() {
        m_today_calorie = Preference.getTodayCalorie(this);
    }

    private void setPercent(int percent) {
        int height = m_img_cup.getHeight();
        float surplus = height/305.0f * 14;
        float calc_height = (height - surplus*2) * (float) percent / 100;
        float margin_top = height - calc_height - surplus;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) m_img_back.getLayoutParams();
        params.height = (int) calc_height;
        params.topMargin = (int) margin_top;
        m_img_back.setLayoutParams(params);

        m_txt_percent.setText(String.valueOf(percent));
    }

    private int getPercent() {
        // SQL에서 데이터를 읽어서, 이곳에 넣어준다.
        int today = 0;
        int sum = 0;

        switch (ActivityTabHome.m_app_use_product) {
            case Fitness:
                sum = sumCalorieFromProvider();
                today = m_today_calorie;
                break;
            case Coach:
                float current = DB_User.getWeight();
                float target = DB_User.getGoalWeight();
                int period = DB_User.getDietPeriod();
                today = (int) Weight.getCalorieConsumeDaily(current, target, period);
                sum = DB_Today.getCalorie();
                break;
        }

        m_txt_calorie.setText(String.valueOf(sum));
        Double percent = ((double) sum / today * 100);
        if (percent.isInfinite() || percent.isNaN()) {
            percent = 0.0;
        }

        if (percent > 100.0) {
            percent = 100.0;
        }

        return percent.intValue();
    }

    private int sumCalorieFromProvider() {
        CoachResolver res = new CoachResolver();
        ArrayList<ActivityData> datas =  res.getActivityDataArray();
        if (datas == null)
            return 0;

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int sum = 0;

        for (ActivityData data : datas) {
            cal.setTimeInMillis(data.getStart_time());
            int data_year = cal.get(Calendar.YEAR);
            int data_month = cal.get(Calendar.MONTH);
            int data_day = cal.get(Calendar.DAY_OF_MONTH);

            if (day != data_day || month != data_month || year != data_year) {
                continue;
            }

            sum += data.getAct_calorie();
        }

        return sum;
    }

    @Override
    public void run(Intent intent) {

    }
}
