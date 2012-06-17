package com.tianxia.app.starworld;

import android.content.Context;

import com.tianxia.app.starworld.R;
import com.tianxia.app.starworld.setting.SettingTabActivity;
import com.tianxia.lib.baseworld.BaseApplication;

public class AppApplication extends BaseApplication {

    @Override
    public void fillTabs() {
        mTabActivitys.add(SettingTabActivity.class);

        mTabNormalImages.add(R.drawable.setting_normal);

        mTabPressImages.add(R.drawable.setting_press);
    }

    @Override
    public void initDb() {
    }

    @Override
    public void initEnv() {
    }

    @Override
    public void exitApp(Context context) {
    }
}
