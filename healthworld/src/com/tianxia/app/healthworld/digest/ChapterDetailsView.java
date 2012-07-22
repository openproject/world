/*
 * Copyright (C) 2012 Jayfeng.
 */

package com.tianxia.app.healthworld.digest;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

import android.util.AttributeSet;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ChapterDetailsView extends View{

    private String mInitText;
    private String mContent;
    private List<String> mLines;

    private int mPage = 1;
    private int mPageLines;
    private int mPageMax;

    private int mMarginTopAndBottom = 50;
    private int mMarginLeftAndRight = 25;

    private Paint mPaint;
    private int mFontHeight;

    public ChapterDetailsView(Context context,AttributeSet attrs) {
        super(context, attrs);
        mLines = new ArrayList<String>();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(25);
        FontMetrics fm = mPaint.getFontMetrics();
        mFontHeight = (int)(Math.ceil(fm.descent - fm.top));
    }

    public void setInitText(String initText) {
        mInitText = initText;
        invalidate();
    }

    public void setContent(String content) {
        mContent = content;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mContent != null && !"".equals(mContent)) {
            mPageLines = (getHeight() - mMarginTopAndBottom*2)/mFontHeight;
            int start = 0, end;
            mLines.clear();
            int textWidth = getWidth() - mMarginLeftAndRight*2;
            for (int i = 0; i < mContent.length(); i++) {
                end = i;
                String line = mContent.substring(start, end + 1);
                if (mPaint.measureText(line) >= textWidth) {
                    line = mContent.substring(start, end);
                    mLines.add(line);
                    start = i;
                } else if (i == mContent.length() - 1 ){
                    mLines.add(line);
                    start = i;
                } else if (mContent.charAt(i) == '\r' && mContent.charAt(i+1) == '\n') {
                    // if goto this if branch, there is must be : i < mContent.length() - 1
                    mLines.add(line);
                    start = i + 2;
                }
            }

            if (mLines.size()%mPageLines == 0) {
                mPageMax = mLines.size()/mPageLines;
            } else {
                mPageMax = mLines.size()/mPageLines + 1;
            }

            int drawTop = mMarginTopAndBottom;
            int startLine = (mPage - 1)*mPageLines;
            for (int i = startLine; i < startLine + mPageLines && i < mLines.size(); i++) {
                canvas.drawText(mLines.get(i), mMarginLeftAndRight, drawTop, mPaint);
                drawTop += mFontHeight;
            }
        } else if (mInitText != null && !"".equals(mInitText)) {
            int initTextWidth = (int) mPaint.measureText(mInitText);
            int initTextX = (getWidth() - initTextWidth)/2;
            int initTextY = (getHeight() - mFontHeight)/2;
            canvas.drawText(mInitText, initTextX, initTextY, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() > getWidth()/2) {
                    if (mPage < mPageMax) {
                        mPage++;
                        invalidate();
                    } else {
                    }
                } else if (event.getX() < getWidth()/2) {
                    if (mPage > 1) {
                        mPage--;
                        invalidate();
                    } else {
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
    public void drawPrePage(String text) {
    }
    public void drawPostPage(String text) {
    }
}


