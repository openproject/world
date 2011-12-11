package com.tianxia.app.healthworld.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter;

import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.PreferenceActivity;
import com.tianxia.lib.baseworld.widget.CornerListView;

public class SettingTabActivity extends PreferenceActivity{

    @Override
    public void setLayout() {
        setContentView(R.layout.main_tab_setting);
        cornerContainer = (LinearLayout) findViewById(R.id.setting);

        int size = listDatas.size();
        CornerListView cornerListView;
        LayoutParams lp;
        SimpleAdapter adapter;
        for (int i = 0; i < size; i++) {
            cornerListView = new CornerListView(this);
            lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            lp.setMargins(8, 16, 8, 0);
            cornerListView.setLayoutParams(lp);
            cornerListView.setCacheColorHint(0);
            cornerContainer.addView(cornerListView);

            adapter = new SimpleAdapter(getApplicationContext(), listDatas.get(i), R.layout.main_tab_setting_list_item , new String[]{"text"}, new int[]{R.id.setting_list_item_text});
            cornerListView.setAdapter(adapter);
        }
    }

    @Override
    public void setListDatas() {
        List<Map<String,String>> listData = new ArrayList<Map<String,String>>();

        Map<String,String> map = new HashMap<String, String>();
        map = new HashMap<String, String>();
        map.put("text", "检查新版本");
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", "反馈意见");
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", "关于我们");
        listData.add(map);
        listDatas.add(listData);

        listData = new ArrayList<Map<String,String>>();
        map = new HashMap<String, String>();
        map.put("text", "支持我们，请点击这里的广告");
        listData.add(map);

        listDatas.add(listData);
    }
    
}
