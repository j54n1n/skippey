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

/**
 * AudioManager provides access to volume and ringer mode control.
 * <p>
 * Use <code>Context.getSystemService(Context.AUDIO_SERVICE)</code> to get
 * an instance of this class.
 */
public class AudioManagerCompat {

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
}
