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

public class AboutLicenseDialogFragment extends AppCompatDialogFragment {

    private static final boolean IS_CLICKABLE = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_license, container, false);
        // Prepare item list.
        AboutAdapter aboutAdapter = new AboutAdapter(
                new ArrayList<>(Arrays.asList(getAboutItems())), IS_CLICKABLE
        );
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_about_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(aboutAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    private About[] getAboutItems() {
        return new About[] {
                new About(
                        getString(R.string.license_android_support_annotation) + " " +
                                getString(R.string.license_android_support_version),
                        getString(R.string.license_android_support_annotation_body) +
                                getString(R.string.license_apache_body)
                ),
                new About(
                        getString(R.string.license_android_support_compat) + " " +
                                getString(R.string.license_android_support_version),
                        getString(R.string.license_android_support_compat_body) +
                                getString(R.string.license_apache_body)
                ),
                new About(
                        getString(R.string.license_android_support_fragment) + " " +
                                getString(R.string.license_android_support_version),
                        getString(R.string.license_android_support_fragment_body) +
                                getString(R.string.license_apache_body)
                ),
                new About(
                        getString(R.string.license_android_support_appcompat_v7) + " " +
                                getString(R.string.license_android_support_version),
                        getString(R.string.license_android_support_appcompat_v7_body) +
                                getString(R.string.license_apache_body)
                ),
                new About(
                        getString(R.string.license_android_support_preference_v7) + " " +
                                getString(R.string.license_android_support_version),
                        getString(R.string.license_android_support_preference_v7_body) +
                                getString(R.string.license_apache_body)
                ),
                new About(
                        getString(R.string.license_android_support_preference_v7_fix) + " " +
                                getString(R.string.license_android_support_fix_version),
                        getString(R.string.license_android_support_preference_v7_fix_body) +
                                getString(R.string.license_apache_body)
                ),
                new About(
                        getString(R.string.license_android_support_recyclerview_v7) + " " +
                                getString(R.string.license_android_support_version),
                        getString(R.string.license_android_support_recyclerview_v7_body) +
                                getString(R.string.license_apache_body)
                ),
                new About(
                        getString(R.string.license_android_support_design) + " " +
                                getString(R.string.license_android_support_version),
                        getString(R.string.license_android_support_design_body) +
                                getString(R.string.license_apache_body)
                ),
                new About(
                        getString(R.string.license_android_sdk) + " " +
                                getString(R.string.license_android_sdk_version),
                        getString(R.string.license_android_sdk_body)
                ),
        };
    }
}
