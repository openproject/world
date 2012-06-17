package com.tianxia.lib.baseworld;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.tianxia.lib.baseworld.db.BaseSQLiteHelper;
import com.tianxia.lib.baseworld.utils.NetworkUtils;

public abstract class BaseApplication extends Application {

    public static BaseSQLiteHelper mSQLiteHelper;

    public static String mAppName;

    protected List<Class<?>> mTabActivitys = new ArrayList<Class<?>>();
    protected static List<Integer> mTabNormalImages = new ArrayList<Integer>();
    protected static List<Integer> mTabPressImages = new ArrayList<Integer>();

    public static int mNetWorkState = NetworkUtils.NETWORN_NONE;

    public static String mDownloadPath;
    public static int mVersionCode;
    public static String mVersionName;
    public static boolean mShowUpdate = true;

    @Override
    public void onCreate() {
        fillTabs();

        initDb();
        initEnv();
        initLocalVersion();
    }

    public List<Class<?>> getTabActivitys(){
        return mTabActivitys;
    }

    public List<Integer> getTabNormalImages(){
        return mTabNormalImages;
    }
    
    public List<Integer> getTabPressImages(){
        return mTabPressImages;
    }

    /**
     * <ul>fill the tab content with:<ul>
     * <li>tab activitys.</li>
     * <li>tab normal background resId.</li>
     * <li>tab press background resId</li>
     */
    public abstract void fillTabs();

    public abstract void initDb();
    public abstract void initEnv();

    public abstract void exitApp(Context context);

    public void initLocalVersion(){
        PackageInfo pinfo;
        try {
            pinfo = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            mVersionCode = pinfo.versionCode;
            mVersionName = pinfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
