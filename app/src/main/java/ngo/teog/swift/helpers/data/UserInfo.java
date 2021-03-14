package ngo.teog.swift.helpers.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;

/**
 * Wraps all information regarding a user. Contains the user itself and the corresponding hospital.
 * @author nitelow
 */
public class UserInfo implements Serializable {
    @Embedded
    private User user;

    @Relation(parentColumn = "hospital", entityColumn = "id")
    private Hospital hospital;

    public UserInfo(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }
}
