package com.tianxia.app.floworld.appreciate;

import android.content.Context;

import com.tianxia.app.floworld.AppApplication;
import com.tianxia.lib.baseworld.utils.FileUtils;

public class AppreciateApi {

    public static String APPRECIATE_CONFIG_URL = AppApplication.mDomain + "floworld/data/json/appreciate.json";
    public static String APPRECIATE_CATEGORY_BASE_URL = AppApplication.mDomain + "floworld/data/json/appreciate/category/";
    /**
     * deprecated
     * 读取本地Config
     * @param context
     * @return
     */
    public String getConfigJson(Context context){

        String result = null;
        try{
            result = FileUtils.readTextInputStream(context.getAssets().open("config.json"));
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }
}
