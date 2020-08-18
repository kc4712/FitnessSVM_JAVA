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
 * Created by young on 2015-08-24.
 */
public class TitlePhotoAdapter
        extends BaseAdapter {
    // 리스트용 데이터
    private ArrayList<TitlePhoto> m_list = new ArrayList<>();
    private Context m_context;
    private LayoutInflater m_layoutInflater;
    private boolean m_enable;

    //생성자
    public TitlePhotoAdapter(Context context, boolean enable) {
        super();
        m_context = context;
        m_layoutInflater = (LayoutInflater) LayoutInflater.from(context);
        m_enable = enable;
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
    public boolean areAllItemsEnabled() {
        return m_enable;
    }

    @Override
    public boolean isEnabled(int position) {
        return m_enable;
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
            convertView = m_layoutInflater.inflate(R.layout.item_title_photo_01, parent, false);
            m_layout = (LinearLayout) convertView.findViewById(R.id.photo_item_main);
        } else {
            m_layout = (LinearLayout) convertView.getTag();
        }

        TitlePhoto info = m_list.get(position);
        Log.d("TitlePhotoAdapter", "GetView: " + position + ", Info: " + info);

        ImageView m_icon = (ImageView) convertView.findViewById(R.id.photo_item_iconImage);
        //Button m_title = (Button) convertView.findViewById(R.id.titleButton);
        TextView m_title = (TextView) convertView.findViewById(R.id.photo_item_titleButton);

        m_title.setText(info.titleText);
        m_icon.setBackgroundResource(info.iconResId);

        convertView.setTag(m_layout);
        return convertView;
    }

    /*
    private void decodeImage() {
        // Get the dimensions of the View
        int targetW = m_icon.getWidth();
        int targetH = m_icon.getHeight();

        SpectrumActivity.Log("Target: " + targetW + ", " + targetH);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        //BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        BitmapFactory.decodeResource(m_context.getResources(), info.iconResId, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        SpectrumActivity.Log("Photo: " + targetW + ", " + targetH);

        // Determine how much to scale down the image
        //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = 4;
        bmOptions.inPurgeable = true;

        //Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Bitmap bitmap = BitmapFactory.decodeResource(m_context.getResources(), info.iconResId, bmOptions);
        m_icon.setImageBitmap(bitmap);
    }
    */
}
