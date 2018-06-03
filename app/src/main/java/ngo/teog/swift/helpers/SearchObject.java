package ngo.teog.swift.helpers;

import java.io.Serializable;

public abstract class SearchObject implements Serializable {
    public abstract String getName();
    public abstract String getInformation();
    public abstract Class<?> getInfoActivityClass();
    public abstract String getExtraIdentifier();
}
