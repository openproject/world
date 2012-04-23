package com.tianxia.app.floworld.discuss;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tianxia.app.floworld.AppApplication;
import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.constant.FavoriteType;
import com.tianxia.lib.baseworld.activity.BaseActivity;

public class DiscussDetailsActivity extends BaseActivity{

    private String mTitle = null;
    private String mThumbnail = null;
    private String mUrl = null;
    private String mCategory = null;
    private String mDate = null;
    
    private WebView mWebView;
    private Button mAppBackButton;
    private ProgressBar mAppLoadingPbar = null;
    private ImageView mAppLoadingImage = null;
    private TextView mAppLoadingTip = null;

    private boolean mWebViewLoadError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discuss_details_activity);
        mThumbnail = getIntent().getStringExtra("thumbnail");
        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");
        mCategory = getIntent().getStringExtra("category");
        mDate = getIntent().getStringExtra("date");

        mAppBackButton = (Button) findViewById(R.id.app_back);
        mAppLoadingPbar = (ProgressBar) findViewById(R.id.app_loading_pbar);
        mAppLoadingImage = (ImageView) findViewById(R.id.app_loading_btn);
        mAppLoadingTip = (TextView) findViewById(R.id.app_loading_tip);
        mAppBackButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DiscussDetailsActivity.this.onBackPressed();
            }
        });
        mAppLoadingImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppLoadingPbar.setVisibility(View.VISIBLE);
                mAppLoadingImage.setVisibility(View.GONE);
                mWebView.clearCache(true);
                mWebView.loadUrl(mUrl);
                mWebViewLoadError = false;
            }
        });

        mWebView = (WebView) findViewById(R.id.discuzz_details_webview);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.loadUrl(mUrl);

        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mWebViewLoadError = true;
                mAppLoadingTip.setVisibility(View.VISIBLE);
                mAppLoadingTip.setText(R.string.app_loading_fail_web);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mAppLoadingPbar.setVisibility(View.GONE);
                mAppLoadingImage.setVisibility(View.VISIBLE);
                if (mWebViewLoadError) {
                    mWebView.setVisibility(View.INVISIBLE);
                } else {
                    mAppLoadingTip.setVisibility(View.INVISIBLE);
                    mWebView.setVisibility(View.VISIBLE);
                }
            }


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isFavorite()) {
            menu.add(R.string.unfavorite);
        } else {
            menu.add(R.string.favorite);
        }
        menu.add(R.string.app_share);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.unfavorite)) || item.getTitle().equals(getString(R.string.favorite))) {
            favorite(item);
        } else if (item.getTitle().equals(getString(R.string.app_share))) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_article_title, mTitle));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_article_text, mTitle) + mUrl);
            startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
        }
        return super.onOptionsItemSelected(item);
    }

    public void favorite(MenuItem item) {
        synchronized (AppApplication.mSQLiteHelper) {
            SQLiteDatabase db = AppApplication.mSQLiteHelper.getWritableDatabase();
            if (!isFavorite()) {
                ContentValues contentValue = new ContentValues();
                contentValue.put("title", mTitle);
                contentValue.put("type", FavoriteType.ARTICLE);
                contentValue.put("thumbnail", mThumbnail);
                contentValue.put("url", mUrl);
                contentValue.put("category", mCategory);
                contentValue.put("date", mDate);
                contentValue.put("description", "");
                db.insert("favorite", null, contentValue);
                Toast.makeText(this, R.string.favorite_add, Toast.LENGTH_SHORT).show();
                item.setTitle(R.string.unfavorite);
            } else {
                db.execSQL("delete from favorite where url = '" + mUrl + "'");
                Toast.makeText(this, R.string.favorite_del, Toast.LENGTH_SHORT).show();
                item.setTitle(R.string.favorite);
            }
        }
    }

    public boolean isFavorite() {
        boolean result = false;
        synchronized (AppApplication.mSQLiteHelper) {
            SQLiteDatabase db = AppApplication.mSQLiteHelper.getWritableDatabase();
            Cursor cursor = db.query("favorite", new String[]{"url"}, "url = ?", new String[]{mUrl}, null, null, null);
            if(cursor == null || cursor.getCount() == 0) {
                result = false;
            } else {
                result = true;
            }
            cursor.close();
        }
        return result;
    }
}
