package com.example.vision;

import com.example.vision.login.LoginActivity;

public class SettingConfig {
    private boolean useAudio;    //
    private int distance;       // 测试距离
    public SettingConfig(boolean useAudio, int distance){
        this.useAudio = useAudio;
        this.distance = distance;
    }

    private String convert2db(){
        String ans;
        if (useAudio==true) ans="1-";
        else ans="0-";
        return ans+String.valueOf(distance);
    }

    // 0-0 useAudio-distance
    public void setDistance(int distance) {
        this.distance = distance;
        LoginActivity.userDBManager.update(
                MainActivity.UserName,
                LoginActivity.userDBManager.getPassword(MainActivity.UserName),
                convert2db(),
                LoginActivity.userDBManager.getIsSaved(MainActivity.UserName)
        );
    }

    public void setUseAudio(boolean useAudio) {
        this.useAudio = useAudio;
        LoginActivity.userDBManager.update(
                MainActivity.UserName,
                LoginActivity.userDBManager.getPassword(MainActivity.UserName),
                convert2db(),
                LoginActivity.userDBManager.getIsSaved(MainActivity.UserName)
        );
    }

    public int getDistance() {
        return distance;
    }
    public boolean getUseAudio(){
        return useAudio;
    }
}
