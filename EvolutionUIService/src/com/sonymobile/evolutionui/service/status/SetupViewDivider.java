/*
 * Copyright (C) 2014 Sony Mobile Communications AB
 *
 * This file is part of EvolutionUI.
 *
 * EvolutionUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * EvolutionUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EvolutionUI. If not, see <http://www.gnu.org/licenses/>.
 */
package com.sonymobile.evolutionui.service.status;

import com.sonymobile.evolutionui.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

public class SetupViewDivider extends View {

    private Bitmap mDivImage;
    private Paint mPaint;

    public SetupViewDivider(Context context) {
        this(context, null);
    }

    public SetupViewDivider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SetupViewDivider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDivImage = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.setupview_div)).getBitmap();
        mPaint = new Paint();
        mPaint.setXfermode(new PorterDuffXfermode(Mode.OVERLAY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mDivImage,(getWidth() - mDivImage.getWidth()) / 2, 0, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wm = MeasureSpec.getMode(widthMeasureSpec);
        int ws = MeasureSpec.getSize(widthMeasureSpec);
        int hm = MeasureSpec.getMode(heightMeasureSpec);
        int hs = MeasureSpec.getSize(heightMeasureSpec);
        if (wm == MeasureSpec.AT_MOST) {
            ws = Math.min(ws, mDivImage.getWidth());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(ws, MeasureSpec.EXACTLY);
        }
        if (hm == MeasureSpec.AT_MOST) {
            hs = Math.min(hs, mDivImage.getHeight());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(hs, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
