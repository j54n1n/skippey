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

import android.support.v7.widget.RecyclerView;
import android.text.HtmlCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.j54n1n.skippey.R;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {

    private final List<About> aboutList;
    private final boolean isClickable;

    public AboutAdapter(List<About> aboutList, boolean isClickable) {
        this.aboutList = aboutList;
        this.isClickable = isClickable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_about, parent, false);
        itemView.setClickable(isClickable);
        itemView.setFocusable(isClickable);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        About about = aboutList.get(position);
        holder.title.setText(about.title);
        holder.body.setText(HtmlCompat.fromHtml(about.body));
    }

    @Override
    public int getItemCount() {
        return aboutList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView title;
        public final TextView body;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_about_title);
            body = (TextView) itemView.findViewById(R.id.item_about_body);
        }
    }
}
