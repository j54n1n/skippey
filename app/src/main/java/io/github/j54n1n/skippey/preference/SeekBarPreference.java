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

package io.github.j54n1n.skippey.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import java.util.Locale;

import io.github.j54n1n.skippey.R;

public class SeekBarPreference extends DialogPreference {

    private String unit;
    private int min   = 0;
    private int value;
    private int max   = 100;

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (attrs != null) {
            // Read custom attributes.
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.SeekBarPreference, defStyleAttr, defStyleRes
            );
            unit = typedArray.getString(R.styleable.SeekBarPreference_unit);
            min = typedArray.getInteger(R.styleable.SeekBarPreference_min, min);
            max = typedArray.getInteger(R.styleable.SeekBarPreference_max, max);
            typedArray.recycle();
            value = ((max - min) / 2) + min;
        }
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.preference.R.attr.dialogPreferenceStyle);
    }

    public SeekBarPreference(Context context) {
        this(context, null);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        int value = a.getInteger(index, this.value);
        if ((value >= min) && (value <= max)) {
            return value;
        }
        return this.value;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(value) : (int) defaultValue);
    }

    @Override
    public int getDialogLayoutResource() {
        return io.github.j54n1n.skippey.R.layout.pref_dialog_seek_bar;
    }

    public String getUnit() {
        if (unit == null) {
            return "";
        }
        return unit;
    }

    public int getMin() {
        return min;
    }

    public int getValue() {
        return value;
    }

    public String format(int value) {
        return String.format(Locale.getDefault(), "%d%s", value, getUnit());
    }

    /**
     * Sets and persists the value.
     * @return true if the value is within {@link #getMin()} and {@link #getMax()}.
     */
    public boolean setValue(int value) {
        if ((value >= min) && (value <= max)) {
            this.value = value;
            persistInt(value);
            return true;
        }
        return false;
    }

    public int getMax() {
        return max;
    }
}
