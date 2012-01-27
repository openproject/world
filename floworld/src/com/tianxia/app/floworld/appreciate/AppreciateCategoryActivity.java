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
import com.tianxia.app.floworld.model.AppreciateCategoryInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class AppreciateCategoryActivity extends AdapterActivity<AppreciateCategoryInfo> {

    private String appricateCategoryUrl = null;
    private String appreciateCategoryTitle = null;

    private TextView appreciateCategoryTitleView = null;

    private Button appBackButton = null;
    private TextView appLoadingTip = null;
    private ProgressBar appLoadingPbar = null;
    private ImageView appLoadingImage = null;

    private SmartImageView itemImageView = null;
    private TextView itemTextView = null;
    private TextView itemCount = null;

    private Intent appreciateCategoryIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appricateCategoryUrl = getIntent().getStringExtra("url");
        appreciateCategoryTitle = getIntent().getStringExtra("title");

        appreciateCategoryTitleView = (TextView) findViewById(R.id.appreciate_category_title);
        if (appreciateCategoryTitle != null) {
            appreciateCategoryTitleView.setText(appreciateCategoryTitle);
        }

        appBackButton  = (Button) findViewById(R.id.app_back);
        appLoadingTip = (TextView) findViewById(R.id.app_loading_tip);
        appLoadingPbar = (ProgressBar) findViewById(R.id.app_loading_pbar);
        appLoadingImage = (ImageView) findViewById(R.id.app_loading_btn);

        appBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
        appLoadingImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGridView();
            }
        });

        loadGridView();
    }

    private void loadGridView(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(appricateCategoryUrl, new AsyncHttpResponseHandler(){

            @Override
            public void onStart() {
                listView.setAdapter(null);
                appLoadingTip.setVisibility(View.VISIBLE);
                appLoadingPbar.setVisibility(View.VISIBLE);
                appLoadingImage.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(String result) {
                setAppreciateCategoryList(result);
                adapter = new Adapter(AppreciateCategoryActivity.this);
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Throwable arg0) {
            }

            @Override
            public void onFinish() {
                appLoadingTip.setVisibility(View.GONE);
                appLoadingPbar.setVisibility(View.GONE);
                appLoadingImage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setAppreciateCategoryList(String jsonString){
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

        itemImageView = (SmartImageView) view.findViewById(R.id.item_image);
        if (listData != null && position < listData.size()){
            itemImageView.setImageUrl(listData.get(position).thumbnail, R.drawable.app_download_fail, R.drawable.app_download_loading);
        }

        itemTextView = (TextView) view.findViewById(R.id.item_category);
        itemTextView.setText(listData.get(position).category);

        itemCount = (TextView) view.findViewById(R.id.item_count);
        itemCount.setText(listData.get(position).count);
        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        appreciateCategoryIntent = new Intent(AppreciateCategoryActivity.this, AppreciateLatestActivity.class);
        appreciateCategoryIntent.putExtra("url", AppreciateApi.APPRECIATE_CATEGORY_BASE_URL + listData.get(position).filename + ".json");
        appreciateCategoryIntent.putExtra("title", listData.get(position).category);
        startActivity(appreciateCategoryIntent);
    }
}
