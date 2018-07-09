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

    public static final String[] STATES = {"working", "pm due", "repair needed", "in progress", "broken/salvage", "working with limitations"};

    private int id;
    private String assetNumber;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private boolean isWorking;
    private Date nextMaintenance;
    private int state = 1;

    public HospitalDevice(int id, String assetNumber, String type, String serialNumber, String manufacturer, String model, boolean isWorking, Date nextMaintenance) {
        this.id = id;
        this.assetNumber = assetNumber;
        this.type = type;
        this.serialNumber = serialNumber;
        this.manufacturer = manufacturer;
        this.model = model;
        this.isWorking =  isWorking;
        this.nextMaintenance = nextMaintenance;
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

    public boolean isWorking() {
        return isWorking;
    }

    public Date getNextMaintenance() {
        return nextMaintenance;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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
