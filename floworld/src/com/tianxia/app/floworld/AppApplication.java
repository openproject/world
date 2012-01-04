package com.tianxia.app.floworld;

import com.tianxia.app.floworld.appreciate.AppreciateTabActivity;
import com.tianxia.app.floworld.discuss.DiscussTabActivity;
import com.tianxia.app.floworld.favorite.FavoriteTabActivity;
import com.tianxia.app.floworld.identification.IdentificationTabActivity;
import com.tianxia.app.floworld.setting.SettingTabActivity;
import com.tianxia.lib.baseworld.BaseApplication;

public class AppApplication extends BaseApplication {

    private static final String DB_NAME = "floworld.db";

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

}
