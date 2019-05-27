package ngo.teog.swift.helpers.filters;

/**
 * Created by Julian on 24.03.2018.
 */

public class ReportFilter {
    public static final String ID = "r_ID";
    public static final String AUTHOR = "r_author";
    public static final String DEVICE = "r_device";
    public static final String DESCRIPTION = "r_description";
    public static final String PREVIOUS_STATE = "r_previous_state";
    public static final String CURRENT_STATE = "r_current_state";
    public static final String DATETIME = "r_datetime";

    public static final String ACTION_FETCH_REPORT = "fetch_report";

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
