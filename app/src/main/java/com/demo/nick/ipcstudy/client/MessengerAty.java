package com.demo.nick.ipcstudy.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.demo.nick.ipcstudy.R;
import com.demo.nick.ipcstudy.services.MessengerService;
import com.demo.nick.ipcstudy.utils.MyConstants;

/**
 * messenger方式
 */
public class MessengerAty extends AppCompatActivity {
    private static final String TAG = "MessengerAty";
    private Messenger mService;//得到服务端的messenger对象，用来给服务端发送消息。
    private Messenger mMessenger=new Messenger(new MessengerHandler());//传递给服务端自身的Messenger对象，用与服务端与自己交互
    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定成功后调用
            mService=new Messenger(service);//通过服务端返回的binder创建Messenger对象。
            Message msg=Message.obtain(null, MyConstants.MSG_CLIENT);
            Bundle data=new Bundle();
            data.putString("msg","hello服务端");
            msg.setData(data);
            msg.replyTo=mMessenger;//把自身的Messenger对象通过message传递给服务端
            try {
                mService.send(msg);//发送消息
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 用来处理服务端发送的消息
     */
    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MyConstants.MSG_SERVICE:
                    Log.e(TAG, "服务端: "+msg.getData().getString("msg") );
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger_aty);
        Intent intent=new Intent(this, MessengerService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);//绑定服务端

    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);//解绑
        super.onDestroy();
    }
}
