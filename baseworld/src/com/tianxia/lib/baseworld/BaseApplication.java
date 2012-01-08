package com.tianxia.lib.baseworld;

import java.util.ArrayList;
import java.util.List;

import com.tianxia.lib.baseworld.db.BaseSQLiteHelper;

import android.app.Application;

public abstract class BaseApplication extends Application {

    public BaseSQLiteHelper mSQLiteHelper;

    protected List<Class<?>> mTabActivitys = new ArrayList<Class<?>>();
    protected static List<Integer> mTabNormalImages = new ArrayList<Integer>();
    protected static List<Integer> mTabPressImages = new ArrayList<Integer>();

    @Override
    public void onCreate() {
        fillTabs();

        initDb();
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
}
