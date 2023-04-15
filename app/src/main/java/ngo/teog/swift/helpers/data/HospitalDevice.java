package ngo.teog.swift.helpers.data;

import androidx.room.Entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Definition of "devices" table in Room database and wrapper class for a device.
 * @author nitelow
 */
//(foreignKeys = @ForeignKey(entity = Hospital.class, parentColumns = "id", childColumns = "hospital", onDelete = CASCADE))
@Entity(tableName = "devices", primaryKeys = {"id", "hospital"})
public class HospitalDevice implements Serializable {
    private final int id;
    private String assetNumber;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private Integer organizationalUnit;
    private int hospital;
    private int maintenanceInterval;

    private final boolean valid;
    private Date lastUpdate;
    private Date lastSync;

    public HospitalDevice(int id, String assetNumber, String type, String serialNumber, String manufacturer, String model, Integer organizationalUnit, int hospital, int maintenanceInterval, boolean valid, Date lastUpdate) {
        this.id = id;
        this.assetNumber = assetNumber;
        this.type = type;
        this.serialNumber = serialNumber;
        this.manufacturer = manufacturer;
        this.model = model;
        this.organizationalUnit = organizationalUnit;
        this.hospital = hospital;
        this.maintenanceInterval = maintenanceInterval;
        this.valid = valid;
        this.lastUpdate = lastUpdate;
        this.lastSync = new Date();
    }

    public int getId() {
        return id;
    }

    public String getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getHospital() {
        return hospital;
    }

    public void setHospital(int hospital) {
        this.hospital = hospital;
    }

    /**
     * Returns the maintenance interval in weeks.
     * @return Maintenance interval
     */
    public int getMaintenanceInterval() {
        return maintenanceInterval;
    }

    public void setMaintenanceInterval(int maintenanceInterval) {
        this.maintenanceInterval = maintenanceInterval;
    }

    public Integer getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit(Integer organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    public boolean getValid() {
        return valid;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getLastSync() {
        return lastSync;
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
    }
}
