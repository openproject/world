package com.tianxia.app.healthworld.category;

import java.io.File;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.AppApplicationApi;
import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.model.StatusInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.main.MainTabFrame;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld.utils.DownloadUtils;
import com.tianxia.lib.baseworld.utils.FileUtils;
import com.tianxia.lib.baseworld.utils.StringUtils;
import com.tianxia.lib.baseworld.widget.RefreshListView;
import com.tianxia.lib.baseworld.widget.RefreshListView.RefreshListener;
import com.tianxia.widget.image.SmartImageView;
import com.waps.AdView;

public class CategoryListActivity extends AdapterActivity<StatusInfo> implements RefreshListener{

    private TextView mCategoryListTitleTextView = null;

    private LinearLayout mAppLoadingLinearLayout = null;
    private ProgressBar mAppLoadingProgressBar = null;
    private TextView mAppLoadingTextView = null;

    private SmartImageView mItemAvatar;
    private TextView mItemName;
    private TextView mItemDate;
    private TextView mItemText;

    private SimpleDateFormat mSinaWeiboDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new DateFormatSymbols(Locale.US));
    private SimpleDateFormat mSimpleDateFormat;

    private int pageIndex = 0;

    private String categoryString;
    private String categoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryString = getIntent().getStringExtra("category");
        categoryTitle = getIntent().getStringExtra("title");
        if (categoryString == null) {
            categoryString = "else";
        }

        mCategoryListTitleTextView = (TextView) findViewById(R.id.category_list_title);
        mCategoryListTitleTextView.setText(categoryTitle);

        mAppLoadingLinearLayout = (LinearLayout) findViewById(R.id.app_loading);
        mAppLoadingProgressBar = (ProgressBar) findViewById(R.id.app_loading_pbar);
        mAppLoadingTextView = (TextView) findViewById(R.id.app_loading_tip);
        setInfomationList();

        mAppLoadingLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (MainTabFrame.mainTabContainerHeight != 0) {
                    mAppLoadingLinearLayout.getLayoutParams().height = MainTabFrame.mainTabContainerHeight;
                }
            }
        });

        mSimpleDateFormat = new SimpleDateFormat("MM-dd hh:mm");

        LinearLayout container =(LinearLayout)findViewById(R.id.AdLinearLayout);
        new AdView(this,container).DisplayAd();
    }

    private void setInfomationList() {
        String cacheConfigString = ConfigCache.getUrlCache(AppApplicationApi.CATEGORY_URL + categoryString + "/latest.json");
        if (cacheConfigString != null) {
            mAppLoadingLinearLayout.setVisibility(View.GONE);
            showInfomationList(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(AppApplicationApi.CATEGORY_URL + categoryString + "/latest.json", new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                    mAppLoadingLinearLayout.setVisibility(View.VISIBLE);
                    mAppLoadingProgressBar.setVisibility(View.VISIBLE);
                    mAppLoadingTextView.setText(R.string.app_loading);
                }

                @Override
                public void onSuccess(String result){
                    ConfigCache.setUrlCache(result, AppApplicationApi.CATEGORY_URL + categoryString + "/latest.json");
                    mAppLoadingLinearLayout.setVisibility(View.GONE);
                    showInfomationList(result);
                }

                @Override
                public void onFailure(Throwable arg0) {
                    mAppLoadingProgressBar.setVisibility(View.INVISIBLE);
                    mAppLoadingTextView.setText(R.string.app_loading_fail);
                    listView.setAdapter(null);
                    listView.setVisibility(View.INVISIBLE);
                }

            });
        }
    }

    private void moreInfomationList(int pageIndex) {
        String cacheConfigString = ConfigCache.getUrlCache(AppApplicationApi.CATEGORY_URL + categoryString + "/pages/" + pageIndex + ".json");
        if (cacheConfigString != null) {
            showInfomationList(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(AppApplicationApi.CATEGORY_URL + categoryString + "/pages/" + pageIndex + ".json", new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String result){
                    showInfomationList(result);
                    ((RefreshListView)listView).finishFootView();
                }

                @Override
                public void onFailure(Throwable arg0) {
                    ((RefreshListView)listView).finishFootView();
                    Toast.makeText(CategoryListActivity.this, R.string.app_loading_fail, Toast.LENGTH_SHORT).show();
                    arg0.printStackTrace();
                }

            });
        }
    }

    private void showInfomationList(String result) {
        try {
            JSONObject statusConfig = new JSONObject(result);

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
                adapter = new Adapter(CategoryListActivity.this);
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
        setContentView(R.layout.category_list_activity);
        setListView(R.id.infomation_tab_list);
        ((RefreshListView) listView).setOnRefreshListener(this);
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
                if (second > 59 && second < 1800) {
                    mItemDate.setText(second/60 + "分钟前");
                } else if (second < 59) {
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
    public Object refreshing() {
        String result = null;
        if (AppApplication.mSdcardDataDir == null) {
            AppApplication.mSdcardDataDir = Environment.getExternalStorageDirectory().getPath() +  "/healthworld/config/";
        }
        File file = new File(AppApplication.mSdcardDataDir + "/" + StringUtils.replaceUrlWithPlus(AppApplicationApi.CATEGORY_URL + categoryString + "/latest.json"));
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        try {
            DownloadUtils.download(AppApplicationApi.CATEGORY_URL + categoryString + "/latest.json", file, false, null);
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

}
