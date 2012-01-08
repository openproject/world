package com.tianxia.app.floworld;

import android.content.Context;

import com.tianxia.lib.baseworld.db.BaseSQLiteHelper;

public class AppSQLiteHelper extends BaseSQLiteHelper {

    public AppSQLiteHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void InitCreateSql() {
        mCreateSql = "create table if not exists favorite("
                   + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "type INTEGER,"
                   + "title TEXT,"
                   + "url TEXT,"
                   + "description TEXT)";
    }
}
