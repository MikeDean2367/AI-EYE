package com.example.vision;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.example.vision.common.BackListener;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.framework.Packet;
import com.google.mediapipe.framework.PacketGetter;
import com.skydoves.transformationlayout.TransformationLayout;
import com.skydoves.transformationlayout.TransitionExtensionKt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MeasureActivity extends MediapipeBaseActivity {

    private static final String TAG = "measureActivity";
    TextView textView;
    private static final String FOCAL_LENGTH_STREAM_NAME = "focal_length_pixel";
    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "face_landmarks_with_iris";
    private static final String LEFT_IRIS_DEPTH_MM = "left_iris_depth_mm";
    private static final String RIGHT_IRIS_DEPTH_MM = "right_iris_depth_mm";
    private float rightDepth = 0;
    private float leftDepth = 0;
    TextView []textViews = new TextView[7];
    ImageView img_background, img_face;
    ImageButton btn_back;


    boolean ISFACE = true;
    //    boolean ISCONNECTED=false;
    static int cnt=0;

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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            //do something.
            finish();
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


    @Override
    protected void onDestroy() {
        Log.v("test","销毁开始");
        super.onDestroy();

    }


    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TransformationLayout.Params params = getIntent().getParcelableExtra("TransformationParams");
        TransitionExtensionKt.onTransformationEndContainer(this, params);

        super.onCreate(savedInstanceState);

        Log.v(TAG,"SUCCESS");
        FrameLayout frameLayout = findViewById(R.id.preview_display_layout);
        frameLayout.setVisibility(View.INVISIBLE);

        init();

        processor.addPacketCallback(
                RIGHT_IRIS_DEPTH_MM,
                (packet) -> {
                    ISFACE=true;
                    img_background.setBackgroundColor(Color.parseColor("#ADC0A9"));
                    img_face.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.face, null));
                    rightDepth = PacketGetter.getFloat32(packet);
                    Log.v(TAG,"ri: " + rightDepth/10);
                });
        processor.addPacketCallback(
                LEFT_IRIS_DEPTH_MM,
                (packet) -> {
                    ISFACE=true;
                    leftDepth = PacketGetter.getFloat32(packet);
                    Log.v(TAG,"le " + leftDepth/10);
                    send();
                });
        context = this;
    }

    //初始化
    void init(){
        textViews[1] = findViewById(R.id.text_distance);
        textViews[2] = findViewById(R.id.text_left);
        textViews[3] = findViewById(R.id.text_right);
        textViews[4] = findViewById(R.id.text_leftDistance);
        textViews[5] = findViewById(R.id.text_rightDistance);
        textViews[6] = findViewById(R.id.text_tip);
        img_background = findViewById(R.id.img_background);
        img_face = findViewById(R.id.img_faceState);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new BackListener());
    }

    boolean b = false;
    Thread listen(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                b=false;
                while(ISFACE==true){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.v(TAG,"++++++++++++++++++++++++");
                    MeasureActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            img_background.setBackgroundColor(Color.parseColor("#ADC0A9"));
                            img_face.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.face, null));
                        }
                    });
                    b=true;
                    break;
                }
            }
        });
        thread.start();
        return thread;
    }

    //对是否有脸进行判断
    void face(){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                while (true) {
                try {
                    listen();
                    long pre = System.currentTimeMillis()/1000;
                    while(b==false&&System.currentTimeMillis()/1000-pre<=2){

                    }
                    if(b==false){//已经检测不到人脸
                        MeasureActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                img_background.setBackgroundColor(Color.parseColor("#FF7F50"));
                                textViews[6].setText("未检测到人脸");
                                img_face.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.no_face, null));
                            }
                        });
                    }else{
                        MeasureActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                img_background.setBackgroundColor(Color.parseColor("#ADC0A9"));
                                img_face.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.face, null));
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void send(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //更新手机段UI
                MeasureActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViews[1].setText(String.valueOf((int)((leftDepth+rightDepth)/20))+" CM");
                        textViews[4].setText(String.valueOf((int)(leftDepth/10))+" CM");
                        textViews[5].setText(String.valueOf((int)(rightDepth/10))+" CM");
                        if(Math.abs(leftDepth-rightDepth)>=40){
                            textViews[6].setText("请保持与屏幕平行");
                        }else{
                            textViews[6].setText("请保持当前姿势");
                        }
                    }
                });
                ISFACE = false;
                face();
            }
        }).start();
    }


}
