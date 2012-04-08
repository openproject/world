package com.tianxia.app.healthworld.setting;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.feedback.UMFeedbackService;
import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.PreferenceActivity;
import com.tianxia.lib.baseworld.activity.SettingAboutActivity;
import com.tianxia.lib.baseworld.alipay.AlixPay;
import com.tianxia.lib.baseworld.widget.CornerListView;

public class SettingTabActivity extends PreferenceActivity implements OnItemClickListener{

    private String[] mSettingItems = {"离线下载",
                                       "",
                                       "分享该软件给朋友",
                                       "评分",
                                       "",
                                       "检查新版本",
                                       "意见反馈",
                                       "关于",
                                       "",
                                       "捐赠"};
    private String[] mSettingItemMethods = {"setting_offline",
                                            "",
                                            "shareApp",
                                            "jmupToMarket",
                                            "",
                                            "setting_check_new_version",
                                            "feedBackSuggestion",
                                            "about",
                                            "",
                                            "setting_donate"};
    private HashMap<String, String> mSettingItemMethodMap = new HashMap<String, String>();

//    private int mLatestVersionCode = 0;
//    private String mLatestVersionUpdate = null;
//    private String mLatestVersionDownload = null;

    ProgressDialog mProgressDialog;
    @Override
    public void setLayout() {
        setContentView(R.layout.setting_tab_activity);
        cornerContainer = (LinearLayout) findViewById(R.id.setting);

        for (int i = 0; i < mSettingItems.length; i++) {
            mSettingItemMethodMap.put(mSettingItems[i], mSettingItemMethods[i]);
        }

        int size = listDatas.size();
        CornerListView cornerListView;
        LayoutParams lp;
        SimpleAdapter adapter;
        for (int i = 0; i < size; i++) {
            cornerListView = new CornerListView(this);
            lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            if (i == 0 && i == (size - 1)) {
                lp.setMargins(8, 8, 8, 8);
            } else if (i == 0) {
                lp.setMargins(8, 8, 8, 4);
            }else if (i == (size - 1)) {
                lp.setMargins(8, 4, 8, 8);
            } else {
                lp.setMargins(8, 4, 8, 4);
            }
            cornerListView.setLayoutParams(lp);
            cornerListView.setCacheColorHint(0);
            cornerListView.setDivider(getResources().getDrawable(R.drawable.app_divider_h_gray));
            cornerListView.setScrollbarFadingEnabled(false);
            cornerContainer.addView(cornerListView);

            adapter = new SimpleAdapter(getApplicationContext(), listDatas.get(i), R.layout.setting_tab_list_item , new String[]{"text"}, new int[]{R.id.setting_list_item_text});
            cornerListView.setAdapter(adapter);
            cornerListView.setOnItemClickListener(this);
            int height = listDatas.get(i).size() * (int) getResources().getDimension(R.dimen.setting_item_height);
            height += 1;
            cornerListView.getLayoutParams().height = height;
        }
    }

    @Override
    public void setListDatas() {
        List<Map<String,String>> listData = new ArrayList<Map<String,String>>();

        Map<String,String> map;

        for(int i = 0; i < mSettingItems.length; i++) {
            if ("".equals(mSettingItems[i])) {
                listDatas.add(listData);
                listData = new ArrayList<Map<String,String>>();
            } else {
                map = new HashMap<String, String>();
                map.put("text", mSettingItems[i]);
                listData.add(map);
            }
        }

        listDatas.add(listData);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        TextView textView = (TextView) view.findViewById(R.id.setting_list_item_text);
        String key = textView.getText().toString();
        Class<? extends SettingTabActivity> clazz = this.getClass();
        try {
            Method method = clazz.getMethod(mSettingItemMethodMap.get(key));
            method.invoke(SettingTabActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setting_offline() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("即将开放，敬请期待!")
               .setPositiveButton("确定", null)
               .create()
               .show();
    }

    public void setting_donate() {
        AlixPay alixPay = new AlixPay(SettingTabActivity.this);
        alixPay.pay();
    }

    public void setting_check_new_version() {
    }

    public void checkNewVersion(String result){
    }

    public void shareApp() {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_share_app_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_share_app_body) + AppApplication.mApkDownloadUrl);
        startActivity(Intent.createChooser(intent, getString(R.string.setting_share_app_title)));
    }

    public void about() {
        Intent intent = new Intent(this, SettingAboutActivity.class);
        startActivity(intent);
    }

    public void jmupToMarket() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        startActivity(intent);
    }

    public void feedBackSuggestion() {
        UMFeedbackService.openUmengFeedbackSDK(this);
    }
}
