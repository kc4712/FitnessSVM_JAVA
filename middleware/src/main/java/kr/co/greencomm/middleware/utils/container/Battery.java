package kr.co.greencomm.middleware.utils.container;

/**
 * Created by jeyang on 2016-08-26.
 */
public class Battery {
    private Short status = 0;
    private Short voltage = 0;

    public Battery() {

    }

    public Battery(Short status, Short voltage) {
        this.status = status;
        this.voltage = voltage;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Short getVoltage() {
        return voltage;
    }

    public void setVoltage(Short voltage) {
        this.voltage = voltage;
    }
}
