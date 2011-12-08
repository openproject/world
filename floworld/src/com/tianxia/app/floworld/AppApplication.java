package com.tianxia.app.floworld;

import com.tianxia.app.floworld.appreciate.AppreciateTabActivity;
import com.tianxia.app.floworld.discuss.DiscussTabActivity;
import com.tianxia.app.floworld.favorite.FavoriteTabActivity;
import com.tianxia.app.floworld.identification.IdentificationTabActivity;
import com.tianxia.app.floworld.setting.SettingTabActivity;
import com.tianxia.lib.baseworld.BaseApplication;

public class AppApplication extends BaseApplication {

    @Override
    public void fillTabs() {
        tabActivitys.add(AppreciateTabActivity.class);
        tabActivitys.add(DiscussTabActivity.class);
        tabActivitys.add(IdentificationTabActivity.class);
        tabActivitys.add(FavoriteTabActivity.class);
        tabActivitys.add(SettingTabActivity.class);

        tabNormalImages.add(R.drawable.appreciate_normal);
        tabNormalImages.add(R.drawable.discuss_normal);
        tabNormalImages.add(R.drawable.identification_normal);
        tabNormalImages.add(R.drawable.favorite_normal);
        tabNormalImages.add(R.drawable.setting_normal);

        tabPressImages.add(R.drawable.appreciate_press);
        tabPressImages.add(R.drawable.discuss_press);
        tabPressImages.add(R.drawable.identification_press);
        tabPressImages.add(R.drawable.favorite_press);
        tabPressImages.add(R.drawable.setting_press);
    }

}
