package ngo.teog.swift.helpers;

import java.util.List;

import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.User;

public class HospitalInfo {
    private int id;
    private String name;
    private long lastUpdate;
    private List<User> users;
    private List<HospitalDevice> devices;

    public HospitalInfo(int id, String name, long lastUpdate, List<User> users, List<HospitalDevice> devices) {
        this.name = name;
        this.users = users;
        this.devices = devices;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<HospitalDevice> getDevices() {
        return devices;
    }
}
