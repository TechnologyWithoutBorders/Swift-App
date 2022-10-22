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
    private Hospital hospital;

    @Relation(parentColumn = "organizationalUnit", entityColumn = "id")
    private OrganizationalUnit organizationalUnit;

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

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit(OrganizationalUnit organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }
}
