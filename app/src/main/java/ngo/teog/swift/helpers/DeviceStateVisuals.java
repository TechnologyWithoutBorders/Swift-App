package ngo.teog.swift.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;

import ngo.teog.swift.R;

/**
 * Maps visualization attributes for a given device state.
 * @author nitelow
 */
public class DeviceStateVisuals {

    private static final int[] ICONS= {R.drawable.ic_check,
                                        R.drawable.ic_maintenance,
                                        R.drawable.ic_repair,
                                        R.drawable.ic_in_progress,
                                        R.drawable.ic_broken_salvage,
                                        R.drawable.ic_working_with_limitations};

    private static final int[] COLORS = {android.R.color.holo_green_dark,
                                        android.R.color.holo_blue_light,
                                        android.R.color.holo_orange_dark,
                                        android.R.color.holo_green_light,
                                        android.R.color.holo_red_dark,
                                        android.R.color.holo_red_light};

    private String stateString;
    private Drawable stateIcon;
    private int backgroundColor;

    /**
     * Creates new visualization.
     * @param state Corresponding device state
     * @param context Context
     */
    public DeviceStateVisuals(int state, Context context) {
        this.stateString = context.getResources().getStringArray(R.array.device_states)[state];
        this.stateIcon = context.getResources().getDrawable(ICONS[state]);
        this.backgroundColor = context.getResources().getColor(COLORS[state]);
    }

    /**
     * Returns string representation of device state.
     * @return string representation
     */
    public String getStateString() {
        return stateString;
    }

    /**
     * Returns image representation of device state.
     * @return image representation
     */
    public Drawable getStateIcon() {
        return stateIcon;
    }

    /**
     * Returns background color corresponding to device state.
     * @return background color for device state
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }
}


