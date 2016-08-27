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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManagerCompat;
import android.os.Build;
import android.os.PowerManager;

public class VolumeChangeReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        switch (action) {
            case AudioManagerCompat.VOLUME_CHANGED_ACTION:
                final int streamType = intent.getIntExtra(
                        AudioManagerCompat.EXTRA_VOLUME_STREAM_TYPE, -1
                );
                final int newVolume = intent.getIntExtra(
                        AudioManagerCompat.EXTRA_VOLUME_STREAM_VALUE, 0
                );
                final int oldVolume = intent.getIntExtra(
                        AudioManagerCompat.EXTRA_PREV_VOLUME_STREAM_VALUE, 0
                );
                if (newVolume != oldVolume) {
                    onVolumeChanged(streamType, newVolume, oldVolume);
                }
                break;
            case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                onAudioInterrupted();
                break;
        }
    }

    private boolean isScreenOff() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return !powerManager.isInteractive();
        } else {
            return !powerManager.isScreenOn();
        }
    }

    protected void onVolumeChanged(int streamType, int newVolume, int oldVolume) {
        if ((streamType != -1) && (streamType == AudioManager.STREAM_MUSIC) && isScreenOff()) {
            Intent intent = new Intent(context, MediaButtonService.class);
            intent.setAction(MediaButtonService.ACTION_SKIP_TRACK);
            intent.putExtra(AudioManagerCompat.EXTRA_VOLUME_STREAM_TYPE, streamType);
            intent.putExtra(AudioManagerCompat.EXTRA_VOLUME_STREAM_VALUE, newVolume);
            intent.putExtra(AudioManagerCompat.EXTRA_PREV_VOLUME_STREAM_VALUE, oldVolume);
            context.startService(intent);
        }
    }

    protected void onAudioInterrupted() {
        context.stopService(new Intent(context, MediaButtonService.class));
    }
}
