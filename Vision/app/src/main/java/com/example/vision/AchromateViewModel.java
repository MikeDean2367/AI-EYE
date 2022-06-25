package com.example.vision;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class AchromateViewModel extends ViewModel {
    private MutableLiveData<Integer> IsTrueNumber,IsFalseNumber;
    private char choice='F';//存储用户做出的选择

    public AchromateViewModel(){
        setStage(1);
    }

    public char getChoice() {
        return choice;
    }

    public void setChoice(char choice) {
        this.choice = choice;
    }

    //正确个数
    public MutableLiveData<Integer> getIsTrueNumber() {
        if(IsTrueNumber==null){
            IsTrueNumber=new MutableLiveData<>();
            IsTrueNumber.setValue(0);
        }
        return IsTrueNumber;
    }

    //错误个数
    public MutableLiveData<Integer> getIsFalseNumber() {
        if(IsFalseNumber==null){
            IsFalseNumber=new MutableLiveData<>();
            IsFalseNumber.setValue(0);
        }
        return IsFalseNumber;
    }

    public void addIsTrueNumber(){
        IsTrueNumber.setValue(IsTrueNumber.getValue()+1);
    }

    public void addIsFalseNumber(){
        IsFalseNumber.setValue(IsFalseNumber.getValue()+1);
    }
    public void reset(){
        IsTrueNumber.setValue(0);
        IsFalseNumber.setValue(0);
    }


    //通用图组
    public int[] GeneralTestPicture = {
            R.drawable.test1, R.drawable.test2, R.drawable.test3, R.drawable.test4,
            R.drawable.test5, R.drawable.test6, R.drawable.test7, R.drawable.test8,
            R.drawable.test9, R.drawable.test10, R.drawable.test11, R.drawable.test12,
            R.drawable.test13, R.drawable.test14, R.drawable.test15, R.drawable.test16,
            R.drawable.test17, R.drawable.test18, R.drawable.test19, R.drawable.test20,
            R.drawable.test21, R.drawable.test22, R.drawable.test23, R.drawable.test24,
            R.drawable.test25, R.drawable.test26, R.drawable.test27, R.drawable.test28,
            R.drawable.test29, R.drawable.test30
    };
    public String GeneralTestPictureChoiceA="图中无明显图案";
    public String[] GeneralTestPictureChoiceB={
            "909","38","66","88","80","162","616","2","528","602","6","8","0289","6298","8901",
            "8609","狮子","△ ○","正方形","鸡","鸟","鹅","蜻蜓","金鱼","羊","狗狗","蜻蜓","剪刀",
            "899 022","299"
    };
    public String[] GeneralTestPictureChoiceC={
            "989","29","69","66","69","6","619","56","529","98","9","95","6289","6289","2901",
            "8009","狗熊","△","圆形","鸭","蝴蝶","鸡","金鱼","虫子","牛","鸭","蝴蝶","○",
            "902","618"
    };
    public String[] GeneralTestPictureChoiceD={
            "609","28","96","99","66","16","916","22","629","96","268","685","6299","6098","2801",
            "8904","熊猫","○","三角形","羊","蜻蜓","鸟","鸡","燕子","鸡","鸡","燕子","○ 剪刀",
            "892","621 989"
    };
    public String[] GeneralTestPictureChoiceE={
            "606","39","99","86","60","163","919","58","628","609","269","985","6298","6099","8801",
            "8600","老鼠","○ ○","长方形","牛","虫子","狗狗","鸭","蝴蝶","鹅","兔子","鸡","△ 剪刀",
            "899 02","621 898"
    };
    public char[] GeneralTestPictureAnswer={'B', 'C', 'C', 'B', 'E', 'B', 'D','C','E', 'B',
            'D', 'E', 'C', 'D', 'C', 'B','D', 'B', 'C', 'E',
            'D', 'B','C', 'D', 'B', 'E', 'C', 'D', 'B', 'D'};
    //单色图组
    public int[] SingleTestPicture={
            R.drawable.test31, R.drawable.test32, R.drawable.test33, R.drawable.test34, R.drawable.test35
    };
    public String SingleTestPictureChoiceA="红色";
    public String SingleTestPictureChoiceB="黄色";
    public String SingleTestPictureChoiceC="蓝色";
    public String SingleTestPictureChoiceD="绿色";
    public String SingleTestPictureChoiceE="紫色";
    public char[] SingleTestPictureAnswer={'A','B','C','D','E'};

    //功能测试具体分类图组
    public int[] ClassifyTestPicture={R.drawable.test39, R.drawable.test40,
            R.drawable.test41};
    public String ClassifyTestPictureChoiceA="图中无明显图案";
    public String ClassifyTestPictureChoiceB[]={"8","0","正方形"};
    public String ClassifyTestPictureChoiceC[]={"5","9","三角形"};
    public String ClassifyTestPictureChoiceD[]={"5/8","0/9","三角形 正方形"};
    public String ClassifyTestPictureChoiceE[]={"5/9","0/6","圆形 正方形"};

    public char ClassifyTestPictureAnswer='D';

    //功能图组
    public int[] FunctionTestPicture={
            R.drawable.test37, R.drawable.test38, R.drawable.test39, R.drawable.test40,
            R.drawable.test41, R.drawable.test42, R.drawable.test43
    };
    public String FunctionTestPictureChoiceA="图中无明显图案";
    public String FunctionTestPictureChoiceB[]={"两颗五角星","622","826","5","09","三角形 圆形","9","水杯"};
    public String FunctionTestPictureChoiceC[]={"两个圆","600","828","58","0","三角形","86","蝴蝶"};
    public String FunctionTestPictureChoiceD[]={"两个正方形","522","825","8","9","正方形","89","熊猫"};
    public String FunctionTestPictureChoiceE[]={"两个三角形","500","855","68","00","三角形 正方形","6","茶壶"};
    public char[] FunctionTestPictureAnswer={'B','D','D','C','B','E','C','E'};

    //后天性图组
    public int[] PosteriorityTestPicture={
            R.drawable.test44, R.drawable.test45, R.drawable.test46, R.drawable.test47, R.drawable.test48
    };
    public String PosteriorityTestPictureChoiceA="图中无明显图案";
    public String PosteriorityTestPictureChoiceB[]={"21","5","66","两个圆圈","两个正方形"};
    public String PosteriorityTestPictureChoiceC[]={"2","2","69","两个正方形","两个三角形"};
    public String PosteriorityTestPictureChoiceD[]={"1","52","99","两个三角形","一个正方形 一个圆形"};
    public String PosteriorityTestPictureChoiceE[]={"27","50","6","一个圆圈 一个三角形","一个正方形 一个三角形"};
    public char[] PosteriorityTestPictureAnswer={'B','D','C','B','E'};

    //自定义Toast显示时间
    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3500);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }

    // stage
    private int stage;
    public void setStage(int stage){
        this.stage = stage;
        show_stage.setValue(stage);
    }
    private MutableLiveData<Integer> show_stage = new MutableLiveData<>();
    public MutableLiveData<Integer> getShow_stage(){return show_stage;}
}
