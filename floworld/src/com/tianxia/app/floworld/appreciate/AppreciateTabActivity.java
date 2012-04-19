package com.tianxia.app.floworld.appreciate;

import java.util.HashMap;
import java.util.Map;

import net.youmi.android.AdManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.feedback.NotificationType;
import com.feedback.UMFeedbackService;
import com.tianxia.app.floworld.AppApplication;
import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.cache.ConfigCache;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.main.MainTabFrame;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld.upgrade.AppUpgradeService;
import com.tianxia.lib.baseworld.utils.PreferencesUtils;

public class AppreciateTabActivity extends AdapterActivity<Map<String,String>> {
//    private AppreciateApi appreciateApi = null;
    private int latestNum = 0;
    private String latestTitle = "";
    private int categoryNum = 0;
    private int archiverNum = 0;
    private String latestListUrl = null;
    private String categoryListUrl = null;
    private String archiverListUrl = null;
    private String companyListUrl = null;

    private Intent appreciateListIntent = null;

    private TextView itemTextView = null;
    private TextView itemDescriptionTextView = null;
    private ImageView itemImageView = null;
    private ImageView itemNewImageView = null;
    private int imageHeight = 0;
    private int dividerHeight;

    private int mLatestVersionCode = 0;
    private String mLatestVersionUpdate = null;
    private String mLatestVersionDownload = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AdManager.init(this,"c9329cdab07f3fe8", "b7c1c6a540132372", 30, false);
        UMFeedbackService.enableNewReplyNotification(this, NotificationType.NotificationBar);

