/*
 * Copyright (C) 2012 Jayfeng.
 */

package com.tianxia.app.healthworld.digest;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.AdapterView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.model.ChapterInfo;
import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.app.healthworld.AppApplication;

public class ChapterListActivity extends AdapterActivity<ChapterInfo>{

    private String title;
    private String url;

    private TextView mAppBannerTextView = null;

    private TextView mItemTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       

        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");

        mAppBannerTextView = (TextView)findViewById(R.id.app_banner_title);
        mAppBannerTextView.setText(title);
        setChapterList();
    }

    @Override
    protected void setLayoutView(){
        setContentView(R.layout.chapter_list_activity);
        setListView(R.id.chapter_list);
    }

    private void setChapterList() {
         String cacheConfigString = ConfigCache.getUrlCache(AppApplication.mDomain + url);
         if (cacheConfigString != null) {
             //mAppLoadingLinearLayout.setVisibility(View.GONE);
             showChapterList(cacheConfigString);
             System.out.println(cacheConfigString);
         } else {
             AsyncHttpClient client = new AsyncHttpClient();
             client.get(AppApplication.mDomain + url, new AsyncHttpResponseHandler(){

                 @Override
                 public void onStart() {
                     //mAppLoadingLinearLayout.setVisibility(View.VISIBLE);
                 }

                 @Override
                 public void onSuccess(String result){
                     System.out.println(result);
                     ConfigCache.setUrlCache(result,url);
                     //mAppLoadingLinearLayout.setVisibility(View.GONE);
                     showChapterList(result);
                 }

                 @Override
                 public void onFailure(Throwable arg0) {
                     System.out.println("fail -----------------------------");
                     //mAppLoadingProgressBar.setVisibility(View.INVISIBLE);
                     //mAppLoadingTextView.setText(R.string.app_loading_fail);
                     listView.setAdapter(null);
                     listView.setVisibility(View.INVISIBLE);
                 }

             });
         }
    }

    private void showChapterList(String result) {
        try {
            JSONObject chapterConfig = new JSONObject(result);
            JSONArray chapterList = chapterConfig.getJSONArray("chapters");
            ChapterInfo chapterInfo = null;
            for (int i = 0; i < chapterList.length(); i++) {
                chapterInfo = new ChapterInfo();
                chapterInfo.title = chapterList.getJSONObject(i).optString("title");
                chapterInfo.url = chapterList.getJSONObject(i).optString("url");
                listData.add(chapterInfo);
                chapterInfo.subChapters = recurseChapters(chapterList.getJSONObject(i).optJSONArray("subChapters"));
            }

            adapter = new Adapter(ChapterListActivity.this);
            listView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<ChapterInfo> recurseChapters(JSONArray jsonarray) {
        List<ChapterInfo> result = null;
        if (jsonarray == null || jsonarray.length() == 0) {
            return result;
        }

        result = new ArrayList<ChapterInfo>();
        for (int i = 0; i < jsonarray.length(); i++) {
            ChapterInfo chapterInfo = new ChapterInfo();
            try {
                chapterInfo.title = jsonarray.getJSONObject(i).optString("title");
                chapterInfo.url = jsonarray.getJSONObject(i).optString("url");
                chapterInfo.subChapters = recurseChapters(jsonarray.getJSONObject(i).optJSONArray("subChapters"));
                result.add(chapterInfo);
                listData.add(chapterInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    protected View getView(int position, View convertView) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.chapter_list_item, null);
        }

        mItemTextView = (TextView) view.findViewById(R.id.item_text);
        mItemTextView.setText(listData.get(position).title);
        return view;
    }

    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //toko
    }

}


