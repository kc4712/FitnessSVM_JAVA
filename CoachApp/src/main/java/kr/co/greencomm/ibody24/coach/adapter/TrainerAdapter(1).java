package kr.co.greencomm.ibody24.coach.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.data.TrainerInfo;


/**
 * Created by young on 2015-08-24.
 */
public class TrainerAdapter
        extends BaseAdapter {
    private static final String TAG = "TrainerAdapter";

    // 리스트용 데이터
    private ArrayList<TrainerInfo> m_list;
    private LayoutInflater m_layoutInflater;

    //생성자
    public TrainerAdapter(Context context) {
        super();
        m_layoutInflater = (LayoutInflater) LayoutInflater.from(context);
        m_list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return m_list.size();
    }

    @Override
    public Object getItem(int position) {
        return m_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void bindData(ArrayList<TrainerInfo> listData) {
        if (listData != null && listData.size() > 0) {
            //m_list = listData;
            m_list.clear();
            for (TrainerInfo info : listData) {
                m_list.add(info);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout m_layout;

        // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 convertView가 null인 상태로 들어 옴
        if (convertView == null) {
            convertView = m_layoutInflater.inflate(R.layout.item_fitness_program, parent, false);
            m_layout = (LinearLayout) convertView.findViewById(R.id.fitness_program_item_main);
        } else {
            m_layout = (LinearLayout) convertView.getTag();
        }

        TrainerInfo info = m_list.get(position);
        Log.d(TAG, "GetView: " + position + ", Info: " + info);

        ImageView m_icon = (ImageView) convertView.findViewById(R.id.fitness_program_iconImage);
        TextView m_spec = (TextView) convertView.findViewById(R.id.fitness_program_spec_text);
        TextView m_name = (TextView) convertView.findViewById(R.id.fitness_program_name_text);
        TextView m_title = (TextView) convertView.findViewById(R.id.fitness_program_title_text);

        m_icon.setBackgroundResource(info.photoResId);
        m_spec.setText(info.specResId);
        m_name.setText(info.nameResId);
        m_title.setText(info.titleResId);

        convertView.setTag(m_layout);
        return convertView;
    }
}
