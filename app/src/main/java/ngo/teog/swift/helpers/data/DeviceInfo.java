package ngo.teog.swift.helpers.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

/**
 * Wraps all information regarding a device. Contains the device itself, all of its reports and the corresponding hospital.
 * @author nitelow
 */
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
