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
import kr.co.greencomm.ibody24.coach.data.TitlePhoto;


/**
 * 밴드 장치 목록을 위한 어댑터
 * 1111
 *
 * @author 김영일
 */
public class CoachBasicListAdapter
        extends BaseAdapter {
    private static final String TAG = "CoachBasicListAdapter";

    // 리스트용 데이터
    private ArrayList<TitlePhoto> m_list = new ArrayList<>();
    private LayoutInflater m_layoutInflater;

    //생성자
    public CoachBasicListAdapter(Context context) {
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

    public void bindData(ArrayList<TitlePhoto> listData) {
        if (listData != null && listData.size() > 0) {
            //m_list = listData;
            m_list.clear();
            for (TitlePhoto info : listData) {
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
            convertView = m_layoutInflater.inflate(R.layout.item_coach_basic, parent, false);

            m_layout = (LinearLayout) convertView.findViewById(R.id.coachbasic_item_main);
        } else {
            m_layout = (LinearLayout) convertView.getTag();
        }

        TitlePhoto info = m_list.get(position);
        Log.d(TAG, "GetView: " + position + ", Info: " + info);

        ImageView m_icon = (ImageView) convertView.findViewById(R.id.coachbasic_item_iconImage);
        TextView m_title = (TextView) convertView.findViewById(R.id.coachbasic_item_titleText);

        m_icon.setBackgroundResource(info.iconResId);
        m_title.setText(info.titleResId);

        convertView.setTag(m_layout);
        return convertView;
    }
}
