package com.tianxia.app.floworld.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class ScreenUtils {

    private int mWidth;
    private static int mHeight;

    public int getWidth() {
        return mWidth;
    }

    public static int getHeight() {
        return mHeight;
    }

    public ScreenUtils(Activity activity){
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        mWidth = metric.widthPixels;
        mHeight = metric.heightPixels;
    }
}
