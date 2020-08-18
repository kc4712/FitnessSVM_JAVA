package kr.co.greencomm.ibody24.coach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by young on 2015-08-25.
 */
public class ImageAdapter
        extends BaseAdapter {
    // 리스트용 데이터
    private ArrayList<Integer> m_list = new ArrayList<>();
    private LayoutInflater m_layoutInflater;
    private Context m_context;

    public ImageAdapter(Context context) {
        super();
        m_context = context;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv = new ImageView(m_context);
        iv.setImageResource(m_list.get(position));
        //AdapterViewFlipper.LayoutParams param = new AdapterViewFlipper.LayoutParams(250, 307);
        //iv.setLayoutParams(new AdapterViewFlipper.LayoutParams(250, 307));
        //iv.setScaleType(ImageView.ScaleType.FIT_XY);
        //iv.setBackgroundResource(mGalleryItemBackground );
        return iv;
    }

    public void bindData(ArrayList<Integer> listData) {
        if (listData != null && listData.size() > 0) {
            m_list.clear();
            for (Integer info : listData) {
                m_list.add(info);
            }
            notifyDataSetChanged();
        }
    }
}
