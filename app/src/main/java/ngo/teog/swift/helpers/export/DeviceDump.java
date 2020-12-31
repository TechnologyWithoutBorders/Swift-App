package ngo.teog.swift.helpers.export;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;

/**
 * Wraps all information which is needed to export a device and its reports e.g. in CSV format.
 * @author nitelow
 */
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
