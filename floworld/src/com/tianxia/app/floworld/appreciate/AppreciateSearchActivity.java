package com.tianxia.app.floworld.appreciate;

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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.cache.ConfigCache;
import com.tianxia.app.floworld.discuss.DiscussApi;
import com.tianxia.app.floworld.discuss.DiscussDetailsActivity;
import com.tianxia.app.floworld.model.AppreciateSearchInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class AppreciateSearchActivity extends AdapterActivity<AppreciateSearchInfo> {

    private static final int SEARCH_TYPE_DISCUSS = 0;
    private static final int SEARCH_TYPE_PIC = 1;

    private String mAppricateCategoryUrl = null;

    private EditText mAppreciateSearchEditText;
    private Button mAppreciateSearchBtn;

    private SmartImageView mItemImageView;
    private TextView mItemTitleTextView;
    private TextView mItemCategoryTextView;
    private Intent mItemIntent;

    private Button mAppBackButton;
    private TextView mAppLoadingTip;

    private AssetManager mAssetManager = null;
    private String[] mKindImages = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppricateCategoryUrl = getIntent().getStringExtra("url");

        mAppreciateSearchEditText = (EditText) findViewById(R.id.appreciate_search_keyword);
        mAppreciateSearchBtn = (Button) findViewById(R.id.appreciate_search_btn);
        mAppreciateSearchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = mAppreciateSearchEditText.getText().toString();
                if(keyword != null) {
                    keyword = keyword.trim();
                }
                if (keyword == null || "".equals(keyword)) {
                    Toast.makeText(AppreciateSearchActivity.this, R.string.appreciate_search_empty, Toast.LENGTH_SHORT).show();
                    mAppLoadingTip.setText(R.string.appreciate_search_empty);
                    return;
                }
                if (getString(R.string.appreciate_search_wide_word).indexOf(keyword + "|") > -1) {
                    Toast.makeText(AppreciateSearchActivity.this, R.string.appreciate_search_too_wide, Toast.LENGTH_SHORT).show();
                    mAppLoadingTip.setText(R.string.appreciate_search_too_wide);
                    return;
                }
                listData.clear();
                setListData(keyword);
            }
        });

        mAssetManager = getResources().getAssets();
        try {
            mKindImages = mAssetManager.list("kinds");
        } catch (IOException e) {
            e.printStackTrace();
        }

        mAppBackButton = (Button) findViewById(R.id.app_back);
        mAppLoadingTip = (TextView) findViewById(R.id.app_loading_tip);
        mAppBackButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setListData(final String keyword){
        listView.setAdapter(null);
        mAppLoadingTip.setVisibility(View.VISIBLE);
        mAppLoadingTip.setText(R.string.app_loading);

        String discussConfigString = ConfigCache.getUrlCache(DiscussApi.DISCUSS_CONFIG_URL);
        if (discussConfigString != null) {
            setDiscussConfig(discussConfigString, keyword);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(DiscussApi.DISCUSS_CONFIG_URL, new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String result){
                    ConfigCache.setUrlCache(result, DiscussApi.DISCUSS_CONFIG_URL);
                    setDiscussConfig(result, keyword);
                }

                @Override
                public void onFailure(Throwable arg0) {
                    mAppLoadingTip.setText(R.string.app_loading_fail);
                }

            });
        }

        String categoryConfigString = ConfigCache.getUrlCache(mAppricateCategoryUrl);
        if (categoryConfigString != null) {
            setAppreciateCategoryList(categoryConfigString, keyword);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(mAppricateCategoryUrl, new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String result) {
                    ConfigCache.setUrlCache(result, mAppricateCategoryUrl);
                    setAppreciateCategoryList(result, keyword);
                }

                @Override
                public void onFailure(Throwable arg0) {
                    mAppLoadingTip.setText(R.string.app_loading_fail);
                }

            });
        }
    }

    private void setDiscussConfig(String result, String keyword){
        try {
            JSONObject discussConfig = new JSONObject(result);

            String baseUrl = discussConfig.getString("base-url");
            JSONArray searchList = discussConfig.getJSONArray("list");
            AppreciateSearchInfo searchInfo = null;
            String searchTitle = null;
            for (int i = 0; i < searchList.length(); i++) {
                searchTitle = searchList.getJSONObject(i).optString("title");
                if (searchTitle != null && searchTitle.indexOf(keyword) > -1) {
                    searchInfo = new AppreciateSearchInfo();
                    searchInfo.type = SEARCH_TYPE_DISCUSS;
                    searchInfo.title = searchList.getJSONObject(i).getString("title");
                    searchInfo.category = searchList.getJSONObject(i).getString("category");
                    searchInfo.path = baseUrl + searchList.getJSONObject(i).getString("path");
                    listData.add(searchInfo);
                }
            }

            if (listData != null && listData.size() > 0) {
                mAppLoadingTip.setVisibility(View.GONE);
            } else {
                mAppLoadingTip.setText(R.string.app_loading_none);
            }

            adapter = new Adapter(AppreciateSearchActivity.this);
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAppreciateCategoryList(String result, String keyword){
        try {
            JSONObject json = new JSONObject(result);
            JSONArray searchList = json.getJSONArray("list");
            AppreciateSearchInfo searchInfo = null;
            String searchTitle = null;
            for (int i = 0; i < searchList.length(); i++) {
                searchTitle = searchList.getJSONObject(i).optString("category");
                if (searchTitle != null && searchTitle.indexOf(keyword) > -1) {
                    searchInfo = new AppreciateSearchInfo();
                    searchInfo.type = SEARCH_TYPE_PIC;
                    searchInfo.category = searchList.getJSONObject(i).getString("category");
                    searchInfo.thumbnail = searchList.getJSONObject(i).optString("thumbnail");
                    searchInfo.filename = searchList.getJSONObject(i).optString("filename");
                    searchInfo.count = searchList.getJSONObject(i).optString("count");
                    listData.add(searchInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (listData != null && listData.size() > 0) {
            mAppLoadingTip.setVisibility(View.GONE);
        } else {
            mAppLoadingTip.setText(R.string.app_loading_none);
        }

        adapter = new Adapter(AppreciateSearchActivity.this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.appreciate_search_activity);
        setListView(R.id.appreciate_search_list);
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.appreciate_search_list_item, null);
        }

        mItemImageView = (SmartImageView) view.findViewById(R.id.item_image);
        mItemCategoryTextView = (TextView) view.findViewById(R.id.item_category);
        mItemTitleTextView = (TextView) view.findViewById(R.id.item_title);

        if (listData.get(position).type == SEARCH_TYPE_DISCUSS) {
            mItemTitleTextView.setText(listData.get(position).title);
            mItemCategoryTextView.setText("分类:" + listData.get(position).category);
            try {
                mItemImageView.setImageBitmap(BitmapFactory.decodeStream(mAssetManager.open("kinds/" + mKindImages[(listData.size() - position - 1) % mKindImages.length])));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (listData.get(position).type == SEARCH_TYPE_PIC) {
            mItemImageView.setScaleType(ScaleType.FIT_XY);
            mItemImageView.setImageUrl(listData.get(position).thumbnail, R.drawable.app_download_fail, R.drawable.app_download_loading);
            mItemTitleTextView.setText(listData.get(position).category);
            mItemCategoryTextView.setText("分类:图片 数量:" + listData.get(position).count + "张");
        }

        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (listData.get(position).type == SEARCH_TYPE_DISCUSS) {
            mItemIntent = new Intent(this, DiscussDetailsActivity.class);
            mItemIntent.putExtra("url", listData.get(position).path);
            mItemIntent.putExtra("title", listData.get(position).title);
            mItemIntent.putExtra("category", listData.get(position).category);
            startActivity(mItemIntent);
        } else if (listData.get(position).type == SEARCH_TYPE_PIC) {
            mItemIntent = new Intent(this, AppreciateLatestActivity.class);
            mItemIntent.putExtra("url", AppreciateApi.APPRECIATE_CATEGORY_BASE_URL + listData.get(position).filename + ".json");
            mItemIntent.putExtra("title", listData.get(position).category + "(" + listData.get(position).count + ")");
            startActivity(mItemIntent);
        }
    }
}
