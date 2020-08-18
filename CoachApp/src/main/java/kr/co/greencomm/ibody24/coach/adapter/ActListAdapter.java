package kr.co.greencomm.ibody24.coach.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.utils.ActListItem;

public class ActListAdapter
        extends BaseAdapter {
    private static final String TAG = ActListAdapter.class.getSimpleName();

    // 화면에 미리 그려줄 List의 개수
    private int dummy_count = 15;
    // 리스트용 데이터
    private ArrayList<ActListItem> m_list = new ArrayList<>();
    private LayoutInflater m_layoutInflater;

    //생성자
    public ActListAdapter(Context context) {
        super();
        m_layoutInflater = (LayoutInflater) LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return m_list.size() < dummy_count ? dummy_count : m_list.size();
    }

    @Override
    public Object getItem(int position) {
        return m_list.size() == 0 ? "" : m_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void bindData(ArrayList<ActListItem> listData) {
        if (listData != null && listData.size() > 0) {
            //m_list = listData;
            Log.d(TAG," listData size " + listData.size());
            m_list.clear();
            for (ActListItem info : listData) {
                m_list.add(info);
            }
            notifyDataSetChanged();
            Log.d(TAG," m list size " + m_list.size());
        }
    }

    public void notifyData() {
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout m_layout;

        // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 convertView가 null인 상태로 들어 옴
        if (convertView == null) {
            convertView = m_layoutInflater.inflate(R.layout.item_activity_list, parent, false);

            m_layout = (LinearLayout) convertView.findViewById(R.id.item_list_layout_main);
        } else {
            m_layout = (LinearLayout) convertView.getTag();
        }

        String info = "";
        if (m_list.size() - 1 < position && position < dummy_count) {
            info = "";
        } else {
            info = m_list.get(position).getDate();
        }

        Log.d(TAG, "GetView: " + position + ", Info: " + info);

        TextView m_date = (TextView) convertView.findViewById(R.id.item_list_txt_activity_date);

        if (info.equals("")) {
            m_layout.setVisibility(View.INVISIBLE);
        } else {
            m_layout.setVisibility(View.VISIBLE);
        }
        m_date.setText(info);

        convertView.setTag(m_layout);
        return convertView;
    }
}
