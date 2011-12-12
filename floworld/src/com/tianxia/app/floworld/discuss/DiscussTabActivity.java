package com.tianxia.app.floworld.discuss;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.model.DiscussInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;

public class DiscussTabActivity extends AdapterActivity<DiscussInfo> {

    private ImageView itemImageView;
    private TextView itemTitleTextView;
    private TextView itemCategoryTextView;
    private TextView itemDateTextView;

    private AssetManager assetManager = null;
    private String[] kindImages = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assetManager = getResources().getAssets();
        try {
            kindImages = assetManager.list("kinds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.discuss_tab_activity);
        setListView(R.id.discuss_tab_list);

        setListData();
    }

    private void setListData(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(DiscussApi.DISCUSS_CONFIG_URL, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(String result){
                try {
                    System.out.println("result:" + result);
                    JSONObject discussConfig = new JSONObject(result);

                    String baseUrl = discussConfig.getString("base-url");
                    JSONArray discussList = discussConfig.getJSONArray("list");
                    DiscussInfo discussInfo = null;
                    for (int i = 0; i < discussList.length(); i++) {
                        discussInfo = new DiscussInfo();
                        discussInfo.id = discussList.getJSONObject(i).getInt("id");
                        discussInfo.title = discussList.getJSONObject(i).getString("title");
                        discussInfo.category = discussList.getJSONObject(i).getString("category");
                        discussInfo.date = discussList.getJSONObject(i).getString("date");
                        discussInfo.path = baseUrl + discussList.getJSONObject(i).getString("path");
                        listData.add(discussInfo);
                    }
                    

                    adapter = new Adapter(DiscussTabActivity.this);
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
            }

            @Override
            public void onFailure(Throwable arg0) {
                listView.setAdapter(null);
            }

        });
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view==null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.discuss_tab_list_item, null);
        }

        itemImageView = (ImageView) view.findViewById(R.id.item_image);
        try {
            itemImageView.setImageBitmap(BitmapFactory.decodeStream(assetManager.open("kinds/" + kindImages[position])));
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemTitleTextView = (TextView) view.findViewById(R.id.item_title);
        itemTitleTextView.setText(listData.get(position).title);

        itemCategoryTextView = (TextView) view.findViewById(R.id.item_category);
        itemCategoryTextView.setText(listData.get(position).category);

        itemDateTextView = (TextView) view.findViewById(R.id.item_date);
        itemDateTextView.setText(listData.get(position).date);
        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        
    }
}
