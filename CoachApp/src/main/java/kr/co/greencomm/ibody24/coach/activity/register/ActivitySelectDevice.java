package kr.co.greencomm.ibody24.coach.activity.register;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.adapter.DeviceListAdapter;
import kr.co.greencomm.ibody24.coach.base.ApplicationStatus;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabSetting;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.middleware.bluetooth.ConnectStatus;
import kr.co.greencomm.middleware.bluetooth.DeviceRecord;
import kr.co.greencomm.middleware.bluetooth.ScanMode;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver.Action;
import kr.co.greencomm.middleware.utils.ProductCode;
import kr.co.greencomm.middleware.wrapper.MWControlCenter;


/**
 * Created by young on 2015-08-17.
 */
public class ActivitySelectDevice
        extends CoachBaseActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String tag = ActivitySelectDevice.class.getSimpleName();
    private Button m_btn_reload;
    private Button m_btn_next;

    private TextView m_txt_title;

    private boolean isSelect, isClickBtn;

    private final ArrayList<DeviceRecord> m_array = new ArrayList<>();
    private ListView m_listView;
    private DeviceListAdapter m_adapter;
    private DeviceRecord m_device;

    private BroadcastReceiver mReceiver;

    private boolean isRegister;

    private static ProductCode m_productCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentScale1440(R.layout.coachplus_select_device, R.id.screen_layout_main);
        setContentView(R.layout.coachplus_select_device);

        setApplicationStatus(ApplicationStatus.SelectDeviceScreen);

        int code = getIntent().getIntExtra("product_code", 0);
        switch (code) {
            case 200003:
                m_productCode = ProductCode.Coach;
                break;
            case 220004:
                m_productCode = ProductCode.Fitness;
                break;
        }

        m_txt_title = (TextView) findViewById(R.id.select_device_txt_title);
        m_txt_title.setText(String.format(Locale.getDefault(), m_txt_title.getText().toString(), m_productCode.getProductName()));

        m_btn_reload = (Button) findViewById(R.id.select_btn_reload);
        m_btn_reload.setOnClickListener(this);

        m_btn_next = (Button) findViewById(R.id.select_btn_next);
        m_btn_next.setOnClickListener(this);

        m_adapter = new DeviceListAdapter(this);
        m_listView = (ListView) findViewById(R.id.select_item_list);
        m_listView.setOnItemClickListener(this);
        m_listView.setAdapter(m_adapter);
    }

    private void registerReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d(tag, "select device popup mode onReceive-> action : " + action);
                run(intent);
            }
        };


        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.Ec.ACTION_MW_BATTERY);
        filter.addAction(Action.Bm.ACTION_MW_CONNECTION_SUCCESS);
        filter.addAction(Action.Bm.ACTION_MW_CONNECTION_FAILED);
        filter.addAction(Action.Bm.ACTION_MW_CONNECTION_STATE);
        filter.addAction(Action.Bm.ACTION_MW_GENERATE_SCANLIST);
        filter.addAction(Action.Bm.ACTION_MW_DEVICE_INFORMATION);
        filter.addAction(Action.Bm.ACTION_MW_END_OF_SCANLIST);
        registerReceiver(mReceiver, filter);
    }

    private void unregistReceiver() {
        unregisterReceiver(mReceiver);
    }

    @Override
    public void run(Intent intent) {
        String action = intent.getAction();
        if (action.equals(Action.Bm.ACTION_MW_CONNECTION_STATE)) {
            int state = intent.getIntExtra(Action.EXTRA_NAME_INT_1, 0);
            if (state == ConnectStatus.STATE_CONNECTED.ordinal()) {
                DB_User.setDeviceAddress(m_device.getMac());
                DB_User.setDeviceName(m_device.getName());

                Log.d(tag, "2.DB dev name : " + DB_User.getDeviceName());
                Log.d(tag, "2.DB dev addr : " + DB_User.getDeviceAddress());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DB_User.runQuery(QueryCode.SetDevice, ActivitySelectDevice.this);
                            }
                        };
                        displayMsg(R.string.success_connect, listener);
                    }
                });
            }
        } else if (action.equals(Action.Bm.ACTION_MW_GENERATE_SCANLIST)) {
            if (isSelect)
                return;
            HashMap<String, DeviceRecord> m_list = (HashMap<String, DeviceRecord>) intent.getSerializableExtra(Action.EXTRA_NAME_OBJECT);
            displayList(m_list);
        } else if (action.equals(Action.Bm.ACTION_MW_CONNECTION_SUCCESS)) {
        } else if (action.equals(Action.Bm.ACTION_MW_END_OF_SCANLIST)) {
            isClickBtn = false;
            if (m_adapter.isEmpty()) {
                if (m_popup_mode)
                    ActivityTabSetting.pushActivity(this, ActivityConnectionFailed.class);
                else {
                    intent = new Intent(this, ActivityConnectionFailed.class);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_btn_reload:
                if (!isClickBtn) {
                    m_adapter.resetData();
                    m_listView.clearChoices();
                    isSelect = false;
                    SendReceive.sendStartScan(this);
                    isClickBtn = true;
                }
                break;
            case R.id.select_btn_next:
                Log("선택: " + m_device);
                if (m_device != null) {
                    Preference.putBluetoothName(getApplicationContext(), m_device.getName());
                    Preference.putBluetoothMac(getApplicationContext(), m_device.getMac());
                    connectDevice(this, m_device.getMac());
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isSelect) {
            SendReceive.sendStopScan(this);
            isSelect = true;
        }
        m_device = (DeviceRecord) m_listView.getItemAtPosition(position);
        Log("선택: " + position + ", Name: " + m_device.getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRegister) {
            unregistReceiver();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!m_popup_mode) {
            registerReceiver();
            isRegister = true;
        }
        // 규창 12.22 디바이스 삭제후 연결 성공한 뒤 탭셋팅 버튼 누르면 연결 끊기고 재 스캔하는 문제 수정
        if (MWControlCenter.getInstance(getApplicationContext()).getConnectionState() == ConnectStatus.STATE_DISCONNECTED) {
            SendReceive.sendSetScanMode(this, ScanMode.MANUAL);
            SendReceive.sendStartScan(this);
        }

    }

    public void displayList(HashMap<String, DeviceRecord> list) {
        if (list != null) {
            Log.d(tag, "장치데이터: " + list.size());
            m_adapter.bindData(list);
        }
    }
}
