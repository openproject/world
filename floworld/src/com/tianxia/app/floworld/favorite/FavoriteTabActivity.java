package com.tianxia.app.floworld.favorite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.tianxia.app.floworld.AppApplication;
import com.tianxia.app.floworld.appreciate.AppreciateLatestDetailsActivity;
import com.tianxia.app.floworld.cache.ImagePool;
import com.tianxia.app.floworld.constant.FavoriteType;
import com.tianxia.app.floworld.discuss.DiscussDetailsActivity;
import com.tianxia.app.floworld.model.AppreciateLatestInfo;
import com.tianxia.app.floworld.model.FavoriteInfo;
import com.tianxia.lib.baseworld.R;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.widget.image.SmartImageView;


public class FavoriteTabActivity extends AdapterActivity<FavoriteInfo>{

    public int mFavoriteType = FavoriteType.PICTURE;

    private Button mAppCategotyLeft = null;
    private Button mAppCategotyRight = null;

    private SmartImageView mItemSmartImageView = null;
    private ImageView mItemImageView = null;
    private TextView mItemTextView = null;
    private TextView mItemCategoryTextView = null;
    private TextView mItemDateTextView = null;

    private AssetManager assetManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assetManager = getResources().getAssets();

        mAppCategotyLeft = (Button) findViewById(R.id.app_category_left);
        mAppCategotyRight = (Button) findViewById(R.id.app_category_right);
        mAppCategotyLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFavoriteType == FavoriteType.ARTICLE) {
                    mFavoriteType = FavoriteType.PICTURE;
                    showFavoriteList(mFavoriteType);
                }
            }
        });
        mAppCategotyRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFavoriteType == FavoriteType.PICTURE) {
                    mFavoriteType = FavoriteType.ARTICLE;
                    showFavoriteList(mFavoriteType);
                }
            }
        });
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.favorite_tab_activity);
        setListView(R.id.favorite_tab_grid);
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if (mFavoriteType == FavoriteType.PICTURE) {
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.favorite_tab_list_item_picture, null);
            mItemSmartImageView = (SmartImageView) view.findViewById(R.id.item_image);
            if (listData != null && position < listData.size()){
                mItemSmartImageView.setImageUrl(listData.get(position).thumbnail, R.drawable.app_download_fail, R.drawable.app_download_loading);
            }

            mItemTextView = (TextView) view.findViewById(R.id.item_title);
            mItemTextView.setText(listData.get(position).title);
        } else if (mFavoriteType == FavoriteType.ARTICLE) {
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.favorite_tab_list_item_article, null);
            mItemImageView = (ImageView) view.findViewById(R.id.item_image);
            try {
                mItemImageView.setImageBitmap(BitmapFactory.decodeStream(assetManager.open(listData.get(position).thumbnail)));
            } catch (IOException e) {
                e.printStackTrace();
            }

            mItemTextView = (TextView) view.findViewById(R.id.item_title);
            mItemTextView.setText(listData.get(position).title);

            mItemCategoryTextView = (TextView) view.findViewById(R.id.item_category);
            mItemCategoryTextView.setText(listData.get(position).category);

            mItemDateTextView = (TextView) view.findViewById(R.id.item_date);
            mItemDateTextView.setText(listData.get(position).date);
        }

        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = null;
        if (mFavoriteType == FavoriteType.PICTURE) {

            List<AppreciateLatestInfo> list = new ArrayList<AppreciateLatestInfo>();
            for (FavoriteInfo fi : listData) {
                AppreciateLatestInfo ali = new AppreciateLatestInfo();
                ali.origin = fi.url;
                ali.title = fi.title;
                ali.prefix = fi.prefix;
                list.add(ali);
            }
            ImagePool.sImageList = list;

            intent = new Intent(this, AppreciateLatestDetailsActivity.class);
            intent.putExtra("url", listData.get(position).url);
            intent.putExtra("title", listData.get(position).title);
            intent.putExtra("thumbnail", listData.get(position).thumbnail);
            intent.putExtra("position", position);
            intent.putExtra("prefix", listData.get(position).prefix);
        } else if (mFavoriteType == FavoriteType.ARTICLE) {
            intent = new Intent(this, DiscussDetailsActivity.class);
            intent.putExtra("thumbnail", listData.get(position).thumbnail);
            intent.putExtra("url", listData.get(position).url);
            intent.putExtra("title", listData.get(position).title);
            intent.putExtra("category", listData.get(position).category);
            intent.putExtra("date", listData.get(position).date);
        }
        startActivity(intent);
    }

    private void getFavoriteList(int favoriteType) {
        mFavoriteType = favoriteType;
        if (mFavoriteType == FavoriteType.PICTURE) {
            findViewById(R.id.favorite_tab_list).setVisibility(View.GONE);
            setListView(R.id.favorite_tab_grid);
        } else if (mFavoriteType == FavoriteType.ARTICLE) {
            findViewById(R.id.favorite_tab_grid).setVisibility(View.GONE);
            setListView(R.id.favorite_tab_list);
        }
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FavoriteTabActivity.this.onItemClick(adapterView, view, position, id);
            }
        });
        listView.setVisibility(View.VISIBLE);
        listData.clear();
        synchronized (AppApplication.mSQLiteHelper) {
            SQLiteDatabase db = AppApplication.mSQLiteHelper.getReadableDatabase();
            Cursor cursor = null;
            try {
                cursor = db.query("favorite", null, "type=?", new String[]{favoriteType + ""}, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        FavoriteInfo favoriteInfo = new FavoriteInfo();
                        favoriteInfo.id = cursor.getInt(cursor.getColumnIndex("_id"));
                        favoriteInfo.title = cursor.getString(cursor.getColumnIndex("title"));
                        favoriteInfo.type = favoriteType;
                        favoriteInfo.thumbnail = cursor.getString(cursor.getColumnIndex("thumbnail"));
                        favoriteInfo.url = cursor.getString(cursor.getColumnIndex("url"));
                        favoriteInfo.category = cursor.getString(cursor.getColumnIndex("category"));
                        favoriteInfo.date = cursor.getString(cursor.getColumnIndex("date"));
                        favoriteInfo.prefix = cursor.getString(cursor.getColumnIndex("prefix"));
                        listData.add(favoriteInfo);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    private void showFavoriteList(int favoriteType) {
        getFavoriteList(favoriteType);
        adapter = new Adapter(FavoriteTabActivity.this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        showFavoriteList(mFavoriteType);
        super.onResume();
    }
}
