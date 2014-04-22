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
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LevelSelector extends View {

    private static final int LEVELS = 4;

    private Bitmap mLevelNormal;
    private Bitmap mLevelSelected;
    private Bitmap mLevelArrows;
    private Paint mPaint;
    private int mLevel = 1;

    public LevelSelector(Context context) {
        this(context, null);
    }

    public LevelSelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevelSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLevelNormal = getBitmap(R.drawable.level_normal);
        mLevelSelected = getBitmap(R.drawable.level_selected);
        mLevelArrows = getBitmap(R.drawable.level_arrows);
        mPaint = new Paint();
    }

    public void setLevel(int value) {
        mLevel = value;
        invalidate();
    }

    public int getLevel() {
        return mLevel;
    }

    private Bitmap getBitmap(int id) {
        return ((BitmapDrawable)getResources().getDrawable(id)).getBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mLevelNormal, 0, 0, mPaint);
        canvas.drawBitmap(mLevelArrows, 0, mLevelNormal.getHeight(), mPaint);
        canvas.save();
        canvas.clipRect(0, 0, mLevelNormal.getWidth() * mLevel / LEVELS, mLevelNormal.getHeight());
        canvas.drawBitmap(mLevelSelected, 0, 0, mPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int level = 1 + (int) (event.getX() * LEVELS / getWidth());
        level = Math.min(Math.max(level, 1), LEVELS);
        if (level != mLevel) {
            mLevel = level;
            invalidate();
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wm = MeasureSpec.getMode(widthMeasureSpec);
        int ws = MeasureSpec.getSize(widthMeasureSpec);
        int hm = MeasureSpec.getMode(heightMeasureSpec);
        int hs = MeasureSpec.getSize(heightMeasureSpec);
        int iw = mLevelNormal.getWidth();
        int ih = mLevelNormal.getHeight() + mLevelArrows.getHeight();
        if (wm == MeasureSpec.AT_MOST) {
            ws = Math.min(ws, iw);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(ws, MeasureSpec.EXACTLY);
        }
        if (hm == MeasureSpec.AT_MOST) {
            hs = Math.min(hs, ih);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(hs, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
