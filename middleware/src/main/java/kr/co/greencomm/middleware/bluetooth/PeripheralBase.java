package kr.co.greencomm.middleware.bluetooth;

import android.content.Context;

import kr.co.greencomm.middleware.main.BluetoothLEManager;
import kr.co.greencomm.middleware.utils.ProductCode;

/**
 * Created by jeyang on 2016-08-26.
 */
public class PeripheralBase extends Thread {
    private boolean set_vibrateLock = false;
    private int[] motionVibrate = new int[]{200, 100, 200, 100, 200, 2000 /*dummy*/};

    protected BluetoothLEManager mBle;

    public PeripheralBase(Context context) {
        super();
        mBle = BluetoothLEManager.getInstance(context);
    }

    public void startVibrate() {
        if (set_vibrateLock) {
            return;
        }

        start();
        set_vibrateLock = true;
    }

    public void stopVibrate() {
        set_vibrateLock = false;

        this.interrupt();
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void vibrate() throws InterruptedException {
        String product = DeviceBaseScan.getSelectedDeviceName();

        for (int i = 0; i < motionVibrate.length; i++) {
            if (i % 2 == 0) {
                if (product.equals(ProductCode.Fitness.getBluetoothDeviceName())) {
                    mBle.requestVibrate(RequestAction.Start);
                } else if (product.equals(ProductCode.Coach.getBluetoothDeviceName())) {
                    mBle.requestVibrateProductCoach(RequestAction.Start);
                }
            } else {
                if (product.equals(ProductCode.Fitness.getBluetoothDeviceName())) {
                    mBle.requestVibrate(RequestAction.End);
                } else if (product.equals(ProductCode.Coach.getBluetoothDeviceName())) {
                    mBle.requestVibrateProductCoach(RequestAction.End);
                }
            }

            Thread.sleep(motionVibrate[i]);
        }

        set_vibrateLock = false;
    }

    @Override
    public void run() {
        super.run();
        try {
            vibrate();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
