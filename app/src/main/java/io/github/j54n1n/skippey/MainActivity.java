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

package io.github.j54n1n.skippey;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import io.github.j54n1n.skippey.about.AboutDialogFragment;

public class MainActivity extends AppCompatActivity {

    private final static String TAG_FRAGMENT_ABOUT = "fragment_about";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Handle service switch.
        setupSwitchOptionsMenu(menu);
        return true;
    }

    public void setupSwitchOptionsMenu(Menu menu) {
        // Handle service switch.
        MenuItem menuItem = menu.findItem(R.id.menu_item_service);
        final SwitchCompat switchService = (SwitchCompat) MenuItemCompat.getActionView(menuItem)
                .findViewById(R.id.toolbar_switch);
        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        final Resources resources = getResources();
        final boolean isServiceEnabled = sharedPreferences.getBoolean(
                resources.getString(R.string.pref_service),
                resources.getBoolean(R.bool.pref_service_default)
        );
        setSwitchState(switchService, isServiceEnabled);
        switchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSwitchState(switchService, isChecked);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getResources().getString(R.string.pref_service), isChecked);
                editor.apply();
            }
        });
    }

    private static void setSwitchState(SwitchCompat buttonView, boolean isChecked) {
        if (isChecked) {
            buttonView.setText(buttonView.getTextOn());
        } else {
            buttonView.setText(buttonView.getTextOff());
        }
        buttonView.setChecked(isChecked);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.action_about) {
            // Close existing dialog fragments.
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ABOUT);
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
            AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
            aboutDialogFragment.show(fragmentManager, TAG_FRAGMENT_ABOUT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
        // HACK: The appcompat overflow menu interferes with the service switch widget.
        // Redraw the menu to compensate.
        invalidateOptionsMenu();
    }
}
