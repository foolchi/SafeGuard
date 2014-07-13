package com.foolchi.safeguard.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foolchi.safeguard.domain.AppInfo;
import com.foolchi.safeguard.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by foolchi on 7/11/14.
 * App lock data
 */
public class AppLockDao {
    private DBHelper dbHelper;

    public AppLockDao(Context context){
        dbHelper = new DBHelper(context);
    }

    public boolean find(String packageName){
        boolean result = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (db.isOpen()){
            Cursor cursor = db.rawQuery("select packagename from applock where packagename = ?", new String[]{packageName});
            if (cursor.moveToNext()){
                result = true;
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    public void add(String packageName){
        if (find(packageName)){
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()){
            db.execSQL("insert into applock (packagename) values (?)", new Object[]{packageName});
            db.close();
        }
    }

    public void delete(String packageName){
        if (!find(packageName)){
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()){
            db.execSQL("delete from applock where packagename = ?", new Object[]{packageName});
            db.close();
        }
    }

    public List<String> getAllPackageName(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> packageNames = new ArrayList<String>();

        if (db.isOpen()){
            Cursor cursor = db.rawQuery("select packagename from applock", null);
            while (cursor.moveToNext()){
                packageNames.add(cursor.getString(0));
            }
            cursor.close();
            db.close();
        }

        return packageNames;
    }

}
