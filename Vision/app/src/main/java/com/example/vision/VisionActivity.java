package com.example.vision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.vision.common.BackListener;
import com.example.vision.databinding.ActivityVisionBinding;
import com.skydoves.transformationlayout.TransformationLayout;
import com.skydoves.transformationlayout.TransitionExtensionKt;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class VisionActivity extends AppCompatActivity {

    String TAG = "MikeDean";

    // music
    public SoundPool sp; // 声明SoundPool的引用
    public HashMap<Integer, Integer> hm; // 声明一个HashMap来存放声音文件
    public int currStreamId;// 当前正播放的streamId
    public final int MaxSight=13;

    // 类名: layout名称+Binding
    ActivityVisionBinding binding;
    VisionViewModel viewModel;

    // 多线程
    Handler handler;

    // 计时器
    Timer timer;
    TimerTask prg_task;
    int interval;           // 隔多久执行一次

    // 手势识别器
    GestureDetector detector;
    Boolean isRunning;

    private double getStatusBarHeight(Context context){
        return Math.ceil(25 * context.getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // transition
        TransformationLayout.Params paramss = getIntent().getParcelableExtra("TransformationParams");
        TransitionExtensionKt.onTransformationEndContainer(this, paramss);

        // 加载布局文件
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vision);

//        binding.textTitle.setPadding(binding.textTitle.getLeft(), (int)(getStatusBarHeight(this)*2),
//                binding.textTitle.getRight(),binding.textTitle.getBottom());
//        binding.btnBack.setPadding(binding.btnBack.getLeft(),binding.textTitle.getPaddingTop(),
//                binding.btnBack.getRight(),binding.btnBack.getBottom());

        // 设置状态栏的颜色为深色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        viewModel = ViewModelProviders.of(this).get(VisionViewModel.class);
        binding.setData(viewModel);
        binding.setLifecycleOwner(this);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        //窗口的dpi
        int densitydpi=dm.densityDpi;
        int ScreeenHeight=dm.heightPixels;
        int ScreenWidth=dm.widthPixels;
        double dpi=densitydpi/25.2;
        viewModel.setDpi(dpi);

        load_data_from_db();
        init();
        setListener();


    }

    // 从数据库中加载必要的配置，比如时长、测试距离等
    private void load_data_from_db(){
        interval = 20;
        viewModel.setDuration(6000);
        isRunning = false;
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

    // 初始化声音池
    public void initSoundPool() {
        sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 0); // 创建SoundPool对象
        hm = new HashMap<Integer, Integer>(); // 创建HashMap对象
        hm.put(1, sp.load(this, R.raw.myvoice, 1)); // 加载声音文件musictest并且设置为1号声音放入hm中
    }
    // 播放声音
    public void playSound(int sound, int loop) { // 获取AudioManager引用
        AudioManager am = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        // 获取当前音量
        float streamVolumeCurrent = am
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        // 获取系统最大音量
        float streamVolumeMax = am
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 计算得到播放音量
        float volume = streamVolumeCurrent / streamVolumeMax;
        // 调用SoundPool的play方法来播放声音文件
        currStreamId = sp.play(hm.get(sound), volume, volume, 1, loop, 1.0f);
    }

    private void init(){
        handler = new Handler(){
            public void handleMessage(@NonNull Message message) {
                switch (message.what){
                    case 1:
                        // 进度条
                        int delta = binding.outProcess.getWidth() * interval / viewModel.getDuration();
//                        Log.v(TAG,String.valueOf(binding.inProcess.getWidth()));
//                        binding.inProcess.setBackgroundColor(Color.parseColor("#AAAAAA"));
                        binding.inProcess.setLayoutParams(new FrameLayout.LayoutParams(
                                binding.inProcess.getWidth()+delta, binding.inProcess.getHeight()
                        ));
                        // 超时
                        if (binding.inProcess.getWidth()>=binding.outProcess.getWidth()){
                            resetProgressBar();
                            viewModel.checkAnswer(-1);
                            update();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        reloadTimer();
        // 手势检测
        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (isRunning==false) return false;
                // e1: 手势起点 e2: 当前手势点 x: 每秒钟x轴移动的像素点 y: 每秒钟y轴移动的像素点
                // 可以在这里放声音

                float deltaX = e1.getX() - e2.getX();
                float deltaY = e1.getY() - e2.getY();
                int direction = -1;
                if (Math.abs(deltaX)>Math.abs(deltaY) && deltaX>0){
                    // 左滑
                    Log.v(TAG, "Left");
                    direction = 2;
                }else if(Math.abs(deltaX)>Math.abs(deltaY) && deltaX<=0){
                    // 右滑
                    Log.v(TAG, "Right");
                    direction = 3;
                }else if(Math.abs(deltaX)<=Math.abs(deltaY) && deltaY>0){
                    // 上滑
                    Log.v(TAG, "Up");
                    direction = 0;
                }else{
                    // 下滑
                    Log.v(TAG, "Down");
                    direction = 1;
                }
                viewModel.checkAnswer(direction);
                resetProgressBar();
                update();
                return false;
            }
        });
        initSoundPool();
        //
        viewModel.useAudio = MainActivity.settingConfig.getUseAudio();
        if(viewModel.useAudio==false) binding.visionSetting.button2.setImageResource(R.drawable.volume_down);
        else binding.visionSetting.button2.setImageResource(R.drawable.volume_up);
        viewModel.setCurrentDistance(MainActivity.settingConfig.getDistance());
    }

    // 对进度条重新复位
    private void resetProgressBar(){
        binding.inProcess.setLayoutParams(new FrameLayout.LayoutParams(0, binding.inProcess.getHeight()));
    }

    // 更新图片大小和方向
    private void update(){
        viewModel.generateNext(); // 产生下一组
        if (viewModel.ifFinish()){
            stop();
            return ;
        }
        binding.imageView.setImageResource(viewModel.getCurrentPicId());
        ViewGroup.LayoutParams params = binding.imageView.getLayoutParams();
        params.height = params.width = viewModel.getCurrentPicSize();
        binding.imageView.setLayoutParams(params);
        // play music
        if(viewModel.useAudio){
            playSound(1, 0);
        }
    }

    // 结束要干的事情
    private void stop(){
        isRunning = false;
        timer.cancel();
        binding.visionStart.setVisibility(View.VISIBLE);
        resetProgressBar();
        binding.visionStart.setVisibility(View.VISIBLE); // 按钮消失，开始测试
        binding.imageView.setVisibility(View.INVISIBLE);
        binding.imgLeftEye.setClickable(true);
        binding.imgRightEye.setClickable(true);
        binding.visionSetting.button.setClickable(true);
        save("0", String.valueOf(viewModel.getCurrentEye()), viewModel.getFirst().getValue()+"."+viewModel.getSecond().getValue(), String.valueOf(System.currentTimeMillis()));
    }

    // save data
    private void save(String type,String eyes, String value, String TimeStamp){
        Log.v(TAG, "Save:" + type + "," + eyes + "," + value + "," + TimeStamp);
        MainActivity.visionDBManager.insert(TimeStamp, type, eyes, value);
    }

    private void start(){
        binding.visionStart.setVisibility(View.INVISIBLE); // 按钮消失，开始测试
        binding.imageView.setVisibility(View.VISIBLE);
        binding.imgLeftEye.setClickable(false);
        binding.imgRightEye.setClickable(false);
        binding.visionSetting.button.setClickable(false);
        isRunning = true;
    }

    private void setListener(){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.vision_start: // 单击开始按钮
                        start();
                        viewModel.start();  // 为了得到尺寸大小
                        update();
                        viewModel.start();  // 使得计数器清0
                        reloadTimer();      // 计数器一旦cancel了就需要重新赋值
                        timer.schedule(prg_task, 0, interval);
                        break;
                    case R.id.imgLeftEye:
                        binding.imgLeftEye.setImageResource(R.drawable.eye_check);
                        binding.imgRightEye.setImageResource(R.drawable.eye_uncheck);
                        binding.visionSetting.button3.setImageResource(R.drawable.left_eye);
                        viewModel.setCurrentEye(0);
                        break;
                    case R.id.imgRightEye:
                        binding.imgLeftEye.setImageResource(R.drawable.eye_uncheck);
                        binding.imgRightEye.setImageResource(R.drawable.eye_check);
                        binding.visionSetting.button3.setImageResource(R.drawable.right_eye);
                        viewModel.setCurrentEye(1);
                        break;
                    default:
                        break;
                }
            }
        };
        binding.visionStart.setOnClickListener(listener);
        binding.imgLeftEye.setOnClickListener(listener);
        binding.imgRightEye.setOnClickListener(listener);

        // helps
        binding.visionSetting.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewModel.getCurrentEye()==1)
                {
                    ToastUtil.makeText(VisionActivity.this,"已切换到左眼");
                    viewModel.setCurrentEye(0);
                    binding.imgLeftEye.setImageResource(R.drawable.eye_check);
                    binding.imgRightEye.setImageResource(R.drawable.eye_uncheck);
                    //Toast.makeText(MainActivity.this,,Toast.LENGTH_SHORT).show();
                    binding.visionSetting.button3.setImageResource(R.drawable.left_eye);
                } else {
                    ToastUtil.makeText(VisionActivity.this,"已切换到右眼");
                    viewModel.setCurrentEye(1);
                    binding.imgLeftEye.setImageResource(R.drawable.eye_uncheck);
                    binding.imgRightEye.setImageResource(R.drawable.eye_check);
                    binding.visionSetting.button3.setImageResource(R.drawable.right_eye);
                }
            }
        });
        binding.visionSetting.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1=new AlertDialog.Builder(VisionActivity.this);
                builder1.setTitle("请选择您想要进行测试时的距离");
                final String[] Distance=new String[]{"0.5","0.8","1.0"};

                builder1.setSingleChoiceItems(Distance, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                viewModel.setCurrentDistance(0);
                                Log.d(TAG,"0");
                                break;
                            case 1:
                                viewModel.setCurrentDistance(1);
                                Log.d(TAG,"1");
                                break;
                            case 2:
                                viewModel.setCurrentDistance(2);
                                Log.d(TAG,"2");
                        }
                        dialog.dismiss();
                    }
                });
                builder1.setCancelable(false);
                builder1.show();

            }
        });
        binding.visionSetting.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewModel.useAudio==false) {
                    // Toast.makeText(MainActivity.this,"已切换到有音频",Toast.LENGTH_SHORT).show();
                    ToastUtil.makeText(VisionActivity.this,"已切换到有音频");
                    binding.visionSetting.button2.setImageResource(R.drawable.volume_up);
                    viewModel.useAudio = true;
                }
                else
                {
                    ToastUtil.makeText(VisionActivity.this,"已切换到无音频");
                    //Toast.makeText(MainActivity.this,"已切换到无音频",Toast.LENGTH_SHORT).show();
                    binding.visionSetting.button2.setImageResource(R.drawable.volume_down);
                    viewModel.useAudio = false;
                }
            }
        });
        binding.visionSetting.button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(VisionActivity.this);
                builder.setTitle("小贴士");
                builder.setMessage(R.string.vision_help);
                builder.setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(VisionActivity.this,"那么请开始测试吧！",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        // back
        binding.btnBack.setOnClickListener(new BackListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }
}