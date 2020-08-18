package kr.co.greencomm.ibody24.coach.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.activity.coach.ActivityHome;
import kr.co.greencomm.ibody24.coach.activity.fitness.ActivityHomeFitness;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.base.TabServer;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.data.DataTransfer;
import kr.co.greencomm.ibody24.coach.data.FirmDownload;
import kr.co.greencomm.ibody24.coach.data.FirmInfo;
import kr.co.greencomm.ibody24.coach.data.FirmUpListener;
import kr.co.greencomm.ibody24.coach.data.StepInfo;
import kr.co.greencomm.ibody24.coach.dialog.MessageDialog;
import kr.co.greencomm.ibody24.coach.provider.CoachResolver;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabChart;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabMycoach;
import kr.co.greencomm.ibody24.coach.utils.PermissionUtil;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabSetting;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.middleware.bluetooth.ScanMode;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.service.MWBroadcastReceiver.Action;
import kr.co.greencomm.middleware.service.MWService;
import kr.co.greencomm.middleware.utils.Convert;
import kr.co.greencomm.middleware.utils.FileManager;
import kr.co.greencomm.middleware.utils.StateApp;
import kr.co.greencomm.middleware.utils.container.ActivityData;
import no.nordicsemi.android.dfu.DfuBaseService;


public class ActivityMain extends ActivityGroup {
    private static final String TAG = "MAIN_SCREEN";

    private TabHost m_tabHost;

    public static final int REQUEST_PERMISSION = 1;

