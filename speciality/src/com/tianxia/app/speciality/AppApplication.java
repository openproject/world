package com.tianxia.app.speciality;

import com.tianxia.app.speciality.setting.SettingTabActivity;
import com.tianxia.lib.baseworld.BaseApplication;

public class AppApplication extends BaseApplication {

    @Override
    public void fillTabs() {
        tabActivitys.add(SettingTabActivity.class);

        tabNormalImages.add(R.drawable.setting_normal);

        tabPressImages.add(R.drawable.setting_press);
    }
}
