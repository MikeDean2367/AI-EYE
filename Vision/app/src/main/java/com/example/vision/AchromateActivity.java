package com.example.vision;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.vision.common.BackListener;
import com.example.vision.databinding.ActivityAchromateBinding;
import com.google.android.material.button.MaterialButton;
import com.skydoves.transformationlayout.TransformationLayout;
import com.skydoves.transformationlayout.TransitionExtensionKt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AchromateActivity extends AppCompatActivity {

    ActivityAchromateBinding binding;

    AchromateViewModel achromateViewModel;
    List<MaterialButton> buttonList=new ArrayList<>();
    private int i=0;
    private char result='A';
    private String TestResult="正常";

    private Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:{//通用图组第一次检查
                    if(flag1==6)
                    {
                        OriginalColor();
                        binding.testPicture.setImageResource(R.drawable.end);
                        binding.btnChoice1.setText(" ");
                        binding.btnChoice2.setText(" ");
                        binding.btnChoice3.setText(" ");
                        binding.btnChoice4.setText(" ");
                        binding.btnChoice5.setText(" ");
                        inVisible();
                        if(achromateViewModel.getIsTrueNumber().getValue()<5){
                            Toast toast=Toast.makeText(AchromateActivity.this,"第一部分测试结束，为色觉异常可疑者，进行下一步测试。每题答题时间为8秒，共10题！",Toast.LENGTH_LONG);
                            achromateViewModel.setStage(2);
                            achromateViewModel.showMyToast(toast,5000);
                        }
                    }
                    else{
                        Visible();
                        //获取随机数 0到29
                        achromateViewModel.setChoice('F');
                        int index=Test1Random[TestCount1];
                        OriginalColor();//恢复初始颜色
                        binding.testPicture.setImageResource(achromateViewModel.GeneralTestPicture[index]);
                        binding.btnChoice1.setText("A "+achromateViewModel.GeneralTestPictureChoiceA);
                        binding.btnChoice2.setText("B "+achromateViewModel.GeneralTestPictureChoiceB[index]);
                        binding.btnChoice3.setText("C "+achromateViewModel.GeneralTestPictureChoiceC[index]);
                        binding.btnChoice4.setText("D "+achromateViewModel.GeneralTestPictureChoiceD[index]);
                        binding.btnChoice5.setText("E "+achromateViewModel.GeneralTestPictureChoiceE[index]);
                        result=achromateViewModel.GeneralTestPictureAnswer[index];
                        setListeners();//监听器
                    }
                    break;
                }
                case 2:{//通用图组第二次检查
                    if(flag2==11)
                    {
                        OriginalColor();
                        binding.testPicture.setImageResource(R.drawable.end);
                        binding.btnChoice1.setText(" ");
                        binding.btnChoice2.setText(" ");
                        binding.btnChoice3.setText(" ");
                        binding.btnChoice4.setText(" ");
                        binding.btnChoice5.setText(" ");
                        inVisible();
                        if(achromateViewModel.getIsTrueNumber().getValue()<=4){
                            Toast toast4= Toast.makeText(AchromateActivity.this,"您可能为色盲等级I~II级，接下来进行单色检查，每题答题时间为5秒，共5题！",Toast.LENGTH_LONG);
                            achromateViewModel.setStage(3);
                            achromateViewModel.showMyToast(toast4,5000);
                        }
                    }
                    else{
                        Visible();
                        //获取随机数 0到29
                        achromateViewModel.setChoice('F');//默认错误
                        int index=Test2Random[Test2Count];
                        OriginalColor();//恢复初始颜色
                        binding.testPicture.setImageResource(achromateViewModel.GeneralTestPicture[index]);
                        binding.btnChoice1.setText("A "+achromateViewModel.GeneralTestPictureChoiceA);
                        binding.btnChoice2.setText("B "+achromateViewModel.GeneralTestPictureChoiceB[index]);
                        binding.btnChoice3.setText("C "+achromateViewModel.GeneralTestPictureChoiceC[index]);
                        binding.btnChoice4.setText("D "+achromateViewModel.GeneralTestPictureChoiceD[index]);
                        binding.btnChoice5.setText("E "+achromateViewModel.GeneralTestPictureChoiceE[index]);
                        result=achromateViewModel.GeneralTestPictureAnswer[index];
                        setListeners();//监听器
                    }
                    break;
                }
                case 3:{//单色图组检查
                    if(flag3==6)
                    {
                        OriginalColor();
                        binding.testPicture.setImageResource(R.drawable.end);
                        binding.btnChoice1.setText(" ");
                        binding.btnChoice2.setText(" ");
                        binding.btnChoice3.setText(" ");
                        binding.btnChoice4.setText(" ");
                        binding.btnChoice5.setText(" ");
                        inVisible();
                        if(achromateViewModel.getIsTrueNumber().getValue()<5) {
                            Toast toast6 = Toast.makeText(AchromateActivity.this, "单色检查结果异常,接下来进行色觉异常分类测试。答题时间为8秒", Toast.LENGTH_LONG);
                            achromateViewModel.setStage(3);
                            achromateViewModel.showMyToast(toast6, 5000);
                        }
                    }
                    else{
                        Visible();
                        achromateViewModel.setChoice('F');//默认答案
                        int index=i;
                        OriginalColor();//恢复初始颜色
                        binding.testPicture.setImageResource(achromateViewModel.SingleTestPicture[index]);
                        binding.btnChoice1.setText("A "+achromateViewModel.SingleTestPictureChoiceA);
                        binding.btnChoice2.setText("B "+achromateViewModel.SingleTestPictureChoiceB);
                        binding.btnChoice3.setText("C "+achromateViewModel.SingleTestPictureChoiceC);
                        binding.btnChoice4.setText("D "+achromateViewModel.SingleTestPictureChoiceD);
                        binding.btnChoice5.setText("E "+achromateViewModel.SingleTestPictureChoiceE);
                        result=achromateViewModel.SingleTestPictureAnswer[index];
                        setListeners();
                        i++;
                    }
                    break;
                }
                case 4:{//色觉异常检查
                    if(flag4==2){
                        OriginalColor();
                        binding.testPicture.setImageResource(R.drawable.end);
                        binding.btnChoice1.setText(" ");
                        binding.btnChoice2.setText(" ");
                        binding.btnChoice3.setText(" ");
                        binding.btnChoice4.setText(" ");
                        binding.btnChoice5.setText(" ");
                        inVisible();
                    }else{
                        Visible();
                        achromateViewModel.setChoice('F');//默认答案
                        //获取随机数 0到2
                        Random random=new Random();
                        int j=random.nextInt(3);
                        OriginalColor();
                        char a=achromateViewModel.SingleTestPictureAnswer[j];//答案
                        binding.testPicture.setImageResource(achromateViewModel.ClassifyTestPicture[j]);
                        binding.btnChoice1.setText("A "+achromateViewModel.ClassifyTestPictureChoiceA);
                        binding.btnChoice2.setText("B "+achromateViewModel.ClassifyTestPictureChoiceB[j]);
                        binding.btnChoice3.setText("C "+achromateViewModel.ClassifyTestPictureChoiceC[j]);
                        binding.btnChoice4.setText("D "+achromateViewModel.ClassifyTestPictureChoiceD[j]);
                        binding.btnChoice5.setText("E "+achromateViewModel.ClassifyTestPictureChoiceE[j]);
                        result=achromateViewModel.ClassifyTestPictureAnswer;
                        setListeners();

                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TransformationLayout.Params params = getIntent().getParcelableExtra("TransformationParams");
        TransitionExtensionKt.onTransformationEndContainer(this, params);

        //加载布局文件
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_achromate);


        //设置状态栏文字为深色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

//        //******自定义代码

        achromateViewModel= ViewModelProviders.of(this).get(AchromateViewModel.class);
        binding.setData(achromateViewModel);
        binding.setLifecycleOwner(this);//数据观察 自我更新
        //帮助提示
        binding.setting.getRoot().findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog();
            }
        });

        //测试
