package com.demo.nick.ipcstudy.socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.demo.nick.ipcstudy.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nick on 2017/9/28.
 * author:nicklxz
 * email:nick_lxz@163.com
 */

public class TCPClientActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TCPClientActivity";
    private static final int MESSAGE_RECEVIE_NEW_MES=1;
    private static final int MESSAGE_SOCKET_CONNECTED=2;
    private Button mSendButton;
    private TextView mMessageTextView;
    private EditText mMessageEditText;
    private PrintWriter mPrintWriter;//通过这个与服务端发送消息
    private Socket mClientSocket;
    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_RECEVIE_NEW_MES:
                    mMessageTextView.setText(mMessageTextView.getText()+(String)msg.obj);
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    mSendButton.setEnabled(true);
                    break;
                default:
                    break;
            }

        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp_client);
        mMessageTextView= (TextView) findViewById(R.id.tv_text);
        mSendButton= (Button) findViewById(R.id.bt_button);
        mSendButton.setOnClickListener(this);
        mMessageEditText= (EditText) findViewById(R.id.et_edittext);
        Intent service=new Intent(this,TCPServerService.class);
        startService(service);
        new Thread(){
            @Override
            public void run() {
                connectTCPServer();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if (mClientSocket!=null){
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
    @Override
    public void onClick(View v) {
        if (v==mSendButton){
                    final String msg=mMessageEditText.getText().toString();
                    if (!TextUtils.isEmpty(msg)&&mPrintWriter!=null){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //网络通信不能在ui线程中
                                mPrintWriter.println(msg);
                            }
                        }).start();
                        mMessageEditText.setText("");
                        String time=formatDateTime(System.currentTimeMillis());
                        final  String showedMsg="self"+time+":"+msg+"\n";
                        mMessageTextView.setText(mMessageTextView.getText()+showedMsg);
                    }

        }
    }
    @SuppressLint("SimpleDateFormat")
    private String formatDateTime(long time){
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
    }

    /**
     * 连接服务端
     */
    private void connectTCPServer(){
        Socket socket=null;
        while (socket==null){
            try {
                socket=new Socket("localhost",8668);//创建socket并且连接服务器
                mClientSocket=socket;
                mPrintWriter=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);//先获取输出流
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                System.out.println("连接服务器");
            } catch (IOException e) {
                SystemClock.sleep(2000);
                e.printStackTrace();
                Log.e(TAG, "connectTCPServer: 重连" );
            }
        }
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));//获取输入流，用来读取服务端发送的消息
            while (!TCPClientActivity.this.isFinishing()){
                Log.e(TAG, "循环？: " );
                String msg=br.readLine();
//                System.out.println("service:"+msg);
                if (msg!=null){
                    String time=formatDateTime(System.currentTimeMillis());
                    final  String showedMsg="server"+time+":"+msg+"\n";
                    mHandler.obtainMessage(MESSAGE_RECEVIE_NEW_MES,showedMsg).sendToTarget();
                }
            }
            System.out.println("quit....");
            mPrintWriter.close();
            br.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
