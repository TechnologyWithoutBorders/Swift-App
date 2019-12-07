package ngo.teog.swift.helpers.export;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;

public class DeviceDump implements Serializable {
    @Embedded
    private HospitalDevice device;

    @Relation(parentColumn = "id", entityColumn = "device")
    private List<Report> reports;

    public DeviceDump(HospitalDevice device) {
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
