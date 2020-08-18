package kr.co.greencomm.ibody24.coach.data;

import android.util.Log;

import kr.co.greencomm.ibody24.coach.activity.ActivityChartMain;
import kr.co.greencomm.ibody24.coach.activity.coach.ActivityHome;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.provider.CoachResolver;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.webs.QueryStatus;
import kr.co.greencomm.ibody24.coach.webs.QueryListener;
import kr.co.greencomm.middleware.provider.SQLHelper;
import kr.co.greencomm.middleware.utils.container.ActivityData;
import kr.co.greencomm.middleware.utils.container.CoachActivityData;

/**
 * Created by jeyang on 16. 9. 22..
 */
public class DataTransfer extends Thread implements QueryListener {
    private static String TAG = DataTransfer.class.getSimpleName();

    private CoachActivityData coach_Data;
    private ActivityData activity_Data;

    public static boolean isRun;

    private boolean more_than_one_ExerData = false;
    private boolean finish = false;
    private boolean continues = false;

    public DataTransfer() {
        setName(DataTransfer.class.getSimpleName());
    }

    public void cancel() {
        isRun = false;
        try {
            join();
            interrupt();
            finish = true;
            continues = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (isRun) {
            Log.e(TAG, "already run transfer. escape loop");
            return;
        }
        isRun = true;
        try {
            do {
                Log.d(TAG, "Data Upload...");
                finish = checkData();
                if (finish) {
                    Log.d(TAG, "no data. cancel.");
                    if (more_than_one_ExerData) {
                        TodayInfo today = CoachBaseActivity.DB_Today;
                        today.setUser(CoachBaseActivity.DB_User.getCode());
                        today.runUserQueryToday(this);
                    }

                    cancel();
                    break;
                }

                execUpload();

                while (!continues) {
                    Thread.sleep(1000);
                }
            } while (isAlive());
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException : " + e.getStackTrace());
        }

        Log.d(TAG, "exit upload..");
    }

    private boolean checkData() {
        CoachResolver res = new CoachResolver();

        boolean flag = false;

        CoachActivityData data1;
        ActivityData data2;
        if ((data1 = res.getCoachActivityDataNeedUploadProvider()) != null) {
            coach_Data = data1;
            flag = true;
        }

        if ((data2 = res.getActivityDataNeedUploadProvider()) != null) {
            activity_Data = data2; // resolver 작성 필요.
            flag = true;
        }

        if (flag) {
            return false;
        }

        return true;
    }

    private void execUpload() {
        if (activity_Data != null) {
            continues = false;

            ExerciseInfo parser = new ExerciseInfo();
            parser.setActivityInfo(activity_Data);

            parser.runUserQueryInsert(QueryCode.InsertFitness, this);
        }

        if (coach_Data != null) {
            continues = false;

            ExerciseInfo parser = new ExerciseInfo();
            parser.setExerciseInfo(coach_Data);

            parser.runUserQueryInsert(QueryCode.InsertExercise, this);
        }
    }

    private void success(QueryCode queryCode) {
        CoachResolver res = new CoachResolver();

        if (queryCode == QueryCode.InsertExercise) {
            coach_Data = null;
            more_than_one_ExerData = true;
        } else if (queryCode == QueryCode.InsertFitness) {
            res.updateActivityDataProvider(activity_Data, SQLHelper.SET_UPLOAD);
            activity_Data = null;
        } else if (queryCode == QueryCode.ExerciseToday) {
            if (ActivityHome.Home != null)
                ActivityHome.Home.updateGraph();
            ActivityChartMain.onGraphData();
        }

        continues = true;
    }

    private void fail() {
        continues = true;
    }

    @Override
    public void onQueryResult(QueryCode queryCode, String request, String result) {
        Log.d(TAG, "transfer success!!");
        success(queryCode);
    }

    @Override
    public void onQueryError(QueryCode queryCode, QueryStatus nErrorCode, String szErrMessage) {
        Log.d(TAG, "transfer fail.. :: " + nErrorCode);
        if (nErrorCode == QueryStatus.ERROR_Web_Read) {
            cancel();
            return;
        }
        fail();
    }
}
