package com.tianxia.app.floworld.appreciate;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tianxia.app.floworld.AppApplication;
import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.cache.ImagePool;
import com.tianxia.app.floworld.constant.FavoriteType;
import com.tianxia.app.floworld.utils.ScreenUtils;
import com.tianxia.widget.gallery.PicGallery;
import com.tianxia.widget.image.SmartImageView;

public class AppreciateLatestDetailsActivity extends Activity {

    private String mTitle = null;
    private String mUrl = null;
    private String mThumbnail = null;
    private int mPosition = 0;

    private SmartImageView mItemSmartImageView = null;

    private PicGallery mAppreciateLatestDetailsGallery = null;
    private LastestAdapter adapter = null;

    public int mScreenWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appreciate_latest_details_activity);

        mTitle = getIntent().getStringExtra("title");
        mUrl = getIntent().getStringExtra("url");
        mThumbnail = getIntent().getStringExtra("thumbnail");
        mPosition = getIntent().getIntExtra("position", 0);

        ScreenUtils screenUtils = new ScreenUtils(this);
        mScreenWidth = screenUtils.getWidth();

        mAppreciateLatestDetailsGallery = (PicGallery) findViewById(R.id.appreciate_latest_details_gallery);
        adapter = new LastestAdapter();
        mAppreciateLatestDetailsGallery.setAdapter(adapter);
        mAppreciateLatestDetailsGallery.setSelection(mPosition);
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

    public class LastestAdapter extends BaseAdapter {

        private int width;
        private int height;
        public LastestAdapter() {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            width = dm.widthPixels;
            height = dm.heightPixels;
        }
        @Override
        public int getCount() {
            if (ImagePool.sImageList != null) {
                return ImagePool.sImageList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            view = View.inflate(AppreciateLatestDetailsActivity.this, R.layout.appreciate_latest_details_item, null);
            mItemSmartImageView = (SmartImageView) view.findViewById(R.id.item_image);
            mItemSmartImageView.setLayoutParams(new LinearLayout.LayoutParams(width - 2, height));
            mItemSmartImageView.setImageUrl(ImagePool.sImageList.get(position).origin, R.drawable.app_download_fail, R.drawable.app_download_loading, true);
            mItemSmartImageView.setScaleType(ScaleType.CENTER_CROP);
            return view;
        }
    }
}
