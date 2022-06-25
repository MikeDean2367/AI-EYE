package com.example.handtiqu;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class ClientService extends Service {
    String TAG = "ClientService";

    // 回调函数
    // 在MainActivity中覆写，一旦接收到数据则更换UI
    public interface OnDataReceived{
        // state 表示状态，data表示数据
        void update(int state, String data);
    }

    OnDataReceived received;

    ClientData data = new ClientData();
    ConnectionInfo info;
    IConnectionManager mManager;
    SocketActionAdapter socketActionAdapter = new SocketActionAdapter() {
        @Override
        public void onSocketIOThreadStart(String action) {
            super.onSocketIOThreadStart(action);
        }

        @Override
        public void onSocketIOThreadShutdown(String action, Exception e) {
            super.onSocketIOThreadShutdown(action, e);
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            super.onSocketDisconnection(info, action, e);
            //断开连接
            Log.d(TAG, "断开服务器连接");
        }

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            super.onSocketConnectionSuccess(info, action);
            Log.d(TAG, "客户端连接服务器成功,开启心跳");

            //连接成功,开启心跳
            OkSocket.open(info)
                    .getPulseManager()
                    .setPulseSendable(new IPulseSendable() {
                        @Override
                        public byte[] parse() {
                            byte[] body = "pause".getBytes(Charset.defaultCharset()); // 心跳数据
                            ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
                            bb.order(ByteOrder.BIG_ENDIAN);
                            bb.putInt(body.length);
                            bb.put(body);
                            return bb.array();
                        }
                    })
                    .pulse();//开始心跳,开始心跳后,心跳管理器会自动进行心跳触发
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            super.onSocketConnectionFailed(info, action, e);
            Log.d(TAG, "客户端连接服务器失败");
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            // 接收数据
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            Log.d(TAG, "客户端读取数据回调" + str);
            if (mManager != null && str.equals("ack")) {//是否是心跳返回包,需要解析服务器返回的数据才可知道
                Log.d(TAG, "客户端喂狗");//喂狗操作
                mManager.getPulseManager().feed();
            }else{
                // 一旦接受到数据，则更改
                received.update(0, str);
            }
            super.onSocketReadResponse(info, action, data);
        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            Log.d(TAG, "客户端发送数据回调");
            super.onSocketWriteResponse(info, action, data);
        }

        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
            Log.d(TAG, "客户端发送心跳包");
            super.onPulseSend(info, data);
        }
    };

    String IP = "192.168.24.74";
    int PORT = 8080;

    // 提供的方法■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■

    // 1. 设置接收器
    public void setReceived(OnDataReceived received){
        this.received = received;
    }

    // 2. 发送数据
    public void send(String Data){
        data.setData(Data);
        mManager.send(data);
    }

    // 3. 建立连接
    public void connect(){
        // 连接参数设置
        info = new ConnectionInfo(IP,PORT);
        // 调用OkSocket，开启连接的通道，得到通道的Manager
        mManager = OkSocket.open(info);
        // 设置回调函数
        mManager.registerReceiver(socketActionAdapter);
        // 连接
        mManager.connect();
    }

    // 4. 设置IP和PORT
    public void setIPAndPORT(String Ip, int Port){
        this.IP = Ip;
        this.PORT = Port;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ClientController();
    }

    // 获取当前服务的实例
    public class ClientController extends Binder {
        public ClientService getService(){
            return ClientService.this;
        }
    }

    // 发送的数据数据格式
    public class ClientData implements ISendable {
        String data;
        public void setData(String data){
            this.data = data;
        }

        @Override
        public byte[] parse() {
            byte[] payload = data.getBytes(Charset.defaultCharset());
            //4 is package header fixed length and payload length
            ByteBuffer bb = ByteBuffer.allocate(4 + payload.length);
            bb.order(ByteOrder.BIG_ENDIAN);
            bb.putInt(payload.length);
            bb.put(payload);
            return bb.array();
        }
    }
}
