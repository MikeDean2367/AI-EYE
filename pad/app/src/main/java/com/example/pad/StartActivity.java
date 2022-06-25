package com.example.pad;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.pad.databinding.ActivityStartBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.mediapipe.framework.Packet;
import com.google.mediapipe.framework.PacketGetter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class StartActivity extends MPBaseActivity {
    // 常量
    static int MAX_PROGRESS = 100; // 进度条的最大值
    static int DELTA_PROGRESS = 5;
    static int SHOW_TIMES = 5;     // 相同出现几次出现进度条
    static Typeface font;          // 字体
    static String TAG = "MikeDean";
    static AppCompatActivity context;

    // Music
    static MediaPlayer player = new MediaPlayer();

    // binding
    static ActivityStartBinding binding;
    static StartViewModel viewModel;
    static Handler handler;

    ImageView imageQR;
    int PORT = 8080;


    // 服务
    public static ServerService controller;
    public static ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.v(TAG,"启动中");
            controller = ((ServerService.ServiceController) iBinder).getService();
            controller.listen();
            // 设置回调函数
            controller.setReceived(new ServerService.OnDataReceived() {
                @Override
                public void update(int state, String data) {
                    /*
                    * data: "方向,数字"
                    * 方向: 0 1 2 3           6表示没有
                    * 数字: 1 2 3 4 5         6表示没有 0表示握拳
                    * */
                    // 接收到数据后会被执行
                    int direction = Integer.parseInt(data.split(",")[0]);
                    int num = Integer.parseInt(data.split(",")[1]);
                    if (direction==6) direction=-1;
                    if (num==6) num=-1;
                    Message message = new Message();
                    Log.v(TAG, "方向:" + String.valueOf(direction) + "数字:" + String.valueOf(num));
                    Log.v(TAG,"STAGTE:"+String.valueOf(viewModel.getCurStage()));
                    switch (viewModel.getCurStage()){
                        case 0:
                            // 等待连接
                            viewModel.nextStage();
                            play_music();
                            changeBlock();
                            break;
                        case 1:
                            Log.v(TAG,"STAGTE:"+String.valueOf(viewModel.getCurStage()));
                            // 选择左眼还是右眼
                            viewModel.updateDirection(direction);
                            // 0-上 1-下 2-左 3-右
                            if(direction==2) chooseEye(0);
                            else if(direction==3) chooseEye(1);
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        case 2:
                            // 选择测试距离
                            viewModel.updateNum(num);
                            viewModel.setDistance(num);
                            message.what = 2;
                            handler.sendMessage(message);
                            break;
                        case 3:
                            // 测量距离
                            Log.v(TAG,"IN 3");
//                            viewModel.setCurDistance(100);
//                            message.what = 3;
//                            handler.sendMessage(message);
//                            Log.v(TAG,"SEND");
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.v(TAG,"解绑！");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MPBaseActivity.binding;
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_start);

        viewModel = ViewModelProviders.of(this).get(StartViewModel.class);
        binding.setData(viewModel);
        binding.setLifecycleOwner(this);

        if(getIntent()!=null){
            if(getIntent().getExtras()!=null)
                if(getIntent().getExtras().containsKey("state")){
                    viewModel.nextStage();
                    play_music();
                    viewModel.resetKeep();
                }
        }

        // 隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findView();
        init();
        init_service();
        init_measure();
        context = this;
    }

    // Measure-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private static final String FOCAL_LENGTH_STREAM_NAME = "focal_length_pixel";
    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "face_landmarks_with_iris";
    private static final String LEFT_IRIS_DEPTH_MM = "left_iris_depth_mm";
    private static final String RIGHT_IRIS_DEPTH_MM = "right_iris_depth_mm";
    private float rightDepth = 0;
    private float leftDepth = 0;
    private boolean haveAddedSidePackets = false;

    @Override
    protected void onCameraStarted(SurfaceTexture surfaceTexture) {
        super.onCameraStarted(surfaceTexture);

        // onCameraStarted gets called each time the activity resumes, but we only want to do this once.
        if (!haveAddedSidePackets) {
            float focalLength = cameraHelper.getFocalLengthPixels();
            if (focalLength != Float.MIN_VALUE) {
                Packet focalLengthSidePacket = processor.getPacketCreator().createFloat32(focalLength);
                Map<String, Packet> inputSidePackets = new HashMap<>();
                inputSidePackets.put(FOCAL_LENGTH_STREAM_NAME, focalLengthSidePacket);
                processor.setInputSidePackets(inputSidePackets);
            }
            haveAddedSidePackets = true;
        }
    }

    private void init_measure(){
        processor.addPacketCallback(
                RIGHT_IRIS_DEPTH_MM,
                (packet) -> {
                    rightDepth = PacketGetter.getFloat32(packet);
                    if (viewModel.getCurStage()==3){
                        viewModel.setCurDistance((int) ((rightDepth/10+leftDepth/10)/2));
                        Message message = new Message();
                        message.what = 3;
                        handler.sendMessage(message);
                    }
                    Log.v(TAG,"ri: " + rightDepth/10);
                });
        processor.addPacketCallback(
                LEFT_IRIS_DEPTH_MM,
                (packet) -> {
                    leftDepth = PacketGetter.getFloat32(packet);
                    Log.v(TAG,"le " + leftDepth/10);
                });
//        if (Log.isLoggable(TAG, Log.VERBOSE)) {
//            processor.addPacketCallback(
//                    RIGHT_IRIS_DEPTH_MM,
//                    (packet) -> {
//                        rightDepth = PacketGetter.getFloat32(packet);
//                        viewModel.setCurDistance((int) ((rightDepth/10+leftDepth/10)/2));
//                        Log.v(TAG,"right depth in cm is: " + rightDepth/10);
//                    });
//            processor.addPacketCallback(
//                    LEFT_IRIS_DEPTH_MM,
//                    (packet) -> {
//                        leftDepth = PacketGetter.getFloat32(packet);
//                        Log.v(TAG,"left depth in cm is: " + leftDepth/10);
//                    });
//        }
    }

    // 初始化服务--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private void init_service(){
        Log.v(TAG,"正在启动");
        Intent intent = new Intent(StartActivity.this, ServerService.class);
        boolean result = getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        Log.v(TAG,"启动完毕"+String.valueOf(result));
    }

    private void findView(){
        imageQR = findViewById(R.id.imageQR);
    }

    private void init(){

        // 获取当前ip
        String ip = getIPAddress();
        // 生成图片
        if (ip!=null){
            // ip,port
            generateQR(ip+","+String.valueOf(PORT));
        }

        // 获取字体
        font = Typeface.createFromAsset(getAssets(),"iconfont.ttf");

        // 变换UI
        changeBlock();

        // 多线程
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case 0:
                        break;
                    case 1:
                        Log.v(TAG,"HANDLER");
                        if (viewModel.ifKeep("direction")){
                            if (viewModel.getKeepCnt()==SHOW_TIMES){
                                initProgress(R.string.ok);
                            } else if(viewModel.getKeepCnt()>SHOW_TIMES){
                                updateProgress();
                            } else{
                                resetProgress();
                            }
                        }
                        break;
                    case 2:

                        if(viewModel.getDistance()>=1 && viewModel.getDistance()<=3) chooseDistance(viewModel.getDistance());
                        Log.v(TAG,"HANDLER2");
                        if(viewModel.ifKeep("num")){
                            if (viewModel.getKeepCnt()==SHOW_TIMES){
                                initProgress(R.string.ok);
                            }else if(viewModel.getKeepCnt()>SHOW_TIMES){
                                updateProgress();
                            }else{
                                resetProgress();
                            }
                        }
                        break;
                    case 3:
                        Log.v(TAG,"HANDLER3");
                        if(viewModel.ifKeep("measure")){
                            Log.v(TAG,"HANDLER3in");
                            if(viewModel.getKeepCnt()==SHOW_TIMES){
                                initProgress(R.string.ok);
                            }else if(viewModel.getKeepCnt()>SHOW_TIMES){
                                updateProgress();
                            }else{
                                resetProgress();
                            }
                        }
                        break;
                    case 4:
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        intent.putExtra("distance", String.valueOf(viewModel.getSettingDistance().getValue()/100));
                        intent.putExtra("eye",String.valueOf(viewModel.getEyes()));
                        getApplicationContext().unbindService(conn);
//                        stopService(new Intent(StartActivity.this, ServerService.class));
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    // 生成二维码
    public void generateQR(String string){
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(string, BarcodeFormat.QR_CODE, 250, 250);
            int fixbit[]=new int[bitMatrix.getWidth()*bitMatrix.getHeight()];
            for (int y=0;y<bitMatrix.getHeight();y++){
                for (int x=0;x<bitMatrix.getWidth();x++){
                    if (bitMatrix.get(x,y)){
                        fixbit[x+y*bitMatrix.getWidth()]= Color.BLACK;
                    }else{
                        fixbit[x+y*bitMatrix.getWidth()]= Color.WHITE;
                    }
                }
            }
            Bitmap bitmap=Bitmap.createBitmap(fixbit,bitMatrix.getWidth(),bitMatrix.getHeight(), Bitmap.Config.ARGB_8888);
            imageQR.setImageBitmap(bitmap);

        }catch (WriterException e){

        }

    }

    // 切换block
    public static void changeBlock(){
        switch (viewModel.getCurStage()){
            case 0:
                // 等待连接
                binding.blockSign.setVisibility(View.INVISIBLE);
                binding.blockDistance.setVisibility(View.INVISIBLE);
                binding.blockMeasure.setVisibility(View.INVISIBLE);
                binding.blockEye.setVisibility(View.INVISIBLE);
                binding.blockQrcode.setVisibility(View.VISIBLE);
                break;
            case 1:
                // 选择测试的眼睛
                binding.blockSign.setVisibility(View.INVISIBLE);
                binding.blockDistance.setVisibility(View.INVISIBLE);
                binding.blockMeasure.setVisibility(View.INVISIBLE);
                binding.blockEye.setVisibility(View.VISIBLE);
                binding.blockQrcode.setVisibility(View.INVISIBLE);
                break;
            case 2:
                // 选择距离
                binding.blockSign.setVisibility(View.INVISIBLE);
                binding.blockDistance.setVisibility(View.VISIBLE);
                binding.blockMeasure.setVisibility(View.INVISIBLE);
                binding.blockEye.setVisibility(View.INVISIBLE);
                binding.blockQrcode.setVisibility(View.INVISIBLE);
                break;
            case 3:
                // 测量距离
                binding.blockSign.setVisibility(View.INVISIBLE);
                binding.blockDistance.setVisibility(View.INVISIBLE);
                binding.blockMeasure.setVisibility(View.VISIBLE);
                binding.blockEye.setVisibility(View.INVISIBLE);
                binding.blockQrcode.setVisibility(View.INVISIBLE);
                break;
            case 4:
                // 切换activity
                Message message = new Message();
                message.what=4;
                handler.sendMessage(message);
                break;
        }
    }

    // 选择左眼还是右眼
    @SuppressLint("ResourceAsColor")
    public static void chooseEye(int eyes){
        if(eyes==0){
            // 左眼
            Log.v(TAG, "左");
            binding.textLeft.setBackgroundResource(R.color.teal_700);
            binding.textLeft.setTextColor(R.color.white);
            binding.textRight.setBackgroundResource(R.color.white);
            binding.textRight.setTextColor(R.color.teal_700);
        }else{
            // 右眼
            Log.v(TAG, "右");
            binding.textRight.setBackgroundResource(R.color.teal_700);
            binding.textRight.setTextColor(R.color.white);
            binding.textLeft.setBackgroundResource(R.color.white);
            binding.textLeft.setTextColor(R.color.teal_700);
        }
        viewModel.setEyes(eyes);
    }

    // 选择距离
    @SuppressLint("ResourceAsColor")
    public static void chooseDistance(int distance){
        switch (distance){
            case 1:
                binding.choose1Text1.setTextColor(R.color.white);
                binding.choose2Text1.setTextColor(R.color.teal_700);
                binding.choose3Text1.setTextColor(R.color.teal_700);
                binding.choose1Text2.setTextColor(R.color.white);
                binding.choose2Text2.setTextColor(R.color.teal_700);
                binding.choose3Text2.setTextColor(R.color.teal_700);

                binding.choose1.setBackgroundResource(R.color.teal_700);
                binding.choose2.setBackgroundResource(R.color.white);
                binding.choose3.setBackgroundResource(R.color.white);
                viewModel.setSettingDistance(100);
                break;
            case 2:
                binding.choose1Text1.setTextColor(R.color.teal_700);
                binding.choose2Text1.setTextColor(R.color.white);
                binding.choose3Text1.setTextColor(R.color.teal_700);
                binding.choose1Text2.setTextColor(R.color.teal_700);
                binding.choose2Text2.setTextColor(R.color.white);
                binding.choose3Text2.setTextColor(R.color.teal_700);

                binding.choose1.setBackgroundResource(R.color.white);
                binding.choose2.setBackgroundResource(R.color.teal_700);
                binding.choose3.setBackgroundResource(R.color.white);
                viewModel.setSettingDistance(200);
                break;
            case 3:
                binding.choose1Text1.setTextColor(R.color.teal_700);
                binding.choose2Text1.setTextColor(R.color.teal_700);
                binding.choose3Text1.setTextColor(R.color.white);
                binding.choose1Text2.setTextColor(R.color.teal_700);
                binding.choose2Text2.setTextColor(R.color.teal_700);
                binding.choose3Text2.setTextColor(R.color.white);

                binding.choose1.setBackgroundResource(R.color.white);
                binding.choose2.setBackgroundResource(R.color.white);
                binding.choose3.setBackgroundResource(R.color.teal_700);
                viewModel.setSettingDistance(300);
                break;
            default:
                break;
        }


    }

    // 隐藏进度条并重置
    public static void resetProgress(){
        Log.v(TAG, "重置进度条");
        binding.prgSign.setProgress(0);                     // 重置
//        binding.prgSign.setIndicatorDirection(CircularProgressIndicator.INDICATOR_DIRECTION_CLOCKWISE);
        binding.blockSign.setVisibility(View.INVISIBLE);
    }

    // 展示进度条
    public static void initProgress(int ResID){
        Log.v(TAG, "初始化进度条");
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.blockSign.setVisibility(View.VISIBLE);
                binding.sign.setTypeface(font);
                binding.sign.setText(ResID);
                binding.prgSign.setProgress(0);
            }
        });
    }

    // 更新进度条
    public static void updateProgress(){
        binding.prgSign.setIndeterminate(false);
        Log.v(TAG, "进度条+1:"+String.valueOf(binding.prgSign.getProgress()));
        if (binding.blockSign.getVisibility()==View.INVISIBLE) {
            Log.v(TAG,"显示");
            binding.blockSign.setVisibility(View.VISIBLE);
        }
        binding.sign.setVisibility(View.VISIBLE);
        binding.prgSign.setVisibility(View.VISIBLE);

        if (binding.prgSign.getProgress()+DELTA_PROGRESS<=MAX_PROGRESS){
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.prgSign.setProgress(binding.prgSign.getProgress()+DELTA_PROGRESS);
                }
            });

        }
        else{
            Log.v(TAG, "下一段");
            viewModel.resetKeep();
            viewModel.nextStage();
            play_music();
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resetProgress();
                    changeBlock();
                }
            });
        }
    }

    // 获取本地IP地址
    /**获得IP地址，分为两种情况，一是wifi下，二是移动网络下，得到的ip地址是不一样的*/
    private String getIPAddress() {
        Context context = StartActivity.this;
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //调用方法将int转换为地址字符串
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }
    /**
     * 将得到的int类型的IP转换为String类型
     * @param ip
     * @return
     */
    String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    static void play_music(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                switch (viewModel.getCurStage()){
                    case 0:
                        break;
                    case 1:
                        player = MediaPlayer.create(context, R.raw.choose_eye);
                        player.start();
                        break;
                    case 2:
                        player = MediaPlayer.create(context, R.raw.choose_distance);
                        player.start();
                        break;
                    case 3:
                        player = MediaPlayer.create(context, R.raw.meaturing);
                        player.start();
                        break;
                    default:
                        break;
                }
            }
        });
        thread.start();
    }

}
