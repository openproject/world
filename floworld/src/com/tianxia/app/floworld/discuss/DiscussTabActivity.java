package com.tianxia.app.floworld.discuss;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tianxia.app.floworld.AppApplication;
import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.cache.ConfigCache;
import com.tianxia.app.floworld.model.DiscussInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.main.MainTabFrame;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;

public class DiscussTabActivity extends AdapterActivity<DiscussInfo> {

    private LinearLayout mAppLoadingLinearLayout;
    private TextView mAppLoadingTip = null;
    private ProgressBar mAppLoadingPbar = null;

    private ImageView mItemImageView;
    private TextView mItemTitleTextView;
    private TextView mItemCategoryTextView;

    private AssetManager mAssetManager = null;
    private String[] mKindImages = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppLoadingLinearLayout = (LinearLayout) findViewById(R.id.app_loading);
        mAppLoadingLinearLayout.getLayoutParams().height = MainTabFrame.mainTabContainerHeight;
        mAppLoadingTip = (TextView) findViewById(R.id.app_loading_tip);
        mAppLoadingPbar = (ProgressBar) findViewById(R.id.app_loading_pbar);

        mAssetManager = getResources().getAssets();
        try {
            mKindImages = mAssetManager.list("kinds");
        } catch (IOException e) {
            e.printStackTrace();
        }
        setListData();
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.discuss_tab_activity);
        setListView(R.id.discuss_tab_list);
    }

    private void setListData(){
        String cacheConfigString = ConfigCache.getUrlCache(DiscussApi.DISCUSS_CONFIG_URL);
        if (cacheConfigString != null) {
            setDiscussConfig(cacheConfigString);
            mAppLoadingLinearLayout.setVisibility(View.GONE);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(DiscussApi.DISCUSS_CONFIG_URL, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                    mAppLoadingLinearLayout.setVisibility(View.VISIBLE);
                    mAppLoadingTip.setVisibility(View.VISIBLE);
                    mAppLoadingPbar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(String result){
                    mAppLoadingLinearLayout.setVisibility(View.GONE);
                    ConfigCache.setUrlCache(result, DiscussApi.DISCUSS_CONFIG_URL);
                    setDiscussConfig(result);
                }

                @Override
                public void onFailure(Throwable arg0) {
                    mAppLoadingPbar.setVisibility(View.GONE);
                    mAppLoadingTip.setText(R.string.app_loading_fail);
                    listView.setAdapter(null);
                }

            });
        }
    }

    private void setDiscussConfig(String result){
        try {
            JSONObject discussConfig = new JSONObject(result);

            String baseUrl = AppApplication.mDomain + discussConfig.getString("base-url");
            JSONArray discussList = discussConfig.getJSONArray("list");
            DiscussInfo discussInfo = null;
            for (int i = 0; i < discussList.length(); i++) {
                discussInfo = new DiscussInfo();
                discussInfo.id = discussList.getJSONObject(i).getInt("id");
                discussInfo.title = discussList.getJSONObject(i).getString("title");
                discussInfo.category = discussList.getJSONObject(i).getString("category");
                discussInfo.date = discussList.getJSONObject(i).getString("date");
                discussInfo.path = baseUrl + discussList.getJSONObject(i).getString("path");
                listData.add(discussInfo);
            }

            adapter = new Adapter(DiscussTabActivity.this);
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view==null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.discuss_tab_list_item, null);
        }

        mItemImageView = (ImageView) view.findViewById(R.id.item_image);
        try {
            mItemImageView.setImageBitmap(BitmapFactory.decodeStream(mAssetManager.open("kinds/" + mKindImages[(listData.size() - position - 1) % mKindImages.length])));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mItemTitleTextView = (TextView) view.findViewById(R.id.item_title);
        mItemTitleTextView.setText(listData.get(position).title);

        mItemCategoryTextView = (TextView) view.findViewById(R.id.item_category);
        mItemCategoryTextView.setText("分类:" + listData.get(position).category + " 日期:" + listData.get(position).date);

        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, DiscussDetailsActivity.class);
        intent.putExtra("thumbnail", "kinds/" + mKindImages[(listData.size() - position - 1) % mKindImages.length]);
        intent.putExtra("url", listData.get(position).path);
        intent.putExtra("title", listData.get(position).title);
        intent.putExtra("category", listData.get(position).category);
        intent.putExtra("date", listData.get(position).date);
        startActivity(intent);
    }
}
