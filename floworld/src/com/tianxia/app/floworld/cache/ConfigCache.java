package com.tianxia.app.floworld.cache;

import java.io.File;
import java.io.IOException;

import android.util.Log;

import com.tianxia.app.floworld.AppApplication;
import com.tianxia.app.floworld.utils.FileUtils;
import com.tianxia.app.floworld.utils.NetworkUtils;

public class ConfigCache {

    public static final int CONFIG_CACHE_MOBILE_TIMEOUT  = 3600000;  //1 hour
    public static final int CONFIG_CACHE_WIFI_TIMEOUT    = 300000;   //5 minute

    private static final String TAG = ConfigCache.class.getName();
    public static String getUrlCache(String url) {
        if (url == null) {
            return null;
        }

        String result = null;
        File file = new File(AppApplication.mSdcardDataDir + "/" + getCacheDecodeString(url));
        if (file.exists() && file.isFile()) {
            long expiredTime = System.currentTimeMillis() - file.lastModified();
            if(AppApplication.mNetWorkState == NetworkUtils.NETWORN_WIFI && expiredTime > CONFIG_CACHE_WIFI_TIMEOUT) {
                return null;
            } else if (AppApplication.mNetWorkState == NetworkUtils.NETWORN_WIFI && expiredTime > CONFIG_CACHE_MOBILE_TIMEOUT) {
                return null;
            }
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
