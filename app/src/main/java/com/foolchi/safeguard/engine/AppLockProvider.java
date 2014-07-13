package com.foolchi.safeguard.engine;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.foolchi.safeguard.dao.AppLockDao;
/**
 * Created by foolchi on 7/13/14.
 */
public class AppLockProvider extends ContentProvider {

    private static final int INSERT = 1;
    private static final int DELETE = 0;
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String content = "content://com.foolchi.safeguard.applockprovider";
    private static Uri uri = Uri.parse(content);
    private AppLockDao dao;

    static {
        matcher.addURI(content, "insert", INSERT);
        matcher.addURI(content, "delete", DELETE);
    }
    @Override
    public boolean onCreate() {
        dao = new AppLockDao(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int result = matcher.match(uri);
        if (result == INSERT){
            String packageName = contentValues.getAsString("packageName");
            dao.add(packageName);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        else {
            new IllegalArgumentException("URI is not correct");
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int result = matcher.match(uri);
        if (result == DELETE){
            String packageName = strings[0];
            dao.delete(packageName);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        else {
             new IllegalArgumentException("URI is not correct");
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
