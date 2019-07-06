package ngo.teog.swift.helpers;

import java.util.Date;
import java.util.List;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.User;

public class HospitalInfo {
    private int id;
    private String name;
    private String location;
    private float longitude;
    private float latitude;
    private Date lastUpdate;
    private List<User> users;
    private List<DeviceInfo> devices;

    public HospitalInfo(int id, String name, String location, float longitude, float latitude, Date lastUpdate, List<User> users, List<DeviceInfo> devices) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.lastUpdate = lastUpdate;
        this.users = users;
        this.devices = devices;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<DeviceInfo> getDevices() {
        return devices;
    }
}
