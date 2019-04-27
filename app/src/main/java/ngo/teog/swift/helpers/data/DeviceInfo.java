package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.io.Serializable;
import java.util.List;

public class DeviceInfo implements Serializable {
    @Embedded
    private HospitalDevice device;

    @Relation(parentColumn = "hospital", entityColumn = "id")
    private List<Hospital> hospitals;

    @Relation(parentColumn = "id", entityColumn = "device", entity = Report.class)
    private List<ReportInfo> reports;

    public DeviceInfo(HospitalDevice device) {
        this.device = device;
    }

    public HospitalDevice getDevice() {
        return device;
    }

    public void setDevice(HospitalDevice device) {
        this.device = device;
    }

    public List<ReportInfo> getReports() {
        return reports;
    }

    public void setReports(List<ReportInfo> reports) {
        this.reports = reports;
    }

    public List<Hospital> getHospitals() {
        return hospitals;
    }

    public void setHospitals(List<Hospital> hospitals) {
        this.hospitals = hospitals;
    }
}
