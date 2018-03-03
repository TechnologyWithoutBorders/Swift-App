package ngo.teog.swift.helpers;

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

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
