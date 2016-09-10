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

import android.support.v7.app.NotificationCompat;

import io.github.j54n1n.skippey.MediaNotificationService;

/**
 * Listener for media notification changes. See also {@link MediaNotificationService}.
 */
public interface OnMediaNotificationListener {

    /**
     * Called when new media notification actions are posted.
     * @param actions list of available actions of the media notification.
     */
    void onMediaNotificationActionPosted(NotificationCompat.Action[] actions);

    /**
     * Called when the media notification is removed (or the app that belongs to is terminated).
     */
    void onMediaNotificationActionRemoved();
}
