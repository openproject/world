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
    private SmartImageView mAppreciateLatestDetailsImageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appreciate_latest_details_activity);

        mTitle = getIntent().getStringExtra("title");
        mUrl = getIntent().getStringExtra("url");

        mAppreciateLatestDetailsImageView = (SmartImageView) findViewById(R.id.appreciate_latest_details_image);
        mAppreciateLatestDetailsImageView.setImageUrl(mUrl, R.drawable.app_download_fail, R.drawable.app_download_loading, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("收藏");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("item id:" + item.getItemId());
        switch (item.getItemId()) {
        case 0:
            favorite();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void favorite() {
        synchronized (AppApplication.mSQLiteHelper) {
            SQLiteDatabase db = AppApplication.mSQLiteHelper.getWritableDatabase();
            Cursor cursor = db.query("favorite", new String[]{"url"}, "url = ?", new String[]{mUrl}, null, null, null);
            if(cursor == null || cursor.getCount() == 0) {
                ContentValues contentValue = new ContentValues();
                contentValue.put("title", mTitle);
                contentValue.put("type", FavoriteType.APPRECIATE);
                contentValue.put("url", mUrl);
                contentValue.put("description", "");
                db.insert("favorite", null, contentValue);
                Toast.makeText(this, "收藏成功.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "删除收藏.", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }
    }
}
