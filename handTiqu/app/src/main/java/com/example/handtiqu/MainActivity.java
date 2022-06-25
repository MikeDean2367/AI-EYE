package com.example.handtiqu;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList;
import com.google.mediapipe.formats.proto.RectProto;
import com.google.mediapipe.framework.Packet;
import com.google.mediapipe.framework.PacketCallback;
import com.google.mediapipe.framework.PacketGetter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Main activity of MediaPipe multi-hand tracking app.
 */
public class MainActivity extends MediaPipeBaseActivity {
    private static final String TAG = "MainActivity";
    private static int STATUS_OF_SOCKET=3;
    //0-STOP 1-DIRECTION 2-NUMBER 3-BOTH
    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "multi_hand_landmarks";
    private static final String OUTPUT_HAND_RECT = "multi_hand_rects";
    private List<NormalizedLandmarkList> multiHandLandmarks;
    private EditText txtIp;
    private TextView gesture;
    private TextView moveGesture;
    private ImageView img_hand_num;
    private ImageView img_hand_direction;
    private Drawable[]imgDirection=new Drawable[5];
    private Drawable []imgNum = new Drawable[6];

    TextView msg;
    String message = "";
    String CONNECT_PORT;
    String CONNECT_IP;
    ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CONNECT_PORT = "0";
        CONNECT_IP = "1.1.1.1";
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null) Log.v("MikeDean1","ok");
        else Log.v("MikeDean1","no");
        if(bundle!=null){
            CONNECT_IP = bundle.getString("CONNECT_IP");
            CONNECT_PORT = bundle.getString("CONNECT_PORT");
            Log.v("测试_1","IP:"+CONNECT_IP+" PORT:"+CONNECT_PORT);
        }
        gesture = findViewById(R.id.gesture);
        moveGesture = findViewById(R.id.move_gesture);
