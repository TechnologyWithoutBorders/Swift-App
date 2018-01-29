package ngo.teog.swift.helpers;

import java.io.Serializable;
import java.util.Date;

/**
 * Die HospitalDevice-Klasse kapselt alle Informationen über ein Gerät. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * Created by Julian on 01.11.2017.
 */

public class HospitalDevice implements Serializable {
    private int id;
    private String assetNumber;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private String imagePath;
    private boolean isWorking;
    private Date nextMaintenance;

    public HospitalDevice(int id, String assetNumber, String type, String serialNumber, String manufacturer, String model, String imagePath, boolean isWorking, Date nextMaintenance) {
        this.id = id;
        this.assetNumber = assetNumber;
        this.type = type;
        this.serialNumber = serialNumber;
        this.manufacturer = manufacturer;
        this.model = model;
        this.imagePath = imagePath;
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

    public String getImagePath() {
        return imagePath;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public Date getNextMaintenance() {
        return nextMaintenance;
    }
}
