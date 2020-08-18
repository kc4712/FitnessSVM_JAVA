package kr.co.greencomm.ibody24.coach.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.KeyEvent;

/**
 * Created by young on 2015-09-18.
 */
public class DownProgressDialog
    extends ProgressDialog
{
    public DownProgressDialog(Context context) {
        super(context);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
