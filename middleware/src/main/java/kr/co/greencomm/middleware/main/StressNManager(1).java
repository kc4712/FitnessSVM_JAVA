package kr.co.greencomm.middleware.main;

import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

import kr.co.greencomm.middleware.service.MWBroadcastTop;
import kr.co.greencomm.middleware.video.iStressNormal;
import kr.co.greencomm.middleware.utils.container.DataBase;

/**
 * Created by jeyang on 16. 9. 23..
 */
public class StressNManager {
    private static final String tag = "StressNManager";

    public MWBroadcastTop mBrTop;
    protected DataBase mDatabase;
    private static StressNManager mSNManager = null;

    final ArrayList<Double> stressHrArray;// = new ArrayList<>();
    boolean isMeasuring;
    int measureSteady = 0;

    public static StressNManager getInstance(Context context) {
        if (mSNManager == null) {
            mSNManager = new StressNManager(context);
        }
        return mSNManager;
    }
    private final WeakReference<Context> m_context;
    protected Context getContext() {
        return m_context.get();
    }

    private StressNManager(Context context) {
        //super(context);
        isMeasuring = false;
        mDatabase = mDatabase.getInstance();
        stressHrArray = new ArrayList<>();
        m_context = new WeakReference<>(context);
    }

    //--- 재생을 담당하며 자동으로 Raw 데이터를 기록. 재생 시, 설정된 파일명으로 Raw 데이터를 기록한다.
    //---시작을 담당.
    //--- @return true:성공, false:실패(이미 재생중. end function을 실행 시켜주세요.)
    public void play() { //-> Bool {
        Log.i(tag, "StressNManager play()");

        isMeasuring = true;

        BluetoothLEManager.registDataCallback(new iStressNormal() {
            //double sensor로 raw 데이터가 들어옴.
            @Override
            public void onStresshr(double[] stressHr) {
                Log.d(tag, "play case"+stressHr+ "size"+stressHrArray.size());
                final double hearteRate = stressHr[7];
                measureSteady = measureSteady +1;
                Log.d(tag, "measureSteady:"+hearteRate +" " + measureSteady + "size" + stressHrArray.size());
                if (measureSteady >= 500) {
                    if (hearteRate != 0) {
                        stressHrArray.add(hearteRate);
                        Log.d(tag, "play case" + hearteRate + "size" + stressHrArray.size());
                    }
                }
                //Log.d(tag, "play case"+hearteRate+ "size"+stressHrArray.size());
            }
        });

    }


    public void end() {
        Log.i(tag, "StressNManager end()"+ stressHrArray.size());
        isMeasuring = false;

        if (stressHrArray.size() != 0) {
            minHRwithErrorCheck(stressHrArray);
        }else {
            mBrTop = new MWBroadcastTop(getContext());
            Log.i(tag, "심박 보정중 문제가 발생!!!");
            //MWNotification.postNotification(MWNotification.Bluetooth.NormalStressErrorInform)
            //mBrTop.sendBroadcastStressError();
            final short sendStressNIndex = 3;
            Log.i(tag, "스트레스 인덱스 체크!!!! " + sendStressNIndex);
            mDatabase.setStress(sendStressNIndex);
            mBrTop.sendBroadcastStressData(sendStressNIndex);
            measureSteady = 0;
        }
        //stressHrArray.clear();

    }
    public void minHRwithErrorCheck(final ArrayList<Double> HrArrayTemp){
        final ArrayList<Double> FindMinHR = new ArrayList<>();
        for (double i : HrArrayTemp) {
            if (i != 0 && i > 1) {
                FindMinHR.add(i);
            }
        }
        Log.i(tag, "보정된 심박" + FindMinHR);
        if (FindMinHR.size() == 0) {
            mBrTop = new MWBroadcastTop(getContext());
            Log.i(tag, "심박 보정중 문제가 발생!!!");
            //MWNotification.postNotification(MWNotification.Bluetooth.NormalStressErrorInform)
            //mBrTop.sendBroadcastStressError();
            final short sendStressNIndex = 3;
            Log.i(tag, "스트레스 인덱스 체크!!!! " + sendStressNIndex);
            mDatabase.setStress(sendStressNIndex);
            mBrTop.sendBroadcastStressData(mDatabase.getStress());
        } else {
            fBPM2NNCalc(Collections.min(FindMinHR), HrArrayTemp);
        }

    }


    private double calculateSum(final ArrayList<Double> array){
        if(array == null|| array.isEmpty()){
            return 0;
        }

        double sum = 0;
        for(double arr : array ){
            sum += arr;
        }
        return sum;
    }

    private double calculateAverage(final ArrayList<Double> array){
        if(array == null|| array.isEmpty()){
            return 0;
        }

        double sum = 0;
        for(double arr : array ){
            sum += arr;
        }
        return sum / array.size();
    }



