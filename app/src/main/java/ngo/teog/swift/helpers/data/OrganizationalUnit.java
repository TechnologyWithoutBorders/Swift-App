package ngo.teog.swift.helpers.data;

import androidx.room.Entity;

import java.io.Serializable;

@Entity(tableName = "organizational_units", primaryKeys = {"id", "hospital"})
public class OrganizationalUnit implements Serializable {
    private final int id;
    private final int hospital;
    private final String name;

    public OrganizationalUnit(int id, int hospital, String name) {
        this.id = id;
        this.hospital = hospital;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getHospital() {
        return hospital;
    }

    public String getName() {
        return name;
    }
}
