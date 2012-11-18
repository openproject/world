package com.tianxia.app.healthworld;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;

import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.category.CategoryTabActivity;
import com.tianxia.app.healthworld.digest.DigestTabActivity;
import com.tianxia.app.healthworld.favorite.FavoriteTabActivity;
import com.tianxia.app.healthworld.infomation.InfomationTabActivity;
import com.tianxia.app.healthworld.setting.SettingTabActivity;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld.utils.NetworkUtils;
import com.tianxia.lib.baseworld.utils.PreferencesUtils;
import com.waps.AppConnect;

public class AppApplication extends BaseApplication {

    public static final String DOMAIN = "domain";
    public static final String DOMAIN_URL = "url";
    public static String mDomain = "http://www.kaiyuanxiangmu.com/";
    public static String mBakeDomain = "http://1.kaiyuanxiangmu.sinaapp.com/";

    private static final String DB_NAME = "healthworld.db";

    public static String mSdcardDataDir;
    public static String mApkDownloadUrl = null;

    @Override
    public void fillTabs() {
        mTabActivitys.add(InfomationTabActivity.class);
        mTabActivitys.add(CategoryTabActivity.class);
        mTabActivitys.add(DigestTabActivity.class);
        mTabActivitys.add(FavoriteTabActivity.class);
        mTabActivitys.add(SettingTabActivity.class);

        mTabNormalImages.add(R.drawable.infomation_normal);
        mTabNormalImages.add(R.drawable.category_normal);
        mTabNormalImages.add(R.drawable.digest_normal);
        mTabNormalImages.add(R.drawable.favorite_normal);
        mTabNormalImages.add(R.drawable.setting_normal);

        mTabPressImages.add(R.drawable.infomation_press);
        mTabPressImages.add(R.drawable.category_press);
        mTabPressImages.add(R.drawable.digest_press);
        mTabPressImages.add(R.drawable.favorite_press);
        mTabPressImages.add(R.drawable.setting_press);
    }

    @Override
    public void initDb() {
        mSQLiteHelper = new AppSQLiteHelper(getApplicationContext(), DB_NAME, 1);
    }

    @Override
    public void initEnv() {
        mAppName = "healthworld";
        mDownloadPath = "/healthworld/download";
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getPath() +  "/healthworld/config/");
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
        AppConnect.getInstance(getApplicationContext());
    }

    @Override
    public void exitApp(final Context context) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(this.getString(R.string.app_exit_title))
            .setMessage(this.getString(R.string.app_exit_message))
            .setPositiveButton("退出",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AppConnect.getInstance(context).finalize();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
            ).setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
            );
        alertBuilder.create().show();
    }

    public void checkDomain(final String domain, final boolean stop){
        AppApplication.mDomain = PreferencesUtils.getStringPreference(getApplicationContext(), DOMAIN, DOMAIN_URL, mDomain);
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
                PreferencesUtils.setStringPreferences(getApplicationContext(), DOMAIN, DOMAIN_URL, domain);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
