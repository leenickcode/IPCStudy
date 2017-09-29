package com.demo.nick.ipcstudy.socket;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by Nick on 2017/9/28.
 * author:nicklxz
 * email:nick_lxz@163.com
 */

public class TCPServerService extends Service {
    private static final String TAG = "TCPServerService";
    private boolean mIsServiceDestoryed=false;

    private String[] mDefinedMessages=new String[]{
            "你好啊",
            "hello",
            "今天天气不错",
            "学无止境"
    };
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: " );
        new Thread(new TcpServer()).start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private class TcpServer implements Runnable{
        @SuppressLint("resource")
        @Override
        public void run() {
            ServerSocket serverSocket=null;
            try {
                serverSocket=new ServerSocket(8668);//监听端口
                Log.e(TAG, "run: 监听端口");
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!mIsServiceDestoryed){
                try {
                    final Socket client=serverSocket.accept();//有链接则会返回，没有则会阻塞
                    Log.e(TAG, "accept: ");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                responseClient(client);//链接成功就响应客户端
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 响应客户端
     * @param client
     * @throws IOException
     */
    private void responseClient(Socket client) throws IOException {
        BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));//得到输入流，用来读取客户端发送过来的消息
        PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);//创建输出流，
        out.println("欢迎来到聊天室");
        while (!mIsServiceDestoryed){
            String st=in.readLine();//读取消息
            System.out.println(st);
            if (st==null){
                //客户端断开连接
                break;
            }
            int i=new Random().nextInt(mDefinedMessages.length);
            String msg=mDefinedMessages[i];
            out.println(msg);//发消息给客户端
            System.out.println(msg);
        }
        out.close();
        in.close();
        client.close();
    }
}
