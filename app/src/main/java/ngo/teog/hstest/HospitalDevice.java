package ngo.teog.hstest;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Julian on 01.11.2017.
 */

public class HospitalDevice implements Serializable {
    private int id;
    private String name;
    private String type;
    private String manufacturer;
    private String serialNumber;
    private String ward;
    private String hospital;
    private boolean isWorking;
    private Date due;

    public HospitalDevice(int id, String name, String type, String manufacturer, String serialNumber, String ward, String hospital, boolean isWorking, Date due) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.manufacturer = manufacturer;
        this.serialNumber = serialNumber;
        this.ward = ward;
        this.hospital = hospital;
        this.isWorking = isWorking;
        this.due = due;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public Date getDue() {
        return due;
    }
}
