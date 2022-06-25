package com.example.vision;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AstigmatismViewModel extends ViewModel {

    /*
    * 此处控制的三个阶段分别为:
    * 1. 下图中的线条是否同样粗细
    * 2. 请单击你认为较粗的线条
    * 3. 存在散光，散光轴向为10°
    * */

    // 常量
    String []QUESTION = {"下图中的线条是否同样粗细", "请单击你认为较粗的线条","存在散光，散光轴向为"};     // 标题的显示内容
    String []CHOOSE = new String[]{"不是同样粗细","同样粗细","确认", "确认","",""};                // 按钮的显示内容
    int []CENTER_BUTTON = new int[]{R.string.start, R.string.start, R.string.restart};                 // 中间的按钮显示的内容
    int []STATE_1 = new int[]{View.VISIBLE, View.VISIBLE, View.INVISIBLE};
    int []STATE_2 = new int[]{View.VISIBLE, View.INVISIBLE, View.INVISIBLE};
    int []STATE_CENTER = new int[]{View.INVISIBLE, View.INVISIBLE, View.VISIBLE};             // 中心按钮
    int MAX_STAGE = 3;

    private int cursor = 0; // 当前所处哪个阶段
    private int direction = 0;  // 散光偏度
    private int currentEye = 0; // which eyes
    private MutableLiveData<String> question = new MutableLiveData<String>();
    private MutableLiveData<Integer> btn_1_state = new MutableLiveData<Integer>(View.INVISIBLE);
    private MutableLiveData<Integer> btn_2_state = new MutableLiveData<Integer>(View.INVISIBLE);
    private MutableLiveData<Integer> btn_center_state = new MutableLiveData<Integer>();             // 中心按钮是否显示
    private MutableLiveData<String> btn_1_show = new MutableLiveData<String>();
    private MutableLiveData<String> btn_2_show = new MutableLiveData<String>();
    private MutableLiveData<String> show_eye = new MutableLiveData<String>();
    private MutableLiveData<String> show_title = new MutableLiveData<String>();

    public AstigmatismViewModel(){
        leftValue = "未测试";
        rightValue = "未测试";
        show_title.setValue("未测试");
        setCurrentEye(0);
    }

    // 供主程序调用的
    // 获取中心按钮显示的图案
    public int getCenterShow(){
        if (cursor==-1) return CENTER_BUTTON[0];
        else return CENTER_BUTTON[cursor];
    }

    public int getMAX_STAGE(){
        return MAX_STAGE-1;
    }

    // 是否可以点击
    public boolean canClick(){
        return cursor==MAX_STAGE-2;
    }

    public int getStage(){
        return cursor;
    }
    public void jumpState(int cursor){
        this.cursor = cursor - 1;
        nextStage();
    }

    public void nextStage(){
        cursor = (cursor + 1) % MAX_STAGE;
        question.setValue(QUESTION[cursor]);
        if (cursor==2){ // 显示结果
            if (direction>=0) question.setValue(question.getValue()+String.valueOf(direction)+"°");
            else question.setValue("您当前不存在散光哦，请继续保持");
        }
        btn_1_state.setValue(STATE_1[cursor]);
        btn_1_show.setValue(CHOOSE[cursor*2]);
        btn_2_state.setValue(STATE_2[cursor]);
        btn_2_show.setValue(CHOOSE[cursor*2+1]);
        btn_center_state.setValue(STATE_CENTER[cursor]);
    }

    public void setCurrentEye(int CurrentEye){
        // 0-left 1-right
        this.currentEye = CurrentEye;
        if (currentEye==0){
            show_eye.setValue("左眼");
            show_title.setValue(leftValue);
        } else {
            show_eye.setValue("右眼");
            show_title.setValue(rightValue);
        }
    }
    public int getCurrentEye(){
        return this.currentEye;
    }

    // 恢复到开始的时候
    public void reset(){
        cursor = -1;
        question.setValue("单击下方按钮开始测试");
        btn_1_state.setValue(View.INVISIBLE);
        btn_2_state.setValue(View.INVISIBLE);
        btn_center_state.setValue(View.VISIBLE);
    }

    String leftValue, rightValue;
    // 返回近视的偏向
    public void setDirection(int direction){
        if(direction==-1){
            if(currentEye==0) leftValue="无散光";
            else rightValue="无散光";
            show_title.setValue("无散光");
        } else {
            if(currentEye==0) leftValue=String.valueOf(direction+"°");
            else rightValue=String.valueOf(direction+"°");
            show_title.setValue(String.valueOf(direction+"°"));
        }
        this.direction = direction;
    }
    public int getDirection() { return direction; }

    // 与前端绑定
    public MutableLiveData<String> getQuestion(){ return question; }            // 问题的题目
    public MutableLiveData<Integer> getBtn_1_state(){ return btn_1_state; }     // 按钮是否显示
    public MutableLiveData<Integer> getBtn_2_state(){ return btn_2_state; }     // 按钮是否显示
    public MutableLiveData<String> getBtn_1_show(){ return btn_1_show; }     // 按钮显示内容
    public MutableLiveData<String > getBtn_2_show(){ return btn_2_show; }     // 按钮显示内容
    public MutableLiveData<Integer> getBtn_center_state(){ return btn_center_state; }   // 中间按钮是否显示
    public MutableLiveData<String > getShow_eye() { return show_eye; }
    public MutableLiveData<String> getShow_title() {return show_title;}
}
