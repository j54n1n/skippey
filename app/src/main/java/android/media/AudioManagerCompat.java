/*
 * Copyright (C) 2007 The Android Open Source Project
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

package android.media;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IInterface;
import android.util.Log;
import android.view.KeyEvent;

import java.lang.reflect.Method;

/**
 * AudioManager provides access to volume and ringer mode control.
 * <p>
 * Use <code>Context.getSystemService(Context.AUDIO_SERVICE)</code> to get
 * an instance of this class.
 */
public final class AudioManagerCompat {

    private static final String TAG = AudioManagerCompat.class.getSimpleName();

    private AudioManagerCompat() { }

    /**
     * Broadcast intent when the volume for a particular stream type changes.
     * Includes the stream, the new volume and previous volumes.
     * Notes:
     *  - hidden API (@hide)
     *  - added in API level 1?
     *  - for internal platform use only, do not make public,
     *  - never used for "remote" volume changes
     *
     * @see #EXTRA_VOLUME_STREAM_TYPE
     * @see #EXTRA_VOLUME_STREAM_VALUE
     * @see #EXTRA_PREV_VOLUME_STREAM_VALUE
     */
    //@SdkConstant(SdkConstantType.BROADCAST_INTENT_ACTION)
    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";

    /**
     * The stream type for the volume changed intent.
     * Notes:
     *  - hidden API (@hide)
     *  - added in API level 1?
     */
    public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

    /**
     * The volume associated with the stream for the volume changed intent.
     * Notes:
     *  - hidden API (@hide)
     *  - added in API level 1?
     */
    public static final String EXTRA_VOLUME_STREAM_VALUE =
            "android.media.EXTRA_VOLUME_STREAM_VALUE";

    /**
     * The previous volume associated with the stream for the volume changed intent.
     * Notes:
     *  - hidden API (@hide)
     *  - added in API level 8 (FROYO)
     */
    public static final String EXTRA_PREV_VOLUME_STREAM_VALUE =
            "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE";

    /**
     * Sends a simulated key event for a media button.
     * To simulate a key press, you must first send a KeyEvent built with a
     * {@link KeyEvent#ACTION_DOWN} action, then another event with the {@link KeyEvent#ACTION_UP}
     * action.
     * <p>The key event will be sent to the current media key event consumer which registered with
     * {@link AudioManager#registerMediaButtonEventReceiver}.
     * @param keyEvent a {@link KeyEvent} instance whose key code is one of
     *     {@link KeyEvent#KEYCODE_MUTE},
     *     {@link KeyEvent#KEYCODE_HEADSETHOOK},
     *     {@link KeyEvent#KEYCODE_MEDIA_PLAY},
     *     {@link KeyEvent#KEYCODE_MEDIA_PAUSE},
     *     {@link KeyEvent#KEYCODE_MEDIA_PLAY_PAUSE},
     *     {@link KeyEvent#KEYCODE_MEDIA_STOP},
     *     {@link KeyEvent#KEYCODE_MEDIA_NEXT},
     *     {@link KeyEvent#KEYCODE_MEDIA_PREVIOUS},
     *     {@link KeyEvent#KEYCODE_MEDIA_REWIND},
     *     {@link KeyEvent#KEYCODE_MEDIA_RECORD},
     *     {@link KeyEvent#KEYCODE_MEDIA_FAST_FORWARD},
     *     {@link KeyEvent#KEYCODE_MEDIA_CLOSE},
     *     {@link KeyEvent#KEYCODE_MEDIA_EJECT},
     *     or {@link KeyEvent#KEYCODE_MEDIA_AUDIO_TRACK}.
     * Notes:
     *  - added in API level 16 (JELLY_BEAN)
     *  - resorts to broadcasting the key event if API is not available
     */
    public static void dispatchMediaKeyEvent(Context context, KeyEvent keyEvent) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Use KITKAT or reflection API.
            audioManager.dispatchMediaKeyEvent(keyEvent);
        } else if (!dispatchMediaKeyEvent(keyEvent)){
            // Else resort to broadcasting the action.
            Intent keyEventIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            keyEventIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
            context.sendOrderedBroadcast(keyEventIntent, null);
        }
    }

    private static Method dispatchMediaKeyEvent;

    private static boolean dispatchMediaKeyEvent(KeyEvent keyEvent) {
        try {
            IInterface service = getService();
            if ((dispatchMediaKeyEvent == null) && (service != null)) {
                dispatchMediaKeyEvent = service.getClass().getDeclaredMethod(
                        "dispatchMediaKeyEvent", KeyEvent.class);
            }
            if (dispatchMediaKeyEvent != null) {
                dispatchMediaKeyEvent.setAccessible(true);
                dispatchMediaKeyEvent.invoke(service, keyEvent);
                return true;
            }
        } catch (Throwable e) {
            Log.e(TAG, "Error invoking dispatchMediaKeyEvent:", e);
        }
        return false;
    }

    private static IInterface iAudioService;

    private static IInterface getService() {
        if (iAudioService != null) {
            return iAudioService;
        }
        try {
            Method getService = AudioManager.class.getDeclaredMethod("getService");
            if (getService == null) {
                return null;
            }
            getService.setAccessible(true);
            iAudioService = (IInterface) getService.invoke(null);
        } catch (Throwable e) {
            Log.e(TAG, "Error obtaining IAudioService:", e);
        }
        return iAudioService;
    }
}
