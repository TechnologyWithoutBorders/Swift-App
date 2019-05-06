package ngo.teog.swift.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;

import ngo.teog.swift.R;

public class DeviceState {

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

    private String statestring;
    private Drawable stateicon;
    private int backgroundcolor;

    public DeviceState(String statestring, Drawable stateicon, int backgroundcolor) {
        this.statestring = statestring;
        this.stateicon = stateicon;
        this.backgroundcolor = backgroundcolor;
    }

    public String getStatestring() {
        return statestring;
    }

    public Drawable getStateicon() {
        return stateicon;
    }

    public int getBackgroundcolor() {
        return backgroundcolor;
    }

    public static DeviceState buildState(int state, Context context){
        return new DeviceState(context.getResources().getStringArray(R.array.device_states)[state],context.getResources().getDrawable(ICONS[state]), context.getResources().getColor(COLORS[state]));
    }
}


