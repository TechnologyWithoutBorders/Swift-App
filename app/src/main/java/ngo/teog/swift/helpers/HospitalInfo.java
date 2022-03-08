package ngo.teog.swift.helpers;

import java.util.Date;
import java.util.List;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.User;

/**
 * Contains all necessary information (including member and devices) about a hospital.
 * This class does not implement any database attributes and is therefore not used
 * for database communication.
 * @author nitelow
 */
public class HospitalInfo {
    private Date syncTime;
    private final int id;
    private final String name;
    private final String location;
    private final float longitude;
    private final float latitude;
    private Date lastUpdate;
    private final List<User> users;
    private final List<DeviceInfo> devices;

    /**
     * Creates a new hospital info object.
     * @param id ID
     * @param name Name
     * @param location Location
     * @param longitude Longitude of location
     * @param latitude Latitude of location
     * @param lastUpdate last time the hospital has been updated
     * @param users list of members
     * @param devices list of devices
     */
    public HospitalInfo(Date syncTime, int id, String name, String location, float longitude, float latitude, Date lastUpdate, List<User> users, List<DeviceInfo> devices) {
        this.syncTime = syncTime;
        this.id = id;
        this.name = name;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.lastUpdate = lastUpdate;
        this.users = users;
        this.devices = devices;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    /**
     * Returns ID of hospital.
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns name of hospital.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns location of hospital.
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns longitude of hospital location.
     * @return longitude of location
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Returns latitude of hospital location.
     * @return latitude of location
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Returns date of last time the hospital has been updated.
     * @return date of last update
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Returns list of users associated with hospital.
     * @return list of hospital's members
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Returns list of devices associated with hospital.
     * @return list of hospital's devices
     */
    public List<DeviceInfo> getDevices() {
        return devices;
    }

    /**
     * Sets date of the last time the hospital has been updated.
     * @param lastUpdate date of last update
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