    public void fBPM2NNCalc(final double FixHR,final ArrayList<Double> HrArrayTemp) {
        final ArrayList<Double> HrArray = new ArrayList<>();
        mBrTop = new MWBroadcastTop(getContext());


        //int BPMlength = HrArrayTemp.size();
        //double BPMavg = (HrArray.reduce(0, {$0 + $1}) / BPMlength).rountToDecimal(4);
        //double BPMavg = (HrArray.stream().mapToDouble(i -> i).average().orElse(0));

        for (double i : HrArrayTemp) {
            if (i > 1) {
                HrArray.add(i);
            }else {
                HrArray.add(FixHR);
            }
        }

        Log.i(tag, "*******************************************");
        Log.i(tag, "보정된 심박개수" + HrArray.size());
        Log.i(tag, "보정 안된 심박개수" + HrArrayTemp.size());
        Log.i(tag, "*******************************************");
        Log.i(tag, "보정된 심박" + HrArray);
        Log.i(tag, "보정 안된 심박" + HrArrayTemp);
        Log.i(tag, "*******************************************");

        final ArrayList<Double> NNValue = new ArrayList<>();

        final double BPMavg = calculateAverage(HrArray);

        final double maxHR = Collections.max(HrArray);
        final double minHR = Collections.min(HrArray);

        for (double i : HrArray) {
            NNValue.add(60000 / i);
        }
        Log.i(tag, "BPM->NN value" + NNValue);
        Log.i(tag, "*******************************************");


        final double vAVNN = calAVNN(NNValue);
        final double vSDNN = calSDNN(NNValue);
        final double vRMSSD = calRMSSD(NNValue);
        final double vPNN50 = calcPNN50(NNValue);





        /***** BPM *****/
        Log.i(tag, "심박 아이템갯수:" + HrArray.size());
        Log.i(tag, "최고 심박수:" + maxHR);
        Log.i(tag, "최저 심박수:"+ minHR);
        //Log.i(tag, "전체값:"+calculateSum(HrArray))
        Log.i(tag, "평균:" + BPMavg);

        /***** NN *****/
        Log.i(tag, "vAVNN:"+ vAVNN);
        Log.i(tag, "vSDNN:" + vSDNN);
        Log.i(tag, "vRMSSD:"+ vRMSSD);
        Log.i(tag, "PNN50:"+ vPNN50);



        final short sendStressNIndex;

        if (1 < vSDNN && vSDNN < 20) {
            sendStressNIndex = 4;
        }
        else if (20 < vSDNN && vSDNN < 40) {
            sendStressNIndex = 3;
        }
        else if(40 < vSDNN && vSDNN < 60) {
            sendStressNIndex = 2;
        }
        else if(60 < vSDNN && vSDNN < 200) {
            sendStressNIndex = 1;
        }
        else if(vSDNN == 0){
            sendStressNIndex = 3;
        }
        else {
            sendStressNIndex = 3;
        }


        Log.i(tag, "스트레스 인덱스 체크!!!! " + sendStressNIndex);
        mDatabase.setStress(sendStressNIndex);
        mBrTop.sendBroadcastStressData(mDatabase.getStress());

        HrArray.clear();
        HrArrayTemp.clear();
        NNValue.clear();
        measureSteady = 0;

        /* 참고 BluetoothLEManager
        case Stress:
        mDataBase.setStress(getDataShort(getFrame, 2));
        mBrTop.sendBroadcastStressData(mDataBase.getStress());
        break;
        */
    }

    public double calAVNN(final ArrayList<Double> interval) {
        double sum = 0.0;

        for (double i : interval) {
            sum += i;
        }
        int size = interval.size();
        return sum / size;
    }

    public double calSDNN(final ArrayList<Double> interval) {
        double average = calAVNN(interval);
        double d = 0.0;
        for (double i : interval) {
            double v = i - average;
            d += (v * v);
        }
        int size = interval.size();
        return Math.sqrt(d / size);
    }


    public double calRMSSD(final ArrayList<Double> interval) {
        double d = 0.0;
        int size = interval.size();
        for (int i = 0 ; i < size - 1 ; i++) {
            double interval0 = interval.get(i);
            double interval1 = interval.get(i + 1);
            double diff = interval1 - interval0;
            d += (diff * diff);
        }
        return Math.sqrt(d / size - 1);
    }

    public double calcPNN50(final ArrayList<Double> interval) {
        int count = 0;
        int size = interval.size();

        for (int i = 0 ; i < size - 1 ; i++) {
            double interval0 = interval.get(i);
            double interval1 = interval.get(i + 1);
            double diff = interval1 - interval0;
            if (diff < 0.0) {
                diff = -diff;
            }
            if (diff > 50.0) {
                //grater than 50ms
                count += 1;
            }

        }
        return  count / size * 100.0;
    }

}
