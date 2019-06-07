package ngo.teog.swift.helpers.data;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

public class UserInfo implements Serializable {
    @Embedded
    private User user;

    @Relation(parentColumn = "hospital", entityColumn = "id")
    private List<Hospital> hospitals;

    public UserInfo(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Hospital> getHospitals() {
        return hospitals;
    }

    public void setHospitals(List<Hospital> hospitals) {
        this.hospitals = hospitals;
    }
}
