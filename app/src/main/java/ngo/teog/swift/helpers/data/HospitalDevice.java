package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Die HospitalDevice-Klasse kapselt alle Informationen über ein Gerät. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

//(foreignKeys = @ForeignKey(entity = Hospital.class, parentColumns = "id", childColumns = "hospital", onDelete = CASCADE))
@Entity(tableName = "devices", primaryKeys = {"id", "hospital"})
public class HospitalDevice implements Serializable {
    private int id;
    private String assetNumber;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private String ward;
    private int hospital;
    private int maintenanceInterval;
    private Date lastUpdate;

    public HospitalDevice(int id, String assetNumber, String type, String serialNumber, String manufacturer, String model, String ward, int hospital, int maintenanceInterval, Date lastUpdate) {
        this.id = id;
        this.assetNumber = assetNumber;
        this.type = type;
        this.serialNumber = serialNumber;
        this.manufacturer = manufacturer;
        this.model = model;
        this.ward = ward;
        this.hospital = hospital;
        this.maintenanceInterval = maintenanceInterval;
        this.lastUpdate = lastUpdate;
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

    public int getMaintenanceInterval() {
        return maintenanceInterval;
    }

    public void setMaintenanceInterval(int maintenanceInterval) {
        this.maintenanceInterval = maintenanceInterval;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
