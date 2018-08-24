package ngo.teog.swift.helpers;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

import ngo.teog.swift.gui.DeviceInfoActivity;

/**
 * Die HospitalDevice-Klasse kapselt alle Informationen über ein Gerät. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

public class HospitalDevice extends SearchObject {

    public static final int STATE_WORKING = 0;
    public static final int STATE_PM_DUE = 1;
    public static final int STATE_REPAIR_NEEDED = 2;
    public static final int STATE_IN_PROGRESS = 3;
    public static final int STATE_BROKEN_SALVAGE = 4;
    public static final int STATE_WORKING_WITH_LIMITATIONS = 5;

    private int id;
    private String assetNumber;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private String ward;
    private int state;
    private String hospital;
    private int maintenanceInterval;
    private Date lastReportDate;

    public HospitalDevice(int id, String assetNumber, String type, String serialNumber, String manufacturer, String model, String ward, int state, String hospital, int maintenanceInterval, Date lastReportDate) {
        this.id = id;
        this.assetNumber = assetNumber;
        this.type = type;
        this.serialNumber = serialNumber;
        this.manufacturer = manufacturer;
        this.model = model;
        this.ward = ward;
        this.state = state;
        this.hospital = hospital;
        this.maintenanceInterval = maintenanceInterval;
        this.lastReportDate = lastReportDate;
    }

    public int getID() {
        return id;
    }

    public String getAssetNumber() {
        return assetNumber;
    }

    public String getType() {
        return type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public int getState() {
        return state;
    }

    public String getHospital() {
        return hospital;
    }

    public int getMaintenanceInterval() {
        return maintenanceInterval;
    }

    public Date getLastReportDate() {
        return lastReportDate;
    }

    public String getWard() {
        return ward;
    }

    @Override
    public String getName() {
        return model;
    }

    @Override
    public String getInformation() {
        return type;
    }

    @Override
    public Class<?> getInfoActivityClass() {
        return DeviceInfoActivity.class;
    }

    @Override
    public String getExtraIdentifier() {
        return "device";
    }
}
