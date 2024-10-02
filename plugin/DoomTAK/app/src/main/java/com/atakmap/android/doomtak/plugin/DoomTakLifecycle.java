package com.atakmap.android.doomtak.plugin;


import com.atak.plugins.impl.AbstractPlugin;
import gov.tak.api.plugin.IServiceController;
import com.atak.plugins.impl.PluginContextProvider;
import com.atakmap.android.doomtak.DoomTakMapComponent;


/**
 *
 * 
 *
 */
public class DoomTakLifecycle extends AbstractPlugin {

   private final static String TAG = "DoomTakLifecycle";

   public DoomTakLifecycle(IServiceController serviceController) {
        super(serviceController, new DoomTakTool(serviceController.getService(PluginContextProvider.class).getPluginContext()), new DoomTakMapComponent());
    }
}

