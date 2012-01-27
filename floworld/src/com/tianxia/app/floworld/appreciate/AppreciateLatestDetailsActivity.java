package com.tianxia.app.floworld.appreciate;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tianxia.app.floworld.AppApplication;
import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.constant.FavoriteType;
import com.tianxia.widget.image.SmartImageView;

public class AppreciateLatestDetailsActivity extends Activity {

    private String mTitle = null;
    private String mUrl = null;
    private String mThumbnail = null;
    private SmartImageView mAppreciateLatestDetailsImageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appreciate_latest_details_activity);

        mTitle = getIntent().getStringExtra("title");
        mUrl = getIntent().getStringExtra("url");
        mThumbnail = getIntent().getStringExtra("thumbnail");

        mAppreciateLatestDetailsImageView = (SmartImageView) findViewById(R.id.appreciate_latest_details_image);
        mAppreciateLatestDetailsImageView.setImageUrl(mUrl, R.drawable.app_download_fail, R.drawable.app_download_loading, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isFavorite()) {
            menu.add(R.string.unfavorite);
        } else {
            menu.add(R.string.favorite);
        }
        menu.add(R.string.share);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.unfavorite)) || item.getTitle().equals(getString(R.string.favorite))) {
            favorite(item);
        } else if (item.getTitle().equals(getString(R.string.share))) {
        }
        return super.onOptionsItemSelected(item);
    }

    public void favorite(MenuItem item) {
        synchronized (AppApplication.mSQLiteHelper) {
            SQLiteDatabase db = AppApplication.mSQLiteHelper.getWritableDatabase();
            if (!isFavorite()) {
                ContentValues contentValue = new ContentValues();
                contentValue.put("title", mTitle);
                contentValue.put("type", FavoriteType.PICTURE);
                contentValue.put("url", mUrl);
                contentValue.put("thumbnail", mThumbnail);
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
