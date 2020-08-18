package kr.co.greencomm.ibody24.coach.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import kr.co.greencomm.ibody24.coach.R;

/**
 * Created by young on 2015-08-19.
 */
public class MessageDialogConfirm extends Dialog {

    private TextView m_text_msg;

    private Button m_btn_yes;
    private Button m_btn_no;

    private String m_msg;
    private DialogInterface.OnClickListener m_listener;

    public MessageDialogConfirm(Context context, final String msg, final DialogInterface.OnClickListener listener) {
        super(context);
        m_msg = msg;
        m_listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_message_yesno);

        // 배경을 투명하게 하기
        Window win = getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Dialog 사이즈 조절 하기
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);

        m_text_msg  = (TextView) findViewById(R.id.display_msg);
        //Utils.setTextFont(subject, mContext);
        m_text_msg.setText(m_msg);

        m_btn_yes = (Button) findViewById(R.id.btn_yes);
        m_btn_no = (Button) findViewById(R.id.btn_no);
        //btn_next.setText(button_text);
        //btn_next.setOnClickListener(okListener);
        m_btn_yes.setOnClickListener(buttonClickListener);
        m_btn_no.setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
            if (m_listener != null) {
                if (v.getId() == R.id.btn_yes) {
                    dismiss();
                    m_listener.onClick(MessageDialogConfirm.this, DialogInterface.BUTTON_POSITIVE);
                }
                else {
                    dismiss();
                    m_listener.onClick(MessageDialogConfirm.this, DialogInterface.BUTTON_NEGATIVE);
                }
            }
        }
    };
}
