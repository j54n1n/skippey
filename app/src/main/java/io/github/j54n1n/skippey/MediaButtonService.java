/*
 * Copyright (C) 2014 Julian Sanin
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
import android.media.AudioManager;
import android.media.AudioManagerCompat;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

public final class MediaButtonService extends Service {

    public static final String ACTION_SKIP_TRACK =
            "io.github.j54n1n.skippey.intent.action.SKIP_TRACK";

    private static final String TAG = MediaButtonService.class.getSimpleName();

    private Handler handler;
    private AudioManager audioManager;
    private VolumeChangeInfo volumeChangeInfo;
    private SharedPreferences sharedPreferences;
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setupTimeoutMillis();
        if ((intent != null) && (ACTION_SKIP_TRACK.equals(intent.getAction()))) {
            checkVolumeChangeInfo(intent);
        }
        return START_STICKY;
    }

    private void checkVolumeChangeInfo(Intent intent) {
        // Update volume change info.
        final VolumeChangeInfo lastVolumeChangeInfo = volumeChangeInfo;
        volumeChangeInfo = new VolumeChangeInfo(intent, audioManager);
        // Check for volume change skip track event.
        if (lastVolumeChangeInfo != null) {
            final long deltaTimeMillis =
                    volumeChangeInfo.eventTimeMillis - lastVolumeChangeInfo.eventTimeMillis;
            Log.i(TAG, "checkVolumeChangeInfo() deltaTimeMillis=" + deltaTimeMillis +
                    ", timeoutMillis=" + timeoutMillis);
            if (deltaTimeMillis < timeoutMillis) {
                //handle skipping
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
            timeoutMillis = 750; // sharedPreferences.getLong(KEY_TIMEOUT_MILLIS, TIMEOUT_MILLIS_DEFAULT);
            handler.postDelayed(destroyService, timeoutMillis);
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
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
}
