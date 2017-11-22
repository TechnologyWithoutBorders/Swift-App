package ngo.teog.hstest;

import java.io.Serializable;
import java.util.ArrayList;

import ngo.teog.hstest.helpers.Report;

/**
 * Created by Julian on 07.11.2017.
 */

public class User implements Serializable {
    private String name;
    private String telephone;
    private String eMail;
    private String hospital;
    private String position;
    private String qualifications;

    public User(String name, String telephone, String eMail, String hospital, String position, String qualifications) {
        this.name = name;
        this.telephone = telephone;
        this.eMail = eMail;
        this.hospital = hospital;
        this.position = position;
        this.qualifications = qualifications;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEMail() {
        return eMail;
    }

    public String getHospital() {
        return hospital;
    }

    public String getPosition() {
        return position;
    }

    public String getQualifications() {
        return qualifications;
    }
}
