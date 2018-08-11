package ngo.teog.swift.helpers;

public final class DeviceState {
    public static final int WORKING = 0;
    public static final int PREVENTIVE_MAINTENANCE_DUE = 1;
    public static final int REPAIR_NEEDED = 2;
    public static final int IN_PROGRESS = 3;
    public static final int BROKEN_SALVAGE = 4;
    public static final int WORKING_WITH_RESTRICTIONS = 5;

    public static final String[] STATE_NAMES = {"working", "preventive maintenance due", "repair needed", "in progress", "broken/salvage", "working with restrictions"};
}
