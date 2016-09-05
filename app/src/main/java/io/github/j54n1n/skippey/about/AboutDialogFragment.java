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

package io.github.j54n1n.skippey.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.j54n1n.skippey.R;

public class AboutDialogFragment extends AppCompatDialogFragment {

    private static final boolean IS_CLICKABLE = true;
    private final static String TAG_FRAGMENT_ABOUT_LICENSE = "fragment_about_license";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        About[] aboutItems = {
                new About(
                        getString(R.string.fragment_about_version),
                        getString(R.string.fragment_about_version_summary)
                ),
                new About(
                        getString(R.string.fragment_about_license),
                        getString(R.string.fragment_about_license_summary)
                )
        };
        // Prepare item list.
        AboutAdapter aboutAdapter = new AboutAdapter(
                new ArrayList<>(Arrays.asList(aboutItems)), IS_CLICKABLE
        );
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_about_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(aboutAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new OnAdapterItemTouchListener(
                getContext(), recyclerView) {

            @Override
            public void onAdapterItemTouch(View view, int position) {
                switch (position) {
                    case 0:
                        // TODO: Add link to Google Play.
                        break;
                    case 1:
                        // Close existing dialog fragments.
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ABOUT_LICENSE);
                        if (fragment != null) {
                            fragmentManager.beginTransaction().remove(fragment).commit();
                        }
                        AboutLicenseDialogFragment aboutLicenseDialogFragment =
                                new AboutLicenseDialogFragment();
                        aboutLicenseDialogFragment.show(
                                fragmentManager, TAG_FRAGMENT_ABOUT_LICENSE);
                        break;
                }
            }

            @Override
            public void onAdapterItemLongPress(View view, int position) { }
        });
        return view;
    }
}
