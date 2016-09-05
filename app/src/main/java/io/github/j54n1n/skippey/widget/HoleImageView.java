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

package io.github.j54n1n.skippey.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;

import io.github.j54n1n.skippey.R;

public class HoleImageView extends ImageView {

    private static final int ALPHA = 0xDF;

    private Paint drawPaint;
    private PorterDuffXfermode xfermode;
    private RectF circleRect;
    private int radius;
    private boolean isStatusBarTransparent;

    public HoleImageView(Context context) {
        this(context, null);
    }

    public HoleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HoleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HoleImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        //In versions > 3.0 need to define layer type.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        prepareDrawing();
        circleRect = new RectF();
        radius = 0;
        // HACK: Check if status bar is transparent and such that coordinates must be compensated
        // or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedArray typedArray = getContext().obtainStyledAttributes(new int[] {
                    android.R.attr.windowDrawsSystemBarBackgrounds
            });
            isStatusBarTransparent = typedArray.getBoolean(0, false);
            typedArray.recycle();
        } else {
            isStatusBarTransparent = false;
        }
    }

    private void prepareDrawing() {
        drawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        drawPaint.setAlpha(ALPHA);
        drawPaint.setStyle(Paint.Style.FILL);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    /**
     * Punch a hole trough to the view. To be executed when the measurement phase is finished.
     * @see ViewTreeObserver#addOnGlobalLayoutListener
     */
    public void setHole(Window window, View view) {
        // Get sizes and position.
        Rect windowRect = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(windowRect);
        final int statusBarHeight = windowRect.top;
        Rect globalRect = new Rect();
        view.getGlobalVisibleRect(globalRect);
        final int radius = Math.max(
                globalRect.right - globalRect.left, globalRect.bottom - globalRect.top
        ) / 2;
        final RectF rectf;
        if (isStatusBarTransparent) {
            rectf = new RectF(
                    globalRect.left, globalRect.top,
                    globalRect.right, globalRect.bottom
            );
        } else {
            rectf = new RectF(
                    globalRect.left, globalRect.top - statusBarHeight,
                    globalRect.right, globalRect.bottom - statusBarHeight
            );
        }
        setHole(rectf, radius);
    }

    public void setHole(RectF rectf, int radius) {
        this.circleRect = rectf;
        this.radius = radius;
        //Redraw after defining the circle area.
        prepareDrawing();
        postInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final float centerX = ((circleRect.right - circleRect.left) / 2) + circleRect.left;
        final float centerY = ((circleRect.bottom - circleRect.top) / 2) + circleRect.top;
        canvas.drawPaint(drawPaint);
        drawPaint.setXfermode(xfermode);
        canvas.drawCircle(centerX, centerY, radius, drawPaint);
    }
}
