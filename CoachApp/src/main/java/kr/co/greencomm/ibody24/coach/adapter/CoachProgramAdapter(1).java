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
 * Created by young on 2015-08-28.
 */
public class CoachProgramAdapter extends BaseAdapter {
    // 리스트용 데이터
    private ArrayList<Integer> m_codeList;
    private ArrayList<String> m_nameList;
    private LayoutInflater m_layoutInflater;

    //생성자
    public CoachProgramAdapter(Context context) {
        super();
        m_layoutInflater = (LayoutInflater) LayoutInflater.from(context);
        m_codeList = new ArrayList<>();
        m_nameList = new ArrayList<>();
    }

    public void add(int code, String name) {
        m_codeList.add(code);
        m_nameList.add(name);
    }

    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return m_nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return m_nameList.get(position);
    }

    public Integer getCode(int position) {
        return m_codeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = m_layoutInflater.inflate(R.layout.item_mycoach, parent, false);
        TextView m_title = (TextView) convertView.findViewById(R.id.mycoach_item_titleText);
        m_title.setText(m_nameList.get(position));
        return convertView;
    }
}
