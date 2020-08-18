package kr.co.greencomm.ibody24.coach.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.adapter.ProgramAdapter;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;


/**
 * Created by young on 2015-08-25.
 */
public class ActivityTrainerProgram
        extends CoachBaseActivity
        implements AdapterView.OnItemClickListener {
    private ArrayList<String> m_array;
    private ListView m_listView;
    private ProgramAdapter m_adapter;
    private int m_select;
//    private int m_trainerCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentScale1440(R.layout.activity_coach_basic, R.id.screen_layout_main);
        setContentView(R.layout.activity_trainer_program);

        Intent intent = getIntent();
//        m_trainerCode = intent.getExtras().getInt("TrainerCode", 0);

        ImageView photoSmall = (ImageView) findViewById(R.id.trainer_program_photoSmall);
        TextView partName = (TextView) findViewById(R.id.trainer_program_partText);
        TextView trainerName = (TextView) findViewById(R.id.trainer_program_nameText);
        TextView titleText = (TextView) findViewById(R.id.trainer_program_titleText);

        m_array = new ArrayList<>();

        switch (trainer) {
            case Lee_Joo_young:
                photoSmall.setBackgroundResource(R.drawable.trainer_01_photo_small);
                partName.setText(R.string.trainer_spec_1);
                trainerName.setText(R.string.trainer_name_1);
                titleText.setText(R.string.trainer_title_1);
                m_array.add(getString(R.string.program_001_001));
                m_array.add(getString(R.string.program_001_002));
                break;

            case Hong_Doo_Han:
                photoSmall.setBackgroundResource(R.drawable.trainer_02_photo_small);
                partName.setText(R.string.trainer_spec_2);
                trainerName.setText(R.string.trainer_name_2);
                titleText.setText(R.string.trainer_title_1);
                m_array.add(getString(R.string.program_002_001));
                m_array.add(getString(R.string.program_002_002));
                break;

            case Kwon_Do_Ye:
                photoSmall.setBackgroundResource(R.drawable.trainer_03_photo_small);
                partName.setText(R.string.trainer_spec_3);
                trainerName.setText(R.string.trainer_name_3);
                titleText.setText(R.string.trainer_title_3);
                m_array.add(getString(R.string.program_003_001));
                m_array.add(getString(R.string.program_003_002));
                break;
        }


        m_adapter = new ProgramAdapter(this);
        m_listView = (ListView) findViewById(R.id.trainer_program_listView);
        m_listView.setOnItemClickListener(this);
        m_listView.setAdapter(m_adapter);
        m_adapter.bindData(m_array);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log("onDstory()");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        m_select = position;
//        int progCode = m_trainerCode * 100 + position + 1;
//        Log("선택: " + position + ", 프로그램: " + progCode);
//        ActivityTabHome.startFitnessProgram(this, progCode);
        program = trainer.getProgramList()[position];
        ActivityTabHome.pushActivity(this, ActivityCourse.class);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                default:
                    break;
            }
        }
    }

    @Override
    public void run(Intent intent) {

    }
}
