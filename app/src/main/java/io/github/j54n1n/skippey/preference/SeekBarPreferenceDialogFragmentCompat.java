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

import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import io.github.j54n1n.skippey.R;

public class SeekBarPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private TextView textView;
    private SeekBar seekBar;

    public static SeekBarPreferenceDialogFragmentCompat newInstance(String key) {
        SeekBarPreferenceDialogFragmentCompat fragment =
                new SeekBarPreferenceDialogFragmentCompat();
        Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        textView = (TextView) view.findViewById(R.id.pref_dialog_seek_bar_value);
        seekBar = (SeekBar) view.findViewById(R.id.pref_dialog_seek_bar);
        // Check for widget resource ids.
        if (textView == null) {
            throw new IllegalStateException(
                    "Dialog view must contain a TextView with id 'pref_dialog_seek_bar_value'."
            );
        }
        if (seekBar == null) {
            throw new IllegalStateException(
                    "Dialog view must contain a SeekBar with id 'pref_dialog_seek_bar'."
            );
        }
        Integer progress = null;
        DialogPreference dialogPreference = getPreference();
        if (dialogPreference instanceof SeekBarPreference) {
            // Retrieve the initial preference value.
            final SeekBarPreference seekBarPreference = (SeekBarPreference) dialogPreference;
            progress = seekBarPreference.getValue() - seekBarPreference.getMin();
            // Setup and keep updated the widgets.
            seekBar.setMax(seekBarPreference.getMax() - seekBarPreference.getMin());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textView.setText(seekBarPreference.format(
                            progress + seekBarPreference.getMin()
                    ));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
        }
        if (progress != null) {
            seekBar.setProgress(progress);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            DialogPreference dialogPreference = getPreference();
            if (dialogPreference instanceof SeekBarPreference) {
                // Retrieve current value.
                SeekBarPreference seekBarPreference = (SeekBarPreference) dialogPreference;
                int newValue = seekBar.getProgress() + seekBarPreference.getMin();
                // Allow the dialog client to ignore the user set value.
                if (seekBarPreference.callChangeListener(newValue)) {
                    // Persist the new value.
                    seekBarPreference.setValue(newValue);
                }
            }
        }
    }
}
