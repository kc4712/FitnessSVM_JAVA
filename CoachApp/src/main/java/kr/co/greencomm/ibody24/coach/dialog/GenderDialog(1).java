package kr.co.greencomm.ibody24.coach.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import kr.co.greencomm.ibody24.coach.R;

/**
 * Created by young on 2015-08-20.
 */
public class GenderDialog extends Dialog {

    public interface OnDateSetListener {
        public void onDateSet(View view, int gender);
    }

    private TextView m_text_title;
    private ImageView m_img_women;
    private ImageView m_img_man;
    private Button m_btn_ok;

    private String m_title;
    private int m_gender;
    private OnDateSetListener m_listener;

    public GenderDialog(Context context, String title, int gender, OnDateSetListener listener) {
        super(context);
        m_title = title;
        m_gender = gender;
        m_listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_gender);

        // 배경을 투명하게 하기
        Window win = getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Dialog 사이즈 조절 하기
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);

        m_text_title = (TextView) findViewById(R.id.chartyear_txt_title);
        //Utils.setTextFont(subject, mContext);
        m_text_title.setText(m_title);

        m_img_women = (ImageView) findViewById(R.id.img_women);
        m_img_man = (ImageView) findViewById(R.id.img_man);
        changeBackgrground();

        m_img_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               m_gender = 1;
                changeBackgrground();
            }
        });
        m_img_women.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_gender = 2;
                changeBackgrground();
            }
        });

        m_btn_ok = (Button) findViewById(R.id.btn_ok);
        m_btn_ok.setOnClickListener(buttonClickListener);
    }

    private void changeBackgrground() {
        if (m_gender == 1) {
            m_img_man.setBackgroundResource(R.drawable.icon_man_p);
            m_img_women.setBackgroundResource(R.drawable.icon_women_n);
        }
        else {
            m_img_man.setBackgroundResource(R.drawable.icon_man_n);
            m_img_women.setBackgroundResource(R.drawable.icon_women_p);
        }
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
            if (m_listener != null) {
                m_listener.onDateSet(v, m_gender);
            }
        }
    };
}
