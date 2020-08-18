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
import android.widget.NumberPicker;
import android.widget.TextView;

import kr.co.greencomm.ibody24.coach.R;

/**
 * Created by young on 2015-08-19.
 */
public class NumberPickerDialog extends Dialog {

    public interface OnDateSetListener {
        public void onDateSet(NumberPicker view, int value);
    }

    private TextView m_text_title;
    private NumberPicker m_picker;
    private Button m_btn_ok;

    private String m_title;
    private int m_value;
    private int m_min;
    private int m_max;
    private OnDateSetListener m_listener;

    public NumberPickerDialog(Context context, String title, int value, int max, int min, OnDateSetListener listener) {
        super(context);
        m_title = title;
        m_value = value;
        m_max = max;
        m_min = min;
        m_listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_number_picker);

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

        m_picker = (NumberPicker) findViewById(R.id.num_picker);
        m_picker.setMaxValue(m_max);
        m_picker.setMinValue(m_min);
        m_picker.setValue(m_value);

        m_btn_ok = (Button) findViewById(R.id.btn_ok);
        m_btn_ok.setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
            if (m_listener != null) {
                m_listener.onDateSet(m_picker, m_picker.getValue());
            }
        }
    };
}
