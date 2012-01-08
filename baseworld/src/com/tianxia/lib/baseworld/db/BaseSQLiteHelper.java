package com.tianxia.lib.baseworld.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseSQLiteHelper extends SQLiteOpenHelper {

    public String mCreateSql;
    public String mUpgradeSql;

    public BaseSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void InitCreateSql() {}

    public void InitUpgradeSql() {}

    @Override
    public void onCreate(SQLiteDatabase db) {
        InitCreateSql();
        db.execSQL(mCreateSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        InitUpgradeSql();
        db.execSQL(mUpgradeSql);
    }

}
