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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.model.AppreciateLatestInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.widget.image.SmartImageView;

public class AppreciateLatestActivity extends AdapterActivity<AppreciateLatestInfo> {

    private String appricateLatestUrl = null;
    private String appreciateLatestTitle = null;

    private TextView appreciateLatestTitleView = null;

    private Button appBackButton = null;
    private TextView appLoadingTip = null;
    private ProgressBar appLoadingPbar = null;
    private ImageView appLoadingImage = null;

    private SmartImageView appreciateLatestImageView = null;
    private TextView appreciateLatestTextView = null;
    private Intent appreciateDetailsIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appricateLatestUrl = getIntent().getStringExtra("url");
        appreciateLatestTitle = getIntent().getStringExtra("title");

        appreciateLatestTitleView = (TextView) findViewById(R.id.appreciate_latest_title);
        if (appreciateLatestTitle != null) {
            appreciateLatestTitleView.setText(appreciateLatestTitle);
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
        client.get(appricateLatestUrl, new AsyncHttpResponseHandler(){

            @Override
            public void onStart() {
                listView.setAdapter(null);
                appLoadingTip.setVisibility(View.VISIBLE);
                appLoadingPbar.setVisibility(View.VISIBLE);
                appLoadingImage.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(String result) {
                setAppreciateLatestList(result);
                adapter = new Adapter(AppreciateLatestActivity.this);
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Throwable arg0) {
                super.onFailure(arg0);
            }

            @Override
            public void onFinish() {
                appLoadingTip.setVisibility(View.GONE);
                appLoadingPbar.setVisibility(View.GONE);
                appLoadingImage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setAppreciateLatestList(String jsonString){
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray jsonArray = json.getJSONArray("list");
            listData = new ArrayList<AppreciateLatestInfo>();
            AppreciateLatestInfo appreciateLatestInfo = null;
            for(int i = 0; i < jsonArray.length(); i++){
                appreciateLatestInfo = new AppreciateLatestInfo();
                appreciateLatestInfo.title = jsonArray.getJSONObject(i).optString("title");
                appreciateLatestInfo.origin = jsonArray.getJSONObject(i).optString("origin");
                appreciateLatestInfo.thumbnail = jsonArray.getJSONObject(i).optString("thumbnail");
                appreciateLatestInfo.tag = jsonArray.getJSONObject(i).optString("tag");
                appreciateLatestInfo.category = jsonArray.getJSONObject(i).optString("category");
                listData.add(appreciateLatestInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.appreciate_latest_activity);
        setListView(R.id.appreciate_latest_list);
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.appreciate_latest_list_item, null);
        }

        appreciateLatestImageView = (SmartImageView) view.findViewById(R.id.item_image);
        if (listData != null && position < listData.size()){
            appreciateLatestImageView.setImageUrl(listData.get(position).thumbnail, R.drawable.app_download_fail, R.drawable.app_download_loading);
        }

        appreciateLatestTextView = (TextView) view.findViewById(R.id.item_title);
        appreciateLatestTextView.setText(listData.get(position).title);
        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        appreciateDetailsIntent = new Intent(AppreciateLatestActivity.this, AppreciateLatestDetailsActivity.class);
        appreciateDetailsIntent.putExtra("url", listData.get(position).origin);
        startActivity(appreciateDetailsIntent);
    }

}
