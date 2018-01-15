package ngo.teog.hstest.helpers;

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
    private String serviceNumber;
    private String manufacturer;
    private String model;
    private String imagePath;

    public HospitalDevice(int id, String assetNumber, String type, String serviceNumber, String manufacturer, String model, String imagePath) {
        this.id = id;
        this.assetNumber = assetNumber;
        this.type = type;
        this.serviceNumber = serviceNumber;
        this.manufacturer = manufacturer;
        this.model = model;
        this.imagePath = imagePath;
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

    public String getServiceNumber() {
        return serviceNumber;
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
}
