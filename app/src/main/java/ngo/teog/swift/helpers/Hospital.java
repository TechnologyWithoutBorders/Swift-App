package ngo.teog.swift.helpers;

import java.io.Serializable;

public class Hospital implements Serializable {
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
