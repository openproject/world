package com.tianxia.lib.baseworld.widget;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

import android.util.AttributeSet;

import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.view.MotionEvent;
import android.content.Intent;
import android.net.Uri;

public class TagCloudView extends View {

    private int mWidth;
    private int mHeight;

    private Random mRandom;
    private int[] mColors = {Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.RED,
        Color.rgb(145, 178, 230),
        Color.rgb(40, 64, 105),
        Color.rgb(141, 156, 162),
        Color.rgb(145, 76, 1),
        Color.rgb(148, 1, 151),
        Color.rgb(132, 157, 53),
        Color.rgb(63, 167, 144),
        Color.rgb(13, 46, 63)};

    private Paint mPaint;
    private int mRandomX = 0;
    private int mRandomY = 0;
    private static final int LINE_SPACE = 15;

    private int stringWidth;
    private int stringHeight;
    private int minTop;
    private int maxBottom;

    private int mUpY = 0;
    private int mDownY = 0;
    private int mLeftX = 0;
    private int mRightX = 0;

    private boolean mFirstDraw = true;
    private int mSelectedPosition = -1;

    private List<TagCloudInfo> mTagClouds;

    public TagCloudView(Context context) {
        super(context);
        init();
    }
    public TagCloudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        setPadding(10, 10, 10, 10);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mRandom = new Random();
        setTagList();
    }

    public void setTagList() {
        String[] result = {"坚持跑步!",
                           "多喝水!",
                           "早点睡觉，不熬夜!",
                           "吃早餐!",
                           "眼睛多转一转!",
                           "戒烟戒酒！",
                           "勤洗脸洗头洗澡！",
                           "少坐着",
                           "少坐着，多走走，常舒展运动一下",
                           "开心就好",
                           "给自己加油",
                           "就是爱音乐",
                           "拒绝懒惰的借口",
                           "每天都是新的一天，微笑！",
                           "保持居住环境清洁"};
        mTagClouds = new ArrayList<TagCloudInfo>();
        TagCloudInfo tagCloudInfo = null;
        for (String str : result) {
            tagCloudInfo = new TagCloudInfo();
            tagCloudInfo.title = str;
            mTagClouds.add(tagCloudInfo);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TagCloudInfo tagCloudInfo = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //if (mSelectedPosition == -1) {
                for (int i = 0; i < mTagClouds.size(); i++) {
                    tagCloudInfo = mTagClouds.get(i);
                    if (event.getX() >= tagCloudInfo.rect.left
                            && event.getX() <= tagCloudInfo.rect.right
                            && event.getY() >= tagCloudInfo.rect.top
                            && event.getY() <= tagCloudInfo.rect.bottom) {
                        mSelectedPosition = i;
                        break;
                    }
                }
                if (mSelectedPosition > -1 && tagCloudInfo != null) {
                    tagCloudInfo.color = mColors[mRandom.nextInt(mColors.length)];
                    //tagCloudInfo.textSize = tagCloudInfo.textSize + 5;
                    invalidate();

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com/s?wd=" + tagCloudInfo.title));
                    getContext().startActivity(intent);

                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(100);
                                //mTagClouds.get(mSelectedPosition).textSize -= 5;
                                postInvalidate();
                                mSelectedPosition = -1;
                            } catch (InterruptedException e) {
                            }
                        }
                    }.start();
                }
                //}
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onDraw(Canvas canvas) {
        mWidth = getWidth();
        mHeight = getHeight();

        minTop = mHeight;

        mLeftX = mRightX = mWidth/2;
        mUpY = mDownY = mHeight/2;

        TagCloudInfo tagCloudInfo;

        if (mFirstDraw) {
            computePosition();

            centerVertialTagClouds();

            for (int i = 0; i < mTagClouds.size(); i++) {
                tagCloudInfo = mTagClouds.get(i);
                setMinTopAndMaxBottom(tagCloudInfo);
            }

            centerVertialTagClouds();
            mFirstDraw = false;
        }

        for (int i = 0; i < mTagClouds.size(); i++) {
            tagCloudInfo = mTagClouds.get(i);
            if (tagCloudInfo.rect.top > 5 && tagCloudInfo.rect.bottom < mHeight - 5) {
                mPaint.setTextSize(tagCloudInfo.textSize);
                mPaint.setColor(tagCloudInfo.color);
                canvas.drawText(tagCloudInfo.title, tagCloudInfo.rect.left, tagCloudInfo.rect.bottom, mPaint);
            }
        }

        if (mSelectedPosition > -1) {
            tagCloudInfo = mTagClouds.get(mSelectedPosition);
            mPaint.setTextSize(tagCloudInfo.textSize);
            mPaint.setAlpha(100);
            Rect rect = new Rect(tagCloudInfo.rect.left,
                    tagCloudInfo.rect.bottom + (int)mPaint.ascent(),
                    tagCloudInfo.rect.right,
                    tagCloudInfo.rect.bottom + (int)mPaint.descent());
            canvas.drawRect(rect, mPaint);
            mPaint.setAlpha(255);
        }
    }

    public void computePosition() {
        TagCloudInfo tagCloudInfo;
        for (int i = 0; i < mTagClouds.size(); i++) {
            tagCloudInfo = mTagClouds.get(i);

            mPaint.setTextSize(mRandom.nextInt(15) + 28);
            scalePaint(tagCloudInfo, mPaint);

            tagCloudInfo.textSize = (int)mPaint.getTextSize();
            tagCloudInfo.color = mColors[mRandom.nextInt(mColors.length)];

            int leftWidth = (mWidth - stringWidth)/2;
            //int leftWidth = 3;
            if (i == 0) {
                int drawX = mLeftX - stringWidth/2;
                int drawY = mUpY - stringHeight/2;
                tagCloudInfo.rect = new Rect(drawX, drawY, drawX + stringWidth, drawY + stringHeight);
            } else if (i == 1) {
                mUpY = mUpY - stringHeight - mRandom.nextInt(LINE_SPACE);
                int drawX = mRandom.nextInt(leftWidth);
                int drawY = mUpY - stringHeight/2;
                tagCloudInfo.rect = new Rect(drawX, drawY, drawX + stringWidth, drawY + stringHeight);
            } else if (i == 2) {
                mDownY = mDownY + stringHeight + mRandom.nextInt(LINE_SPACE);
                int drawX = mRandom.nextInt(leftWidth);
                int drawY = mDownY - stringHeight/2;
                tagCloudInfo.rect = new Rect(drawX, drawY, drawX + stringWidth, drawY + stringHeight);
            } else if (i % 2 == 1) {
                int space = mRandom.nextInt(50);
                int preWishRight = mTagClouds.get(i - 2).rect.right;
                if (stringWidth + preWishRight + space > mWidth) {
                    mUpY = mUpY - stringHeight - mRandom.nextInt(LINE_SPACE);
                    int drawX = mRandom.nextInt(leftWidth);
                    int drawY = mUpY - stringHeight/2;
                    tagCloudInfo.rect = new Rect(drawX, drawY, drawX + stringWidth, drawY + stringHeight);
                } else {
                    int drawY = mUpY - stringHeight/2;
                    preWishRight = preWishRight + space;
                    tagCloudInfo.rect = new Rect(preWishRight , drawY, preWishRight + stringWidth, drawY + stringHeight);
                }
            } else if (i % 2 == 0) {
                int space = mRandom.nextInt(50);
                int preWishRight = mTagClouds.get(i - 2).rect.right;
                if (stringWidth + preWishRight + space > mWidth) {
                    mDownY = mDownY + stringHeight + mRandom.nextInt(LINE_SPACE);
                    int drawX = mRandom.nextInt(leftWidth);
                    int drawY = mDownY - stringHeight/2;
                    tagCloudInfo.rect = new Rect(drawX, drawY, drawX + stringWidth, drawY + stringHeight);
                } else {
                    int drawY = mDownY - stringHeight/2;
                    preWishRight = preWishRight + space;
                    tagCloudInfo.rect = new Rect(preWishRight , drawY, preWishRight + stringWidth, drawY + stringHeight);
                }
            }

            setMinTopAndMaxBottom(tagCloudInfo);
        }
    }

    private Paint scalePaint(TagCloudInfo tagCloudInfo, Paint p) {
        FontMetrics fm = p.getFontMetrics();
        stringHeight = (int)(fm.descent - fm.top);
        stringWidth = (int)p.measureText(tagCloudInfo.title);

        if (stringWidth >= mWidth || stringHeight >= mHeight) {
            p.setTextSize(p.getTextSize() - 1);
            scalePaint(tagCloudInfo, p);
        }
        return p;
    }

    private void setMinTopAndMaxBottom(TagCloudInfo tagCloudInfo){
        int top = tagCloudInfo.rect.top;
        int bottom = tagCloudInfo.rect.bottom;

        if (top >= 5 && top < minTop) {
            minTop = top;
        }
        if (bottom <= mHeight -5 && bottom > maxBottom) {
            maxBottom = bottom;
        }
    }

    private void centerVertialTagClouds() {
        int move = 0;
        int topPadding = minTop;
        int bottomPadding = mHeight - maxBottom;

        int space = 5; //left some space for top or bottom
        if (topPadding > 0 && bottomPadding > 0) {
            move = (bottomPadding - topPadding) / 2;
        } else if (topPadding > 0) {
            move = space - topPadding;
        } else if (bottomPadding > 0) {
            move = bottomPadding -space;
        }

        if (move != 0) {
            TagCloudInfo tagCloudInfo = null;
            for (int i = 0; i < mTagClouds.size(); i++) {
                tagCloudInfo = mTagClouds.get(i);
                tagCloudInfo.rect.bottom = tagCloudInfo.rect.bottom + move;
                tagCloudInfo.rect.top = tagCloudInfo.rect.top + move;
            }
        }
    }
}
