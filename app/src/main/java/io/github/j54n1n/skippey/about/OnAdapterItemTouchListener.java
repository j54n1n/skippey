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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnAdapterItemTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;

    public OnAdapterItemTouchListener(Context context, final RecyclerView recyclerView) {
        gestureDetector = new GestureDetector(
                context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent event) {
                View childView = recyclerView.findChildViewUnder(event.getX(), event.getY());
                if (childView != null) {
                    onAdapterItemLongPress(
                            childView, recyclerView.getChildAdapterPosition(childView)
                    );
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent event) {
        View childView = recyclerView.findChildViewUnder(event.getX(), event.getY());
        if ((childView != null) && (gestureDetector.onTouchEvent(event))) {
            onAdapterItemTouch(childView, recyclerView.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent event) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }

    public abstract void onAdapterItemTouch(View view, int position);

    public abstract void onAdapterItemLongPress(View view, int position);
}
