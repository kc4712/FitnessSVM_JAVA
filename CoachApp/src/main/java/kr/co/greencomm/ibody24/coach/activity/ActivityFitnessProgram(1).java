package kr.co.greencomm.ibody24.coach.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.adapter.TrainerAdapter;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.data.TrainerInfo;
import kr.co.greencomm.ibody24.coach.utils.TrainerCode;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;


/**
 * Created by young on 2015-08-24.
 */
public class ActivityFitnessProgram
        extends CoachBaseActivity
        implements AdapterView.OnItemClickListener {
    private ArrayList<TrainerInfo> m_array = new ArrayList<>();
    private ListView m_listView;
    private TrainerAdapter m_adapter;
    //private int m_select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentScale1440(R.layout.activity_coach_basic, R.id.screen_layout_main);
        setContentView(R.layout.activity_fitness_program);

        m_array.add(new TrainerInfo(3, R.drawable.trainer_003, R.string.trainer_name_3, R.string.trainer_title_3, R.string.trainer_spec_3));
        m_array.add(new TrainerInfo(1, R.drawable.trainer_001, R.string.trainer_name_1, R.string.trainer_title_1, R.string.trainer_spec_1));
        m_array.add(new TrainerInfo(2, R.drawable.trainer_002, R.string.trainer_name_2, R.string.trainer_title_1, R.string.trainer_spec_2));

        m_adapter = new TrainerAdapter(this);
        m_adapter.bindData(m_array);

        m_listView = (ListView) findViewById(R.id.fitness_program_item_list);
        m_listView.setOnItemClickListener(this);
        m_listView.setAdapter(m_adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log("onDstory()");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TrainerInfo sel = m_array.get(position);
        //m_select = position;
        Log("선택: " + position + ", NO: " + sel.recNo);
//        ActivityTabHome.startTrainer(this, sel.recNo);
        switch (position) {
            case 0:
                trainer = TrainerCode.Kwon_Do_Ye;
                break;
            case 1:
                trainer = TrainerCode.Lee_Joo_young;
                break;
            case 2:
                trainer = TrainerCode.Hong_Doo_Han;
                break;
        }
        ActivityTabHome.pushActivity(this, ActivityTrainer.class);
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
