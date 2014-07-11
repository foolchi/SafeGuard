package com.foolchi.safeguard.dao;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.foolchi.safeguard.utils.DBHelper;
/**
 * Created by foolchi on 7/5/14.
 */
public class BlackNumberDao {
    private DBHelper dbHelper;

    public BlackNumberDao(Context context){
        dbHelper = new DBHelper(context);
    }

    public boolean find(String number){
        boolean result = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()){
            Cursor cursor = db.rawQuery("select number from blacknumber where number = ?", new String[]{number});
            if (cursor.moveToNext()){
                result = true;
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    public void add(String number){
        if (find(number)){
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()){
            db.execSQL("insert into blacknumber (number) value (?)", new Object[]{number});
            db.close();
        }
    }

    public void delete(String number){
        if (!find(number))
            return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()){
            db.execSQL("delete from blacknumber where number = ?", new Object[] {number});
            db.close();
        }
    }

    public void update(String oldNumber, String newNumber){
        if (!find(oldNumber))
            return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()){
            db.execSQL("update blacknumber set number = ? where number = ?", new Object[] {newNumber, oldNumber});
            db.close();
        }
    }

    public List<String> findAll(){
        List<String> numbers = new ArrayList<String>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()){
            Cursor cursor = db.rawQuery("select number from blacknumber", null);
            while (cursor.moveToNext()){
                numbers.add(cursor.getString(0));
            }
            cursor.close();
            db.close();
        }
        return numbers;
    }
}
