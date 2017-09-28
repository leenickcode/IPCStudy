package com.demo.nick.ipcstudy.contentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.demo.nick.ipcstudy.Book;
import com.demo.nick.ipcstudy.R;

public class ProviderAty extends AppCompatActivity {
    private static final String TAG = "ProviderAty";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_aty);
        Uri uri=Uri.parse("content://com.demo.nick.ipcstudy.provider/book");//指定调用哪个provider   content;// + authorities
        ContentValues values=new ContentValues();
        values.put("_id",6);
        values.put("name","java");
        getContentResolver().insert(uri,values);//插入一条数据
        //查询数据
        Cursor bookCursor= getContentResolver().query(uri,null,null,null,null);
        while (bookCursor.moveToNext()){
            Log.e(TAG, "onCreate: "+bookCursor.getInt(0)+"-----"+bookCursor.getString(1) );
        }
    }
}
