package com.tianxia.app.floworld.cache;

import java.io.File;
import java.io.IOException;

import android.util.Log;

import com.tianxia.app.floworld.AppApplication;
import com.tianxia.app.floworld.utils.FileUtils;

public class ConfigCache {

    private static final String TAG = ConfigCache.class.getName();
    public static String getUrlCache(String url) {
        if (url == null) {
            return null;
        }

        String result = null;
        File file = new File(AppApplication.mSdcardDataDir + "/" + getCacheDecodeString(url));
        if (file.exists() && file.isFile()) {
            try {
                result = FileUtils.readTextFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void setUrlCache(String data, String url) {
        File file = new File(AppApplication.mSdcardDataDir + "/" + getCacheDecodeString(url));
        try {
            FileUtils.writeTextFile(file, data);
        } catch (IOException e) {
            Log.d(TAG, "write " + file.getAbsolutePath() + " data failed!");
            e.printStackTrace();
        }
    }

    public static String getCacheDecodeString(String url) {
        if (url != null) {
            return url.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
        }
        return null;
    }
}
