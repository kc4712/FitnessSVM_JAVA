package kr.co.greencomm.ibody24.coach.adapter;

import android.content.Context;
import android.graphics.Color;
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
public class SettingMenuAdapter extends BaseAdapter {
    // 리스트용 데이터
    private ArrayList<String> m_lists;
    private ArrayList<Boolean> m_enables;
    private LayoutInflater m_layoutInflater;

    //생성자
    public SettingMenuAdapter(Context context) {
        super();
        m_layoutInflater = (LayoutInflater) LayoutInflater.from(context);
        m_lists = new ArrayList<>();
        m_enables = new ArrayList<>();
    }

    public void add(String name, Boolean enable) {
        m_lists.add(name);
        m_enables.add(enable);
    }

    @Override
    public int getCount() {
        return m_lists.size();
    }

    @Override
    public Object getItem(int position) {
        return m_lists.get(position);
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
        convertView = m_layoutInflater.inflate(R.layout.item_setting_menu, parent, false);
        TextView m_title = (TextView) convertView.findViewById(R.id.setting_item_titleText);
        m_title.setText(m_lists.get(position));
        boolean enable = m_enables.get(position);
        m_title.setTextColor(enable ? Color.BLACK : Color.GRAY);
        convertView.setEnabled(enable);
        convertView.setClickable(!enable);
        return convertView;
    }
}
