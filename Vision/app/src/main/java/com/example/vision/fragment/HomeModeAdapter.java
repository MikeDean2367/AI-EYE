package com.example.vision.fragment;


import static com.example.vision.fragment.HomeFragment.isTVMode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vision.AchromateActivity;
import com.example.vision.AstigmatismActivity;
import com.example.vision.EmptyActivity;
import com.example.vision.LightActivity;
import com.example.vision.MainActivity;
import com.example.vision.MeasureActivity;
import com.example.vision.R;
import com.example.vision.ScanActivity;
import com.example.vision.VisionActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.skydoves.transformationlayout.TransformationLayout;
import com.uuzuche.lib_zxing.activity.CaptureActivity;

import java.util.ArrayList;

public class HomeModeAdapter extends RecyclerView.Adapter<HomeModeAdapter.HomeModeViewHolder> {

    ArrayList<CardData> cardData = new ArrayList<>();
    Activity activity;
    Fragment fragment;
    Intent app_intent;

    Context context;

    void setFragment(Fragment fragment){this.fragment = fragment;}

    void setActivity(Activity activity){
        this.activity = activity;
    }

    void setCardData(ArrayList<CardData> cardData){
        this.cardData = cardData;
    }

    void setApp_intent(Intent intent){
        this.app_intent = intent;
    }

    @NonNull
    @Override
    public HomeModeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_home_mode,parent,false);
        return new HomeModeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeModeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.imageView.setImageResource(cardData.get(position).getImageResource());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [][]name = {{"视力测试","色盲测试","散光测试","环境亮度"},{"扫一扫","手势识别","环境亮度","距离测量"}};
                String ans;
                if(isTVMode==true){//TV
                    ans = name[1][position];
                }else{//PHONE
                    ans = name[0][position];
                }
                Bundle bundle = holder.transformationLayout.withActivity(activity, "transitionNameA");
                if(isTVMode&&position==3){
                    //打开距离测量
                    Intent intent = new Intent(activity, MeasureActivity.class);
                    intent.putExtra("TransformationParams", holder.transformationLayout.getParcelableParams());
                    activity.startActivity(intent,bundle);
                    return;
                }else if(isTVMode&&position==0){
                    //打开扫一扫
                    Intent intent = new Intent(activity, ScanActivity.class);
//                    Intent intent = new Intent(activity, CaptureActivity.class);
                    fragment.startActivityForResult(intent,111);

//                    IntentIntegrator integrator = IntentIntegrator.forFragment((android.app.Fragment)fragment);
                    return;
                }else if(isTVMode&&position==1) {
                    //打开手势识别
                    if (MainActivity.IP != null){
                        if(app_intent==null) Log.v("NOOOOOOOOOOOOOOO","NOOOOOOOOOOOOOOOOOOO");
                        app_intent.putExtra("CONNECT_IP", MainActivity.IP);
                        app_intent.putExtra("CONNECT_PORT",String.valueOf(MainActivity.PORT));
                        activity.startActivity(app_intent);
                    } else{
                        Toast.makeText(activity, "You don't set connect", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }else if(isTVMode&&position==2){// 光线
                    Intent intent = new Intent(activity, LightActivity.class);
                    intent.putExtra("TransformationParams", holder.transformationLayout.getParcelableParams());
                    activity.startActivity(intent, bundle);
                    return;
                }else if(isTVMode==false&&position==1){
                    Intent intent = new Intent(activity, AchromateActivity.class);
                    intent.putExtra("TransformationParams", holder.transformationLayout.getParcelableParams());
                    activity.startActivity(intent,bundle);
                    return;
                }else if(isTVMode==false&&position==0){//视力
                    Intent intent = new Intent(activity, VisionActivity.class);
                    intent.putExtra("TransformationParams", holder.transformationLayout.getParcelableParams());
                    activity.startActivity(intent,bundle);
                    return;
                }else if(isTVMode==false&&position==2){//散光
                    Intent intent = new Intent(activity, AstigmatismActivity.class);
                    intent.putExtra("TransformationParams", holder.transformationLayout.getParcelableParams());
                    activity.startActivity(intent, bundle);
                    return;
                }else if(isTVMode==false&&position==3){//光线
                    Intent intent = new Intent(activity, LightActivity.class);
                    intent.putExtra("TransformationParams", holder.transformationLayout.getParcelableParams());
                    activity.startActivity(intent, bundle);
                    return;
                }
                Intent intent = new Intent(activity, EmptyActivity.class);
                intent.putExtra("TransformationParams", holder.transformationLayout.getParcelableParams());
                intent.putExtra("Name",ans);
                activity.startActivity(intent, bundle);
            }
        };

        holder.cardView.setOnClickListener(listener);
        holder.button.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return cardData.size();
    }

    static class HomeModeViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        MaterialCardView cardView;
        MaterialButton button;
        TransformationLayout transformationLayout;
        public HomeModeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_img);
            button = itemView.findViewById(R.id.item_btn);
            cardView = itemView.findViewById(R.id.item_card);
            transformationLayout = itemView.findViewById(R.id.item_transition);
        }
    }

}


