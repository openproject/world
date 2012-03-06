package com.tianxia.app.floworld.appreciate;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.cache.ConfigCache;
import com.tianxia.app.floworld.model.AppreciateCategoryInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class AppreciateCategoryActivity extends AdapterActivity<AppreciateCategoryInfo> {

    private String mAppricateCategoryUrl = null;
    private String mAppreciateCategoryTitle = null;

    private TextView mAppreciateCategoryTitleView = null;

    private Button mAppBackButton = null;
    private TextView mAppLoadingTip = null;
    private ProgressBar mAppLoadingPbar = null;
    private ImageView mAppLoadingImage = null;

    private SmartImageView mItemImageView = null;
    private TextView mItemTextView = null;
    private TextView mItemCount = null;

    private Intent mAppreciateCategoryIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppricateCategoryUrl = getIntent().getStringExtra("url");
        mAppreciateCategoryTitle = getIntent().getStringExtra("title");

        mAppreciateCategoryTitleView = (TextView) findViewById(R.id.appreciate_category_title);
        if (mAppreciateCategoryTitle != null) {
            mAppreciateCategoryTitleView.setText(mAppreciateCategoryTitle);
        }

        mAppBackButton  = (Button) findViewById(R.id.app_back);
        mAppLoadingTip = (TextView) findViewById(R.id.app_loading_tip);
        mAppLoadingPbar = (ProgressBar) findViewById(R.id.app_loading_pbar);
        mAppLoadingImage = (ImageView) findViewById(R.id.app_loading_btn);

        mAppBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAppLoadingImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGridView();
            }
        });

        loadGridView();
    }

    private void loadGridView(){
        String cacheConfigString = ConfigCache.getUrlCache(mAppricateCategoryUrl);
        if (cacheConfigString != null) {
            setAppreciateCategoryList(cacheConfigString);
            mAppLoadingTip.setVisibility(View.GONE);
            mAppLoadingPbar.setVisibility(View.GONE);
            mAppLoadingImage.setVisibility(View.VISIBLE);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(mAppricateCategoryUrl, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                    listView.setAdapter(null);
                    mAppLoadingTip.setVisibility(View.VISIBLE);
                    mAppLoadingPbar.setVisibility(View.VISIBLE);
                    mAppLoadingImage.setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(String result) {
                    ConfigCache.setUrlCache(result, mAppricateCategoryUrl);
                    setAppreciateCategoryList(result);
                }

                @Override
                public void onFailure(Throwable arg0) {
                }

                @Override
                public void onFinish() {
                    mAppLoadingTip.setVisibility(View.GONE);
                    mAppLoadingPbar.setVisibility(View.GONE);
                    mAppLoadingImage.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void setAppreciateCategoryList(String jsonString){
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray jsonArray = json.getJSONArray("list");
            listData = new ArrayList<AppreciateCategoryInfo>();
            AppreciateCategoryInfo appreciateCategoryInfo = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                appreciateCategoryInfo = new AppreciateCategoryInfo();
                appreciateCategoryInfo.filename = jsonArray.getJSONObject(i).optString("filename");
                appreciateCategoryInfo.category = jsonArray.getJSONObject(i).optString("category");
                appreciateCategoryInfo.thumbnail = jsonArray.getJSONObject(i).optString("thumbnail");
                appreciateCategoryInfo.count = jsonArray.getJSONObject(i).optString("count");
                listData.add(appreciateCategoryInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new Adapter(AppreciateCategoryActivity.this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.appreciate_category_activity);
        setListView(R.id.appreciate_category_list);
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.appreciate_category_list_item, null);
        }

        mItemImageView = (SmartImageView) view.findViewById(R.id.item_image);
        if (listData != null && position < listData.size()){
            mItemImageView.setImageUrl(listData.get(position).thumbnail, R.drawable.app_download_fail, R.drawable.app_download_loading);
        }

        mItemTextView = (TextView) view.findViewById(R.id.item_category);
        mItemTextView.setText(listData.get(position).category + "(" + listData.get(position).count + ")");

        mItemCount = (TextView) view.findViewById(R.id.item_count);
        mItemCount.setText(listData.get(position).count);
        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mAppreciateCategoryIntent = new Intent(AppreciateCategoryActivity.this, AppreciateLatestActivity.class);
        mAppreciateCategoryIntent.putExtra("url", AppreciateApi.APPRECIATE_CATEGORY_BASE_URL + listData.get(position).filename + ".json");
        mAppreciateCategoryIntent.putExtra("title", listData.get(position).category + "(" + listData.get(position).count + ")");
        startActivity(mAppreciateCategoryIntent);
    }
}
