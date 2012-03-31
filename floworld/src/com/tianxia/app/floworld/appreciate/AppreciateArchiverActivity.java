package com.tianxia.app.floworld.appreciate;

import java.io.IOException;
import java.util.ArrayList;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tianxia.app.floworld.AppApplication;
import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.cache.ConfigCache;
import com.tianxia.app.floworld.model.AppreciateArchiverInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;

public class AppreciateArchiverActivity extends AdapterActivity<AppreciateArchiverInfo> {

    private String mAppreciateArchivertUrl = null;

    private TextView mAppreciateArchiverTitle = null;

    private Button mAppBackButton = null;
    private TextView mAppLoadingTip = null;
    private ProgressBar mAppLoadingPbar = null;
    private ImageView mAppLoadingImage = null;

    private ImageView mItemImageView = null;
    private TextView mItemIndex = null;
    private TextView mItemDate = null;
    private TextView mItemTitle = null;

    private Intent mAppreciateArchiverIntent = null;

    private AssetManager mAssetManager = null;
    private String[] mKindImages = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppreciateArchivertUrl = getIntent().getStringExtra("url");

        mAppreciateArchiverTitle = (TextView) findViewById(R.id.appreciate_archiver_title);

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
                loadListView();
            }
        });
        loadListView();

        mAssetManager = getResources().getAssets();
        try {
            mKindImages = mAssetManager.list("kinds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListView(){
        String cacheConfigString = ConfigCache.getUrlCache(mAppreciateArchivertUrl);
        if (cacheConfigString != null) {
            setAppreciateArchiverList(cacheConfigString);
            mAppLoadingTip.setVisibility(View.GONE);
            mAppLoadingPbar.setVisibility(View.GONE);
            mAppLoadingImage.setVisibility(View.VISIBLE);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(mAppreciateArchivertUrl, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                    listView.setAdapter(null);
                    mAppLoadingTip.setVisibility(View.VISIBLE);
                    mAppLoadingPbar.setVisibility(View.VISIBLE);
                    mAppLoadingImage.setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(String result) {
                    ConfigCache.setUrlCache(result, mAppreciateArchivertUrl);
                    setAppreciateArchiverList(result);
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

    private void setAppreciateArchiverList(String jsonString){
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray jsonArray = json.getJSONArray("archiver");
            listData = new ArrayList<AppreciateArchiverInfo>();
            AppreciateArchiverInfo appreciateArchiverInfo = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                appreciateArchiverInfo = new AppreciateArchiverInfo();
                appreciateArchiverInfo.index =  jsonArray.getJSONObject(i).optString("index");
                appreciateArchiverInfo.title = jsonArray.getJSONObject(i).optString("title");
                appreciateArchiverInfo.date = jsonArray.getJSONObject(i).optString("date");
                appreciateArchiverInfo.json = jsonArray.getJSONObject(i).optString("json");
                listData.add(appreciateArchiverInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new Adapter(AppreciateArchiverActivity.this);
        listView.setAdapter(adapter);
        updateAppreciateTitle(listData.size());
    }

    private void updateAppreciateTitle(int size){
        if (size > 0) {
            mAppreciateArchiverTitle.setText(getString(R.string.appreciate_archiver_title) + "(共" + size + "期)");
        }
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.appreciate_archiver_activity);
        setListView(R.id.appreciate_archiver_list);
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.appreciate_archiver_list_item, null);
        }

        mItemImageView = (ImageView) view.findViewById(R.id.item_image);
        try {
            mItemImageView.setImageBitmap(BitmapFactory.decodeStream(mAssetManager.open("kinds/" + mKindImages[(listData.size() - position - 1) % mKindImages.length])));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mItemIndex = (TextView) view.findViewById(R.id.item_index);
        mItemIndex.setText(listData.get(position).index);

        mItemDate = (TextView) view.findViewById(R.id.item_date);
        mItemDate.setText("(" + listData.get(position).date + ")");

        mItemTitle = (TextView) view.findViewById(R.id.item_title);
        mItemTitle.setText(listData.get(position).title);

        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mAppreciateArchiverIntent = new Intent(AppreciateArchiverActivity.this, AppreciateLatestActivity.class);
        mAppreciateArchiverIntent.putExtra("url", AppApplication.mDomain + listData.get(position).json);
        mAppreciateArchiverIntent.putExtra("title", listData.get(position).index);
        startActivity(mAppreciateArchiverIntent);
    }
}
