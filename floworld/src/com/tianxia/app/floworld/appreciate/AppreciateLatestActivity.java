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
import com.tianxia.app.floworld.model.AppreciateLatestInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class AppreciateLatestActivity extends AdapterActivity<AppreciateLatestInfo> {

    private String mAppricateLatestUrl = null;
    private String mAppreciateLatestTitle = null;

    private TextView mAppreciateLatestTitleView = null;

    private Button mAppBackButton = null;
    private TextView mAppLoadingTip = null;
    private ProgressBar mAppLoadingPbar = null;
    private ImageView mAppLoadingImage = null;

    private SmartImageView mItemImageView = null;
    private TextView mItemTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppricateLatestUrl = getIntent().getStringExtra("url");
        mAppreciateLatestTitle = getIntent().getStringExtra("title");

        mAppreciateLatestTitleView = (TextView) findViewById(R.id.appreciate_latest_title);
        if (mAppreciateLatestTitle != null) {
            mAppreciateLatestTitleView.setText(mAppreciateLatestTitle);
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
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(mAppricateLatestUrl, new AsyncHttpResponseHandler(){

            @Override
            public void onStart() {
                listView.setAdapter(null);
                mAppLoadingTip.setVisibility(View.VISIBLE);
                mAppLoadingPbar.setVisibility(View.VISIBLE);
                mAppLoadingImage.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(String result) {
                setAppreciateLatestList(result);
                adapter = new Adapter(AppreciateLatestActivity.this);
                listView.setAdapter(adapter);
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

        mItemImageView = (SmartImageView) view.findViewById(R.id.item_image);
        if (listData != null && position < listData.size()){
            mItemImageView.setImageUrl(listData.get(position).thumbnail, R.drawable.app_download_fail, R.drawable.app_download_loading);
        }

        mItemTextView = (TextView) view.findViewById(R.id.item_title);
        mItemTextView.setText(listData.get(position).title);
        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(AppreciateLatestActivity.this, AppreciateLatestDetailsActivity.class);
        intent.putExtra("url", listData.get(position).origin);
        intent.putExtra("title", listData.get(position).title);
        intent.putExtra("thumbnail", listData.get(position).thumbnail);
        startActivity(intent);
    }

}
