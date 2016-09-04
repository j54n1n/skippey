/*
 * Copyright (C) 2006 The Android Open Source Project
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

package android.os;

import android.content.Context;

/**
 * Class that operates the vibrator on the device.
 * <p>
 * If your process exits, any vibration you started will stop.
 * </p>
 *
 * To obtain an instance of the system vibrator, call
 * {@link Context#getSystemService} with {@link Context#VIBRATOR_SERVICE} as the argument.
 */
public final class VibratorCompat {

    private VibratorCompat() { }

    /**
     * Check whether the hardware has a vibrator.
     *
     * @return True if the hardware has a vibrator, else false.
     */
    public static boolean hasVibrator(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            //noinspection SimplifiableIfStatement
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                return vibrator.hasVibrator();
            }
            return true;
        }
        return false;
    }
}
