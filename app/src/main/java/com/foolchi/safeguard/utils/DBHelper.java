package com.foolchi.safeguard.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by foolchi on 7/5/14.
 */
public class DBHelper extends SQLiteOpenHelper{

    public DBHelper(Context context) {
        super(context, "security.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table blacknumber (_id integer primary key autoincrement number varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
