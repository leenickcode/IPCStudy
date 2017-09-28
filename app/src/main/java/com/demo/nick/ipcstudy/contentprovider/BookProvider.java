package com.demo.nick.ipcstudy.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Nick on 2017/9/26.
 * author:nicklxz
 * email:nick_lxz@163.com
 */

public class BookProvider extends ContentProvider {
    private static final String TAG = "BookProvider";
    public static final String AUTHORITY="com.demo.nick.ipcstudy.provider";
    public static  final Uri BOOK_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/book");
    public static  final Uri USER_CONTENTURI=Uri.parse("content://"+AUTHORITY+"/user");
    public static final int BOOK_URI_CODE=0;
    public static final  int USER_URI_CODE=1;
    private static  final UriMatcher sUriMather=new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMather.addURI(AUTHORITY,"book",BOOK_URI_CODE);
        sUriMather.addURI(AUTHORITY,"user",USER_URI_CODE);
    }
    private Context mContext;
    private SQLiteDatabase mDb;
    @Override
    public boolean onCreate() {
        Log.e(TAG, "onCreate: "+Thread.currentThread() );
        mContext=getContext();
        initData();
        return true;
    }

    /**
     * 初始化一些数据
     */
    private void initData(){
        mDb=new DbOpenHelper(mContext,"test",null,1).getWritableDatabase();
        mDb.execSQL("delete from "+DbOpenHelper.BOOK_TABLE_NAME);
        mDb.execSQL("delete from "+DbOpenHelper.USER_TABLE_NAME);
        mDb.execSQL("insert into book values(3,'Android');");
        mDb.execSQL("insert into book values(4,'Ios');");
        mDb.execSQL("insert into book values(5,'h5')");
    }
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.e(TAG, "query: "+Thread.currentThread() );
        String table=getTableName(uri);
        if (table==null){
            Log.e(TAG, "uri错误: " );
            throw new IllegalArgumentException("uri错误");
        }
        return mDb.query(table,projection,selection,selectionArgs,null,null,sortOrder,null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.e(TAG, "getType: "+Thread.currentThread() );
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.e(TAG, "insert: "+Thread.currentThread() );
        String table=getTableName(uri);
        if (table==null){
            throw new IllegalArgumentException("表不存在");
        }
        mDb.insert(table,null,values);
        mContext.getContentResolver().notifyChange(uri,null);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.e(TAG, "delete: "+Thread.currentThread() );
        String table=getTableName(uri);
        if (table==null){
            throw new IllegalArgumentException("表不存在");
        }
        int count=mDb.delete(table,selection,selectionArgs);
        if (count>0){
            mContext.getContentResolver().notifyChange(uri,null);
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.e(TAG, "update: "+Thread.currentThread() );
        return 0;
    }
    private String getTableName(Uri uri){
        String tableName=null;
        switch (sUriMather.match(uri)){
            case BOOK_URI_CODE:
                Log.e(TAG, "BOOK_URI_CODE" );
                tableName=DbOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                Log.e(TAG, "USER_URI_CODE" );
                tableName=DbOpenHelper.USER_TABLE_NAME;
            break;
            default:break;
        }
        return tableName;
    }
}
