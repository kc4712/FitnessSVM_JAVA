package kr.co.greencomm.ibody24.coach.activity.fitness;

import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.adapter.ActListAdapter;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.provider.CoachResolver;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.ibody24.coach.utils.ActListItem;
import kr.co.greencomm.middleware.provider.CoachContract;
import kr.co.greencomm.middleware.utils.CourseLabel;
import kr.co.greencomm.middleware.utils.container.ActivityData;

/**
 * Created by jeyang on 16. 9. 21..
 */
public class ActivityActList extends CoachBaseActivity implements AdapterView.OnItemClickListener {
    private static final String tag = ActivityActList.class.getSimpleName();

    private ArrayList<ActListItem> m_list = new ArrayList<>();
    private ActListAdapter m_adapter;
    private ListView m_listview;

    private ContentObserver m_observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coachplus_activity_list);
        setApplicationStatus(ApplicationStatus.ActListScreen);

        m_listview = (ListView) findViewById(R.id.activity_list_listview);

        m_observer = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);

                Log.d(tag, "onChange -> " + uri);
                if (uri.equals(CoachContract.Fitness.CONTENT_URI)) {
                    Log.d(tag, " reload.....");
                    reloadList();
                }
            }
        };

        m_adapter = new ActListAdapter(this);
        reloadList();
        m_listview.setAdapter(m_adapter);
        m_listview.setOnItemClickListener(this);

        m_adapter.notifyData();
    }

    private void reloadList() {
        m_list.clear();
        getActivityData();
        m_adapter.bindData(m_list);
    }

    private void getActivityData() {
        CoachResolver res = new CoachResolver();

        ArrayList<ActivityData> arr = res.getActivityDataArray();
        if (arr == null) {
            return;
        }

        for (ActivityData act : arr) {
            if (act.getLabel() == CourseLabel.Activity.getLabel()) {
                Log.d(tag, "get start time : " + act.getStart_time());
                Log.d(tag, "-> label : " + act.getLabel());
                long time = act.getStart_time();
                m_list.add(new ActListItem(getDateString(time), time));
            }
        }
    }

    private String getDateString(long date) {
        DateFormat fmt = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String ret = fmt.format(new Date(date));

        fmt = new SimpleDateFormat(" HH:mm", Locale.getDefault());
        ret += fmt.format(new Date(date));

        return ret;
    }

    @Override
    public void run(Intent intent) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ActivityActResult.class);
        ActListItem ret = (ActListItem) m_listview.getItemAtPosition(position);

        if (!ret.equals("")) {
            intent.putExtra("start_time", ret.getUtc());
            ActivityTabHome.pushActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(CoachContract.Fitness.CONTENT_URI, false, m_observer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(m_observer);
    }
}
