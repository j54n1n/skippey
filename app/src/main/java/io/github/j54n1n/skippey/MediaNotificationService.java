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

package io.github.j54n1n.skippey;

import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v7.app.NotificationCompat;

import io.github.j54n1n.skippey.plugin.LocalMediaKeyPlugin;

import static io.github.j54n1n.skippey.util.LogUtils.LOGD;
import static io.github.j54n1n.skippey.util.LogUtils.makeLogTag;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MediaNotificationService extends NotificationListenerService {

    private static final String TAG = makeLogTag(MediaNotificationService.class);

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        // The service should wait for the onListenerConnected() event before performing any
        // operations. The requestRebind(ComponentName) method is the only one that is safe to call
        // before onListenerConnected() or after onListenerDisconnected().
        LOGD(TAG, "onListenerConnected");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        LocalMediaKeyPlugin[] localMediaKeyPlugins = ((SkippeyApplication) getApplication())
                .getPluginManager().getLocalMediaKeyPlugins();
        for (LocalMediaKeyPlugin localMediaKeyPlugin : localMediaKeyPlugins) {
            if (localMediaKeyPlugin.packageName.equals(sbn.getPackageName())) {
                // Push the media actions to the plugin.
                Notification notification = sbn.getNotification();
                final int actionCount = NotificationCompat.getActionCount(notification);
                NotificationCompat.Action[] actions = new NotificationCompat.Action[actionCount];
                for (int actionIndex = 0; actionIndex < actionCount; actionIndex++) {
                    actions[actionIndex] = NotificationCompat.getAction(notification, actionIndex);
                }
                localMediaKeyPlugin.onMediaNotificationActionPosted(actions);
                LOGD(TAG, "posted notification for plugin " + localMediaKeyPlugin.packageName);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        LocalMediaKeyPlugin[] localMediaKeyPlugins = ((SkippeyApplication) getApplication())
                .getPluginManager().getLocalMediaKeyPlugins();
        for (LocalMediaKeyPlugin localMediaKeyPlugin : localMediaKeyPlugins) {
            if (localMediaKeyPlugin.packageName.equals(sbn.getPackageName())) {
                // Notify the plugin that the media actions are not valid anymore.
                localMediaKeyPlugin.onMediaNotificationActionRemoved();
                LOGD(TAG, "removed notification for plugin " + localMediaKeyPlugin.packageName);
            }
        }
    }
}