    public static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE};
    private View mLayout;

    private boolean isStart;

    private BroadcastReceiver mReceiver;

    public static Context MainContext;

    public Activity getCurrentActivity() {
        Activity act = this.getLocalActivityManager().getCurrentActivity();
        if (act instanceof TabServer) {
            Log.d(TAG, "CurrentTab: " + act.getClass().getSimpleName());
            act = ((TabServer) act).getCurrentActivity();
        }
        if (act != null) {
            Log.d(TAG, "CurrentView: " + act.getClass().getSimpleName());
        }
        return act;
    }

    private TabHost.TabSpec makeTab(int titleResId, int iconResId, Class<?> cls) {
        View itemLayout = LayoutInflater.from(this).inflate(R.layout.tab_menu_item, null);
        ImageView icon = (ImageView) itemLayout.findViewById(R.id.menu_item_icon);
        icon.setImageResource(iconResId);
        TextView title = (TextView) itemLayout.findViewById(R.id.menu_item_title);
        title.setText(titleResId);

        TabHost.TabSpec tab = m_tabHost.newTabSpec(getText(titleResId).toString());
        tab = tab.setIndicator(itemLayout);
        tab = tab.setContent(new Intent(this, cls));
        return tab;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        MainContext = this;

        m_tabHost = (TabHost) findViewById(R.id.tab_host);
        m_tabHost.setup(this.getLocalActivityManager());

        mLayout = findViewById(R.id.mLayout);

        checkPermission();
        makeUpActivityData();

        SendReceive.sendIsLiveApplication(this, StateApp.STATE_NORMAL);

        //사용하려는 탭 추가
        m_tabHost.addTab(makeTab(R.string.menu_home, R.drawable.main_menu_home, ActivityTabHome.class));
        m_tabHost.addTab(makeTab(R.string.menu_chart, R.drawable.main_menu_chart, ActivityTabChart.class));
        m_tabHost.addTab(makeTab(R.string.menu_coach, R.drawable.main_menu_coach, ActivityTabMycoach.class));
        m_tabHost.addTab(makeTab(R.string.menu_setting, R.drawable.main_menu_setting, ActivityTabSetting.class));

        // 처음 보여질 탭 선택
        m_tabHost.setCurrentTab(0);

        m_tabHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                Log.d(TAG, "Click: " + id);
            }
        });
        m_tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.d(TAG, "Tab Changed: " + tabId);
                if (tabId.equals("Home")) {
                    Activity current = getCurrentActivity();
                    if (current != null) {
                        if (current.getClass().getSimpleName().equals("ActivityHome")) {
                            ActivityHome.Home.updateGraph();
                        } else if (current.getClass().getSimpleName().equals("ActivityHomeFitness")) {
                            ActivityHomeFitness.Home.updateBroadcast();
                        }
                    }
                }
                if (tabId.equals("My Coach")) {
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Convert.getConnectivityStatus(getApplicationContext()) == Convert.CONNECT_TYPE_NOT_CONNECTED) {
                            MessageDialog dialog = new MessageDialog(ActivityMain.this, getString(R.string.not_prepare_wifi), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                            dialog.show();
                        }
                    }
                });
            }
        });

        DataTransfer transfer = new DataTransfer();
        transfer.start();

        //startActivity(getIntentNotificationListenerSettings());
    }

    private void setListener(FirmInfo firmInfo) {
        if (firmInfo != null) {
            firmInfo.setListener(new FirmUpListener() {
                @Override
                public void firmUp(boolean success, String path) {
                    if (success) {
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (path != null){
                                    SendReceive.sendFirmUpdateStart(getApplicationContext(), path);
                                }

                                dismissPopup(null);
                                popup(getString(R.string.start_firm_warnning), false);
                                Preference.removeForFirmUp(getApplicationContext());

                                CoachResolver res = new CoachResolver();
                                res.deleteIndexTimeProvider();

                                Activity activity = getCurrentActivity();
                                if (activity instanceof ActivityHomeFitness) {
                                    ((ActivityHomeFitness) activity).onShow();
                                }
                            }
                        };

                        popup(getString(R.string.notice_firmup), listener, true);

                        // 기존 펌웨어 업데이트 코드 주석처리.
                        /*
                         // start firmup..
                        dismissPopup(null);
                        popup(getString(R.string.start_firm_warnning), false);
                        Preference.removeForFirmUp(getApplicationContext());

                        CoachResolver res = new CoachResolver();
                        res.deleteIndexTimeProvider();

                        Activity activity = getCurrentActivity();
                        if (activity instanceof ActivityHomeFitness) {
                            ((ActivityHomeFitness) activity).onShow();
                        }
                        */
                    }
                }
            });
        }
    }

    private void updateMessage(final String msg) {
        alert.setMessage(msg);
    }

    private void popup(final String msg, boolean existButton) {
        if (existButton) {
            showMessage(msg);
        } else {
            showMessageNonBtn(msg);
        }
    }

    private void popup(final String msg, DialogInterface.OnClickListener listener, boolean existButton) {
        if (existButton) {
            showMessage(msg, listener);
        } else {
            showMessageNonBtn(msg);
        }
    }

    private void dismissPopup(DialogInterface.OnDismissListener listener) {
        if (alert != null && alert.isShowing()) {
            alert.setOnDismissListener(listener);
            alert.dismiss();
            alert = null;
        }
    }

    private AlertDialog alert;
    private void showMessage(final String msg) {
        if (alert != null)
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.MainContext);
                alert = builder.setMessage(msg)
                        .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                alert = null;
                            }
                        })
                        .create();
                alert.show();
            }
        });
    }

    private void showMessage(final String msg, DialogInterface.OnClickListener listener) {
        if (alert != null)
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.MainContext);
                alert = builder.setMessage(msg)
                        .setNegativeButton(getString(R.string.ok), listener).
                                setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismissPopup(null);
                            }
                        })
                        .create();
                alert.show();
            }
        });
    }

    private void showMessageNonBtn(final String msg) {
        if (alert != null)
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.MainContext);
                alert = builder.setMessage(msg).setCancelable(false)
                        .create();
                alert.show();
            }
        });
    }

    private void setProgress(Intent intent) {
        isStart = true;
        int progress = intent.getIntExtra(DfuBaseService.DFU_Extra_Progress, 0);
        String fmt = String.format(Locale.getDefault(), "%s\n%3d/100 (%%)", getString(R.string.firm_progress), progress);

        if (alert == null)
            popup(fmt, false);
        else
            updateMessage(fmt);
    }

    public void switchHome() {
        m_tabHost.setCurrentTab(0);
    }

    private void registerReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d(TAG, "SendReceive onReceive-> action : " + action);

                if (action.equals(Action.Bm.ACTION_MW_FIRM_VERSION)) {
                    // 펌웨어 방송 들어오면, 업데이트 시작.
                    if (FileManager.isExistFile(getApplicationContext(), "firmware.hex")) {

                    }
                    if (FileManager.isExistFile(getApplicationContext(), "firmware.zip")) {

                    }
                    String version = intent.getStringExtra(Action.EXTRA_NAME_STRING_1);
                    Log.i("Activity Main","version!!!!!"+ version);
                    FirmInfo info = new FirmInfo(getApplicationContext(), FileManager.getMainPath(getApplicationContext()),
                            version, Preference.getProductCode(getApplicationContext()));

                    //규창 Fitness 테스트용 쿼리!!!
                    //FirmInfo info = new FirmInfo(getApplicationContext(), FileManager.getMainPath(getApplicationContext()),
                    //        "0.9.29.27", Preference.getProductCode(getApplicationContext()));
                    setListener(info);
                    info.start(); // 시작!!! 주석처리했음.
                } else if (action.equals(Action.Am.ACTION_MW_STEP_CALORIE)) {
                    short step = intent.getShortExtra(Action.EXTRA_NAME_SHORT_1, (short) 0);
                    double[] calorie = intent.getDoubleArrayExtra(Action.EXTRA_NAME_DOUBLE_ARRAY);
                    if (step != Preference.getMainStep(getApplicationContext())) {
                        Preference.putMainStep(getApplicationContext(), step);

                        long current = System.currentTimeMillis();

                        StepInfo m_datas = new StepInfo();
                        m_datas.setReg(current);
                        m_datas.setStep((int) step);

                        m_datas.runQuery(QueryCode.InsertStep, null);
                    }

                    if (calorie == null)
                        calorie = new double[]{0, 0, 0, 0};
                    Preference.putMainCalorieActivity(getApplicationContext(), (int) calorie[0]);
                    Preference.putMainCalorieCoach(getApplicationContext(), (int) calorie[1]);
                    Preference.putMainCalorieSleep(getApplicationContext(), (int) calorie[2]);
                    Preference.putMainCalorieDaily(getApplicationContext(), (int) calorie[3]);
                }

                Activity activity = getCurrentActivity();
                if (activity instanceof CoachBaseActivity) {
                    ((CoachBaseActivity) activity).run(intent);
                }

                if (action.equals(Action.Sm.ACTION_MW_IS_LIVE_APPLICATION)) {
                    SendReceive.sendIsLiveApplication(getApplicationContext(), StateApp.STATE_NORMAL);
                } else if (action.equals(Action.Am.ACTION_MW_GENERATE_COACH_EXER_DATA) || action.equals(Action.Am.ACTION_MW_GENERATE_ACTIVITY_DATA)) {
                    DataTransfer transfer = new DataTransfer();
                    transfer.start();
                } else if (action.equals(DfuBaseService.DFU_Completed)) {
                    dismissPopup(null);
                    isStart = false;
                    popup(getString(R.string.success_firmup), true);
                } else if (action.equals(DfuBaseService.DFU_Failed)) {
                    dismissPopup(null);
                    isStart = false;
                    popup(getString(R.string.failed_firmup), true);
                } else if (action.equals(DfuBaseService.DFU_Progress)) {
                    if (!isStart)
                        dismissPopup(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                //dialogInterface.cancel();
                                setProgress(intent);
                            }
                        });
                    else
                        setProgress(intent);
                } else if (action.equals(Action.Bm.ACTION_MW_AUTO_SCAN_MAXIMUM)) {
                    Log.i("ActivityMain", "스캔 중지 요청함");
                    SendReceive.sendSetScanMode(ActivityMain.this, ScanMode.MANUAL);
                    SendReceive.sendStopBluetooth(ActivityMain.this);
                }
            }
        };


        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.Vm.ACTION_MW_GET_QUERY_CODE);
        filter.addAction(Action.Am.ACTION_MW_STEP_CALORIE);
        filter.addAction(Action.Am.ACTION_MW_ACTIVITY_DATA);
        filter.addAction(Action.Am.ACTION_MW_SLEEP_DATA);
        filter.addAction(Action.Am.ACTION_MW_STRESS_DATA);
        filter.addAction(Action.Am.ACTION_MW_STRESS_ERR);
        filter.addAction(Action.Am.ACTION_MW_GENERATE_COACH_EXER_DATA);
        filter.addAction(Action.Am.ACTION_MW_GENERATE_ACTIVITY_DATA);
        filter.addAction(Action.Ec.ACTION_MW_BATTERY);
        filter.addAction(Action.Ec.ACTION_MW_USER_PROFILE);
        filter.addAction(Action.Ec.ACTION_MW_USER_DIETPERIOD);
        filter.addAction(Action.Ec.ACTION_MW_GET_SELECTED_PRODUCT);
        filter.addAction(Action.Bm.ACTION_MW_DATASYNC);
        filter.addAction(Action.Bm.ACTION_MW_CONNECTION_SUCCESS);
        filter.addAction(Action.Bm.ACTION_MW_CONNECTION_FAILED);
        filter.addAction(Action.Bm.ACTION_MW_CONNECTION_STATE);
        filter.addAction(Action.Bm.ACTION_MW_GENERATE_SCANLIST);
        filter.addAction(Action.Bm.ACTION_MW_DEVICE_INFORMATION);
        filter.addAction(Action.Bm.ACTION_MW_START_FIRMUP);
        filter.addAction(Action.Bm.ACTION_MW_FIRM_VERSION);
        filter.addAction(Action.Bm.ACTION_MW_FIRM_PROGRESS);
        filter.addAction(Action.Bm.ACTION_MW_END_OF_SCANLIST);
        filter.addAction(Action.Bm.ACTION_MW_AUTO_SCAN_MAXIMUM);
        filter.addAction(Action.Bm.ACTION_MW_PERIPHERAL_STATE);
        filter.addAction(Action.Bm.ACTION_MW_IS_BUSY_SENDER);
        filter.addAction(Action.Sm.ACTION_MW_IS_LIVE_APPLICATION);
        filter.addAction(Action.Sm.ACTION_MW_LOGIN_INFORMATION);


        //규창 펌웨어 라이브러리 브로드캐스트 수신
        /**
         * kr.co.greencomm.ibody24.DFUCustomBroadcast.Progress
         *
         * @Extras : Progress
         * @Data : Int
         * @Description : 라이브러리에서 펌웨어 업데이트시 방송하는 펌웨어 업로드 Progress
         */
        filter.addAction(DfuBaseService.DFU_Progress);


        /**
         * kr.co.greencomm.ibody24.DFUCustomBroadcast.Completed
         *
         * @Extras : 없음
         * @Data : 없음
         * @Description : 라이브러리에서 펌웨어 업데이트 진행중 성공시 방송하는 브로드캐스트
         */
        filter.addAction(DfuBaseService.DFU_Completed);

        /**
         * kr.co.greencomm.ibody24.DFUCustomBroadcast.Failed
         *
         * @Extras : 없음
         * @Data : 없음
         * @Description : 라이브러리에서 펌웨어 업데이트 진행중 실패시 방송하는 브로드캐스트
         */
        filter.addAction(DfuBaseService.DFU_Failed);
        registerReceiver(mReceiver, filter);
    }

    private void unregistReceiver() {
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SendReceive.getFirmVersion(this);
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregistReceiver();
        //finish();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        //super.onBackPressed();
    }

    public void checkPermission() {
        Log.i(TAG, "Checking permission.");
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
                return;
            }
        }
        Log.i(TAG, "permission has already been granted.");
    }

    private void requestPermission() {
        Log.i(TAG, "permission has NOT been granted. Requesting permission.");

        // 사용자가 한번 거절하면, 이유를 설명하고 다시 요청하기 위한 코드인데, 그냥 무조건 요청하는 형식으로 사용중.
        /*for (String permission : PERMISSIONS) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS,
                        REQUEST_PERMISSION);
                Log.i(TAG, "permission rationale to provide additional context.");
                return;
            }
        }*/

        ActivityCompat.requestPermissions(this, PERMISSIONS,
                REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            Log.i(TAG, "Received response for permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Log.i(TAG, "permissions were NOT granted.");
//                Snackbar.make(mLayout, R.string.permissions_not_granted,
//                        Snackbar.LENGTH_SHORT)
//                        .show();
                Intent intent = new Intent(this, MWService.class);
                stopService(intent);

                MessageDialog dialog = new MessageDialog(this,
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

    private void makeUpActivityData() {
        CoachResolver res = new CoachResolver();

        ArrayList<ActivityData> arr = res.getActivityDataArray();
        if (arr == null) {
            return;
        }

        long week = 86400000 * 7;
        long today = System.currentTimeMillis();
        for (ActivityData data : arr) {
            long diff = today - data.getStart_time();
            if (diff > week) {
                res.deleteActivityDataProvider(data.getIndex());
            }
        }
    }
}
