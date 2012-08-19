package com.tianxia.app.healthworld;

import android.content.Context;

import com.tianxia.lib.baseworld.db.BaseSQLiteHelper;

public class AppSQLiteHelper extends BaseSQLiteHelper {

    public static final String TABLE_WISH = "wish";

    public AppSQLiteHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void InitCreateSql() {
        mCreateSql = "create table if not exists " + TABLE_WISH + "("
                   + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "title TEXT,"
                   + "sort float)";
    }
}
