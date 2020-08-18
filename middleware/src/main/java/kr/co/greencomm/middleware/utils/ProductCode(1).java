package kr.co.greencomm.middleware.utils;

/**
 * Created by jeyang on 2016-08-26.
 */
public enum ProductCode {
    Coach(200003), Fitness(220004);

    private final int code;

    ProductCode(int code) {
        this.code = code;
    }

    public String getProductName() {
        switch (this) {
            case Coach:
                return "Coach";
            case Fitness:
                return "Fitness";
            default:
                return "";
        }
    }

    public String getBluetoothDeviceName() {
        switch (this) {
            case Coach:
                return "C";
            case Fitness:
                return "F";
            default:
                return "";
        }
    }

    public String getDfuBluetoothDeviceName() {
        switch (this) {
            case Coach:
                return "C1_DfuT";
            case Fitness:
                return "F1_DfuT";
            default:
                return "";
        }
    }

    public int getProductCode() {
        return code;
    }
}
