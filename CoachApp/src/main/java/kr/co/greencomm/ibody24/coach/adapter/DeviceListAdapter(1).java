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
import java.util.HashMap;
import java.util.Map;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.utils.ViewHolder;
import kr.co.greencomm.middleware.bluetooth.DeviceRecord;


/**
 * 밴드 장치 목록을 위한 어댑터
 * 1111
 *
 * @author 김영일
 */
public class DeviceListAdapter
        extends BaseAdapter {
    private static final String TAG = "DeviceListAdapter";

    // 리스트용 데이터
    private ArrayList<DeviceRecord> m_list = new ArrayList<>();
    private LayoutInflater m_layoutInflater;

    //생성자
    public DeviceListAdapter(Context context) {
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

    public void bindData(HashMap<String, DeviceRecord> newData) {
        if(newData != null && newData.size() > 0) {
            this.m_list = new ArrayList<>();
            for(Map.Entry<String, DeviceRecord> entry: newData.entrySet()) {
                m_list.add(entry.getValue());
            }
            notifyDataSetChanged();
        }
    }

    public void resetData() {
        if (m_list != null) {
            m_list.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder m_viewHolder;

        // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 convertView가 null인 상태로 들어 옴
        if (convertView == null) {
            convertView = m_layoutInflater.inflate(R.layout.item_device_information, parent, false);

            m_viewHolder = new ViewHolder();
            m_viewHolder.m_layout = (LinearLayout) convertView.findViewById(R.id.device_item_main);
            m_viewHolder.m_name = (TextView) convertView.findViewById(R.id.device_item_name);
        } else {
            m_viewHolder = (ViewHolder) convertView.getTag();
        }
        DeviceRecord devInfo = m_list.get(position);
        Log.d(TAG, "GetView: " + position + ", Dev: " + devInfo.getName() + ", Mac: " + devInfo.getMac());
        m_viewHolder.m_name.setText(devInfo.getName());
        convertView.setTag(m_viewHolder);
        return convertView;
    }
}
