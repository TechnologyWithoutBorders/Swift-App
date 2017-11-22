package ngo.teog.hstest.helpers;

import java.io.Serializable;
import java.util.Date;

import ngo.teog.hstest.HospitalDevice;
import ngo.teog.hstest.User;

/**
 * Created by Julian on 08.11.2017.
 */

public class Report implements Serializable {
    private int id;
    private User author;
    private HospitalDevice device;
    private Date date;

    public Report(int id, User author, HospitalDevice device, Date date) {
        this.id = id;
        this.author = author;
        this.device = device;
        this.date = date;
    }

    public int getID() {
        return id;
    }

    public HospitalDevice getDevice() {
        return device;
    }

    public Date getDate() {
        return date;
    }

    public User getAuthor() {
        return author;
    }
}
