package com.tianxia.app.floworld.identification;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.appreciate.AppreciateApi;
import com.tianxia.app.floworld.appreciate.AppreciateLatestActivity;
import com.tianxia.app.floworld.cache.ConfigCache;
import com.tianxia.app.floworld.model.AppreciateCategoryInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.main.MainTabFrame;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class IdentificationTabActivity extends AdapterActivity<AppreciateCategoryInfo> {

    private LinearLayout appLoadingLinearLayout;

    private SmartImageView mItemImageView = null;
    private TextView mItemTextView = null;
    private TextView mItemCount = null;

    private Intent mIdentificationIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appLoadingLinearLayout = (LinearLayout) findViewById(R.id.app_loading);
        appLoadingLinearLayout.getLayoutParams().height = MainTabFrame.mainTabContainerHeight;

        loadGridView();
    }

    private void loadGridView(){
        String cacheConfigString = ConfigCache.getUrlCache(IdentificationApi.IDENTIFICATION_CONFIG_URL);
        if (cacheConfigString != null) {
            setAppreciateCategoryList(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(IdentificationApi.IDENTIFICATION_CONFIG_URL, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                    listView.setAdapter(null);
                }

                @Override
                public void onSuccess(String result) {
                    ConfigCache.setUrlCache(result, IdentificationApi.IDENTIFICATION_CONFIG_URL);
                    setAppreciateCategoryList(result);
                }

                @Override
                public void onFailure(Throwable arg0) {
                    appLoadingLinearLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setAppreciateCategoryList(String jsonString){
        appLoadingLinearLayout.setVisibility(View.GONE);
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray jsonArray = json.getJSONArray("list");
            listData = new ArrayList<AppreciateCategoryInfo>();
            AppreciateCategoryInfo appreciateCategoryInfo = null;
            for(int i = 0; i < jsonArray.length(); i++){
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
        adapter = new Adapter(IdentificationTabActivity.this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.identification_tab_activity);
        setListView(R.id.identification_list);
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.identification_tab_list_item, null);
        }

        mItemImageView = (SmartImageView) view.findViewById(R.id.item_image);
        if (listData != null && position < listData.size()){
            mItemImageView.setImageUrl(listData.get(position).thumbnail, R.drawable.app_download_fail, R.drawable.app_download_loading);
        }

        mItemTextView = (TextView) view.findViewById(R.id.item_category);
        mItemTextView.setText(listData.get(position).category);

        mItemCount = (TextView) view.findViewById(R.id.item_count);
        mItemCount.setText(listData.get(position).count);
        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mIdentificationIntent = new Intent(IdentificationTabActivity.this, AppreciateLatestActivity.class);
        mIdentificationIntent.putExtra("url", AppreciateApi.APPRECIATE_CATEGORY_BASE_URL + listData.get(position).filename + ".json");
        mIdentificationIntent.putExtra("title", listData.get(position).category);
        startActivity(mIdentificationIntent);
    }
}
