package com.tianxia.app.floworld.appreciate;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.tianxia.app.floworld.model.AppreciateAdCompanyInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;

public class AppreciateCompanyActivity extends AdapterActivity<AppreciateAdCompanyInfo> {

    private String mUrl = null;

    private Button mAppBackButton = null;
    private TextView mAppLoadingTip = null;
    private ProgressBar mAppLoadingPbar = null;
    private ImageView mAppLoadingImage = null;

    private TextView mItemName = null;
    private TextView mItemContact = null;
    private TextView mItemAddress = null;
    private TextView mItemTel = null;
    private TextView mItemPhone = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getIntent().getStringExtra("url");

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
    }

    protected void loadListView(){
        String cacheConfigString = ConfigCache.getUrlCache(mUrl);
        if (cacheConfigString != null) {
            setAppreciateCompanyList(cacheConfigString);
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
                    setAppreciateCompanyList(result);
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

    private void setAppreciateCompanyList(String jsonString){
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray adCompanyJsonArray = json.optJSONArray("list");
            listData = new ArrayList<AppreciateAdCompanyInfo>();
            AppreciateAdCompanyInfo appreciateCompanyInfo = null;

            for(int i = 0; i < adCompanyJsonArray.length(); i++){
                appreciateCompanyInfo = new AppreciateAdCompanyInfo();
                appreciateCompanyInfo.type = adCompanyJsonArray.getJSONObject(i).optInt("type");
                appreciateCompanyInfo.name = adCompanyJsonArray.getJSONObject(i).optString("name");
                appreciateCompanyInfo.contact = adCompanyJsonArray.getJSONObject(i).optString("contact");
                appreciateCompanyInfo.address = adCompanyJsonArray.getJSONObject(i).optString("address");
                appreciateCompanyInfo.tel = adCompanyJsonArray.getJSONObject(i).optString("tel");
                appreciateCompanyInfo.phone = adCompanyJsonArray.getJSONObject(i).optString("phone");
                appreciateCompanyInfo.business = adCompanyJsonArray.getJSONObject(i).optString("business");
                appreciateCompanyInfo.site = adCompanyJsonArray.getJSONObject(i).optString("site");
                listData.add(appreciateCompanyInfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new Adapter(AppreciateCompanyActivity.this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.appreciate_company_activity);
        setListView(R.id.appreciate_company_list);
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if (position % 2 == 0) {
            view = LayoutInflater.from(this).inflate(R.layout.appreciate_company_list_item, null);
        } else {
            view = LayoutInflater.from(this).inflate(R.layout.appreciate_company_list_item_reverse, null);
        }

        mItemName = (TextView) view.findViewById(R.id.item_name);
        mItemContact = (TextView) view.findViewById(R.id.item_contact);
        mItemAddress = (TextView) view.findViewById(R.id.item_address);
        mItemTel = (TextView) view.findViewById(R.id.item_tel);
        mItemPhone = (TextView) view.findViewById(R.id.item_phone);

        mItemName.setText(listData.get(position).name);
        mItemContact.setText(listData.get(position).contact);
        mItemAddress.setText(listData.get(position).address);
        mItemTel.setText(listData.get(position).tel);
        mItemPhone.setText(listData.get(position).phone);

        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }

}
