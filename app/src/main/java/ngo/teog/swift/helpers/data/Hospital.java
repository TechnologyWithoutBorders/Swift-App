package ngo.teog.swift.helpers.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

/**
 * Definition of "hospitals" table in Room database and wrapper class for a hospital.
 * @author nitelow
 */
@Entity(tableName = "hospitals")
public class Hospital implements Serializable {
    @PrimaryKey
    private final int id;
    private final String name;
    private final String location;
    private final float longitude;
    private final float latitude;
    private final Date lastUpdate;
    private Date lastSync;

    public Hospital(int id, String name, String location, float longitude, float latitude, Date lastUpdate) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.lastUpdate = lastUpdate;
        this.lastSync = new Date();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public String getLocation() {
        return location;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Date getLastSync() {
        return lastSync;
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
    }
}
