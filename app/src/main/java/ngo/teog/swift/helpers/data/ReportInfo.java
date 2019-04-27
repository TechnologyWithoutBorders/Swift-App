package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Relation;

import java.io.Serializable;
import java.util.List;

import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;

public class ReportInfo implements Serializable {
    @Embedded
    private Report report;

    @Relation(parentColumn = "author", entityColumn = "id")
    private List<User> authors;

    public ReportInfo(Report report) {
        this.report = report;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public List<User> getAuthors() {
        return authors;
    }

    public void setAuthors(List<User> authors) {
        this.authors = authors;
    }
}
