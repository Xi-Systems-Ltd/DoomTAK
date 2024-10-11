package com.atakmap.android.doomtak;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.view.View;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.doomtak.plugin.R;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.log.Log;

public class DoomTakDropDownReceiver extends DropDownReceiver implements
        DropDown.OnStateListener {

    public static final String TAG = DoomTakDropDownReceiver.class
            .getSimpleName();

    public static final String SHOW_PLUGIN = "com.atakmap.android.doomtak.SHOW_PLUGIN";
    private final View dropdownView;
    private final Context pluginContext;
    private GLSurfaceView glSurfaceView;

    /**************************** CONSTRUCTOR *****************************/

    public DoomTakDropDownReceiver(final MapView mapView,
                                   final Context context) {
        super(mapView);
        this.pluginContext = context;

        // Remember to use the PluginLayoutInflator if you are actually inflating a custom view
        // In this case, using it is not necessary - but I am putting it here to remind
        // developers to look at this Inflator
        dropdownView = PluginLayoutInflater.inflate(context,
                R.layout.main_layout, null);

    }

    /**************************** PUBLIC METHODS *****************************/

    public void disposeImpl() {
    }

    /**************************** INHERITED METHODS *****************************/

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        if (action == null)
            return;

        if (action.equals(SHOW_PLUGIN)) {
            // Initialize the GLSurfaceView
            glSurfaceView = dropdownView.findViewById(R.id.opengl_surface_view);
            glSurfaceView.setEGLContextClientVersion(2); // Set OpenGL ES 2.0 context

            // Set the renderer to the native layer
            glSurfaceView.setRenderer(new DoomTakGLRenderer(pluginContext));

            Log.d(TAG, "showing plugin drop down");
            showDropDown(dropdownView, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                    HALF_HEIGHT, false, this);
        }
    }

    @Override
    public void onDropDownSelectionRemoved() {
    }

    @Override
    public void onDropDownVisible(boolean v) {
    }

    @Override
    public void onDropDownSizeChanged(double width, double height) {
    }

    @Override
    public void onDropDownClose() {
    }
}
