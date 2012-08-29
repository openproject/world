package com.tianxia.app.healthworld.infomation;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import android.os.Bundle;
import android.os.Environment;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.feedback.NotificationType;
import com.feedback.UMFeedbackService;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.AppApplicationApi;
import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.model.StatusInfo;
import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.main.MainTabFrame;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld.upgrade.AppUpgradeService;
import com.tianxia.lib.baseworld.utils.DownloadUtils;
import com.tianxia.lib.baseworld.utils.EmptyViewUtils;
import com.tianxia.lib.baseworld.utils.FileUtils;
import com.tianxia.lib.baseworld.utils.PreferencesUtils;
import com.tianxia.lib.baseworld.utils.StringUtils;
import com.tianxia.lib.baseworld.widget.RefreshListView;
import com.tianxia.lib.baseworld.widget.RefreshListView.RefreshListener;
import com.tianxia.widget.image.SmartImageView;

import com.waps.AppConnect;
import com.waps.AdView;
import com.waps.UpdatePointsNotifier;

import java.io.File;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InfomationTabActivity extends AdapterActivity<StatusInfo>
        implements RefreshListener, UpdatePointsNotifier{

    private SmartImageView mItemAvatar;
    private TextView mItemName;
    private TextView mItemDate;
    private TextView mItemText;

    private SimpleDateFormat mSinaWeiboDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new DateFormatSymbols(Locale.US));
    private SimpleDateFormat mSimpleDateFormat;

    private int pageIndex = 0;

    private AdView mAdView;

    private int mLatestVersionCode = 0;
    private String mLatestVersionUpdate = null;
    private String mLatestVersionDownload = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInfomationList();

        UMFeedbackService.enableNewReplyNotification(this, NotificationType.NotificationBar);

        mSimpleDateFormat = new SimpleDateFormat("MM-dd hh:mm");

        //获取积分
        AppConnect.getInstance(this).getPoints(this);

        listView .setOnCreateContextMenuListener(this);
    }

    private void setInfomationList() {
        String cacheConfigString = ConfigCache.getUrlCache(AppApplicationApi.INFOMATION_URL);
        if (cacheConfigString != null) {
            showInfomationList(cacheConfigString);
            checkNewVersion();
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(AppApplicationApi.INFOMATION_URL, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(String result){
                    ConfigCache.setUrlCache(result, AppApplicationApi.INFOMATION_URL);
                    showInfomationList(result);
                    checkNewVersion();
                }

                @Override
                public void onFailure(Throwable arg0) {
                    listView.setAdapter(null);
                    showFailEmptyView();
                }

            });
        }
    }

    private void moreInfomationList(int pageIndex) {
        String cacheConfigString = ConfigCache.getUrlCache(AppApplicationApi.INFOMATION_PAGE_URL + pageIndex + ".json");
        if (cacheConfigString != null) {
            showInfomationList(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(AppApplicationApi.INFOMATION_PAGE_URL + pageIndex + ".json", new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String result){
                    showInfomationList(result);
                    ((RefreshListView)listView).finishFootView();
                }

                @Override
                public void onFailure(Throwable arg0) {
                    ((RefreshListView)listView).finishFootView();
                    Toast.makeText(InfomationTabActivity.this, R.string.app_loading_fail, Toast.LENGTH_SHORT).show();
                    arg0.printStackTrace();
                }

            });
        }
    }

    private void showInfomationList(String result) {
        try {
            JSONObject statusConfig = new JSONObject(result);

            mLatestVersionCode = statusConfig.optInt("version-code");
            mLatestVersionUpdate = statusConfig.optString("version-update");
            mLatestVersionDownload = AppApplication.mDomain + statusConfig.optString("version-download");
            if (mLatestVersionDownload != null) {
                AppApplication.mApkDownloadUrl = mLatestVersionDownload;
            }

            JSONArray statusList = statusConfig.getJSONArray("statuses");
            StatusInfo statusInfo = null;
            for (int i = statusList.length() - 1; i >= 0; i--) {
                statusInfo = new StatusInfo();
                statusInfo.created = statusList.getJSONObject(i).optString("created_at");
                statusInfo.avatar = statusList.getJSONObject(i).getString("avatar");
                statusInfo.name = statusList.getJSONObject(i).getString("name");
                statusInfo.author = statusList.getJSONObject(i).getString("author");
                statusInfo.text = statusList.getJSONObject(i).getString("text");
                statusInfo.id = statusList.getJSONObject(i).getLong("id");
                listData.add(statusInfo);
            }
            if (pageIndex == 0) {
                adapter = new Adapter(InfomationTabActivity.this);
                listView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            pageIndex = statusConfig.getInt("page");
            if (pageIndex == 1) {
                //if pageIndex == 1 means the page is the last page
                //so do not need show More FooterView any more
                ((RefreshListView)listView).removeFootView();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.infomation_tab_activity);
        setListView(R.id.infomation_tab_list);
        ((RefreshListView) listView).setOnRefreshListener(this);

        showLoadingEmptyView();
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.infomation_tab_list_item, null);
        }

        mItemAvatar = (SmartImageView) view.findViewById(R.id.item_avatar);
        mItemAvatar.setImageUrl(listData.get(position).avatar, R.drawable.icon, 0);

        mItemName = (TextView) view.findViewById(R.id.item_name);
        mItemName.setText(listData.get(position).name);
        mItemName.getPaint().setFakeBoldText(true);

        mItemDate = (TextView) view.findViewById(R.id.item_date);
        String dateString = listData.get(position).created;
        if ( dateString != null && !"".equals(dateString)) {
            try {
                Date date = mSinaWeiboDateFormat.parse(dateString);
                int second = (int)(System.currentTimeMillis() - date.getTime())/1000;
                if (second > 3600 && second <= 86400 ) {
                    mItemDate.setText(second/3600 + "小时前");
                } else if (second > 59 && second <= 3600) {
                    mItemDate.setText(second/60 + "分钟前");
                } else if (second <= 59) {
                    mItemDate.setText(second + "秒前");
                } else {
                    mItemDate.setText(mSimpleDateFormat.format(date));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            mItemDate.setText("");
        }

        mItemText = (TextView) view.findViewById(R.id.item_text);
        mItemText.setText(listData.get(position).text);

        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getString(R.string.info_options));
        menu.add(0, 1, 1, getString(R.string.info_options_share));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                //get item position
                ContextMenuInfo info = item.getMenuInfo();
                AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterContextMenuInfo) info;
                int position = contextMenuInfo.position - 1;

                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.info_options_share_title));
                intent.putExtra(Intent.EXTRA_TEXT, listData.get(position).text);
                startActivity(Intent.createChooser(intent, getString(R.string.setting_share_app_title)));
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public Object refreshing() {
        String result = null;
        if (AppApplication.mSdcardDataDir == null) {
            AppApplication.mSdcardDataDir = Environment.getExternalStorageDirectory().getPath() +  "/healthworld/config/";
        }
        File file = new File(AppApplication.mSdcardDataDir + "/" + StringUtils.replaceUrlWithPlus(AppApplicationApi.INFOMATION_URL));
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        try {
            DownloadUtils.download(AppApplicationApi.INFOMATION_URL, file, false, null);
            result = FileUtils.readTextFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void refreshed(Object obj) {
        if (obj != null) {
            listData.clear();
            pageIndex = 0;
            ((RefreshListView)listView).addFootView();
            showInfomationList((String)obj);
        }
    };

    @Override
    public void more() {
        if (pageIndex > 1) {
            moreInfomationList(pageIndex - 1);
        } else {
            Toast.makeText(this, "加载完毕", Toast.LENGTH_SHORT).show();
            ((RefreshListView)listView).removeFootView();
        }
    }


    @Override
    public void onBackPressed() {
        ((BaseApplication)getApplication()).exitApp(getParent());
    }

    public void checkNewVersion(){
        if (BaseApplication.mVersionCode < mLatestVersionCode && BaseApplication.mShowUpdate) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.check_new_version)
                .setMessage(mLatestVersionUpdate)
                .setPositiveButton(R.string.app_upgrade_confirm, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(InfomationTabActivity.this, AppUpgradeService.class);
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

    private static final int NO_AD_SPEND_PER_DAY = 25;
    //process the ad show
    //获取成功
    @Override
    public void getUpdatePoints(String currencyName, int pointTotal) {
        final LinearLayout container =(LinearLayout)findViewById(R.id.AdLinearLayout);
        if (pointTotal < NO_AD_SPEND_PER_DAY) {
            runOnUiThread(new Runnable () {
                public void run() {
                    try {
                        mAdView = new AdView(InfomationTabActivity.this,container);
                        mAdView.DisplayAd();
                    } catch (Exception e) {
                        container.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            });
        } else {

            long last_time = PreferencesUtils.getLongPreference(this,
                    AppApplicationApi.SHARE_CREDITS,
                    AppApplicationApi.SHARE_CREDITS_LAST_TIME,
                    0);
            if (System.currentTimeMillis() - last_time > 1000*60*60*24) {
                //spent 15 credits will keep no ad one day
                AppConnect.getInstance(InfomationTabActivity.this).spendPoints(NO_AD_SPEND_PER_DAY, InfomationTabActivity.this);
                PreferencesUtils.setLongPreference(this,
                        AppApplicationApi.SHARE_CREDITS,
                        AppApplicationApi.SHARE_CREDITS_LAST_TIME,
                        System.currentTimeMillis());
            }
        }
    }

    //获取失败
    @Override
    public void getUpdatePointsFailed(String error) {
    }

}
