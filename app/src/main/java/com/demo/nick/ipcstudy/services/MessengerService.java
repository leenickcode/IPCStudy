package com.demo.nick.ipcstudy.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.demo.nick.ipcstudy.utils.MyConstants;

/**
 * Created by Nick on 2017/9/22.
 * author:nicklxz
 * email:nick_lxz@163.com
 * Messenger方式
 */

public class MessengerService extends Service {
    private static final String TAG = "MessengerService";
    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyConstants.MSG_CLIENT:
                    Log.e(TAG, "客户端: " + msg.getData().getString("msg"));
                    Messenger client = msg.replyTo;//得到客户端传递的Messenger对象。
                    Message replyMsg = Message.obtain(null, MyConstants.MSG_SERVICE);//创建Message对象
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", "你好啊，客户端");
                    replyMsg.setData(bundle);
                    try {
                        client.send(replyMsg);//向客户端发送消息
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
