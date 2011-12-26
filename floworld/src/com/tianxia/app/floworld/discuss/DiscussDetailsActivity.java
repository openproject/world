package com.tianxia.app.floworld.discuss;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.tianxia.app.floworld.R;

public class DiscussDetailsActivity extends Activity{

    String url;
    
    private WebView mWebView;
    private Button appBackButton;
    private ProgressBar appLoadingPbar = null;
    private ImageView appLoadingImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discuss_details_activity);
        url = getIntent().getStringExtra("url");

        appBackButton = (Button) findViewById(R.id.app_back);
        appLoadingPbar = (ProgressBar) findViewById(R.id.app_loading_pbar);
        appLoadingImage = (ImageView) findViewById(R.id.app_loading_btn);
        appBackButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DiscussDetailsActivity.this.onBackPressed();
            }
        });
        appLoadingImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                appLoadingPbar.setVisibility(View.VISIBLE);
                appLoadingImage.setVisibility(View.GONE);
                mWebView.loadUrl(url);
            }
        });

        mWebView = (WebView) findViewById(R.id.discuzz_details_webview);
        mWebView.loadUrl(url);

        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                appLoadingPbar.setVisibility(View.GONE);
                appLoadingImage.setVisibility(View.VISIBLE);
            }
        });
    }
}
