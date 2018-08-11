package ngo.teog.swift.helpers.filters;

/**
 * Created by Julian on 03.03.2018.
 */

public class Filter {
    private String type;
    private String value;

    public Filter(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public Filter(String type, int value) {
        this.type = type;
        this.value = Integer.toString(value);
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
