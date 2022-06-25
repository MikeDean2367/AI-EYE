package com.example.vision.fragment;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.airbnb.lottie.L;
import com.example.vision.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private String TAG = "error";
    private MutableLiveData<ArrayList<CardData>> horizonCardData;
    private MutableLiveData<String> UserName = new MutableLiveData<>();
    private MutableLiveData<String> LatestTime = new MutableLiveData<>();

    private ArrayList<Long> alltime = new ArrayList<>();
    public MutableLiveData<String> getUserName() { return UserName; }
    public MutableLiveData<String> getLatestTime() { return LatestTime; }

    public HomeViewModel() {
        horizonCardData = new MutableLiveData<>();
        ArrayList<CardData> cardDataArrayList = new ArrayList<CardData>();
        horizonCardData.setValue(cardDataArrayList);
        UserName.setValue(MainActivity.UserName);

        calLatestTime();
    }

    private String deltaTime(long startDate, long endDate){

        // 相差的毫秒值
        long milliseconds = endDate - startDate;

        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数

        long day = milliseconds / nd; // 计算相差多少天
        long hour = milliseconds % nd / nh; // 计算相差剩余多少小时
        long min = milliseconds % nd % nh / nm; // 计算相差剩余多少分钟
        long sec = milliseconds % nd % nh % nm / ns; // 计算相差剩余多少秒
        if (day>=1) return "上一次测量时间为"+String.valueOf(day)+"天前";
        else{
            if (hour>=1) return "上一次测量时间为"+String.valueOf(hour)+"小时前";
            else{
                if (min>=1) return "上一次测量时间为"+String.valueOf(min)+"分钟前";
                else{
                    if(sec>=1) return "上一次测量时间为"+String.valueOf(sec)+"秒前";
                    else return "您刚才已经测试过视力了";
                }
            }
        }
//        System.out.println("时间相差：" + day + "天" + hour + "小时" + min + "分钟" + sec + "秒");
//        // 时间相差：1天23小时59分钟59秒
//
//        long hourAll = milliseconds / nh; // 计算相差多少小时
//        System.out.println("时间相差：" + hourAll + "小时" + min + "分钟" + sec + "秒");
//        // 时间相差：47小时59分钟59秒
//
//        long min2 = milliseconds / nm; // 计算相差多少分钟
//        System.out.println("时间相差：" + min2 + "分钟" + sec + "秒");
//        // 时间相差：2879分钟59秒
    }

    private void calLatestTime(){
        alltime.clear();
        MainActivity.visionDBManager.readAllTime(alltime);
        if (alltime.size()==0) LatestTime.setValue("您还未测试过视力哦");
        else LatestTime.setValue(deltaTime(alltime.get(alltime.size()-1), System.currentTimeMillis()));
    }

    public MutableLiveData<ArrayList<CardData>> getHorizonCardData() {
        return horizonCardData;
    }
    public void insertCardData(CardData cardData){
        ArrayList<CardData> data = horizonCardData.getValue();
        if(data!=null)
            data.add(cardData);
        else Log.i(TAG,"未赋值");
    }


    public void updateInfo(){
        calLatestTime();
        UserName.setValue(MainActivity.UserName);
    }
}