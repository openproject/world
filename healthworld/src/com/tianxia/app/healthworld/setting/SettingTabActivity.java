package com.tianxia.app.healthworld.setting;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import android.net.Uri;

import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.feedback.UMFeedbackService;
import com.waps.UpdatePointsNotifier;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.AppApplicationApi;
import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.PreferenceActivity;
import com.tianxia.lib.baseworld.activity.SettingAboutActivity;
import com.tianxia.lib.baseworld.alipay.AlixPay;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld.upgrade.AppUpgradeService;
import com.tianxia.lib.baseworld.utils.NetworkUtils;
import com.tianxia.lib.baseworld.widget.CornerListView;

import com.waps.AppConnect;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;

public class SettingTabActivity extends PreferenceActivity
        implements OnItemClickListener, UpdatePointsNotifier{

    private String[] mSettingItems = {"离线下载",
                                       "",
                                       "分享该软件给朋友",
                                       "评分",
                                       "",
                                       "检查新版本",
                                       "意见反馈",
                                       "关于",
                                       "",
                                       "去广告",
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
                                            "ad_app_list",
                                            "setting_donate"};
    private HashMap<String, String> mSettingItemMethodMap = new HashMap<String, String>();

    private int mLatestVersionCode = 0;
    private String mLatestVersionUpdate = null;
    private String mLatestVersionDownload = null;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取积分
        AppConnect.getInstance(this).getPoints(this);
    }
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
        listDatas.clear();
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

    public void ad_app_list() {
        AppConnect.getInstance(this).showOffers(this);
    }

    public void setting_check_new_version() {

        if (AppApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
            Toast.makeText(this, R.string.check_new_version_no_network, Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(R.string.check_new_version_title);
        mProgressDialog.setMessage(getString(R.string.check_new_version_message));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        String cacheConfigString = ConfigCache.getUrlCache(AppApplicationApi.INFOMATION_URL);
        if (cacheConfigString != null) {
            checkNewVersion(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(AppApplicationApi.INFOMATION_URL, new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String result){
                    ConfigCache.setUrlCache(result, AppApplicationApi.INFOMATION_URL);
                    checkNewVersion(result);
                }

                @Override
                public void onFailure(Throwable arg0) {
                    mProgressDialog.cancel();
                }

            });
        }
    }

    public void checkNewVersion(String result){
        if (result == null || "".equals(result)) {
            mProgressDialog.cancel();
            Toast.makeText(this, R.string.check_new_version_null, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject appreciateConfig = new JSONObject(result);
            mLatestVersionCode = appreciateConfig.getInt("version-code");
            mLatestVersionUpdate = appreciateConfig.getString("version-update");
            mLatestVersionDownload = AppApplication.mDomain + appreciateConfig.getString("version-download");
        } catch (JSONException e) {
            e.printStackTrace();
            mProgressDialog.cancel();
            Toast.makeText(this, R.string.check_new_version_exception, Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog.cancel();

        if (BaseApplication.mVersionCode < mLatestVersionCode) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.check_new_version)
                .setMessage(mLatestVersionUpdate)
                .setPositiveButton(R.string.app_upgrade_confirm, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SettingTabActivity.this, AppUpgradeService.class);
                        intent.putExtra("downloadUrl", mLatestVersionDownload);
                        startService(intent);
                    }
                })
                .setNegativeButton(R.string.app_upgrade_cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
        } else {
            Toast.makeText(this, R.string.check_new_version_latest, Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onResume() {
        super.onResume();
        setListDatas();
        setLayout();
    }

    @Override
    public void getUpdatePoints(String currencyName, int pointTotal) {
        mSettingItems[9] = "去广告(当前:" + pointTotal + "" + currencyName +")";
        runOnUiThread(new Runnable () {
            public void run() {
                setListDatas();
                setLayout();
            }
        });
    }

    //获取失败
    @Override
    public void getUpdatePointsFailed(String error) {
    }
}
