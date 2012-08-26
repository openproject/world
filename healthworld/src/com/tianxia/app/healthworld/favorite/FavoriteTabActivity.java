package com.tianxia.app.healthworld.favorite;

import android.app.AlertDialog;

import android.content.ContentValues;
import android.content.DialogInterface;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.EditText;
import android.widget.Toast;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.AppSQLiteHelper;
import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.BaseActivity;
import com.tianxia.lib.baseworld.widget.TagCloudInfo;
import com.tianxia.lib.baseworld.widget.TagCloudView;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class FavoriteTabActivity extends BaseActivity{

    private TagCloudView mTagCloudView;
    private View mEmptyView;
    private Button mEmptyBtnView;

    public static boolean mDataSetChanged = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_tab_activity);

        mDataSetChanged = true;
        mTagCloudView = (TagCloudView) findViewById(R.id.tag_cloud_view);
        mEmptyView = findViewById(R.id.empty_view);
        mEmptyBtnView = (Button) findViewById(R.id.empty_btn);
        mEmptyBtnView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAddWishDialg();
            }
        });
    }

    private List<TagCloudInfo> getTags() {
        List<TagCloudInfo> tags = new ArrayList<TagCloudInfo>();
        synchronized (AppApplication.mSQLiteHelper) {
            SQLiteDatabase db = AppApplication.mSQLiteHelper.getReadableDatabase();
            Cursor cursor = null;
            try {
                cursor = db.query(AppSQLiteHelper.TABLE_WISH, null, null, null, null, null, "sort");
                if (cursor.moveToFirst()) {
                    do {
                        TagCloudInfo tagCloudInfo = new TagCloudInfo();
                        tagCloudInfo.title = cursor.getString(cursor.getColumnIndex("title"));
                        tagCloudInfo.sort = cursor.getInt(cursor.getColumnIndex("sort"));
                        tags.add(tagCloudInfo);
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
        return tags;
    }

    private void showEmptyView(List<TagCloudInfo> list) {
        if (list != null && list.size() > 0) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite_add_wish:
                showAddWishDialg();
                break;
            case R.id.favorite_edit_wish:
                Intent intent = new Intent(this, FavoriteEditActivity.class);
                startActivity(intent);
                break;
            case R.id.favorite_clear_wish:
                clearWishes();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onResume () {
        super.onResume();

        if (mDataSetChanged) {
            mTagCloudView.setTagList(getTags());
            mTagCloudView.refresh();
            mDataSetChanged = false;

            showEmptyView(mTagCloudView.getTagList());
        }
    }

    private void showAddWishDialg() {
        final EditText editText = new EditText(this);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(this.getString(R.string.favorite_add_wish))
            .setView(editText)
            .setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String text = editText.getText().toString();
                        if (text != null && !"".equals(text)) {
                            addWish(text);
                        }
                    }
                }
            ).setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
            );
        alertBuilder.create().show();
    }

    //add a wish
    private void addWish(String title) {
        synchronized (AppApplication.mSQLiteHelper) {
            SQLiteDatabase db = AppApplication.mSQLiteHelper.getWritableDatabase();
            ContentValues contentValue = new ContentValues();
            contentValue.put("title", title);
            contentValue.put("sort", 9999);
            long count = db.insert(AppSQLiteHelper.TABLE_WISH, null, contentValue);
            if (count != -1) {
                //reload the data
                mTagCloudView.setTagList(getTags());
                mTagCloudView.refresh();
                mTagCloudView.invalidate();
                showEmptyView(mTagCloudView.getTagList());
                Toast.makeText(this, "添加成功。", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //clear all wishes
    private void clearWishes() {
        synchronized (AppApplication.mSQLiteHelper) {
            SQLiteDatabase db = AppApplication.mSQLiteHelper.getWritableDatabase();
            int count = db.delete(AppSQLiteHelper.TABLE_WISH, null, null);
            if (count > 0) {
                //reload the data
                mTagCloudView.setTagList(getTags());
                mTagCloudView.refresh();
                mTagCloudView.invalidate();
                showEmptyView(mTagCloudView.getTagList());
                Toast.makeText(this, "清除成功。", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
