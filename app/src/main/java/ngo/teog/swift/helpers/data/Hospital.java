package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;

@Entity
public class Hospital implements Serializable {
    @PrimaryKey
    private int id;
    private String name;

    public Hospital(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
