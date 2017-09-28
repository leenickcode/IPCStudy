package com.demo.nick.ipcstudy.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nick on 2017/9/26.
 * author:nicklxz
 * email:nick_lxz@163.com
 */

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="book_provider.db";
    public static  final String BOOK_TABLE_NAME="book";
    public static final String USER_TABLE_NAME="user";
    private static final int DB_VERSION=1;
    //图书和用户信息表
    private String CREATE_BOOK_TABLE="CREATE TABLE IF NOT EXISTS "+
            BOOK_TABLE_NAME+"(_id INTEGER PRIMARY KEY,"+"name Text)";
    private String CREATE_USER_TABLE="CREATE TABLE IF NOT EXISTS "+
            USER_TABLE_NAME+"(_id INTEGER PRIMARY KEY,"+"name Text,"+"sex INT)";

      public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
            db.execSQL(CREATE_BOOK_TABLE);
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
