package kr.co.greencomm.ibody24.coach.activity.register;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.ActivityMain;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.dialog.MessageDialog;
import kr.co.greencomm.ibody24.coach.utils.PermissionUtil;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabSetting;
import kr.co.greencomm.middleware.service.MWService;
import kr.co.greencomm.middleware.utils.ProductCode;

/**
 * Created by jeyang on 16. 9. 21..
 */
public class ActivityProductSelect extends CoachBaseActivity implements View.OnClickListener {
    private static final String tag = ActivityProductSelect.class.getSimpleName();

    private Button m_btn_coach;
    private Button m_btn_fitness;

    private Intent intent;

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coachplus_select_product);

        mLayout = findViewById(R.id.product_layout);
        m_btn_coach = (Button) findViewById(R.id.select_product_coach);
        m_btn_fitness = (Button) findViewById(R.id.select_product_fitness);

        m_btn_coach.setOnClickListener(this);
        m_btn_fitness.setOnClickListener(this);

        checkPermission();
    }

    @Override
    public void onClick(View v) {
        intent = new Intent(getApplicationContext(), ActivitySelectDevice.class);
        ProductCode code = ProductCode.Coach;
        switch (v.getId()) {
            case R.id.select_product_coach:
                code = ProductCode.Coach;
                break;
            case R.id.select_product_fitness:
                code = ProductCode.Fitness;
                break;
        }
        intent.putExtra("product_code", code.getProductCode());
        SendReceive.sendProductCode(this, code);
        if (m_popup_mode) {
            ActivityTabSetting.pushActivity(intent);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void run(Intent intent) {

    }

    public void checkPermission() {
        Log.i(tag, "Checking permission.");
        for (String permission : ActivityMain.PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
                return;
            }
        }
        Log.i(tag, "permission has already been granted.");
    }

    private void requestPermission() {
        Log.i(tag, "permission has NOT been granted. Requesting permission.");

        // 사용자가 한번 거절하면, 이유를 설명하고 다시 요청하기 위한 코드인데, 그냥 무조건 요청하는 형식으로 사용중.
        /*for (String permission : ActivityMain.PERMISSIONS) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {
                ActivityCompat.requestPermissions(this, ActivityMain.PERMISSIONS,
                        ActivityMain.REQUEST_PERMISSION);
                Log.i(tag, "permission rationale to provide additional context.");
                return;
            }
        }*/

        ActivityCompat.requestPermissions(this, ActivityMain.PERMISSIONS,
                ActivityMain.REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ActivityMain.REQUEST_PERMISSION) {
            Log.i(tag, "Received response for permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Log.i(tag, "permissions were NOT granted.");
//                Snackbar.make(mLayout, R.string.permissions_not_granted,
//                        Snackbar.LENGTH_SHORT)
//                        .show();
                Intent intent = new Intent(this, MWService.class);
                stopService(intent);

                MessageDialog dialog = new MessageDialog(ActivityMain.MainContext == null ? this : ActivityMain.MainContext,
                        getString(R.string.error_permission), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                dialog.show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
