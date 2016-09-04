/*
 * Copyright (C) 2014 & 2016 Julian Sanin
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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.AudioManagerCompat;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.os.VibratorCompat;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import static io.github.j54n1n.skippey.util.LogUtils.makeLogTag;
import static io.github.j54n1n.skippey.util.LogUtils.LOGD;

public final class MediaButtonService extends Service {

    public static final String ACTION_SKIP_TRACK =
            "io.github.j54n1n.skippey.intent.action.SKIP_TRACK";

    private static final String TAG = makeLogTag(MediaButtonService.class);

    private Handler handler;
    private AudioManager audioManager;
    private Vibrator vibrator;
    private VolumeChangeInfo volumeChangeInfo;
    private SharedPreferences sharedPreferences;
    private Resources resources;
    private long timeoutMillis;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        resources = getResources();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setupTimeoutMillis();
        if ((intent != null) && (ACTION_SKIP_TRACK.equals(intent.getAction()))) {
            handleVolumeChange(intent);
        }
        return START_STICKY;
    }

    private void handleVolumeChange(Intent intent) {
        // Update volume change info.
        final VolumeChangeInfo lastVolumeChangeInfo = volumeChangeInfo;
        volumeChangeInfo = new VolumeChangeInfo(intent, audioManager);
        // Check for volume change skip track event.
        if (lastVolumeChangeInfo != null) {
            final long deltaTimeMillis =
                    volumeChangeInfo.eventTimeMillis - lastVolumeChangeInfo.eventTimeMillis;
            LOGD(TAG, "handleVolumeChange: deltaTimeMillis=" + deltaTimeMillis +
                    ", timeoutMillis=" + timeoutMillis);
            if (deltaTimeMillis < timeoutMillis) {
                // Handle track skipping.
                handleMediaKeyEvent(lastVolumeChangeInfo);
                // Compensate volume change.
                final int streamType = lastVolumeChangeInfo.streamType;
                final int oldVolume = lastVolumeChangeInfo.oldVolume;
                final int maxVolume = lastVolumeChangeInfo.maxVolume;
                if ((oldVolume >= 0) && (oldVolume <= maxVolume)) {
                    audioManager.setStreamVolume(streamType, oldVolume, 0);
                }
                volumeChangeInfo = null;
            }
        }
    }

    private void setupTimeoutMillis() {
        if (handler == null) {
            handler = new Handler();
            timeoutMillis = sharedPreferences.getInt(
                    resources.getString(R.string.pref_timeout),
                    resources.getInteger(R.integer.pref_timeout_default)
            );
            handler.postDelayed(destroyService, timeoutMillis);
        }
    }

    private void handleMediaKeyEvent(VolumeChangeInfo volumeChangeInfo) {
        LOGD(TAG, "handleMediaKeyEvent: oldVolume=" + volumeChangeInfo.oldVolume +
                ", newVolume=" + volumeChangeInfo.newVolume
        );
        final boolean isReversed = sharedPreferences.getBoolean(
                resources.getString(R.string.pref_reverse),
                resources.getBoolean(R.bool.pref_reverse_default)
        );
        new SkipTrackInfo(getApplicationContext(), volumeChangeInfo, isReversed).skipTrack();
        final boolean isVibrationEnabled = sharedPreferences.getBoolean(
                resources.getString(R.string.pref_vibrate),
                resources.getBoolean(R.bool.pref_vibrate_default)
        );
        final int milliseconds = getResources().getInteger(R.integer.pref_timeout_min) / 2;
        LOGD(TAG, "handleMediaKeyEvent: isVibrationEnabled=" + isVibrationEnabled +
                ", milliseconds=" + milliseconds
        );
        if (isVibrationEnabled && VibratorCompat.hasVibrator(getApplicationContext())) {
            vibrator.vibrate(milliseconds);
        }
    }

    @Override
    public void onDestroy() {
        LOGD(TAG, "onDestroy: handler=" + handler);
        if (handler != null) {
            handler.removeCallbacks(destroyService);
            handler = null;
        }
        volumeChangeInfo = null;
    }

    private Runnable destroyService = new Runnable() {

        @Override
        public void run() {
            stopSelf();
        }
    };

    private static final class VolumeChangeInfo {

        public final int streamType;
        public final int newVolume;
        public final int oldVolume;
        public final int maxVolume;
        public final long eventTimeMillis;

        public VolumeChangeInfo(Intent intent, AudioManager audioManager) {
            streamType = intent.getIntExtra(AudioManagerCompat.EXTRA_VOLUME_STREAM_TYPE, -1);
            newVolume = intent.getIntExtra(AudioManagerCompat.EXTRA_VOLUME_STREAM_VALUE, 0);
            oldVolume = intent.getIntExtra(AudioManagerCompat.EXTRA_PREV_VOLUME_STREAM_VALUE, 0);
            maxVolume = audioManager.getStreamMaxVolume(streamType);
            eventTimeMillis = System.currentTimeMillis();
        }
    }

    private static final class SkipTrackInfo {

        private final Context context;
        private final VolumeChangeInfo volumeChangeInfo;
        private final boolean isReversed;

        public SkipTrackInfo(
                Context context, VolumeChangeInfo volumeChangeInfo, boolean isReversed) {
            this.context = context;
            this.volumeChangeInfo = volumeChangeInfo;
            this.isReversed = isReversed;
        }

        public void skipTrack() {
            int keyCode;
            if (!isReversed) {
                keyCode = (volumeChangeInfo.oldVolume > volumeChangeInfo.newVolume) ?
                        KeyEvent.KEYCODE_MEDIA_NEXT : KeyEvent.KEYCODE_MEDIA_PREVIOUS;
            } else {
                keyCode = (volumeChangeInfo.oldVolume < volumeChangeInfo.newVolume) ?
                        KeyEvent.KEYCODE_MEDIA_NEXT : KeyEvent.KEYCODE_MEDIA_PREVIOUS;
            }
            LOGD(TAG, "skipTrack: isReversed=" + isReversed + ", keyCode=" +
                    ((keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) ?
                            "KEYCODE_MEDIA_NEXT" : "KEYCODE_MEDIA_PREVIOUS")
            );
            sendMediaKeyEvent(keyCode);
        }

        private void sendMediaKeyEvent(int keyCode) {
            long eventTime = SystemClock.uptimeMillis();
            KeyEvent keyEventDown = new KeyEvent(
                    eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0
            );
            KeyEvent keyEventUp = KeyEvent.changeAction(keyEventDown, KeyEvent.ACTION_UP);
            AudioManagerCompat.dispatchMediaKeyEvent(context, keyEventDown);
            AudioManagerCompat.dispatchMediaKeyEvent(context, keyEventUp);
        }
    }
}
