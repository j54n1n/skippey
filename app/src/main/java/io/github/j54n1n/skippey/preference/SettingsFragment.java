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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;

import io.github.j54n1n.skippey.R;

import android.support.v7.preference.PreferenceManagerFix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

public class SettingsFragment extends PreferenceFragmentCompatDividers
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        /*
        //Example of adding preferences via code.
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        PreferenceCategory preferenceCategory = new PreferenceCategory(preferenceScreen.getContext());
        preferenceCategory.setTitle("Plugins");
        preferenceScreen.addPreference(preferenceCategory);
        SwitchPreferenceCompat switchPreference = new SwitchPreferenceCompat(preferenceScreen.getContext());
        //switchPreference.setKey("");
        switchPreference.setIcon(R.mipmap.ic_launcher);
        switchPreference.setTitle("Plugin");
        switchPreference.setSummary("Desc");
        preferenceCategory.addPreference(switchPreference);
        */
        // Set initial preference UI.
        final SharedPreferences sharedPreferences =
                PreferenceManagerFix.getDefaultSharedPreferences(getActivity());
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.pref_service));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.pref_timeout)
        );
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            return super.onCreateView(inflater, container, savedInstanceState);
        } finally {
            setDividerPreferences(DIVIDER_NONE);
        }
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;
        if (preference instanceof SeekBarPreference) {
            // Handle custom preferences-v7 dialogs.
            dialogFragment = SeekBarPreferenceDialogFragmentCompat.newInstance(preference.getKey());
        }
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(
                    getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG"
            );
        }
        else {
            // Handle preferences-v7 library dialogs.
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Set the state of the preferences.
        if (key.equals(getString(R.string.pref_service))) {
            final boolean isServiceEnabled = sharedPreferences.getBoolean(
                    key, getResources().getBoolean(R.bool.pref_service_default)
            );
            getPreferenceScreen().setEnabled(isServiceEnabled);
        }
        // Update summaries of dialog preferences.
        if (key.equals(getString(R.string.pref_timeout))) {
            SeekBarPreference seekBarPreference = (SeekBarPreference) findPreference(key);
            seekBarPreference.setSummary(seekBarPreference.format(seekBarPreference.getValue()));
        }
    }
}