//        txtIp = findViewById(R.id.txtIp);
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
        //new_code

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();

        processor.addPacketCallback(
                OUTPUT_LANDMARKS_STREAM_NAME,
                (packet) -> {
                    Log.d(TAG, "Received multi-hand landmarks packet.");
                    multiHandLandmarks =
                            PacketGetter.getProtoVector(packet, NormalizedLandmarkList.parser());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String s1=handGestureCalculator(multiHandLandmarks);
                            String s2=handDirectionCalculator(multiHandLandmarks);
                            MessageSender messageSender=new MessageSender();
//                            String []ip=txtIp.getText().toString().split("\\.");
//                            boolean flag=true;
//                            if(ip.length!=4) flag=false;
//                            String ans=txtIp.getText().toString();
                            String ans = CONNECT_IP;
//                            if(flag==true) ans=txtIp.getText().toString();
                            if(STATUS_OF_SOCKET==3) messageSender.execute(ans + "#" + s1+" "+s2,CONNECT_PORT);
                            else if(STATUS_OF_SOCKET==2) messageSender.execute(ans+ "#" + s1,CONNECT_PORT);
                            else if(STATUS_OF_SOCKET==1) messageSender.execute(ans+ "#" + s2,CONNECT_PORT);
//                            String bool="false";
//                            if(flag==true) bool="false";
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
                            if(s2.equals("up")){
                                moveGesture.setText("上");
                                img_hand_direction.setBackground(imgDirection[0]);
                            }else if(s2.equals("down")){
                                moveGesture.setText("下");
                                img_hand_direction.setBackground(imgDirection[1]);
                            }else if(s2.equals("left")){
                                moveGesture.setText("左");
                                img_hand_direction.setBackground(imgDirection[2]);
                            }else if(s2.equals("right")){
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

    private String getMultiHandLandmarksDebugString(List<NormalizedLandmarkList> multiHandLandmarks) {
        if (multiHandLandmarks.isEmpty()) {
            return "No hand landmarks";
        }
        String multiHandLandmarksStr = "Number of hands detected: " + multiHandLandmarks.size() + "\n";
        int handIndex = 0;
        for (NormalizedLandmarkList landmarks : multiHandLandmarks) {
            multiHandLandmarksStr +=
                    "\t#Hand landmarks for hand[" + handIndex + "]: " + landmarks.getLandmarkCount() + "\n";
            int landmarkIndex = 0;
            for (NormalizedLandmark landmark : landmarks.getLandmarkList()) {
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

    private String handGestureCalculator(List<NormalizedLandmarkList> multiHandLandmarks) {
        if (multiHandLandmarks.isEmpty()) {
            return "No hand deal";
        }
        boolean thumbIsOpen = false;
        boolean firstFingerIsOpen = false;
        boolean secondFingerIsOpen = false;
        boolean thirdFingerIsOpen = false;
        boolean fourthFingerIsOpen = false;

        for (NormalizedLandmarkList landmarks : multiHandLandmarks) {

            List<NormalizedLandmark> landmarkList = landmarks.getLandmarkList();
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
                return "ROCK";
            } else if (thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen) {
                return "Spider-Man";
            } else if (!thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen) {
                return "fist";
            } else if (!firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && isThumbNearFirstFinger(landmarkList.get(4), landmarkList.get(8))) {
                return "3";
            } else {
                String info = "thumbIsOpen " + thumbIsOpen + "firstFingerIsOpen" + firstFingerIsOpen
                        + "secondFingerIsOpen" + secondFingerIsOpen +
                        "thirdFingerIsOpen" + thirdFingerIsOpen + "fourthFingerIsOpen" + fourthFingerIsOpen;
                Log.d(TAG, "handGestureCalculator: == " + info);
                return "___";
            }
        }
        return "___";
    }

    float previousXCenter;
    float previousYCenter;
    float previousAngle; // angle between the hand and the x-axis. in radian
    float previous_rectangle_width;
    float previousRectangleHeight;
    boolean frameCounter;

    private String handGestureMoveCalculator(List<RectProto.NormalizedRect> normalizedRectList) {

        RectProto.NormalizedRect normalizedRect = normalizedRectList.get(0);
        float height = normalizedRect.getHeight();
        float centerX = normalizedRect.getXCenter();
        float centerY = normalizedRect.getYCenter();
        if (previousXCenter != 0) {
            double mouvementDistance = getEuclideanDistanceAB(centerX, centerY,
                    previousXCenter, previousYCenter);
            // LOG(INFO) << "Distance: " << mouvementDistance;

            double mouvementDistanceFactor = 0.02; // only large mouvements will be recognized.

            // the height is normed [0.0, 1.0] to the camera window height.
            // so the mouvement (when the hand is near the camera) should be equivalent to the mouvement when the hand is far.
            double mouvementDistanceThreshold = mouvementDistanceFactor * height;
            if (mouvementDistance > mouvementDistanceThreshold) {
                double angle = radianToDegree(getAngleABC(centerX, centerY,
                        previousXCenter, previousYCenter, previousXCenter + 0.1,
                        previousYCenter));
                // LOG(INFO) << "Angle: " << angle;
                if (angle >= -45 && angle < 45) {
                    return "Scrolling right";
                } else if (angle >= 45 && angle < 135) {
                    return "Scrolling up";
                } else if (angle >= 135 || angle < -135) {
                    return "Scrolling left";
                } else if (angle >= -135 && angle < -45) {
                    return "Scrolling down";
                }
            }
        }

        previousXCenter = centerX;
        previousYCenter = centerY;
        // 2. FEATURE - Zoom in/out
        if (previousRectangleHeight != 0) {
            double heightDifferenceFactor = 0.03;

            // the height is normed [0.0, 1.0] to the camera window height.
            // so the mouvement (when the hand is near the camera) should be equivalent to the mouvement when the hand is far.
            double heightDifferenceThreshold = height * heightDifferenceFactor;
            if (height < previousRectangleHeight - heightDifferenceThreshold) {
                return "Zoom out";
            } else if (height > previousRectangleHeight + heightDifferenceThreshold) {
                return "Zoom in";
            }
        }
        previousRectangleHeight = height;
        // each odd Frame is skipped. For a better result.
        frameCounter = !frameCounter;
        if (frameCounter && multiHandLandmarks != null) {

            for (NormalizedLandmarkList landmarks : multiHandLandmarks) {

                List<NormalizedLandmark> landmarkList = landmarks.getLandmarkList();
                NormalizedLandmark wrist = landmarkList.get(0);
                NormalizedLandmark MCP_of_second_finger = landmarkList.get(9);

                // angle between the hand (wirst and MCP) and the x-axis.
                double ang_in_radian =
                        getAngleABC(MCP_of_second_finger.getX(), MCP_of_second_finger.getY(),
                                wrist.getX(), wrist.getY(), wrist.getX() + 0.1, wrist.getY());
                int ang_in_degree = radianToDegree(ang_in_radian);
                // LOG(INFO) << "Angle: " << ang_in_degree;
                if (previousAngle != 0) {
                    double angleDifferenceTreshold = 12;
                    if (previousAngle >= 80 && previousAngle <= 100) {
                        if (ang_in_degree > previousAngle + angleDifferenceTreshold) {
                            return "Slide left";

                        } else if (ang_in_degree < previousAngle - angleDifferenceTreshold) {
                            return "Slide right";

                        }
                    }
                }
                previousAngle = ang_in_degree;
            }

        }
        return "";
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

    private String handDirectionCalculator(List<NormalizedLandmarkList> multiHandLandmarks){
        for (NormalizedLandmarkList landmarks : multiHandLandmarks) {
            List<NormalizedLandmark> landmarkList = landmarks.getLandmarkList();
            float x_new = (landmarkList.get(5).getX() + landmarkList.get(17).getX()) / 2;
            float y_new = (landmarkList.get(5).getY() + landmarkList.get(17).getY()) / 2;
            float x = landmarkList.get(0).getX();
            float y = landmarkList.get(0).getY();
            float sign_x = x - x_new;
            float sign_y = y - y_new;
            if(sign_x==0){
                //上 下
                if(y_new>y) return "up";
                else return "down";
            }else{
                float slope=(y_new-y)/(x_new-x);
                if ((slope > 1 || slope < -1) && (sign_y < 0)){
                    return "down";
                }else if ((slope > 1 || slope < -1) && (sign_y > 0)){
                    return "up";
                }else if ((slope < 1 && slope > -1) && (sign_x > 0)){
                    return "left";
                }else if ((slope < 1 && slope > -1) && (sign_x < 0)) {
                    return "right";
                }
            }
        }
        return "无";
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            finish();
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Hello from Android, you are #" + cnt;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                message += "replayed: " + msgReply + "\n";

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        msg.setText(Integer.toString(STATUS_OF_SOCKET));
                    }
                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    msg.setText(Integer.toString(STATUS_OF_SOCKET));
                }
            });
        }

    }
    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                    }
                });

                while (true) {
                    Socket socket = serverSocket.accept();
                    InputStream isRead=socket.getInputStream();
                    byte[] buffer=new byte[isRead.available()];
                    isRead.read(buffer);
                    String responseInfo=new String(buffer);
                    count++;
                    message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n"+responseInfo;
                    message=responseInfo;
                    if(responseInfo.compareTo("0")==0) STATUS_OF_SOCKET=0;
                    else if(responseInfo.compareTo("1")==0) STATUS_OF_SOCKET=1;
                    else if(responseInfo.compareTo("2")==0) STATUS_OF_SOCKET=2;
                    else if(responseInfo.compareTo("3")==0) STATUS_OF_SOCKET=3;
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            msg.setText(Integer.toString(STATUS_OF_SOCKET));
                        }
                    });

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                            socket, count);
                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}