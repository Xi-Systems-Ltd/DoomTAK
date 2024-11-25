
package com.atakmap.android.doomtak.plugin;

import android.content.Context;

import com.atak.plugins.impl.AbstractPluginTool;
import com.atakmap.android.doomtak.DoomTakDropDownReceiver;

import gov.tak.api.util.Disposable;

/**
 * Please note:
 *     Support for versions prior to 4.5.1 can make use of a copy of AbstractPluginTool shipped with
 *     the plugin.
 */
public class DoomTakTool extends AbstractPluginTool implements Disposable {

    public DoomTakTool(Context context) {
        super(context,
                context.getString(R.string.app_name),
                context.getString(R.string.app_name),
                context.getResources().getDrawable(R.drawable.xi_icon),
                DoomTakDropDownReceiver.SHOW_PLUGIN);
    }

    @Override
    public void dispose() {
    }

}
