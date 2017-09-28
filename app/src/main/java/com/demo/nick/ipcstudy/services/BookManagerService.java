package com.demo.nick.ipcstudy.services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.demo.nick.ipcstudy.IBookManager;
import com.demo.nick.ipcstudy.Book;
import com.demo.nick.ipcstudy.IOnNewBookArrivedListener;
import com.demo.nick.ipcstudy.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Nick on 2017/9/22.
 * author:nicklxz
 * email:nick_lxz@163.com
 */

public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();//书本集合
    //客户端监听器集合 beginBroadcast和finishBroadcast必须配对使用
   private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList=new RemoteCallbackList<>();
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);//是否销毁的标记
//    private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList<>();
    //实现我们刚刚创建的AIDL接口
    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
//            if (!mListenerList.contains(listener)) {
//                mListenerList.add(listener);
//            }else {
//                Log.e(TAG, "already exists: " );
//            }
            mListenerList.register(listener);
            int a=mListenerList.beginBroadcast();
            Log.e(TAG, "当前监听器个数 "+a );
            mListenerList.finishBroadcast();
//            Log.e(TAG, "registerListener监听器个数: "+mListenerList.size());
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
//       if(mListenerList.contains(listener)){
//           mListenerList.remove(listener);
//           Log.e(TAG, "解除成功: " );
//       }else {
//           Log.e(TAG, "没有可解除的: " );
//       }
//            Log.e(TAG, "当前监听器个数 "+mListenerList.size() );
//        }
            mListenerList.unregister(listener);
            int a=mListenerList.beginBroadcast();
            Log.e(TAG, "当前监听器个数 "+a );
            mListenerList.finishBroadcast();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "android"));
        mBookList.add(new Book(2, "ios"));
        new Thread(new ServiceWorker()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //验证权限，自定义个权限，防止其他人连接我们的服务.
        int check=checkCallingOrSelfPermission("com.demo.nick.ipcstudy.permission.TEST");
        if (check== PackageManager.PERMISSION_DENIED){
            //没有权限返回空，这样就绑定不了我们服务了。
            return null;
        }
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed.set(true);
        super.onDestroy();
    }

    private void  onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        final int N=mListenerList.beginBroadcast();
        for (int i=0;i<N;i++){
            IOnNewBookArrivedListener listener=mListenerList.getBroadcastItem(i);
            if (listener !=null) {
                listener.onNewBookArrived(book);
            }

        }
        mListenerList.finishBroadcast();
    }

    private class ServiceWorker implements Runnable{

        @Override
        public void run() {
            //循环添加新书籍
            while (!mIsServiceDestoryed.get()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId=mBookList.size()+1;
                Book newBook=new Book(bookId,"new book"+bookId);
                try {
                    onNewBookArrived(newBook
                    );
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

