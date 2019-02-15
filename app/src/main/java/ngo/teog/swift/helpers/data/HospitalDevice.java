package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.helpers.SearchObject;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Die HospitalDevice-Klasse kapselt alle Informationen über ein Gerät. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

@Entity(foreignKeys = @ForeignKey(entity = Hospital.class, parentColumns = "id", childColumns = "hospital", onDelete = CASCADE))
public class HospitalDevice extends SearchObject {
    @PrimaryKey
    private int id;
    private String assetNumber;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private String ward;
    private int state;
    private int hospital;
    private int maintenanceInterval;
    private long lastUpdate;

    public HospitalDevice(int id, String assetNumber, String type, String serialNumber, String manufacturer, String model, String ward, int state, int hospital, int maintenanceInterval, long lastUpdate) {
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
        this.lastUpdate = lastUpdate;
    }

    public int getId() {
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

    public int getHospital() {
        return hospital;
    }

    public int getMaintenanceInterval() {
        return maintenanceInterval;
    }

    public String getWard() {
        return ward;
    }

    public long getLastUpdate() {
        return lastUpdate;
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
