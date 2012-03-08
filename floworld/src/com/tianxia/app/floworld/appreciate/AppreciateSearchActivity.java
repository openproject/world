package com.tianxia.app.floworld.appreciate;

import net.youmi.android.AdView;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.tianxia.app.floworld.R;
import com.tianxia.lib.baseworld.activity.BaseActivity;

public class AppreciateSearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appreciate_search_activity);

        //初始化广告视图
        AdView adView = new AdView(this);
        FrameLayout.LayoutParams params = new 
        FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, 
        FrameLayout.LayoutParams.WRAP_CONTENT);
        //设置广告出现的位置(悬浮于屏幕右下角)
        params.gravity=Gravity.BOTTOM|Gravity.RIGHT; 
        //将广告视图加入 Activity中
        addContentView(adView, params);
    }
}
