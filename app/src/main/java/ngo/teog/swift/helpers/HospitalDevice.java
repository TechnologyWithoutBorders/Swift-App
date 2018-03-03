package ngo.teog.swift.helpers;

import java.io.Serializable;
import java.util.Date;

/**
 * Die HospitalDevice-Klasse kapselt alle Informationen über ein Gerät. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

public class HospitalDevice implements Serializable {
    private int id;
    private String assetNumber;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private boolean isWorking;
    private Date nextMaintenance;

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
}
