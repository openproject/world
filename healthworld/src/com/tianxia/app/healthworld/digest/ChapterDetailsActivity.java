/*
 * Copyright (C) 2012 Jayfeng.
 */

package com.tianxia.app.healthworld.digest;

import android.app.Activity;

import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;

import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.app.healthworld.AppApplication;

public class ChapterDetailsActivity extends Activity{

    private String mUrl;
    private ChapterDetailsView mChapterDetailsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN,
                      WindowManager.LayoutParams. FLAG_FULLSCREEN);

        setContentView(R.layout.chapter_details_activity);

        mUrl = getIntent().getStringExtra("url");

        System.out.println("url:" + mUrl);
        mChapterDetailsView = (ChapterDetailsView)findViewById(R.id.chapter_details_content);
        mChapterDetailsView.setInitText("正在加载，请稍候...");
        setChapterContent();
    }

    public void setChapterContent() {
         String cacheConfigString = ConfigCache.getUrlCache(AppApplication.mDomain + mUrl);
         if (cacheConfigString != null) {
             mChapterDetailsView.setContent(cacheConfigString);
         } else {
             AsyncHttpClient client = new AsyncHttpClient();
             client.get(AppApplication.mDomain + mUrl, new AsyncHttpResponseHandler(){

                 @Override
                 public void onStart() {
                     //mAppLoadingLinearLayout.setVisibility(View.VISIBLE);
                 }

                 @Override
                 public void onSuccess(String result){
                     mChapterDetailsView.setContent(result);
                     System.out.println(result);
                     ConfigCache.setUrlCache(result, AppApplication.mDomain + mUrl);
                     //mAppLoadingLinearLayout.setVisibility(View.GONE);
                     //showDigestList(result);
                 }

                 @Override
                 public void onFailure(Throwable arg0) {
                     //mAppLoadingProgressBar.setVisibility(View.INVISIBLE);
                     //mAppLoadingTextView.setText(R.string.app_loading_fail);
                     //listView.setAdapter(null);
                     //listView.setVisibility(View.INVISIBLE);
                 }

             });
         }
    }

    private void showChapterContent() {

    }
}
