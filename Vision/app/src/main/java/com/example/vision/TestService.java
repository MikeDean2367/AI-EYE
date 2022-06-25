package com.example.vision;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.Random;

public class TestService extends Service {
    // 每时刻产生一个随机数

    // 回调函数
    public interface OnProgressListener {
        void onProgress(int progress);
    }

    public TestService() {}

    private int num=0;
    private OnProgressListener listener;
    private Random random=new Random();

    public void setListener(OnProgressListener listener){
        this.listener = listener;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // 返回一个Binder对象，主要是返回给主程序，用于控制这里的内容
        return binder;
    }

    public final IBinder binder = new TestBinder();
    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void startRandom(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    num = random.nextInt(100);  // 生成一个0~99的随机整数
                    if (listener!=null) listener.onProgress(num);
                }
            }
        }).start();
    }

    public class TestBinder extends Binder{
        // 获取当前Service的实例
        public TestService getService(){
            return TestService.this;
        }
    }
}
