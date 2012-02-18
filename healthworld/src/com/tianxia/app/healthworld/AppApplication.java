package com.tianxia.app.healthworld;

import com.tianxia.app.healthworld.setting.SettingTabActivity;
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
}
