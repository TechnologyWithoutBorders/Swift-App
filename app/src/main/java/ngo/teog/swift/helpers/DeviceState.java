package ngo.teog.swift.helpers;

/**
 * Contains device state constants.
 * @author nitelow
 */
public final class DeviceState {
    public static final int[] IDS = {0, 1, 2, 3, 4, 5};

    public static final int WORKING = 0;
    public static final int MAINTENANCE = 1;
    public static final int BROKEN = 2;
    public static final int IN_PROGRESS = 3;
    public static final int SALVAGE = 4;
    public static final int LIMITATIONS = 5;
}
