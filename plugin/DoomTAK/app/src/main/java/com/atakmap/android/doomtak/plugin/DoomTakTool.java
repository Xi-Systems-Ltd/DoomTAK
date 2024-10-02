
package com.atakmap.android.doomtak.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.atak.plugins.impl.AbstractPluginTool;
import com.atakmap.android.doomtak.plugin.R;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;

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
                context.getResources().getDrawable(R.drawable.ic_launcher),
                "com.atakmap.android.doomtak.plugin.TEST");
        AtakBroadcast.getInstance().registerReceiver(br,
                new AtakBroadcast.DocumentedIntentFilter("com.atakmap.android.doomtak.plugin.TEST"));
    }

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           MapView mv = MapView.getMapView();
           mv.post(new Runnable() {
               @Override
               public void run() {
                   Toast.makeText(mv.getContext(), "Message from JNI: " + myNativeMethod(),
                           Toast.LENGTH_LONG).show();
               }
           });
        }
    };


    @Override
    public void dispose() {
        AtakBroadcast.getInstance().unregisterReceiver(br);
    }

    private static native String myNativeMethod();

}
