package ngo.teog.swift.helpers.filters;

/**
 * Created by Julian on 24.03.2018.
 */

public class ReportFilter {
    public static final String ID = "r_ID";
    public static final String AUTHOR = "r_author";
    public static final String DEVICE = "r_device";
    public static final String TITLE = "r_title";

    private String type;
    private String value;

    public ReportFilter(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
