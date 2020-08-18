package kr.co.greencomm.ibody24.coach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.adapter.ImageAdapter;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;


/**
 * Created by young on 2015-08-25.
 */
public class ActivityTrainer
    extends CoachBaseActivity
    implements View.OnClickListener
{

    private AdapterViewFlipper m_view;
    private ImageAdapter m_adapter;
    private ArrayList<Integer> m_list;
    private Button m_btn_program;
//    private int m_trainerCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentScale1440(R.layout.activity_coach_basic, R.id.screen_layout_main);
        setContentView(R.layout.activity_trainer);

        m_view = (AdapterViewFlipper) findViewById(R.id.trainer_photoView);
        m_adapter = new ImageAdapter(this);
        m_view.setAdapter(m_adapter);
        m_list = new ArrayList<>();

        m_btn_program = (Button) findViewById(R.id.trainer_btn_program);
        m_btn_program.setOnClickListener(this);

        TextView m_part = (TextView) findViewById(R.id.trainer_partText);
        TextView m_name = (TextView) findViewById(R.id.trainer_nameText);
        TextView m_comment = (TextView) findViewById(R.id.trainer_trainerComment);
        TextView m_history = (TextView) findViewById(R.id.trainer_trainerHistory);
        TextView m_title = (TextView) findViewById(R.id.trainer_titleText);

        Intent intent = getIntent();
//        m_trainerCode = intent.getExtras().getInt("TrainerCode", 0);

        switch (trainer) {
            case Lee_Joo_young:
                m_name.setText(R.string.trainer_name_1);
                m_part.setText(R.string.trainer_spec_1);
                m_title.setText(R.string.trainer_title_1);
                m_list.clear();
                m_list.add(R.drawable.trainer_01_photo_01);
                m_list.add(R.drawable.trainer_01_photo_02);
                m_adapter.bindData(m_list);
                m_comment.setText(R.string.trainer_comment_1);
                m_history.setText(R.string.trainer_career_1);
                break;
            case Hong_Doo_Han:
                m_name.setText(R.string.trainer_name_2);
                m_part.setText(R.string.trainer_spec_2);
                m_title.setText(R.string.trainer_title_1);
                m_list.clear();
                m_list.add(R.drawable.trainer_02_photo_01);
                m_list.add(R.drawable.trainer_02_photo_02);
                m_list.add(R.drawable.trainer_02_photo_03);
                m_adapter.bindData(m_list);
                m_comment.setText(R.string.trainer_comment_2);
                m_history.setText(R.string.trainer_career_2);
                break;

            case Kwon_Do_Ye:
                m_name.setText(R.string.trainer_name_3);
                m_part.setText(R.string.trainer_spec_3);
                m_title.setText(R.string.trainer_title_3);
                m_list.clear();
                m_list.add(R.drawable.trainer_03_photo_01);
                m_list.add(R.drawable.trainer_03_photo_02);
                m_list.add(R.drawable.trainer_03_photo_03);
                m_adapter.bindData(m_list);
                m_comment.setText(R.string.trainer_comment_3);
                m_history.setText(R.string.trainer_career_3);
                break;

        }
        m_view.startFlipping();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trainer_btn_program:
                ActivityTabHome.pushActivity(this, ActivityTrainerProgram.class);
//                ActivityTabHome.startTrainerProgram(this, m_trainerCode);
                break;
        }
    }

    /*
    텍스트 맞추는 코드
    ------------------------------------------
    http://codedb.tistory.com/entry/Android-TextView-%EB%8B%A8%EC%96%B4%EB%8B%A8%EC%9C%84%EB%A1%9C-%EA%B0%9C%ED%96%89%ED%95%98%EA%B8%B0
     */

    @Override
    public void run(Intent intent) {

    }
}
