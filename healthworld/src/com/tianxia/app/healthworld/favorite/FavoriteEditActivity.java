package com.tianxia.app.healthworld.favorite;

import android.app.AlertDialog;

import android.content.ContentValues;
import android.content.DialogInterface;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.AppSQLiteHelper;
import com.tianxia.app.healthworld.model.WishInfo;
import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.activity.BaseActivity;
import com.tianxia.lib.baseworld.widget.DragListView;
import com.tianxia.lib.baseworld.widget.DragListAdapter;
import com.tianxia.lib.baseworld.widget.TagCloudView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteEditActivity extends BaseActivity implements DragListView.OnDropListener {

    private DragListView mListView = null;
    private TextView mItemTextView = null;
    private ImageView mItemDragView = null;

    private List<WishInfo> mListData;
    private DragListAdapter<WishInfo> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.favorite_edit_activity);
        mListView = (DragListView) findViewById(R.id.favorite_edit_list);

        setTagList();
    }

    /*
     * query the tags to listData
     * */
    private void setTagList() {

        mListData = new ArrayList<WishInfo>();

        synchronized (AppApplication.mSQLiteHelper) {
            SQLiteDatabase db = AppApplication.mSQLiteHelper.getReadableDatabase();
            Cursor cursor = null;
            try {
                cursor = db.query(AppSQLiteHelper.TABLE_WISH, null, null, null, null, null, "sort");
                if (cursor.moveToFirst()) {
                    do {
                        WishInfo wishInfo = new WishInfo();
                        wishInfo._id = cursor.getLong(cursor.getColumnIndex("_id"));
                        wishInfo.title = cursor.getString(cursor.getColumnIndex("title"));
                        wishInfo.sort = cursor.getInt(cursor.getColumnIndex("sort"));
                        mListData.add(wishInfo);
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

        adapter = new DragListAdapter<WishInfo>(this, mListData);
        mListView.setAdapter(adapter);
        mListView.setDropListener(this);
    }

    @Override
    public void onDrop (int srcPosition, int destPosition) {
        @SuppressWarnings("unchecked")
        WishInfo dragItem = adapter.getItem(srcPosition);
        //删除原位置数据项
        adapter.remove(dragItem);
        //在新位置插入拖动项
        adapter.insert(dragItem, destPosition);

        updatePosition();
    }

    public void updatePosition () {
        synchronized (AppApplication.mSQLiteHelper) {
            SQLiteDatabase db = AppApplication.mSQLiteHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                for (int i = 0; i < mListData.size(); i++) {
                    WishInfo wishInfo = mListData.get(i);
                    ContentValues contentValue = new ContentValues();
                    contentValue.put("sort", i * 100);
                    String where = "_id = " + wishInfo._id;
                    db.update(AppSQLiteHelper.TABLE_WISH, contentValue, where, null);
                }
                db.setTransactionSuccessful();
                FavoriteTabActivity.mDataSetChanged = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }
}
