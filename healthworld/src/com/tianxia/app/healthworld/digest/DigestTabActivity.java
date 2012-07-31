package com.tianxia.app.healthworld.digest;

import android.content.Intent;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.feedback.UMFeedbackService;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.AppApplicationApi;
import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.model.BookInfo;
import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.activity.BaseActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.view.Gravity;

public class DigestTabActivity extends AdapterActivity<BookInfo>{

    private TextView mItemTitleTextView = null;
    private SmartImageView mItemConverImageView = null;
    private TextView mItemSummaryTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDigestList();
    }

    @Override
    protected void setLayoutView(){
        setContentView(R.layout.digest_tab_activity);
        setListView(R.id.digest_tab_list);
        Button btn = new Button(DigestTabActivity.this);
        btn.setText(R.string.digest_footer_suggest_tips);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UMFeedbackService.openUmengFeedbackSDK(DigestTabActivity.this);
            }
        });
        View emptyView = createEmptyView();
        ((ViewGroup)listView.getParent()).addView(emptyView);
        ((ListView)listView).setEmptyView(emptyView);
        ((ListView)listView).addFooterView(btn);
    }

    private void setDigestList(){
         String cacheConfigString = ConfigCache.getUrlCache(AppApplicationApi.DIGEST_URL);
         if (cacheConfigString != null) {
             //mAppLoadingLinearLayout.setVisibility(View.GONE);
             showDigestList(cacheConfigString);
             System.out.println(cacheConfigString);
         } else {
             AsyncHttpClient client = new AsyncHttpClient();
             client.get(AppApplicationApi.DIGEST_URL, new AsyncHttpResponseHandler(){

                 @Override
                 public void onStart() {
                     //mAppLoadingLinearLayout.setVisibility(View.VISIBLE);
                 }

                 @Override
                 public void onSuccess(String result){
                     System.out.println(result);
                     ConfigCache.setUrlCache(result, AppApplicationApi.DIGEST_URL);
                     //mAppLoadingLinearLayout.setVisibility(View.GONE);
                     showDigestList(result);
                 }

                 @Override
                 public void onFailure(Throwable arg0) {
                     //mAppLoadingProgressBar.setVisibility(View.INVISIBLE);
                     //mAppLoadingTextView.setText(R.string.app_loading_fail);
                     listView.setAdapter(null);
                     listView.setVisibility(View.INVISIBLE);
                 }

             });
         }
    }

    private void showDigestList(String result) {
        try {
            JSONObject digestConfig = new JSONObject(result);
            JSONArray digestList = digestConfig.getJSONArray("books");
            BookInfo bookInfo = null;
            for (int i = 0; i < digestList.length(); i++) {
                bookInfo = new BookInfo();
                bookInfo.title = digestList.getJSONObject(i).optString("title");
                bookInfo.summary = digestList.getJSONObject(i).optString("summary");
                bookInfo.cover = digestList.getJSONObject(i).optString("cover");
                bookInfo.url = digestList.getJSONObject(i).optString("url");
                listData.add(bookInfo);
            }
            adapter = new Adapter(DigestTabActivity.this);
            listView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.digest_tab_list_item, null);
        }

        mItemConverImageView = (SmartImageView) view.findViewById(R.id.item_image);
        mItemConverImageView.setImageUrl(AppApplication.mDomain + listData.get(position).cover, R.drawable.icon, 0);
        System.out.println("cover:" + AppApplication.mDomain + listData.get(position).cover);

        mItemTitleTextView = (TextView) view.findViewById(R.id.item_text);
        mItemTitleTextView.setText(listData.get(position).title);

        mItemSummaryTextView = (TextView) view.findViewById(R.id.item_text_describe);
        mItemSummaryTextView.setText(listData.get(position).summary);

        return view;
    }

    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, ChapterListActivity.class);
        intent.putExtra("title", listData.get(position).title);
        intent.putExtra("url", listData.get(position).url);
        startActivity(intent);
    }

    public View createEmptyView() {
        TextView textView = new TextView(this);
        textView.setText("正在加载中...");
        textView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        textView.setGravity(Gravity.CENTER);
        textView.setVisibility(View.GONE);
        return textView;
    }
}
