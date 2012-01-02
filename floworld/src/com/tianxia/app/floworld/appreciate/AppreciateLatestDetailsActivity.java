package com.tianxia.app.floworld.appreciate;

import android.app.Activity;
import android.os.Bundle;

import com.tianxia.app.floworld.R;
import com.tianxia.widget.image.SmartImageView;

public class AppreciateLatestDetailsActivity extends Activity {

    private String mUrl = null;
    private SmartImageView mAppreciateLatestDetailsImageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appreciate_latest_details_activity);

        mUrl = getIntent().getStringExtra("url");

        mAppreciateLatestDetailsImageView = (SmartImageView) findViewById(R.id.appreciate_latest_details_image);
        mAppreciateLatestDetailsImageView.setImageUrl(mUrl, R.drawable.app_download_fail, R.drawable.app_download_loading, true);
    }
}
