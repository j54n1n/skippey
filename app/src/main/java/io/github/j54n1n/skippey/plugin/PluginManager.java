/*
 * Copyright (C) 2016 Julian Sanin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.j54n1n.skippey.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;

import io.github.j54n1n.skippey.plugin.controller.SemcFmRadioPlugin;
import io.github.j54n1n.skippey.plugin.controller.SoundcloudPlugin;

import static io.github.j54n1n.skippey.util.LogUtils.LOGD;
import static io.github.j54n1n.skippey.util.LogUtils.makeLogTag;

/**
 * Manages plugins of apps that are currently installed in the system.
 */
public class PluginManager extends BroadcastReceiver {

    private static final String TAG = makeLogTag(PluginManager.class);

    private static PluginManager instance = null;

    /**
     * Get the instance of the PluginManager.
     * @param context the application context.
     * @return the instance of the PluginManager.
     */
    public static PluginManager getInstance(Context context) {
        if (instance == null) {
            instance = new PluginManager(context);
            IntentFilter packageIntentFilter = new IntentFilter();
            packageIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            packageIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            packageIntentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            packageIntentFilter.addDataScheme("package");
            context.registerReceiver(instance, packageIntentFilter);
        }
        return instance;
    }

    private final List<LocalMediaKeyPlugin> localMediaKeyPlugins;

    private PluginManager(Context context) {
        localMediaKeyPlugins = new ArrayList<>();
        // Add plugins.
        updateLocalMediaKeyPlugins(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Re-enumerate plugins.
        updateLocalMediaKeyPlugins(context);
    }

    private void updateLocalMediaKeyPlugins(Context context) {
        localMediaKeyPlugins.clear();
        // Add only plugins that have their app installed in the system.
        try {
            localMediaKeyPlugins.add(new SemcFmRadioPlugin(context));
        } catch (Exception e) { /* NOT USED */ }
        try {
            localMediaKeyPlugins.add(new SoundcloudPlugin(context));
        } catch (Exception e) { /* NOT USED */ }
        // TODO: Add further plugins here.
        for (LocalMediaKeyPlugin localMediaKeyPlugin : localMediaKeyPlugins) {
            LOGD(TAG, "package " + localMediaKeyPlugin.packageName);
        }
    }

    /**
     * Retrieves the list of current installed LocalMediaKeyPlugins.
     * @return array of LocalMediaKeyPlugins.
     */
    public LocalMediaKeyPlugin[] getLocalMediaKeyPlugins() {
        return localMediaKeyPlugins.toArray(new LocalMediaKeyPlugin[localMediaKeyPlugins.size()]);
    }
}
