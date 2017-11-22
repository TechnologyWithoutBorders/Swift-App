package ngo.teog.hstest.helpers;

/**
 * Created by Julian on 18.11.2017.
 */

public class UserFilter {
    public static final String ID = "id";
    public static final String NAME = "name";

    private String type;
    private String value;

    public UserFilter(String type, String value) {
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
