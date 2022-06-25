package com.example.pad;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class StartViewModel extends ViewModel {
    // 常数
    private int DELTA = 70; // 和目标值小于10cm都算正确

    // 前端绑定
    private MutableLiveData<Integer> curDistance = new MutableLiveData<>();  // 当前距离
//    public void setCurDistance(int curDistance){ Log.v("MikeDean","设置:"+String.valueOf(curDistance));this.curDistance.setValue(curDistance); }
    public MutableLiveData<Integer> getCurDistance(){ return curDistance; }

    private MutableLiveData<Integer> settingDistance = new MutableLiveData<>(); // 选择的距离
    public void setSettingDistance(int settingDistance){ this.settingDistance.setValue(settingDistance); }
    public MutableLiveData<Integer> getSettingDistance(){ return settingDistance; }

    // Constructor
    public StartViewModel(){
        curDistance.setValue(0);
        settingDistance.setValue(0);
    }

    // 当前位于第几个
    private int curStage = 0;
    // 测试距离
    private int distance = 0;
    // 眼睛   0-左眼 1-右眼
    private int eyes = 0;

    // 传过来的参数
    private int preDirection = -1;      // 前一个时刻的手势方向
    private int preNum = -1;            // 前一个时刻的数字
    private int curDirection = -1;      // 当前时刻的方向
    private int curNum = -1;            // 当前时刻的数字
    private int keepCnt = 0;            // 持续个数


    // 设置
    public void nextStage(){
        curStage++;
    }
    public void setDistance(int Distance){
        this.distance = Distance;
    }
    public void setEyes(int Eyes){
        this.eyes = Eyes;
    }
    private int cur_distance;
    public void setCurDistance(int CurDistance) {
        Log.v("MikeDean","设置:"+String.valueOf(CurDistance));
        cur_distance = CurDistance;
    }
    public void updateDirection(int Direction){
        this.preDirection = this.curDirection;
        this.curDirection = Direction;
    }
    public void updateNum(int Num){
        this.preNum = this.curNum;
        this.curNum = Num;
    }
    // 判断是否和前一个一样
    public void resetKeep(){
        keepCnt = 0;
    }
    public boolean ifKeep(String type){
        if (type.equals("direction")){
            if (preDirection==-1) {
                keepCnt = 0;
                return false;
            }
            if (this.preDirection == this.curDirection){
                keepCnt++;
                return true;
            }else{
                keepCnt = 0;
                return false;
            }
        }else if(type.equals("num")){
            if (preNum==-1){
                keepCnt = 0;
                return false;
            }
            if (this.preNum == this.curNum){
                keepCnt++;
                return true;
            }else{
                keepCnt=0;
                return false;
            }
        }else{
            curDistance.setValue(cur_distance);
            // 距离是否符合要求

            if (Math.abs(curDistance.getValue()-settingDistance.getValue())<=DELTA){
                keepCnt++;
                return true;
            }else{
                keepCnt = 0;
                return false;
            }
        }
    }

    // 获取
    public int getCurStage(){
        return curStage;
    }
    public int getEyes(){
        return eyes;
    }
    public int getDistance(){
        return distance;
    }
    public int getKeepCnt(){
        return keepCnt;
    }

}
