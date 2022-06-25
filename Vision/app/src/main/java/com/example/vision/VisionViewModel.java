package com.example.vision;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Random;

public class VisionViewModel extends ViewModel {

    // 常量■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    // 4.0 4.1 4.2 4.3 ... 5.3
    public boolean useAudio = true;
    public Double [] sizeMap= {
            72.72,57.76,45.88,36.45,28.95,23.00,18.27,14.51,11.53,9.16,7.27,5.78,4.59,3.64
    };
    public double[] distanceMap = { 10,6.25,5 }; // 对应不同的距离
    public int[] sightSign= { // 视力图的资源文件，分别对应不同的方向
            R.drawable.visionimage2, R.drawable.visionimage1, R.drawable.visionimage3, R.drawable.visionimage4
    };
    private int MAX_CORRECT_COUNT = 2;      // 对2个就对
    private int MAX_ERROR_COUNT = 4;        // 如果单个错超过4个则停止
    private double dpi;

    // 设置里的内容■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    private int duration = 5000;           // 测试一次的时长，单位为毫秒
    private int currentEye = 0;             // 0-左眼 1-右眼
    private int currentDistance = 0;      // 当前测试距离 -- 对应的是下标 distanceMap

    // 记录器■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    private int left = 0, right = sizeMap.length;
    private int currentState = 0;         // 0-4.0 1-4.1 ... 11-5.1
    private int currentDirection = 0;     // 方向
    private double currentPicSize = 0;  // 当前图片大小
    private boolean userAnswer = false;   // 用户是否答对
    private float []finalsight = new float[2]; // 最终测试结果
    private boolean isFinish = false;   // 是否测试结束

    // 前端绑定■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    private MutableLiveData<Float> currentSight = new MutableLiveData<Float>(); // 当前测试视力
    private MutableLiveData<Integer> correct_counter = new MutableLiveData<Integer>();  // 正确个数计数器
    private MutableLiveData<Integer> error_counter = new MutableLiveData<Integer>();    // 错误个数计数器
    private MutableLiveData<Integer> show_first = new MutableLiveData<>();              // 十位
    private MutableLiveData<Integer> show_second = new MutableLiveData<>();              // 个位
    private MutableLiveData<String> show_eye = new MutableLiveData<>();                 // 当前是哪只眼睛
    private MutableLiveData<String> show_distance = new MutableLiveData<>();


    // 构造函数
    public VisionViewModel(){
        currentDistance = 0;                    // 初始测试距离: 0.5m
        currentDirection = generateRandom();    // 初始化方向
        currentSight.setValue(4.5F);            // 默认从4.5开始
        currentState = (int)(currentSight.getValue()*10-40);
        show_first.setValue(currentState / 10 + 4);
        show_second.setValue(currentState % 10);
        correct_counter.setValue(0);
        error_counter.setValue(0);
        isFinish = false;
        finalsight[0] = finalsight[1] = -1F;
        String []distance = {"0.5","0.8","1"};
        String []eye = {"左眼","右眼"};
        show_distance.setValue("测试距离:" + distance[currentDistance] + "M");
        show_eye.setValue(eye[currentEye]);
    }
    // 初始化
    public void init(float lastSight){
        currentDirection = generateRandom();    // 初始化方向
        currentSight.setValue(lastSight);
        show_first.setValue((int)(currentSight.getValue() / 1));
        show_second.setValue((int) (currentSight.getValue() * 10 % 10));
        currentState = (int)(currentSight.getValue()*10-40);
        show_first.setValue(currentState / 10 + 4);
        show_second.setValue(currentState % 10);
    }


