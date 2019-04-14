package ngo.teog.swift.helpers;

import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;

public class DeviceInfo {
    private HospitalDevice device;
    private Report lastReport;//TODO ne Liste würde mehr Sinn machen

    public DeviceInfo(HospitalDevice device, Report lastReport) {
        this.device = device;
        this.lastReport = lastReport;
    }

    public HospitalDevice getDevice() {
        return device;
    }

    public Report getLastReport() {
        return lastReport;
    }
}
