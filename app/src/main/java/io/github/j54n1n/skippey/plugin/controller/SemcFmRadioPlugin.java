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

package io.github.j54n1n.skippey.plugin.controller;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;

import io.github.j54n1n.skippey.R;
import io.github.j54n1n.skippey.plugin.LocalMediaKeyPlugin;

import static io.github.j54n1n.skippey.util.LogUtils.LOGD;
import static io.github.j54n1n.skippey.util.LogUtils.LOGE;
import static io.github.j54n1n.skippey.util.LogUtils.makeLogTag;

/** Sony Xperia FM Radio plugin. */
public final class SemcFmRadioPlugin extends LocalMediaKeyPlugin {

    private static final String TAG = makeLogTag(SemcFmRadioPlugin.class);

    // FM radio media notification button title.
    private static final String ACTION_PREVIOUS = "prev";
    private static final String ACTION_NEXT = "next";

    // FM radio media notification button intent.
    private PendingIntent previousPendingIntent;
    private PendingIntent nextPendingIntent;

    public SemcFmRadioPlugin(Context context) throws PackageManager.NameNotFoundException,
            UnsupportedOperationException {
        super(context, R.string.plugin_semc_fmradio, R.string.plugin_semc_fmradio_pkg,
                R.string.plugin_semc_fmradio_desc
        );
    }

    /** {@inheritDoc } */
    @Override
    public void sendMediaKeyEvent(int keyCode) {
        super.sendMediaKeyEvent(keyCode);
        if ((previousPendingIntent != null) && (nextPendingIntent != null)) {
            PendingIntent pendingIntent;
            if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                pendingIntent = previousPendingIntent;
            } else {
                pendingIntent = nextPendingIntent;
            }
            try {
                LOGD(TAG, "skipping station with " + pendingIntent);
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                LOGE(TAG, "Error sending pending intent.", e);
            }
        }
    }

    /** {@inheritDoc } */
    @Override
    public void onMediaNotificationActionPosted(NotificationCompat.Action[] actions) {
        for (NotificationCompat.Action action : actions) {
            // Update and filter the pending intents.
            PendingIntent pendingIntent = action.getActionIntent();
            if (ACTION_PREVIOUS.equals(action.getTitle())) {
                previousPendingIntent = pendingIntent;
            } else if (ACTION_NEXT.equals(action.getTitle())) {
                nextPendingIntent = pendingIntent;
            }
        }
    }

    /** {@inheritDoc } */
    @Override
    public void onMediaNotificationActionRemoved() {
        // Invalidate pending intents.
        previousPendingIntent = null;
        nextPendingIntent = null;
    }
}
