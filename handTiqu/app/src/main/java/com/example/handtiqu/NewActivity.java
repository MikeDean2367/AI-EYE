package com.example.handtiqu;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.formats.proto.RectProto;
import com.google.mediapipe.framework.Packet;
import com.google.mediapipe.framework.PacketCallback;
import com.google.mediapipe.framework.PacketGetter;

import java.util.List;

public class NewActivity extends MediaPipeBaseActivity{

    // Variable
    String TAG = "MikeDean";
    //0-STOP 1-DIRECTION 2-NUMBER 3-BOTH
    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "multi_hand_landmarks";
    private static final String OUTPUT_HAND_RECT = "multi_hand_rects";
    private List<LandmarkProto.NormalizedLandmarkList> multiHandLandmarks;
    private EditText txtIp;
    private TextView gesture;
    private TextView moveGesture;
    private ImageView img_hand_num;
    private ImageView img_hand_direction;
    private ImageButton btn_back;
    private Drawable[]imgDirection=new Drawable[5];
    private Drawable []imgNum = new Drawable[6];

    private String IP;
    private int PORT;


    // SendBroadCast
    private String BROADCAST_NAME = "com.example.pad.VISION_DATA";

    // 服务
    // 服务的控制器
    ClientService controller;
    // 服务的连接时候的回调
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            controller = ((ClientService.ClientController) iBinder).getService();
            controller.setIPAndPORT(IP, PORT);
            controller.connect();
            // 设置回调函数，该函数当收到数据时会被调用
            controller.setReceived(new ClientService.OnDataReceived() {
                @Override
                public void update(int state, String data) {
                    Intent intent = new Intent(BROADCAST_NAME);
                    intent.putExtra("data",data);
                    sendBroadcast(intent);
                    Log.v("BroadCast", "send!" + data);
//                    receive_text.setText(data);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    // onCreate----------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // bundle
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null) Log.v("MikeDean1","ok");
        else Log.v("MikeDean1","no");
        if(bundle!=null){
            IP = bundle.getString("CONNECT_IP");
            PORT = Integer.valueOf(bundle.getString("CONNECT_PORT"));
            Log.v("测试_1","IP:"+IP+" PORT:"+PORT);
        }

        findView();
        init_service();
        /*
         * data: "方向,数字"
         * 方向: 0 1 2 3           6表示没有
         * 数字: 1 2 3 4 5         6表示没有 0表示握拳
         * */
        // 0-上 1-下 2-左 3-右
        processor.addPacketCallback(
                OUTPUT_LANDMARKS_STREAM_NAME,
                (packet) -> {
                    Log.d(TAG, "Received multi-hand landmarks packet.");
                    multiHandLandmarks = PacketGetter.getProtoVector(packet, LandmarkProto.NormalizedLandmarkList.parser());
                    runOnUiThread(new Runnable() {
                        @SuppressLint("NewApi")
                        @Override
                        public void run() {
                            String s1=handGestureCalculator(multiHandLandmarks);        // number
                            String s2=handDirectionCalculator(multiHandLandmarks);      // direction
                            controller.send(s2+","+s1);
                            //手势
                            Log.v("测试",s1+" "+s2);
                            gesture.setText(s1);
                            try{
                                img_hand_num.setBackground(imgNum[Integer.parseInt(s1)-1]);
                            }catch (Exception e){
                                img_hand_num.setBackground(imgNum[5]);
                                gesture.setText("无");
                            }
                            //方向
                            // 0-上 1-下 2-左 3-右
                            if(s2.equals("0")){
                                moveGesture.setText("上");
                                img_hand_direction.setBackground(imgDirection[0]);
                            }else if(s2.equals("1")){
                                moveGesture.setText("下");
                                img_hand_direction.setBackground(imgDirection[1]);
                            }else if(s2.equals("2")){
                                moveGesture.setText("左");
                                img_hand_direction.setBackground(imgDirection[2]);
                            }else if(s2.equals("3")){
                                moveGesture.setText("右");
                                img_hand_direction.setBackground(imgDirection[3]);
                            }else{
                                moveGesture.setText("无");
                                img_hand_direction.setBackground(imgDirection[4]);
                            }
//                            moveGesture.setText(ans);
                        }
                    });
                    Log.d(
                            TAG,
                            "[TS:"
                                    + packet.getTimestamp()
                                    + "] "
                                    + getMultiHandLandmarksDebugString(multiHandLandmarks));
                });
        processor.addPacketCallback(
                OUTPUT_HAND_RECT
                , new PacketCallback() {
                    @Override
                    public void process(Packet packet) {

                        List<RectProto.NormalizedRect> normalizedRectsList = PacketGetter.getProtoVector(packet, RectProto.NormalizedRect.parser());

                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    moveGesture.setText(handGestureMoveCalculator(normalizedRectsList));
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private void findView(){
        gesture = findViewById(R.id.gesture);
        moveGesture = findViewById(R.id.move_gesture);
        img_hand_direction = findViewById(R.id.img_hand_direction);
        img_hand_num = findViewById(R.id.img_hand_num);
        imgDirection[0] = getDrawable(R.drawable.fup);
        imgDirection[1] = getDrawable(R.drawable.fdown);
        imgDirection[2] = getDrawable(R.drawable.fleft);
        imgDirection[3] = getDrawable(R.drawable.fright);
        imgDirection[4] = getDrawable(R.drawable.fno);
        imgNum[0] = getDrawable(R.drawable.f1);
        imgNum[1] = getDrawable(R.drawable.f2);
        imgNum[2] = getDrawable(R.drawable.f3);
        imgNum[3] = getDrawable(R.drawable.f4);
        imgNum[4] = getDrawable(R.drawable.f5);
        imgNum[5] = getDrawable(R.drawable.fno);
        btn_back = findViewById(R.id.btn_back3);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Instrumentation inst = new Instrumentation();
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                    }
                }).start();
            }
        });
    }

    private void init_service(){
        Intent intent = new Intent(NewActivity.this, ClientService.class);
        boolean result = getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    // OtherFunction-----------------------------------------------------------------------------------------------------------------------------------------

    private String getMultiHandLandmarksDebugString(List<LandmarkProto.NormalizedLandmarkList> multiHandLandmarks) {
        if (multiHandLandmarks.isEmpty()) {
            return "No hand landmarks";
        }
        String multiHandLandmarksStr = "Number of hands detected: " + multiHandLandmarks.size() + "\n";
        int handIndex = 0;
        for (LandmarkProto.NormalizedLandmarkList landmarks : multiHandLandmarks) {
            multiHandLandmarksStr +=
                    "\t#Hand landmarks for hand[" + handIndex + "]: " + landmarks.getLandmarkCount() + "\n";
            int landmarkIndex = 0;
            for (LandmarkProto.NormalizedLandmark landmark : landmarks.getLandmarkList()) {
                multiHandLandmarksStr +=
                        "\t\tLandmark ["
                                + landmarkIndex
                                + "]: ("
                                + landmark.getX()
                                + ", "
                                + landmark.getY()
                                + ", "
                                + landmark.getZ()
                                + ")\n";
                ++landmarkIndex;
            }
            ++handIndex;
        }
        return multiHandLandmarksStr;
    }

    private String handGestureCalculator(List<LandmarkProto.NormalizedLandmarkList> multiHandLandmarks) {
        /*
         * data: "方向,数字"
         * 方向: 0 1 2 3           6表示没有
         * 数字: 1 2 3 4 5         6表示没有 0表示握拳
         * */
        if (multiHandLandmarks.isEmpty()) {
            return "6";
        }
        boolean thumbIsOpen = false;
        boolean firstFingerIsOpen = false;
        boolean secondFingerIsOpen = false;
        boolean thirdFingerIsOpen = false;
        boolean fourthFingerIsOpen = false;

        for (LandmarkProto.NormalizedLandmarkList landmarks : multiHandLandmarks) {

            List<LandmarkProto.NormalizedLandmark> landmarkList = landmarks.getLandmarkList();
            float pseudoFixKeyPoint = landmarkList.get(2).getX();
            if (pseudoFixKeyPoint < landmarkList.get(9).getX()) {
                if (landmarkList.get(3).getX() < pseudoFixKeyPoint && landmarkList.get(4).getX() < pseudoFixKeyPoint) {
                    thumbIsOpen = true;
                }
            }
            if (pseudoFixKeyPoint > landmarkList.get(9).getX()) {
                if (landmarkList.get(3).getX() > pseudoFixKeyPoint && landmarkList.get(4).getX() > pseudoFixKeyPoint) {
                    thumbIsOpen = true;
                }
            }
            Log.d(TAG, "pseudoFixKeyPoint == " + pseudoFixKeyPoint + "\nlandmarkList.get(2).getX() == " + landmarkList.get(2).getX()
                    + "\nlandmarkList.get(4).getX() = " + landmarkList.get(4).getX());
            pseudoFixKeyPoint = landmarkList.get(6).getY();
            if (landmarkList.get(7).getY() < pseudoFixKeyPoint && landmarkList.get(8).getY() < landmarkList.get(7).getY()) {
                firstFingerIsOpen = true;
            }else{
                firstFingerIsOpen = false;
            }
            pseudoFixKeyPoint = landmarkList.get(10).getY();
            if (landmarkList.get(11).getY() < pseudoFixKeyPoint && landmarkList.get(12).getY() < landmarkList.get(11).getY()) {
                secondFingerIsOpen = true;
            }else{
                secondFingerIsOpen = false;
            }
            pseudoFixKeyPoint = landmarkList.get(14).getY();
            if (landmarkList.get(15).getY() < pseudoFixKeyPoint && landmarkList.get(16).getY() < landmarkList.get(15).getY()) {
                thirdFingerIsOpen = true;
            }else{
                thirdFingerIsOpen = false;
            }
            pseudoFixKeyPoint = landmarkList.get(18).getY();
            if (landmarkList.get(19).getY() < pseudoFixKeyPoint && landmarkList.get(20).getY() < landmarkList.get(19).getY()) {
                fourthFingerIsOpen = true;
            }else{
                fourthFingerIsOpen = false;
            }

            // Hand gesture recognition
            if (thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen) {
                return "5";
            } else if (!thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen) {
                return "4";
            } else if (thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen ||
                    !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && !fourthFingerIsOpen) {
                return "3";
            } else if (thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen) {
                return "2";
            } else if (!thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen) {
                return "1";
            } else if (!thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen) {
                return "2";
            } else if (!thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen) {
                return "6";
//                return "ROCK";
            } else if (thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen) {
//                return "Spider-Man";
                return "6";
            } else if (!thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen) {
                return "0";
            } else if (!firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && isThumbNearFirstFinger(landmarkList.get(4), landmarkList.get(8))) {
                return "3";
            } else {
                String info = "thumbIsOpen " + thumbIsOpen + "firstFingerIsOpen" + firstFingerIsOpen
                        + "secondFingerIsOpen" + secondFingerIsOpen +
                        "thirdFingerIsOpen" + thirdFingerIsOpen + "fourthFingerIsOpen" + fourthFingerIsOpen;
                Log.d(TAG, "handGestureCalculator: == " + info);
                return "6";
            }
        }
        return "___";
    }


    private boolean isThumbNearFirstFinger(LandmarkProto.NormalizedLandmark point1, LandmarkProto.NormalizedLandmark point2) {
        double distance = getEuclideanDistanceAB(point1.getX(), point1.getY(), point2.getX(), point2.getY());
        return distance < 0.1;
    }

    private double getEuclideanDistanceAB(double a_x, double a_y, double b_x, double b_y) {
        double dist = Math.pow(a_x - b_x, 2) + Math.pow(a_y - b_y, 2);
        return Math.sqrt(dist);
    }

    private double getAngleABC(double a_x, double a_y, double b_x, double b_y, double c_x, double c_y) {
        double ab_x = b_x - a_x;
        double ab_y = b_y - a_y;
        double cb_x = b_x - c_x;
        double cb_y = b_y - c_y;

        double dot = (ab_x * cb_x + ab_y * cb_y);   // dot product
        double cross = (ab_x * cb_y - ab_y * cb_x); // cross product

        return Math.atan2(cross, dot);
    }

    private int radianToDegree(double radian) {
        return (int) Math.floor(radian * 180. / Math.PI + 0.5);
    }
    private float max(float x1,float x2, float x3){
        float m;
        m = (x1 > x2) ? x1 : x2;
        return (m > x3) ? m : x3;
    }

    private String handDirectionCalculator(List<LandmarkProto.NormalizedLandmarkList> multiHandLandmarks){
        /*
         * data: "方向,数字"
         * 方向: 0 1 2 3           6表示没有
         * 数字: 1 2 3 4 5         6表示没有 0表示握拳
         * */
        // 0-上 1-下 2-左 3-右
        for (LandmarkProto.NormalizedLandmarkList landmarks : multiHandLandmarks) {
            List<LandmarkProto.NormalizedLandmark> landmarkList = landmarks.getLandmarkList();
            float x_new = (landmarkList.get(5).getX() + landmarkList.get(17).getX()) / 2;
            float y_new = (landmarkList.get(5).getY() + landmarkList.get(17).getY()) / 2;
            float x = landmarkList.get(0).getX();
            float y = landmarkList.get(0).getY();
            float sign_x = x - x_new;
            float sign_y = y - y_new;
            if(sign_x==0){
                //上 下
                if(y_new>y) return "0";
                else return "1";
//                if(y_new>y) return "up";
//                else return "down";
            }else{
                float slope=(y_new-y)/(x_new-x);
                if ((slope > 1 || slope < -1) && (sign_y < 0)){// down
                    return "1";
                }else if ((slope > 1 || slope < -1) && (sign_y > 0)){// up
                    return "0";
                }else if ((slope < 1 && slope > -1) && (sign_x > 0)){// left
                    return "2";
                }else if ((slope < 1 && slope > -1) && (sign_x < 0)) {// right
                    return "3";
                }
            }
        }
        return "6";
    }
}
