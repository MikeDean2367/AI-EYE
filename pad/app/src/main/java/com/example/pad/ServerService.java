package com.example.pad;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.dispatcher.IRegister;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClient;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientIOCallback;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientPool;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerActionListener;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerManager;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerShutdown;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class ServerService extends Service {
    // 成员变量
    String TAG = "ServerService";
    IRegister<IServerActionListener, IServerManager> server;
    IServerManager serverManager;
    ServerData data = new ServerData();
    OnDataReceived received;
    IClientPool pool;
    int PORT = 8080; // 端口
    IServerActionListener serverActionListener;

    // 回调函数
    public interface OnDataReceived{
        // state 表示状态，data表示数据
        void update(int state, String data);
    }

    // 提供的方法
    // 1. 设置回调函数
    public void setReceived(OnDataReceived received){
        this.received = received;
    }

    // 2. 设置端口
    public void setPort(int Port){
        this.PORT = Port;
    }

    // 3. 监听
    public void listen(){
        server = OkSocket.server(PORT);
        serverManager = server.registerReceiver(serverActionListener);
        serverManager.listen();
    }

    // 4. 发送数据
    public void send(String Data){
        data.setData(Data);
        pool = serverManager.getClientPool();
        pool.sendToAll(data);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG,"绑定中");
        serverActionListener = new IServerActionListener() {
            @Override
            public void onServerListening(int serverPort) {
                Log.d(TAG,"服务器启动完成.正在监听端口:" + serverPort);
            }
            @Override
            public void onClientConnected(IClient client, int serverPort, IClientPool clientPool) {

                Log.d(TAG, client.getUniqueTag() + " 客户端已连接");

                client.addIOCallback(new IClientIOCallback() {
                    @Override
                    public void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool) {

                        String str = new String(originalData.getBodyBytes(), Charset.forName("utf-8"));
                        if(str.equals("pause")){ //是否是心跳返回包,若是心跳包则发送应答包
                            Log.d(TAG,"收到客户端"+client.getUniqueTag()+"的心跳数据："+str);

                            client.send(new ISendable() {
                                @Override
                                public byte[] parse() {
                                    byte[] body = "ack".getBytes(Charset.defaultCharset()); // 心跳响应数据
                                    ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
                                    bb.order(ByteOrder.BIG_ENDIAN);
                                    bb.putInt(body.length);
                                    bb.put(body);
                                    return bb.array();
                                }
                            });
                        }else {
                            received.update(0, str);
                            Log.d(TAG,"收到客户端"+str+"的指令");
                        }
                    }
                    @Override
                    public void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool) {
                        Log.d(TAG,"发送数据到客户端:"+new String(sendable.parse(),Charset.forName("utf-8")));
                    }
                });

            }
            @Override
            public void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool) {
                Log.d(TAG, client.getUniqueTag() + " 客户端已断开连接");
                client.removeAllIOCallback();
            }
            @Override
            public void onServerWillBeShutdown(int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable) {
                Log.d(TAG, "服务器即将关闭");
            }
            @Override
            public void onServerAlreadyShutdown(int serverPort) {
                Log.d(TAG, "服务器已经关闭,serverPort="+serverPort);
            }
        };
        return new ServiceController();
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(TAG,"重新绑定");

        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG,"解除绑定");
        serverManager.shutdown();
        return super.onUnbind(intent);
    }

    // 控制器
    public class ServiceController extends Binder {
        public ServerService getService(){
            return ServerService.this;
        }
    }

    // 发送数据的数据格式
    public class ServerData implements ISendable {
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
