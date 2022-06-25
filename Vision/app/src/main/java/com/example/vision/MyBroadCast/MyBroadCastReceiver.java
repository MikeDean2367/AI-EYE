package com.example.vision.MyBroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadCastReceiver extends BroadcastReceiver {

    OnReceivedBroadCast receivedBroadCast;

    public interface OnReceivedBroadCast{
        void deal(String data);
    }

    public void setOnReceivedBroadCastListener(OnReceivedBroadCast receivedBroadCast){
        this.receivedBroadCast = receivedBroadCast;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        receivedBroadCast.deal(intent.getExtras().getString("data"));
    }
}
