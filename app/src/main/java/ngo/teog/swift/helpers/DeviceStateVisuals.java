package ngo.teog.swift.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import ngo.teog.swift.R;

/**
 * Maps visualization attributes for a given device state.
 * @author nitelow
 */
public class DeviceStateVisuals {

    private static final int[] ICONS = {R.drawable.ic_check_white_24dp,
                                        R.drawable.ic_timer_white_24dp,
                                        R.drawable.ic_build_white_24dp,
                                        R.drawable.ic_hourglass_empty_white_24dp,
                                        R.drawable.ic_block_white_24dp,
                                        R.drawable.ic_warning_white_24dp};

    private static final int[] COLORS = {android.R.color.holo_green_dark,
                                        android.R.color.holo_blue_light,
                                        android.R.color.holo_orange_dark,
                                        android.R.color.holo_green_light,
                                        android.R.color.holo_red_dark,
                                        android.R.color.holo_red_light};

    private final String stateString;
    private final Drawable stateIcon;
    private final int backgroundColor;

    /**
     * Creates new visualization.
     * @param state Corresponding device state
     * @param context Context
     */
    public DeviceStateVisuals(int state, Context context) {
        this.stateString = context.getResources().getStringArray(R.array.device_states)[state];
        this.stateIcon = ResourcesCompat.getDrawable(context.getResources(), ICONS[state], null);
        this.backgroundColor = context.getResources().getColor(COLORS[state], context.getTheme());
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


