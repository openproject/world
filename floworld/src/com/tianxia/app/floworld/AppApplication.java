package com.tianxia.app.floworld;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;

import com.tianxia.app.floworld.appreciate.AppreciateTabActivity;
import com.tianxia.app.floworld.cache.ConfigCache;
import com.tianxia.app.floworld.discuss.DiscussTabActivity;
import com.tianxia.app.floworld.favorite.FavoriteTabActivity;
import com.tianxia.app.floworld.identification.IdentificationTabActivity;
import com.tianxia.app.floworld.setting.SettingTabActivity;
import com.tianxia.app.floworld.utils.NetworkUtils;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.db.BaseSQLiteHelper;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;

public class AppApplication extends BaseApplication {

    public static String mDomain = "http://www.kaiyuanxiangmu.com/";
    public static String mBakeDomain = "http://1.kaiyuanxiangmu.sinaapp.com/";
    private static final String DB_NAME = "floworld.db";

    public static BaseSQLiteHelper mSQLiteHelper;

    public static String mSdcardDataDir;
    public static String mApkDownloadUrl = null;
    public static int mNetWorkState = NetworkUtils.NETWORN_NONE;

    @Override
    public void fillTabs() {
        mTabActivitys.add(AppreciateTabActivity.class);
        mTabActivitys.add(DiscussTabActivity.class);
        mTabActivitys.add(IdentificationTabActivity.class);
        mTabActivitys.add(FavoriteTabActivity.class);
        mTabActivitys.add(SettingTabActivity.class);

        mTabNormalImages.add(R.drawable.appreciate_normal);
        mTabNormalImages.add(R.drawable.discuss_normal);
        mTabNormalImages.add(R.drawable.identification_normal);
        mTabNormalImages.add(R.drawable.favorite_normal);
        mTabNormalImages.add(R.drawable.setting_normal);

        mTabPressImages.add(R.drawable.appreciate_press);
        mTabPressImages.add(R.drawable.discuss_press);
        mTabPressImages.add(R.drawable.identification_press);
        mTabPressImages.add(R.drawable.favorite_press);
        mTabPressImages.add(R.drawable.setting_press);
    }

    @Override
    public void initDb() {
        mSQLiteHelper = new AppSQLiteHelper(getApplicationContext(), DB_NAME, 1);
    }

    @Override
    public void initEnv() {
        mDownloadPath = "/floworld/download";
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getPath() +  "/floworld/config/");
            if(!file.exists()) {
                if (file.mkdirs()) {
                    mSdcardDataDir = file.getAbsolutePath();
                }
            } else {
                mSdcardDataDir = file.getAbsolutePath();
            }
        }

        mNetWorkState = NetworkUtils.getNetworkState(this);
        checkDomain(mDomain, false);
    }

    public void checkDomain(final String domain, final boolean stop){
        String cacheConfigString = ConfigCache.getUrlCache(domain + "host.json");
        if (cacheConfigString != null) {
            updateDomain(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(domain + "host.json", new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(String result) {
                    ConfigCache.setUrlCache(result, domain + "host.json");
                    updateDomain(result);
                }

                @Override
                public void onFailure(Throwable arg0) {
                    if (!stop) {
                        checkDomain(mBakeDomain, true);
                    }
                }

                @Override
                public void onFinish() {
                }
            });
        }
    }

    public void updateDomain(String result) {
        try {
            JSONObject appreciateConfig = new JSONObject(result);
            String domain = appreciateConfig.optString("domain");
            if (domain != null && !"".equals(domain)) {
                AppApplication.mDomain = domain;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
