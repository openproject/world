package com.tianxia.app.floworld;

import java.io.File;

import android.os.Environment;

import com.tianxia.app.floworld.appreciate.AppreciateTabActivity;
import com.tianxia.app.floworld.discuss.DiscussTabActivity;
import com.tianxia.app.floworld.favorite.FavoriteTabActivity;
import com.tianxia.app.floworld.identification.IdentificationTabActivity;
import com.tianxia.app.floworld.setting.SettingTabActivity;
import com.tianxia.app.floworld.utils.NetworkUtils;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.db.BaseSQLiteHelper;

public class AppApplication extends BaseApplication {

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
    }

}
