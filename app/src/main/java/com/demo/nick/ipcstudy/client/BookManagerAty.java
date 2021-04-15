package com.demo.nick.ipcstudy.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.demo.nick.ipcstudy.Book;
import com.demo.nick.ipcstudy.IBookManager;
import com.demo.nick.ipcstudy.IOnNewBookArrivedListener;
import com.demo.nick.ipcstudy.R;
import com.demo.nick.ipcstudy.services.BookManagerService;

import java.util.List;

/**
 * Created by Nick on 2017/9/22.
 * author:nicklxz
 * email:nick_lxz@163.com
 */

public class BookManagerAty extends AppCompatActivity {
    private static final String TAG = "BookManagerAty";
    private static final int MESSAGE_NEW_BOOK_ARRIVED=1;
    private IBookManager mRemoteBookManager;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.e(TAG, "new BOOK: "+msg.obj );
                break;
            default:
                super.handleMessage(msg);
            }

        }
    };
    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected: "+"绑定成功" );
            IBookManager bookManager=IBookManager.Stub.asInterface(service);
            try {
                mRemoteBookManager=bookManager;
                List<Book> list=bookManager.getBookList();
                Log.e(TAG, "onServiceConnected: "+list.getClass().getCanonicalName());
                Log.e(TAG, "onServiceConnected: "+list.toString() );
                Book book=new Book(3,"java");
                bookManager.addBook(book);
                List<Book> newList=bookManager.getBookList();
                Log.e(TAG, "onServiceConnected: "+newList.toString() );
                bookManager.registerListener(mOnNewBookArrivedLisener);//注册监听
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteBookManager=null;
            Log.e(TAG, "onServiceDisconnected: " );
        }
    };
    private IOnNewBookArrivedListener mOnNewBookArrivedLisener=new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED,book).sendToTarget();
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager_aty);
        Intent intent=new Intent(this, BookManagerService.class);
        startActivity(intent);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mRemoteBookManager!=null&&mRemoteBookManager.asBinder().isBinderAlive()){
            Log.e(TAG, "onDestroy: "+mOnNewBookArrivedLisener );
            try {
                mRemoteBookManager.unregisterListener(mOnNewBookArrivedLisener);//解除监听
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
