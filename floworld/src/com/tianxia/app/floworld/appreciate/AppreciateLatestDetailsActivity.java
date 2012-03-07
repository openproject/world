package com.tianxia.app.floworld.appreciate;

import java.io.File;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tianxia.app.floworld.AppApplication;
import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.cache.ImagePool;
import com.tianxia.app.floworld.constant.FavoriteType;
import com.tianxia.app.floworld.utils.FileUtils;
import com.tianxia.app.floworld.utils.ScreenUtils;
import com.tianxia.lib.baseworld.utils.StringUtils;
import com.tianxia.widget.gallery.PicGallery;
import com.tianxia.widget.image.SmartImageView;

public class AppreciateLatestDetailsActivity extends Activity {

    private String mTitle = null;
    private String mUrl = null;
    private String mThumbnail = null;
    private String mPrefix = null;
    private int mPosition = 0;

    public RelativeLayout mAppTitltBar = null;
    private TextView mAppTitleBarText = null;
    private Button mAppBack = null;
    private SmartImageView mItemSmartImageView = null;
    private TextView mPicTitleTextView = null;

    private PicGallery mAppreciateLatestDetailsGallery = null;
    private LastestAdapter adapter = null;

    public static final int IMAGE_SCALE_TYPE_CROP = 0;
    public static final int IMAGE_SCALE_TYPE_FIT = 1;
    public int mImageScaleType = 0;

    public int mScreenWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appreciate_latest_details_activity);

        mTitle = getIntent().getStringExtra("title");
        mUrl = getIntent().getStringExtra("url");
        mThumbnail = getIntent().getStringExtra("thumbnail");
        mPrefix = getIntent().getStringExtra("prefix");
        mPosition = getIntent().getIntExtra("position", 0);
        mImageScaleType = getIntent().getIntExtra("imageScaleType", 0);

        mAppTitltBar = (RelativeLayout) findViewById(R.id.app_titlebar);
        mAppTitleBarText = (TextView) findViewById(R.id.appreciate_latest_title);
        mAppBack = (Button) findViewById(R.id.app_back);
        mPicTitleTextView = (TextView) findViewById(R.id.pic_title);

        ScreenUtils screenUtils = new ScreenUtils(this);
        mScreenWidth = screenUtils.getWidth();

        mAppreciateLatestDetailsGallery = (PicGallery) findViewById(R.id.appreciate_latest_details_gallery);
        adapter = new LastestAdapter();
        mAppreciateLatestDetailsGallery.setAdapter(adapter);
        mAppreciateLatestDetailsGallery.setSelection(mPosition);
        mAppreciateLatestDetailsGallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mAppTitltBar.getVisibility() == View.VISIBLE) {
                    mAppTitltBar.setVisibility(View.INVISIBLE);
                    mPicTitleTextView.setVisibility(View.INVISIBLE);
                } else {
                    mAppTitltBar.setVisibility(View.VISIBLE);
                    mPicTitleTextView.setVisibility(View.VISIBLE);
                }
            }
        });
        mAppreciateLatestDetailsGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                mUrl = ImagePool.sImageList.get(position).origin;
                mTitle = ImagePool.sImageList.get(position).title;
                mThumbnail = ImagePool.sImageList.get(position).thumbnail;
                mPrefix = ImagePool.sImageList.get(position).prefix;
                if (mPrefix != null && !"".equals(mPrefix)) {
                    mPicTitleTextView.setText(mPrefix + ":" + ImagePool.sImageList.get(position).title);
                } else {
                    mPicTitleTextView.setText(ImagePool.sImageList.get(position).title);
                }
                mAppTitleBarText.setText((position + 1) + "/" + ImagePool.sImageList.size());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
        mAppBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
            if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/jpeg");
                    File cacheFile = new File(Environment.getExternalStorageDirectory().getPath() + "/floworld/image/" + StringUtils.replaceUrlWithPlus(mUrl));
                    if (!cacheFile.exists()) {
                        Toast.makeText(this, R.string.share_pic_no_data, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    File shareFile = new File(Environment.getExternalStorageDirectory().getPath() + "/floworld/image/share.jpg");
                    FileUtils.copyFile(cacheFile, shareFile);
                    if (shareFile.exists() && shareFile.isFile()) {
                        Uri shareUri = Uri.fromFile(shareFile);
                        intent.putExtra(Intent.EXTRA_STREAM, shareUri);
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_pic_title, mTitle));
                        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_pic_text, mTitle));
                        startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, R.string.share_pic_no_sdcard, Toast.LENGTH_SHORT).show();
            }
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
                contentValue.put("prefix", mPrefix);
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

            if (mImageScaleType == 0) {
                mItemSmartImageView.setScaleType(ScaleType.CENTER_CROP);
                mItemSmartImageView.setImageUrl(ImagePool.sImageList.get(position).origin, R.drawable.app_download_fail, R.drawable.app_download_loading, true);
            } else {
                mItemSmartImageView.setImageUrl(ImagePool.sImageList.get(position).origin, R.drawable.app_download_fail, R.drawable.app_download_loading, false);
                mItemSmartImageView.setScaleType(ScaleType.FIT_CENTER);
            }
            return view;
        }
    }
}
