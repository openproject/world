package com.tianxia.app.healthworld.infomation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tianxia.app.healthworld.AppApplicationApi;
import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.model.StatusInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.main.MainTabFrame;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class InfomationTabActivity extends AdapterActivity<StatusInfo>{

    private LinearLayout mAppLoadingLinearLayout = null;
    private ProgressBar mAppLoadingProgressBar = null;
    private TextView mAppLoadingTextView = null;

    private SmartImageView mItemAvatar;
    private TextView mItemName;
    private TextView mItemText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void setInfomationList() {
        String cacheConfigString = ConfigCache.getUrlCache(AppApplicationApi.INFOMATION_URL);
        if (cacheConfigString != null) {
            mAppLoadingLinearLayout.setVisibility(View.GONE);
            showInfomationList(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(AppApplicationApi.INFOMATION_URL, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                    mAppLoadingLinearLayout.setVisibility(View.VISIBLE);
                    mAppLoadingProgressBar.setVisibility(View.VISIBLE);
                    mAppLoadingTextView.setText(R.string.app_loading);
                }

                @Override
                public void onSuccess(String result){
                    ConfigCache.setUrlCache(result, AppApplicationApi.INFOMATION_URL);
                    mAppLoadingLinearLayout.setVisibility(View.GONE);
                    showInfomationList(result);
                }

                @Override
                public void onFailure(Throwable arg0) {
                    mAppLoadingProgressBar.setVisibility(View.INVISIBLE);
                    mAppLoadingTextView.setText(R.string.app_loading_fail);
                    listView.setAdapter(null);
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
                statusInfo.avatar = statusList.getJSONObject(i).getString("avatar");
                statusInfo.name = statusList.getJSONObject(i).getString("name");
                statusInfo.author = statusList.getJSONObject(i).getString("author");
                statusInfo.text = statusList.getJSONObject(i).getString("text");
                statusInfo.id = statusList.getJSONObject(i).getLong("id");
                listData.add(statusInfo);
            }
            adapter = new Adapter(InfomationTabActivity.this);
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.infomation_tab_activity);
        setListView(R.id.infomation_tab_list);
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.infomation_tab_list_item, null);
        }

        mItemAvatar = (SmartImageView) view.findViewById(R.id.item_avatar);
        mItemAvatar.setImageUrl(listData.get(position).avatar, R.drawable.icon, R.drawable.icon);

        mItemName = (TextView) view.findViewById(R.id.item_name);
        mItemName.setText(listData.get(position).name);
        mItemName.getPaint().setFakeBoldText(true);

        mItemText = (TextView) view.findViewById(R.id.item_text);
        mItemText.setText(listData.get(position).text);

        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }
}
