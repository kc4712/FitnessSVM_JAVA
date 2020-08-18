package kr.co.greencomm.ibody24.coach.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.dialog.MessageDialogConfirm;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabSetting;
import kr.co.greencomm.middleware.bluetooth.ScanMode;
import kr.co.greencomm.middleware.db.Preference;

/**
 * Created by jeyang on 16. 9. 21..
 */
public class ActivityDeviceEdit extends CoachBaseActivity implements View.OnClickListener {
    private static final String tag = ActivityDeviceEdit.class.getSimpleName();

    private LinearLayout m_layout_dev_name;
    private TextView m_txt_dev_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coachplus_device_edit);

        setApplicationStatus(ApplicationStatus.DeviceEditScreen);

        m_txt_dev_name = (TextView) findViewById(R.id.device_edit_txt_dev_name);
        m_layout_dev_name = (LinearLayout) findViewById(R.id.device_edit_layout_dev_name);

        m_layout_dev_name.setOnClickListener(this);
        m_txt_dev_name.setText(DB_User.getDeviceName());
    }

    @Override
    public void run(Intent intent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_edit_layout_dev_name:
                MessageDialogConfirm confirm = new MessageDialogConfirm(ActivityMain.MainContext, getString(R.string.delete_device), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            // 펌웨어 파일 존재하면 삭제 필요.
                            // ZIP, HEX 따로 존재 가능하므로, 둘다 삭제 필요.

                            DB_User.setDeviceAddress("");
                            DB_User.setDeviceName("");
                            SendReceive.sendSetScanMode(ActivityDeviceEdit.this, ScanMode.MANUAL);
                            SendReceive.sendStopBluetooth(ActivityDeviceEdit.this);
                            Preference.putBluetoothMac(ActivityDeviceEdit.this, "");
                            Preference.putBluetoothName(ActivityDeviceEdit.this, "");

                            DB_User.runQuery(QueryCode.SetDevice, null);

                            ActivityTabSetting.pushActivityNoStack(ActivityDeviceEdit.this, ActivityRegisterDevice.class);
                        }
                    }
                });
                confirm.show();
                break;
        }
    }
}
