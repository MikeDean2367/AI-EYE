package com.example.vision;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vision.database.DBVisionDataManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DataViewModel extends ViewModel {

    /*
     * user_id time_stamp type eyes value
     * string  string     string string string
     * type:
     *   0-vision
     *   1-achromate
     *   2-astigmatism
     *
     * eyes:
     *   0-left
     *   1-right
     *
     * value:
     *   "4.2"
     *   "YELLOW-I"
     *   "150"
     * */

    // UI
    private MutableLiveData<String> latestTime = new MutableLiveData<>();
    public MutableLiveData<String> getLatestTime(){ return latestTime; }


    private String userName = MainActivity.UserName;
    private String dataBaseName = "AIEYE.db";
    private Context context;

    public ArrayList<Long> leftEyeVisionTimeStamp = new ArrayList<>();
    public ArrayList<Float> leftEyeVisionValue = new ArrayList<>();
    public ArrayList<Long> rightEyeVisionTimeStamp = new ArrayList<>();
    public ArrayList<Float> rightEyeVisionValue = new ArrayList<>();

    public ArrayList<Long> leftEyeAstigTimeStamp = new ArrayList<>();
    public ArrayList<Integer> leftEyeAstigValue = new ArrayList<>();
    public ArrayList<Long> rightEyeAstigTimeStamp = new ArrayList<>();
    public ArrayList<Integer> rightEyeAstigValue = new ArrayList<>();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private ArrayList<Long> allTime = new ArrayList<>();
    public ArrayList<String> years = new ArrayList<>(); // ALL YEARS
    public ArrayList<Integer> countYear = new ArrayList<>();
    // current year
    public ArrayList<Integer> countMonth = new ArrayList<>();
    // current month
    public ArrayList<Integer> countDay = new ArrayList<>();

    public String achromateValue;

    DBVisionDataManager manager;

    // data
    public void setUserName(String userName){ this.userName = userName; }
    public void setContext(Context context) { this.context = context; }
    public void init(){
        manager = new DBVisionDataManager(context, dataBaseName, userName);
        ArrayList<String> _tmp = new ArrayList<>();
        // Left Vision
        manager.read("0", "0", leftEyeVisionTimeStamp, _tmp);
        for(int i=0;i<_tmp.size();i++)
            if (_tmp.get(i).contains("-")) leftEyeVisionValue.add(4.0F);
            else leftEyeVisionValue.add(Float.parseFloat(_tmp.get(i)));

        // Right Vision
        _tmp.clear();
        manager.read("0","1", rightEyeVisionTimeStamp, _tmp);
        for(int i=0;i<_tmp.size();i++) rightEyeVisionValue.add(Float.parseFloat(_tmp.get(i)));

        // Left Astig
        _tmp.clear();
        manager.read("2","0", leftEyeAstigTimeStamp, _tmp);
        for(int i=0;i<_tmp.size();i++) leftEyeAstigValue.add(Integer.parseInt(_tmp.get(i)));

        // Right Astig
        _tmp.clear();
        manager.read("2","1", rightEyeAstigTimeStamp, _tmp);
        for(int i=0;i<_tmp.size();i++) rightEyeAstigValue.add(Integer.parseInt(_tmp.get(i)));

        // count
        String today = sdf.format(System.currentTimeMillis());  // yyyy-mm-dd
        Log.v("MikeDean", today);
        int toMonth = Integer.parseInt(today.substring(5, 7));  // 这个月
        String toYear = today.substring(0,4);                   // 今年
        for(int i=0;i<12;i++) countMonth.add(0);    // 12month
        if (toMonth==1 || toMonth==3 || toMonth==5 || toMonth==7 || toMonth==8 || toMonth==10 || toMonth==12){
            for(int i=0;i<31;i++) countDay.add(0);
        }else if(toMonth==2){
            for (int i=0;i<28;i++) countDay.add(0);
        }else{
            for(int i=0;i<30;i++) countDay.add(0);
        }
        manager.readAllTime(allTime);
        String _date, _year, _preyear="";
        int _month;
        int _day;
        for(int i=0;i<allTime.size();i++){
            _date = sdf.format(allTime.get(i));
            _month = Integer.parseInt(_date.substring(5,7));
            _year = _date.substring(0,4);
            if(!_preyear.equals(_year)){
                years.add(_year);
                countYear.add(0);
            }
            countYear.set(years.size()-1, countYear.get(years.size()-1)+1);

            _preyear = _year;
            if(_year.equals(toYear)){
                // 今年
                countMonth.set(_month-1, countMonth.get(_month-1)+1);
            }
            if(_year.equals(toYear) && _month==toMonth){
                // 今年的这个月
                _day = Integer.parseInt(_date.substring(8,10));
                countDay.set(_day-1, countDay.get(_day-1)+1);
            }

        }

        // achromate
        achromateValue = manager.readAchromate();

        // set time
        if (allTime.size()==0) latestTime.setValue("您还未进行视力测试");
        else latestTime.setValue("最近测量时间: "+sdf.format(allTime.get(allTime.size()-1)));
    }



}
