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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManagerCompat;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.view.KeyEvent;

import com.takisoft.fix.support.v7.preference.SwitchPreferenceCompat;

/**
 * Media key plugin for apps that do not respond to
 * {@link AudioManagerCompat#dispatchMediaKeyEvent}.
 */
public abstract class LocalMediaKeyPlugin implements OnMediaNotificationListener {

    public final Context context;
    public final String key;
    public final String packageName;
    public final Drawable icon;
    public final CharSequence title;
    public final CharSequence summary;

    public LocalMediaKeyPlugin(
            Context context, @StringRes int keyResId, @StringRes int packageNameResId,
            @StringRes int summaryResId)
            throws PackageManager.NameNotFoundException, UnsupportedOperationException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Less than JB API 18 is not supported since there exists no
            // NotificationListenerService.
            throw new UnsupportedOperationException();
        }
        this.context = context;
        key = context.getString(keyResId);
        packageName = context.getString(packageNameResId);
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        icon = packageManager.getApplicationIcon(applicationInfo);
        title = packageManager.getApplicationLabel(applicationInfo);
        summary = context.getString(summaryResId);
    }

    /**
     * Construct a preference item.
     * @param preferenceCategoryContext preference category context
     * @return the preference item.
     */
    public Preference getPreference(Context preferenceCategoryContext) {
        SwitchPreferenceCompat switchPreference =
                new SwitchPreferenceCompat(preferenceCategoryContext);
        switchPreference.setKey(key);
        switchPreference.setIcon(icon);
        switchPreference.setTitle(title);
        switchPreference.setSummary(summary);
        return switchPreference;
    }

    /**
     * Check if preference item is enabled.
     * @return true otherwise false if preference item is not enabled.
     */
    public boolean isEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    /**
     * Sends a media key event to the plugin app.
     * @param keyCode either {@link KeyEvent#KEYCODE_MEDIA_NEXT} or
     * {@link KeyEvent#KEYCODE_MEDIA_PREVIOUS}
     */
    public void sendMediaKeyEvent(int keyCode) {
        if ((keyCode != KeyEvent.KEYCODE_MEDIA_NEXT) &&
                (keyCode != KeyEvent.KEYCODE_MEDIA_PREVIOUS)) {
            throw new UnsupportedOperationException("Unsupported key code");
        }
    }
}
