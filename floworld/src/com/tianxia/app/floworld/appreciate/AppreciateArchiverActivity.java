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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.model.AppreciateArchiverInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;

public class AppreciateArchiverActivity extends AdapterActivity<AppreciateArchiverInfo> {

    private String appreciateArchivertUrl = null;

    private TextView appreciateArchiverTitle = null;

    private Button appBackButton = null;
    private TextView appLoadingTip = null;
    private ProgressBar appLoadingPbar = null;
    private ImageView appLoadingImage = null;

    private ImageView itemImageView = null;
    private TextView itemIndex = null;
    private TextView itemDate = null;
    private TextView itemTitle = null;

    private Intent appreciateArchiverIntent = null;

    private AssetManager assetManager = null;
    private String[] appreciateArchiverImages = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appreciateArchivertUrl = getIntent().getStringExtra("url");

        appreciateArchiverTitle = (TextView) findViewById(R.id.appreciate_archiver_title);
  
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
                loadListView();
            }
        });
        loadListView();

        assetManager = getResources().getAssets();
        try {
            appreciateArchiverImages = assetManager.list("kinds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadListView(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(appreciateArchivertUrl, new AsyncHttpResponseHandler(){

            @Override
            public void onStart() {
                listView.setAdapter(null);
                appLoadingTip.setVisibility(View.VISIBLE);
                appLoadingPbar.setVisibility(View.VISIBLE);
                appLoadingImage.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(String result) {
                setAppreciateArchiverList(result);
                adapter = new Adapter(AppreciateArchiverActivity.this);
                listView.setAdapter(adapter);
                updateAppreciateTitle(listData.size());
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
    }

    private void updateAppreciateTitle(int size){
        if (size > 0) {
            appreciateArchiverTitle.setText(appreciateArchiverTitle.getText() + "(共" + size + "期)");
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

        itemImageView = (ImageView) view.findViewById(R.id.item_image);
        try {
            itemImageView.setImageBitmap(BitmapFactory.decodeStream(assetManager.open("kinds/" + appreciateArchiverImages[position])));
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemIndex = (TextView) view.findViewById(R.id.item_index);
        itemIndex.setText(listData.get(position).index);

        itemDate = (TextView) view.findViewById(R.id.item_date);
        itemDate.setText("日期:" + listData.get(position).date);
        
        itemTitle = (TextView) view.findViewById(R.id.item_title);
        itemTitle.setText("题语:" + listData.get(position).title);

        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        appreciateArchiverIntent = new Intent(AppreciateArchiverActivity.this, AppreciateLatestActivity.class);
        appreciateArchiverIntent.putExtra("url", listData.get(position).json);
        appreciateArchiverIntent.putExtra("title", listData.get(position).index);
        startActivity(appreciateArchiverIntent);
    }
}