//        Toast toast1=Toast.makeText(AchromateActivity.this,"接下来进行色盲测试。每题答题时间为5秒，共5题！",Toast.LENGTH_LONG);
//        achromateViewModel.showMyToast(toast1,5000);
        AllinVisible();
        binding.begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Test1();
            }
        });

        binding.imgLeftEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.timeProgress.setProgress(binding.timeProgress.getProgress()+5);
                Log.v("MikeDean", String.valueOf(binding.timeProgress.getProgress()));
            }
        });

        binding.btnBack.setOnClickListener(new BackListener());
    }

    int TestCount1=0,flag1=0;//第一次测试图数
    int[] Test1Random=getRandomArray(6);//第一次测试随机数数组
    private void Test1(){
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Message message=new Message();
                if(TestCount1<6)
                {
                    if(binding.myprogressbar.getProgress()!=0){
                        CompareColorToJudge(result);
                    }
                    message.what=1;
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            binding.timeProgress.setProgress(0);
                        }
                    });

                    proes=0;
                    TimeProgresbar1();

                    binding.myprogressbar.setProgress(binding.myprogressbar.getProgress()+20);
                    int delta = binding.outProcess.getWidth() * 20 / 100;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            binding.inProcess.setLayoutParams(new FrameLayout.LayoutParams(
                                    binding.inProcess.getWidth()+delta, binding.inProcess.getHeight()
                            ));
                        }
                    });
                    handler.sendMessage(message);

                }else
                {
                    if(achromateViewModel.getIsTrueNumber().getValue()<5)
                    {
                        Looper.prepare();
                        binding.myprogressbar.setProgress(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.inProcess.setLayoutParams(new FrameLayout.LayoutParams(0, binding.inProcess.getHeight()));
                            }
                        });
                        achromateViewModel.getIsTrueNumber().postValue(0);
                        achromateViewModel.getIsFalseNumber().postValue(0);
                        Test2();
                        Looper.loop();
                    }
                    else{
                        Looper.prepare();
                        Toast toast2= Toast.makeText(AchromateActivity.this,"色盲测试结束，色盲检查结果为正常",Toast.LENGTH_LONG);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                achromateViewModel.setStage(3);
                            }
                        });
                        level = 0;
                        content = "无色盲";
                        save();
                        achromateViewModel.showMyToast(toast2,5000);
                        setTestResult("正常");
                        timer.cancel();
                        Looper.loop();
                    }

                }
                TestCount1++;
                flag1++;
            }
        }, 0,5000);
    }

    //通用第二次检查
    int Test2Count=0,flag2=0;
    int[] Test2Random=getRandomArray(12);//第二次测试随机数数组
    private void Test2(){
        Timer timer2=new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Message message=new Message();
                if(Test2Count<11) {
                    if(binding.myprogressbar.getProgress()!=0){
                        CompareColorToJudge(result);
                    }
                    message.what = 2;
                    proes=0;
                    TimeProgresbar2();
                    binding.myprogressbar.setProgress(binding.myprogressbar.getProgress() + 10);
                    int delta = binding.outProcess.getWidth() * 10 / 100;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.inProcess.setLayoutParams(new FrameLayout.LayoutParams(
                                    binding.inProcess.getWidth()+delta, binding.inProcess.getHeight()
                            ));
                        }
                    });
                    handler.sendMessage(message);
                }else{
                    if(achromateViewModel.getIsTrueNumber().getValue()>=9){
                        Looper.prepare();
                        Toast toast3= Toast.makeText(AchromateActivity.this,"色盲测试结束，色盲检查结果为正常",Toast.LENGTH_LONG);
                        level = 0;
                        content = "无色盲";
                        achromateViewModel.setStage(3);
                        achromateViewModel.showMyToast(toast3,5000);
                        save();
                        setTestResult("正常");
                        Looper.loop();
                    }
                    else if(achromateViewModel.getIsTrueNumber().getValue()<=4){
                        Looper.prepare();
                        binding.myprogressbar.setProgress(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.inProcess.setLayoutParams(new FrameLayout.LayoutParams(0, binding.inProcess.getHeight()));
                            }
                        });
                        achromateViewModel.getIsTrueNumber().postValue(0);
                        achromateViewModel.getIsFalseNumber().postValue(0);
                        SinglePictureTest();
                        Looper.loop();
                    }
                    else{
                        Looper.prepare();
                        Toast toast5= Toast.makeText(AchromateActivity.this,"您可能为色弱III~IV级",Toast.LENGTH_LONG);
                        achromateViewModel.setStage(3);
                        level = 4;
                        achromateViewModel.showMyToast(toast5,5000);
                        setTestResult("色弱III~IV级");
                        Looper.loop();
                    }
                }
                Test2Count++;
                flag2++;
            }
        },0,8000);
    }

    //单色检查
    int SingleTestCount=0,flag3=0;
    public  void SinglePictureTest()
    {
        Timer timer3=new Timer();
        timer3.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Message message=new Message();
                if(SingleTestCount<6) {
                    if(binding.myprogressbar.getProgress()!=0){
                        CompareColorToJudge(result);
                    }
                    message.what =3;
                    proes=0;
                    TimeProgresbar1();
                    binding.myprogressbar.setProgress(binding.myprogressbar.getProgress() + 20);
                    int delta = binding.outProcess.getWidth() * 20/100;
                    binding.inProcess.setLayoutParams(new FrameLayout.LayoutParams(
                            binding.inProcess.getWidth()+delta, binding.inProcess.getHeight()
                    ));
                    handler.sendMessage(message);
                }else{
                    if(achromateViewModel.getIsTrueNumber().getValue()<5){
                        Looper.prepare();
                        binding.myprogressbar.setProgress(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.inProcess.setLayoutParams(new FrameLayout.LayoutParams(0, binding.outProcess.getHeight()));
                            }
                        });
                        achromateViewModel.getIsTrueNumber().postValue(0);
                        achromateViewModel.getIsFalseNumber().postValue(0);
                        ClassifyTest();
                        Looper.loop();
                    }else{
                        Looper.prepare();
                        Toast toast7= Toast.makeText(AchromateActivity.this,"单色检查结果正常，可能为其他类型色盲",Toast.LENGTH_LONG);
                        level = 2;
                        content = "其他色盲";
                        save();
                        achromateViewModel.setStage(3);
                        achromateViewModel.showMyToast(toast7,5000);
                        setTestResult("色盲等级I~II级 其他类型色盲");
                        Looper.loop();
                    }
                }
                SingleTestCount++;
                flag3++;
            }
        },0,5000);
    }

    //色觉异常分类测试
    int ClassifyTestCount=0,flag4=0;
    private void ClassifyTest()
    {
        Timer timer4=new Timer();
        timer4.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Message message=new Message();
                if(ClassifyTestCount<2){
                    if(binding.myprogressbar.getProgress()!=0) {
                        ClassifyJudge();
                    }
                    message.what =4;
                    proes=0;
                    TimeProgresbar2();
                    binding.myprogressbar.setProgress(binding.myprogressbar.getProgress() + 100);
                    int delta = binding.outProcess.getWidth() * 100 / 100;
                    binding.inProcess.setLayoutParams(new FrameLayout.LayoutParams(
                            binding.inProcess.getWidth()+delta, binding.inProcess.getHeight()
                    ));
                    handler.sendMessage(message);
                }
                else{
                    timer4.cancel();
                }
                ClassifyTestCount++;
                flag4++;
            }
        },0,8000);
    }

    //监听器
    private void setListeners()
    {
        Onclick onclick=new Onclick();
        binding.btnChoice1.setOnClickListener(onclick);
        binding.btnChoice2.setOnClickListener(onclick);
        binding.btnChoice3.setOnClickListener(onclick);
        binding.btnChoice4.setOnClickListener(onclick);
        binding.btnChoice5.setOnClickListener(onclick);
    }
    private class Onclick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_choice1:
                {
                    setChangeColor(binding.btnChoice1);
                    achromateViewModel.setChoice('A');
                    break;
                }

                case R.id.btn_choice2:
                {
                    achromateViewModel.setChoice('B');
                    setChangeColor(binding.btnChoice2);
                    break;
                }
                case R.id.btn_choice3:
                {
                    achromateViewModel.setChoice('C');
                    setChangeColor(binding.btnChoice3);
                    break;
                }
                case R.id.btn_choice4:
                {
                    achromateViewModel.setChoice('D');
                    setChangeColor(binding.btnChoice4);
                    break;
                }
                case  R.id.btn_choice5:
                {
                    achromateViewModel.setChoice('E');
                    setChangeColor(binding.btnChoice5);
                    break;
                }
            }
        }
    }

    //改变颜色
    private void setChangeColor(MaterialButton btn){
        if(buttonList.size()==0){
            buttonList.add(binding.btnChoice1);
            buttonList.add(binding.btnChoice2);
            buttonList.add(binding.btnChoice3);
            buttonList.add(binding.btnChoice4);
            buttonList.add(binding.btnChoice5);
        }
        for(int i=0;i<buttonList.size();i++){
            buttonList.get(i).setBackgroundColor(0xE5EFE6);
        }
        btn.setBackgroundColor(0xFFADCDA9);
    }
    //恢复初始颜色
    private void OriginalColor(){
        for(int i=0;i<buttonList.size();i++){
            buttonList.get(i).setBackgroundColor(0xE5EFE6);
        }
    }
    //判断正误
    private void CompareColorToJudge(char a){
        if(achromateViewModel.getChoice()==a){
            new Thread(){
                public void run(){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            achromateViewModel.addIsTrueNumber();
                        }
                    });
                }
            }.start();
        }
        else{
            new Thread(){
                public void run(){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            achromateViewModel.addIsFalseNumber();
                        }
                    });
                }
            }.start();
        }
    }
    //色觉分类
    private void ClassifyJudge(){
        if(achromateViewModel.getChoice()=='D'){
            new Thread(){
                public void run(){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            achromateViewModel.addIsTrueNumber();
                            Toast toast11= Toast.makeText(AchromateActivity.this,"您色盲等级为I~II级，可能为其他类型色盲！",Toast.LENGTH_LONG);
                            setTestResult("色盲等级I~II级 其他类型色盲");
                            level = 2;
                            content = "其他色盲";
                            achromateViewModel.setStage(3);
                            save();
                            achromateViewModel.showMyToast(toast11,5000);
                        }
                    });
                }
            }.start();
        }
        else if(achromateViewModel.getChoice()=='A'||achromateViewModel.getChoice()=='E'||achromateViewModel.getChoice()=='F'){
            new Thread(){
                public void run(){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            achromateViewModel.addIsFalseNumber();
                            Toast toast8=Toast.makeText(AchromateActivity.this,"您色盲等级为I~II级，可能为红绿色盲！",Toast.LENGTH_LONG);
                            achromateViewModel.showMyToast(toast8,5000);
                            setTestResult("色盲等级I~II级 红绿色盲");
                            level = 2;
                            content = "红绿色盲";
                            achromateViewModel.setStage(3);
                            save();
                        }
                    });
                }
            }.start();
        }
        else if(achromateViewModel.getChoice()=='C') {
            new Thread() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            achromateViewModel.addIsFalseNumber();
                            Toast toast10= Toast.makeText(AchromateActivity.this,"您色盲等级为I~II级，可能为绿色盲！",Toast.LENGTH_LONG);
                            level = 2;
                            content = "绿色盲";
                            setTestResult("色盲等级I~II级 绿色盲");
                            achromateViewModel.setStage(3);
                            save();
                            achromateViewModel.showMyToast(toast10,5000);
                        }
                    });
                }
            }.start();
        }
        else if(achromateViewModel.getChoice()=='B'){
            new Thread(){
                @Override
                public void run() {
                    achromateViewModel.addIsFalseNumber();
                    Toast toast9=Toast.makeText(AchromateActivity.this,"您色盲等级为I~II级，可能为红色盲！",Toast.LENGTH_LONG);
                    level = 2;
                    content = "红色盲";
                    setTestResult("色盲等级I~II级 红色盲");
                    achromateViewModel.setStage(3);
                    save();
                    achromateViewModel.showMyToast(toast9,5000);
                }
            }.start();
        }
    }
    //获取不重复的随机数数组
    public int[] getRandomArray(int i) {
        int[] a = new int[i]; // a 随机数数组
        for (int m = 0; m < i; m++) { // m 已产生的随机数个数
            int temp = random();
            if (m == 0)
                a[0] = temp;
            else {
                for (int n = 0; n < m; n++) { // n 遍历已产生的随机数
                    if (temp == a[n]) {
                        temp = random();
                        n = -1;
                    }
                }
                a[m] = temp;
            }
        }
        return a;
    }
    //获得0~29的随机数
    private int random() {
        return (int) (30 * Math.random());
    }

    //5秒时间进度条
    int proes=0;
    private void TimeProgresbar1()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("MikeDean:time",String.valueOf(proes));
                        proes++;   //增加进度值
                        if (proes == 100) {
                            timer.cancel();  //当proes到最大值时停止增加
                        } else {
                            binding.timeProgress.setProgress(proes);  // proes当前进度
                        }
                    }
                });
            }
        }, 0, 50);
    }
    //8秒时间进度条
    private void TimeProgresbar2()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        proes++;   //增加进度值
                        if (proes == 100) {
                            timer.cancel();  //当proes到最大值时停止增加
                        } else {
                            binding.timeProgress.setProgress(proes);  // proes当前进度
                        }
                    }
                });
            }
        }, 0, 80);
    }
    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(AchromateActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage(R.string.achromate_help);
//        normalDialog.setMessage("1、测试需在明亮自然光下进行，被测试者距离屏幕60~80厘米，不得佩戴有色眼镜和使用有色的角膜接触镜。\n" +
//                "2、测试主要分为4部分，第一阶段为5张图的5秒速读，若能全部正确读出，则被测者为色觉正常者，测试结束，否则进行下一阶段测试。\n" +
//                "3、第二阶段对于色觉异常可疑者，采取10张图8秒进行识图，若答对9道题及以上，则为色觉正常者，测试结束；若答对5~8道题，则为色弱III~IV级；若答对0~4题，则为色盲I~II级，进行下一阶段测试。\n" +
//                "4、第三阶段对于色盲I~II级者进行单色测试，采取5张单色图5秒速读方式，全部答对，则单色检查通过，否则单色异常，进行下一阶段的色觉异常性质测试。\n" +
//                "5、对于色觉异常者性质的检查，只有一道题，答题时间为8秒，根据答案选项的不同做出大致判断。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        normalDialog.show();
    }

    public String getTestResult() {
        return TestResult;
    }

    public void setTestResult(String testResult) {
        TestResult = testResult;
    }
    //隐藏按钮
    public void inVisible() {
        binding.begin.setVisibility(binding.begin.INVISIBLE);
        binding.endPicture.setVisibility(binding.endPicture.VISIBLE);
        binding.testPicture.setVisibility(binding.testPicture.INVISIBLE);
        binding.btnChoice1.setVisibility(binding.btnChoice1.INVISIBLE);
        binding.btnChoice2.setVisibility(binding.btnChoice2.INVISIBLE);
        binding.btnChoice3.setVisibility(binding.btnChoice3.INVISIBLE);
        binding.btnChoice4.setVisibility(binding.btnChoice4.INVISIBLE);
        binding.btnChoice5.setVisibility(binding.btnChoice5.INVISIBLE);
    }
    //显示按钮
    public void Visible(){
        binding.begin.setVisibility(binding.begin.INVISIBLE);
        binding.endPicture.setVisibility(binding.endPicture.INVISIBLE);
        binding.testPicture.setVisibility(binding.testPicture.VISIBLE);
        binding.btnChoice1.setVisibility(binding.btnChoice1.VISIBLE);
        binding.btnChoice2.setVisibility(binding.btnChoice2.VISIBLE);
        binding.btnChoice3.setVisibility(binding.btnChoice3.VISIBLE);
        binding.btnChoice4.setVisibility(binding.btnChoice4.VISIBLE);
        binding.btnChoice5.setVisibility(binding.btnChoice5.VISIBLE);
    }
    //全部隐藏
    public void AllinVisible() {
        binding.endPicture.setVisibility(binding.endPicture.INVISIBLE);
        binding.testPicture.setVisibility(binding.testPicture.INVISIBLE);
        binding.btnChoice1.setVisibility(binding.btnChoice1.INVISIBLE);
        binding.btnChoice2.setVisibility(binding.btnChoice2.INVISIBLE);
        binding.btnChoice3.setVisibility(binding.btnChoice3.INVISIBLE);
        binding.btnChoice4.setVisibility(binding.btnChoice4.INVISIBLE);
        binding.btnChoice5.setVisibility(binding.btnChoice5.INVISIBLE);
    }

    // save
    private int level;
    private String content;
    private void save(){
        // attention! in this module, column eyes has no meaning
        // value:
        //  level-content
        // level: 0/2/4  --> 正常视觉,I~II色弱,III~IV色盲
        // content:
        MainActivity.visionDBManager.insert(String.valueOf(System.currentTimeMillis()), "1","0",String.valueOf(level)+"-"+content);
    }
}
