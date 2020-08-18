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
import kr.co.greencomm.ibody24.coach.adapter.CoachBasicListAdapter;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.ibody24.coach.utils.ProgramCode;
import kr.co.greencomm.ibody24.coach.data.TitlePhoto;


/**
 * Created by young on 2015-08-21.
 */
public class ActivityCoachBasic
        extends CoachBaseActivity
        implements AdapterView.OnItemClickListener {
    private ArrayList<TitlePhoto> m_array = new ArrayList<>();
    private ListView m_listView;
    private CoachBasicListAdapter m_adapter;
    private int m_select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentScale1440(R.layout.activity_coach_basic, R.id.screen_layout_main);
        setContentView(R.layout.activity_coach_basic);

        m_array.add(new TitlePhoto(R.drawable.coach_basic_icon_01, R.string.coach_basic_title_001));
        m_array.add(new TitlePhoto(R.drawable.coach_basic_icon_02, R.string.coach_basic_title_002));
        m_array.add(new TitlePhoto(R.drawable.coach_basic_icon_03, R.string.coach_basic_title_003));
        m_array.add(new TitlePhoto(R.drawable.coach_basic_icon_04, R.string.coach_basic_title_004));

        m_adapter = new CoachBasicListAdapter(this);
        m_adapter.bindData(m_array);

        m_listView = (ListView) findViewById(R.id.coachbasic_item_list);
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
        m_select = position;
        Log("선택: " + position);
        switch (position) {
            case 0:
                program = ProgramCode.Basic;
                break;
            case 1:
                program = ProgramCode.Full_Body;
                break;
            case 2:
                program = ProgramCode.Mat;
                break;
            case 3:
                program = ProgramCode.Active;
                break;
        }
        ActivityTabHome.pushActivity(this, ActivityCourse.class);
//        ActivityTabHome.startFitnessProgram(this, position + 1);
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