        setListData(false);
        getAppreciateConfig();

    }

    /**
     * 装载列表数据
     * @param isUpdate false=初始化,true=网络获取数据后更新列表数据
     */
    private void setListData(boolean isUpdate){

        listData.clear();
        Map<String,String> map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.appreciate_tab_list_item_new));
        if(!isUpdate){
            map.put("name", "最新(数据加载中...)");
        }else{
            if(latestNum == 0){
                map.put("name", "最新");
            }else{
                map.put("name", "最新(本期:" + latestNum + ")");
            }
        }
        if (latestTitle != null && !"".equals(latestTitle)) {
            map.put("description", "本期主题:" + latestTitle);
        } else {
            map.put("description", "最新推荐，最给力图片欣赏!");
        }
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.appreciate_tab_list_item_category));
        map.put("name", "分类");
        if (categoryNum == 0) {
            map.put("description", "花卉高清图片,百花真艳!");
        } else {
            map.put("description", "共" + categoryNum + "种花卉,百花真艳!");
        }
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.appreciate_tab_list_item_archive));
        if (archiverNum == 0) {
            map.put("name", "归档");
        } else {
            map.put("name", "归档(共" + archiverNum + "期)");
        }
        map.put("description", "每期一个主题,期期精彩!");
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.appreciate_tab_list_item_search));
        map.put("name", "搜索");
        map.put("description", "搜索花卉,花语,百科,美文...");
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.appreciate_tab_list_item_hot));
        map.put("name", "合作");
        map.put("description", "精诚合作 ,互惠互利!");
        listData.add(map);

        adapter = new Adapter(this);
        listView.setAdapter(adapter);
    }

    /**
     * 读取所需的Appreciate基本信息
     */
    protected void getAppreciateConfig(){
        String cacheConfigString = ConfigCache.getUrlCache(AppreciateApi.APPRECIATE_CONFIG_URL);
        if (cacheConfigString != null) {
            showAppreciateConfig(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(AppreciateApi.APPRECIATE_CONFIG_URL, new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String result){
                    ConfigCache.setUrlCache(result, AppreciateApi.APPRECIATE_CONFIG_URL);
                    showAppreciateConfig(result);
                    checkNewVersion();
                }

                @Override
                public void onFailure(Throwable arg0) {
                    setListData(true);
                }
                
            });
        }
    }

    private void showAppreciateConfig(String result) {
        try {
            JSONObject appreciateConfig = new JSONObject(result);

            mLatestVersionCode = appreciateConfig.optInt("version-code");
            mLatestVersionUpdate = appreciateConfig.optString("version-update");
            mLatestVersionDownload = AppApplication.mDomain + appreciateConfig.optString("version-download");
            if (mLatestVersionDownload != null) {
                AppApplication.mApkDownloadUrl = mLatestVersionDownload;
            }

            latestNum = appreciateConfig.getJSONObject("latest").optInt("add");
            latestTitle = appreciateConfig.getJSONObject("latest").optString("title");
            categoryNum = appreciateConfig.getJSONObject("category").optInt("c_num");
            archiverNum = appreciateConfig.getJSONObject("archiver").optInt("a_num");
            latestListUrl = AppApplication.mDomain + appreciateConfig.getJSONObject("latest").getString("list");
            categoryListUrl = AppApplication.mDomain + appreciateConfig.getJSONObject("category").getString("list");
            archiverListUrl = AppApplication.mDomain + appreciateConfig.getJSONObject("archiver").getString("list");
            companyListUrl = AppApplication.mDomain + appreciateConfig.getJSONObject("company").getString("list");

            setListData(true);

            //server time id
            String time = appreciateConfig.getJSONObject("latest").optString("time");
            if (time != null) {
                PreferencesUtils.setStringPreferences(this, "config", "serverTime", time);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.appreciate_tab_activity);
        setListView(R.id.appreciate_tab_list);
    }

    @Override
    protected View getView(int position, View convertView) {
        dividerHeight = ((ListView) getListView()).getDividerHeight();
        View view = convertView;
        if(view==null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.appreciate_tab_list_item, null);
        }

        itemImageView = (ImageView) view.findViewById(R.id.item_image);
        itemImageView.setImageResource(Integer.parseInt(listData.get(position).get("image")));
        if(MainTabFrame.mainTabContainerHeight != 0){
            if(imageHeight==0){
                imageHeight = MainTabFrame.mainTabContainerHeight/5 - itemImageView.getPaddingTop() - itemImageView.getPaddingBottom() - dividerHeight*2;
            }
            itemImageView.getLayoutParams().height = imageHeight;
        }

        itemTextView = (TextView) view.findViewById(R.id.item_text);
        itemTextView.setText(listData.get(position).get("name"));
        itemDescriptionTextView = (TextView) view.findViewById(R.id.item_text_description);
        itemDescriptionTextView.setText(listData.get(position).get("description"));

        if (position == 0
                && !(PreferencesUtils.getStringPreference(this, "config", "localTime", "0")
                        .equals(PreferencesUtils.getStringPreference(this, "config", "serverTime", "0")))) {
            itemNewImageView = (ImageView) view.findViewById(R.id.item_new_image);
            itemNewImageView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
        case 0:
            //最新
            if (latestListUrl == null) {
                setListData(false);
                getAppreciateConfig();
                return;
            }
            appreciateListIntent = new Intent(AppreciateTabActivity.this, AppreciateLatestActivity.class);
            appreciateListIntent.putExtra("url", latestListUrl);
            if (!PreferencesUtils.getStringPreference(this, "config", "localTime", "0")
                    .equals(PreferencesUtils.getStringPreference(this, "config", "serverTime", "0"))) {
                PreferencesUtils.setStringPreferences(this, "config", "localTime", 
                        PreferencesUtils.getStringPreference(this, "config", "serverTime", "0"));
                getAppreciateConfig();
            }
            break;

        case 1:
            //分类
            if (categoryListUrl == null) {
                setListData(false);
                getAppreciateConfig();
                return;
            }
            appreciateListIntent = new Intent(AppreciateTabActivity.this, AppreciateCategoryActivity.class);
            appreciateListIntent.putExtra("url", categoryListUrl);
            break;

        case 2:
            //归档
            if (archiverListUrl == null) {
                setListData(false);
                getAppreciateConfig();
                return;
            }
            appreciateListIntent = new Intent(AppreciateTabActivity.this, AppreciateArchiverActivity.class);
            appreciateListIntent.putExtra("url", archiverListUrl);
            break;

        case 3:
            appreciateListIntent = new Intent(AppreciateTabActivity.this, AppreciateSearchActivity.class);
            appreciateListIntent.putExtra("url", categoryListUrl);
            break;

        case 4:
            //合作
            if (companyListUrl == null) {
                setListData(false);
                getAppreciateConfig();
                return;
            }
            appreciateListIntent = new Intent(AppreciateTabActivity.this, AppreciateCompanyActivity.class);
            appreciateListIntent.putExtra("url", companyListUrl);
            break;

        default:
            appreciateListIntent = null;
            break;
        }

        if (appreciateListIntent != null){
            startActivity(appreciateListIntent);
        }
    }

    public void checkNewVersion(){
        if (BaseApplication.mVersionCode < mLatestVersionCode && BaseApplication.mShowUpdate) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.check_new_version)
                .setMessage(mLatestVersionUpdate)
                .setPositiveButton(R.string.app_upgrade_confirm, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AppreciateTabActivity.this, AppUpgradeService.class);
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
            BaseApplication.mShowUpdate = false;
        }
    }
}
