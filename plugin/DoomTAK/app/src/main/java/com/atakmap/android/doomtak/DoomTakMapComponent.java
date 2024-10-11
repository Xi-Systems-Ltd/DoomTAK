
package com.atakmap.android.doomtak;

import android.content.Context;
import android.content.Intent;

import com.atakmap.android.doomtak.plugin.PluginNativeLoader;

import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.AbstractMapComponent;
import com.atakmap.android.maps.MapView;

import com.atakmap.coremap.log.Log;
import com.atakmap.android.doomtak.plugin.R;

/**
 * This is an example of a MapComponent within the ATAK 
 * ecosphere.   A map component is the building block for all
 * activities within the system.   This defines a concrete 
 * thought or idea. 
 */
public class DoomTakMapComponent extends AbstractMapComponent {

    public static final String TAG = "DoomTakMapComponent";

    private Context pluginContext;

    private DoomTakDropDownReceiver ddr;

    @Override
    public void onStart(final Context context, final MapView view) {
        Log.d(TAG, "onStart");
    }

    @Override
    public void onPause(final Context context, final MapView view) {
        Log.d(TAG, "onPause");
    }

    @Override
    public void onResume(final Context context,
            final MapView view) {
        Log.d(TAG, "onResume");
    }

    @Override
    public void onStop(final Context context,
            final MapView view) {
        Log.d(TAG, "onStop");
    }

    public void onCreate(final Context context, Intent intent,
            final MapView view) {

        // Set the theme.  Otherwise, the plugin will look vastly different
        // than the main ATAK experience.   The theme needs to be set 
        // programatically because the AndroidManifest.xml is not used.
        context.setTheme(R.style.ATAKPluginTheme);

        pluginContext = context;

        // The MapComponent serves as the primary entry point for the plugin, load
        // the JNI library here
        PluginNativeLoader.init(pluginContext);

        // load the JNI library. Note that if the library has one or more
        // dependencies, those dependencies must be explicitly loaded in
        // correct order. Android will not automatically load dependencies
        // even if they are on the system library path
        PluginNativeLoader.loadLibrary("doomtak");

        ddr = new DoomTakDropDownReceiver(
                view, context);

        Log.d(TAG, "registering the plugin filter");
        AtakBroadcast.DocumentedIntentFilter ddFilter = new AtakBroadcast.DocumentedIntentFilter();
        ddFilter.addAction(DoomTakDropDownReceiver.SHOW_PLUGIN);
        registerReceiver(null, ddr, ddFilter);
    }


    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        Log.d(TAG, "calling on destroy");

    }
}
