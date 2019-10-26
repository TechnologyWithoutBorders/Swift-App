package ngo.teog.swift.helpers.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

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
