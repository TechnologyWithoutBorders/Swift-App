package ngo.teog.swift.helpers;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.io.Serializable;
import java.util.List;

import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;

public class DeviceInfo implements Serializable {
    @Embedded
    private HospitalDevice device;
    @Relation(parentColumn = "id", entityColumn = "device")
    private List<Report> reports;

    public DeviceInfo(HospitalDevice device) {
        this.device = device;
    }

    public HospitalDevice getDevice() {
        return device;
    }

    public void setDevice(HospitalDevice device) {
        this.device = device;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }
}
