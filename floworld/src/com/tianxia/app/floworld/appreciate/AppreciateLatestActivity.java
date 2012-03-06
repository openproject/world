package com.tianxia.app.floworld.appreciate;

import java.util.ArrayList;

import net.youmi.android.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.cache.ConfigCache;
import com.tianxia.app.floworld.cache.ImagePool;
import com.tianxia.app.floworld.model.AppreciateAdCompanyInfo;
import com.tianxia.app.floworld.model.AppreciateLatestInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class AppreciateLatestActivity extends AdapterActivity<AppreciateLatestInfo> {

    private String mUrl = null;
    private String mAppreciateLatestTitle = null;
    private int mAppreciateImageScaleType = 0;

    private TextView mAppreciateLatestTitleView = null;

    private Button mAppBackButton = null;
    private TextView mAppLoadingTip = null;
    private ProgressBar mAppLoadingPbar = null;
    private ImageView mAppLoadingImage = null;

    private SmartImageView mItemImageView = null;
    private TextView mItemTextView = null;

    private LinearLayout mAdContainer = null;
    private AppreciateAdCompanyInfo mAdCompanyInfo;
    private TextView mAdCompanyName = null;
    private TextView mAdCompanyContact = null;
    private TextView mAdCompanyAddress = null;
    private TextView mAdCompanyTel = null;
    private TextView mAdCompanyPhone = null;
    private TextView mAdCompanyBusiness = null;

    public AdView mAdView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUrl = getIntent().getStringExtra("url");
        mAppreciateLatestTitle = getIntent().getStringExtra("title");

        mAppreciateLatestTitleView = (TextView) findViewById(R.id.appreciate_latest_title);
        if (mAppreciateLatestTitle != null) {
            mAppreciateLatestTitleView.setText(mAppreciateLatestTitle);
        }
        mAdContainer = (LinearLayout) findViewById(R.id.appreciate_ad);

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
        String cacheConfigString = ConfigCache.getUrlCache(mUrl);
        if (cacheConfigString != null) {
            setAppreciateLatestList(cacheConfigString);
            mAppLoadingTip.setVisibility(View.GONE);
            mAppLoadingPbar.setVisibility(View.GONE);
            mAppLoadingImage.setVisibility(View.VISIBLE);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(mUrl, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                    listView.setAdapter(null);
                    mAppLoadingTip.setVisibility(View.VISIBLE);
                    mAppLoadingPbar.setVisibility(View.VISIBLE);
                    mAppLoadingImage.setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(String result) {
                    ConfigCache.setUrlCache(result, mUrl);
                    setAppreciateLatestList(result);
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

    private void setAppreciateLatestList(String jsonString){
        try {
            JSONObject json = new JSONObject(jsonString);
            mAppreciateImageScaleType = json.optInt("image-scale-type", 0);
            JSONObject adCompanyJsonObject = json.optJSONObject("ad-company");
            if (adCompanyJsonObject != null) {
                mAdCompanyInfo = new AppreciateAdCompanyInfo();
                mAdCompanyInfo.name = adCompanyJsonObject.optString("name");
                mAdCompanyInfo.contact = adCompanyJsonObject.optString("contact");
                mAdCompanyInfo.address = adCompanyJsonObject.optString("address");
                mAdCompanyInfo.tel = adCompanyJsonObject.optString("tel");
                mAdCompanyInfo.phone = adCompanyJsonObject.optString("phone");
                mAdCompanyInfo.business = adCompanyJsonObject.optString("business");
                setHeaderView();
            }

            JSONArray jsonArray = json.getJSONArray("list");
            listData = new ArrayList<AppreciateLatestInfo>();
            AppreciateLatestInfo appreciateLatestInfo = null;

            for(int i = 0; i < jsonArray.length(); i++){
                appreciateLatestInfo = new AppreciateLatestInfo();
                appreciateLatestInfo.prefix = jsonArray.getJSONObject(i).optString("prefix");
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
        adapter = new Adapter(AppreciateLatestActivity.this);
        listView.setAdapter(adapter);
        showAd();
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.appreciate_latest_activity);
        setListView(R.id.appreciate_latest_list);
    }

    private void setHeaderView() {
        View view = LayoutInflater.from(this).inflate(R.layout.appreciate_ad_company_shop, null);
        mAdCompanyName = (TextView) view.findViewById(R.id.ad_company_name);
        mAdCompanyContact = (TextView) view.findViewById(R.id.ad_company_contact);
        mAdCompanyAddress = (TextView) view.findViewById(R.id.ad_company_address);
        mAdCompanyTel = (TextView) view.findViewById(R.id.ad_company_tel);
        mAdCompanyPhone = (TextView) view.findViewById(R.id.ad_company_phone);
        mAdCompanyBusiness = (TextView) view.findViewById(R.id.ad_company_business);

        mAdCompanyName.setText(mAdCompanyInfo.name);
        mAdCompanyContact.setText(mAdCompanyInfo.contact);
        mAdCompanyAddress.setText(mAdCompanyInfo.address);
        mAdCompanyTel.setText(mAdCompanyInfo.tel);
        mAdCompanyPhone.setText(mAdCompanyInfo.phone);
        mAdCompanyBusiness.setText(mAdCompanyInfo.business);
        mAdContainer.removeAllViews();
        mAdContainer.addView(view);
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
        if (listData.get(position).prefix != null && !"".equals(listData.get(position).prefix)) {
            mItemTextView.setText(listData.get(position).prefix + ":" + listData.get(position).title);
        } else {
            mItemTextView.setText(listData.get(position).title);
        }
        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ImagePool.sImageList = listData;
        Intent intent = new Intent(AppreciateLatestActivity.this, AppreciateLatestDetailsActivity.class);
        intent.putExtra("url", listData.get(position).origin);
        intent.putExtra("title", listData.get(position).title);
        intent.putExtra("thumbnail", listData.get(position).thumbnail);
        intent.putExtra("prefix", listData.get(position).prefix);
        intent.putExtra("position", position);
        intent.putExtra("imageScaleType", mAppreciateImageScaleType);
        startActivity(intent);
    }

    public void showAd() {
        //初始化广告视图
        if (listData.size() < 10 && mAdView == null) {
            mAdView = new AdView(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, 
                            FrameLayout.LayoutParams.WRAP_CONTENT);
            //设置广告出现的位置(悬浮于屏幕右下角)
            params.gravity=Gravity.BOTTOM|Gravity.RIGHT; 
            //将广告视图加入 Activity 中
            addContentView(mAdView, params);
        }
    }
}
