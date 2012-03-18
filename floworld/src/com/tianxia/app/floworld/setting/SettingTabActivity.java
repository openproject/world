package com.tianxia.app.floworld.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.tianxia.app.floworld.AppApplication;
import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.appreciate.AppreciateApi;
import com.tianxia.app.floworld.cache.ConfigCache;
import com.tianxia.app.floworld.utils.NetworkUtils;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.activity.PreferenceActivity;
import com.tianxia.lib.baseworld.activity.SettingAboutActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld.upgrade.AppUpgradeService;
import com.tianxia.lib.baseworld.widget.CornerListView;

public class SettingTabActivity extends PreferenceActivity implements OnItemClickListener{

    private String Setting_1 = "离线下载";
    private String Setting_2 = "分享该软件给朋友";
    private String Setting_3 = "评分";
    private String Setting_4 = "投稿";
    private String Setting_5 = "检查新版本";
    private String Setting_6 = "意见反馈";
    private String Setting_7 = "关于";
    private String Setting_8 = "捐赠";
    private String Setting_9 = "给我们发短信";
    private String Setting_10 = "电话联系我们";

    private int mLatestVersionCode = 0;
    private String mLatestVersionUpdate = null;
    private String mLatestVersionDownload = null;

    ProgressDialog mProgressDialog;
    @Override
    public void setLayout() {
        setContentView(R.layout.setting_tab_activity);
        cornerContainer = (LinearLayout) findViewById(R.id.setting);

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

        Map<String,String> map = new HashMap<String, String>();
        map.put("text", Setting_1);
        listData.add(map);
        listDatas.add(listData);

        listData = new ArrayList<Map<String,String>>();
        map = new HashMap<String, String>();
        map.put("text", Setting_2);
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", Setting_3);
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", Setting_4);
        listData.add(map);
        listDatas.add(listData);

        listData = new ArrayList<Map<String,String>>();
        map = new HashMap<String, String>();
        map.put("text", Setting_5);
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", Setting_6);
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", Setting_7);
        listData.add(map);
        listDatas.add(listData);

        listData = new ArrayList<Map<String,String>>();
        map = new HashMap<String, String>();
        map.put("text", Setting_9);
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", Setting_10);
        listData.add(map);
        listDatas.add(listData);

        listData = new ArrayList<Map<String,String>>();
        map = new HashMap<String, String>();
        map.put("text", Setting_8);
        listData.add(map);

        listDatas.add(listData);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        TextView textView = (TextView) view.findViewById(R.id.setting_list_item_text);
        String settingText = textView.getText().toString();
        if (Setting_1.equals(settingText)) {
            setting_offline();
        } else if (Setting_2.equals(settingText)) {
            shareApp();
        } else if (Setting_3.equals(settingText)) {
            jmupToMarket();
        } else if (Setting_4.equals(settingText)) {
        } else if (Setting_5.equals(settingText)) {
            setting_check_new_version();
        } else if (Setting_6.equals(settingText)) {
            feedBackSuggestion();
        } else if (Setting_7.equals(settingText)) {
            Intent intent = new Intent(this, SettingAboutActivity.class);
            startActivity(intent);
        } else if (Setting_8.equals(settingText)) {
        } else if (Setting_9.equals(settingText)) {
            Uri uri = Uri.parse(getString(R.string.setting_contact_smsto));
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(intent);
        } else if (Setting_10.equals(settingText)) {
            Uri uri = Uri.parse(getString(R.string.setting_contact_tel));
            Intent intent = new Intent(Intent.ACTION_CALL, uri);
            startActivity(intent);
        }
    }

    private void setting_offline() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("即将开放，敬请期待!")
               .setPositiveButton("确定", null)
               .create()
               .show();
    }

    private void setting_check_new_version() {

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

        String cacheConfigString = ConfigCache.getUrlCache(AppreciateApi.APPRECIATE_CONFIG_URL);
        if (cacheConfigString != null) {
            checkNewVersion(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(AppreciateApi.APPRECIATE_CONFIG_URL, new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String result){
                    ConfigCache.setUrlCache(result, AppreciateApi.APPRECIATE_CONFIG_URL);
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
            mLatestVersionDownload = AppApplication.domain + appreciateConfig.getString("version-download");
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

    public void jmupToMarket() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        startActivity(intent);
    }

    public void feedBackSuggestion() {
        UMFeedbackService.openUmengFeedbackSDK(this);
    }
}
