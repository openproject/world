package com.tianxia.lib.baseworld;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

public abstract class BaseApplication extends Application {

    protected List<Class<?>> tabActivitys = new ArrayList<Class<?>>();
    protected static List<Integer> tabNormalImages = new ArrayList<Integer>();
    protected static List<Integer> tabPressImages = new ArrayList<Integer>();
    
    @Override
    public void onCreate() {
        fillTabs();
    }

    public List<Class<?>> getTabActivitys(){
        return tabActivitys;
    }

    public List<Integer> getTabNormalImages(){
        return tabNormalImages;
    }
    
    public List<Integer> getTabPressImages(){
        return tabPressImages;
    }

    /**
     * <ul>fill the tab content with:<ul>
     * <li>tab activitys.</li>
     * <li>tab normal background resId.</li>
     * <li>tab press background resId</li>
     */
    public abstract void fillTabs();
    
}
