package com.example.pad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.pad.databinding.ActivityMainBinding;
import androidx.lifecycle.ViewModelProviders;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    String TAG = "MikeDean";

    ActivityMainBinding binding;
    VisionViewModel viewModel;

    // 计时器
    Timer timer;
    TimerTask prg_task;
    int interval;           // 隔多久执行一次
    Boolean isRunning;      // 是否正在运行
    boolean isFirst = true;

    // Mediaplayer
    MediaPlayer player = new MediaPlayer();

    // 多线程
    Handler handler;

    // 服务相关的
    ServerService controller;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.v(TAG,"启动中");

            controller = ((ServerService.ServiceController) iBinder).getService();
            controller.listen();
            // 设置回调函数
            controller.setReceived(new ServerService.OnDataReceived() {
                @Override
                public void update(int state, String data) {
                    if (!isRunning && isFirst) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                init();
                                start();
                                isFirst = false;
                            }
                        });
                    }
                    // 接收到数据后会被执行
                    int direction = Integer.parseInt(data.split(",")[0]);
                    int num = Integer.parseInt(data.split(",")[1]);
                    viewModel.add2List(direction);
                    update_hand(direction);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(VisionViewModel.class);
        binding.setData(viewModel);
        binding.setLifecycleOwner(this);

        // 获取屏幕dpi
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        //窗口的dpi
        int densitydpi=dm.densityDpi;
        double dpi=densitydpi/25.2;
        viewModel.setDpi(dpi);

        // 隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        Log.v(TAG, "INTENT_EYE:" + intent.getStringExtra("eye"));
        Log.v(TAG, "INTENT_DISTANCE:" + intent.getStringExtra("eye"));
        viewModel.setCurrentEye(Integer.parseInt(intent.getStringExtra("eye")));
        viewModel.setCurrentDistance(Integer.parseInt(intent.getStringExtra("eye")));
        new Thread(new Runnable() {
            @Override
            public void run() {
                player = MediaPlayer.create(MainActivity.this, R.raw.prepare);
                player.start();
            }
        }).start();
        load_data_from_db();
//        init();

//        start();
        setListener();
        init_service();
    }

    // 从数据库中加载必要的配置，比如时长、测试距离等
    private void load_data_from_db(){
        interval = 20;
        viewModel.setDuration(6000);
        isRunning = false;
    }

    private void init(){
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case 1:
                        // 进度条
                        int delta = binding.outProgress.getWidth() * interval / viewModel.getDuration();
                        binding.inProgress.setLayoutParams(new FrameLayout.LayoutParams(
                                binding.inProgress.getWidth()+delta, binding.inProgress.getHeight()
                        ));
                        // 超时
                        if (binding.inProgress.getWidth()>=binding.outProgress.getWidth()){
                            resetProgressBar();
                            viewModel.generateAnswer();
                            update();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        reloadTimer();
        binding.blockEnd.setVisibility(View.INVISIBLE);
        binding.blockLoading.setVisibility(View.INVISIBLE);
    }

    // 设置监听器
    // no use
    private void setListener(){
        binding.imageE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
    }

    // 初始化服务
    private void init_service(){
        Log.v(TAG,"开始启动");
        Intent intent = new Intent(MainActivity.this, ServerService.class);
        boolean result = getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        Log.v(TAG,"启动完毕"+String.valueOf(result));
    }

    // 对进度条重新复位
    private void resetProgressBar(){
        binding.inProgress.setLayoutParams(new FrameLayout.LayoutParams(0, binding.inProgress.getHeight()));
    }

    // 更新图片大小和方向
    private void update(){
        viewModel.generateNext(); // 产生下一组
        if (viewModel.ifFinish()){
            // 测试结束
            stop();
            String data = String.valueOf(viewModel.getFirst().getValue())+"."+String.valueOf(viewModel.getSecond().getValue())+","+(viewModel.getShow_eye().getValue().equals("左眼")?"0":"1");
            controller.send(data);
            Log.v("BroadCast", data);
            binding.blockEnd.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(MainActivity.this, StartActivity.class);
                    intent.putExtra("state","0");
                    getApplicationContext().unbindService(conn);
                    startActivity(intent);
                    finish();
                }
            }).start();
                // 解绑
            return ;
        }
        binding.imageE.setImageResource(viewModel.getCurrentPicId());
        ViewGroup.LayoutParams params = binding.imageE.getLayoutParams();
        params.height = params.width = viewModel.getCurrentPicSize();
        binding.imageE.setLayoutParams(params);
    }

    // 结束要干的事情
    private void stop(){
        isRunning = false;
        timer.cancel();
        resetProgressBar();
    }

    // 做一些初始工作，对全局变量进行赋值
    private void reloadTimer(){
        /*
         * 时间间隔为interval
         * 持续时长为duration
         * 屏幕总宽度width
         * 每次更新的长度为 width/(duration/interval)
         * width * interval / duration
         * */
        timer = new Timer();
        prg_task = new TimerTask() { // 要实现的东西
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
    }

    // 更新手势
    private int[] ANGLE = {0,180,270,90};
    private void update_hand(int direction){
        // 0-上 1-下 2-左 3-右
        binding.imageHand.setRotation(ANGLE[direction]);
    }


    // 开始测试
    private void start(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                player = MediaPlayer.create(MainActivity.this, R.raw.kaishiceshi);
                player.start();
            }
        }).start();
        binding.blockLoading.setVisibility(View.INVISIBLE);
        isRunning = true;
        viewModel.start();
        update();
        viewModel.start();
        reloadTimer();
        timer.schedule(prg_task, 0, interval);
    }
}