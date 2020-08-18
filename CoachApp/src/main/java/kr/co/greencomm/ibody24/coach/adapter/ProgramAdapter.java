package kr.co.greencomm.ibody24.coach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.greencomm.ibody24.coach.R;


/**
 * Created by young on 2015-08-25.
 */
public class ProgramAdapter
        extends BaseAdapter {
    // 리스트용 데이터
    private ArrayList<String> m_list = new ArrayList<>();
    private LayoutInflater m_layoutInflater;

    //생성자
    public ProgramAdapter(Context context) {
        super();
        m_layoutInflater = (LayoutInflater) LayoutInflater.from(context);
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

    public void bindData(ArrayList<String> listData) {
        if (listData != null && listData.size() > 0) {
            //m_list = listData;
            m_list.clear();
            for (String devInfo : listData) {
                m_list.add(devInfo);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = m_layoutInflater.inflate(R.layout.item_trainer_program, parent, false);
        TextView m_title = (TextView) convertView.findViewById(R.id.trainer_item_titleText);
        m_title.setText(m_list.get(position));
        return convertView;
    }
}