    // 设置■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    // 设置时长
    public void setDuration(int Duration){
        duration = Duration;
    }
    public int getDuration(){
        return duration;
    }
    // 设置测试哪个眼睛
    public void setCurrentEye(int EyeType){
        if (EyeType>=0 && EyeType<=1){
            currentEye=EyeType;
            if (EyeType==0) show_eye.setValue("左眼");
            else show_eye.setValue("右眼");
        }
    }
    public int getCurrentEye(){
        return currentEye;
    }
    // 设置测试距离
    public void setCurrentDistance(int DistanceType){
        String []distance = {"0.5","0.8","1"};
        if (DistanceType>=0 && DistanceType<=2) {
            currentDistance = DistanceType;
            show_distance.setValue("测试距离:" + distance[DistanceType] + "M");
        }
    }
    public int getCurrentDistance(){
        return currentDistance;
    }
    public void setDpi(double Dpi){
        dpi = Dpi;
    }

    // 功能+逻辑■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    // 产生随机数
    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(4);
    }

    // 根据屏幕dpi、当前视力、测试距离来生成E的尺寸
    public void updateSize(){
        double distance = distanceMap[currentDistance];
        double size = sizeMap[currentState];   // 当前的实际大小
        size = size / distance;
        currentPicSize = size * dpi;
    }

    // 判断用户是否正确
    public void checkAnswer(int Direction){
        // 0-上 1-下 2-左 3-右
        if (Direction==currentDirection) userAnswer=true;
        else userAnswer=false;
    }

    // 产生下一个符号
    public void generateNext(){
        // 更新正确、错误计数器
        if (userAnswer) correct_counter.setValue(correct_counter.getValue()+1);
        else error_counter.setValue(error_counter.getValue()+1);

        // 判断当前的视力值是否结束
        if (correct_counter.getValue()==MAX_CORRECT_COUNT){
            correct_counter.setValue(0);
            error_counter.setValue(0);
            left = currentState + 1;
            currentState = (left + right) / 2;
            show_first.setValue(currentState / 10 + 4);
            show_second.setValue(currentState % 10);
        }else if (error_counter.getValue()==MAX_ERROR_COUNT){
            correct_counter.setValue(0);
            error_counter.setValue(0);
            right = currentState;
            currentState = (left + right) / 2;
            show_first.setValue(currentState / 10 + 4);
            show_second.setValue(currentState % 10);
        }

        // 判断整个是否结束
        if (left==right){// 表明整轮结束
            isFinish = true;
            finalsight[currentEye] = (float)(left - 1F)/10 + 4F;
            show_first.setValue((left-1) / 10 + 4);
            show_second.setValue((left-1) % 10);
            if(show_second.getValue()<0) show_second.setValue(0);
        } else{
            // 产生新的方向
            currentDirection = generateRandom();
            // 更新大小
            updateSize();
        }
    }

    public void start(){
        // 恢复到原始状态，便于下一次
        left = 0;
        right = sizeMap.length;
        correct_counter.setValue(0);
        error_counter.setValue(0);
        currentDirection = generateRandom();    // 初始化方向
        currentSight.setValue(4.5F);            // 默认从4.5开始
        currentState = (int)(currentSight.getValue()*10-40);
        show_first.setValue(currentState / 10 + 4);
        show_second.setValue(currentState % 10);
        isFinish = false;
    }

    // 用户绑定前端■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    public MutableLiveData<Float> getCurrentSight(){
        return currentSight;
    }

    // 获取图片大小
    public int getCurrentPicSize(){
        return (int)currentPicSize;
    }
    // 获取图片方向
    public int getCurrentPicId(){
        return sightSign[currentDirection];
    }
    // 判断是否结束
    public boolean ifFinish(){
        return isFinish;
    }

    public MutableLiveData<Integer> getFirst(){
        return show_first;
    }
    public MutableLiveData<Integer> getSecond(){
        return show_second;
    }
    public MutableLiveData<String> getShow_eye(){
        return show_eye;
    }
    public MutableLiveData<String> getShow_distance(){
        return show_distance;
    }

    /*
    * 1. 判断方向是否正确
    * 2. 产生下一回合的E的尺寸、方向
    * 3. 判断当前是哪个眼睛
    * 4. 记录正确和错误的个数
    * 5. 记录是否开启声音
    * 6.
    * */

}
