package com.example.vision;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.vision.common.BackListener;
import com.example.vision.databinding.ActivityAstigmatismBinding;
import com.google.android.material.card.MaterialCardView;
import com.skydoves.transformationlayout.TransformationLayout;
import com.skydoves.transformationlayout.TransitionExtensionKt;

import java.util.ArrayList;

public class AstigmatismActivity extends AppCompatActivity {

    ActivityAstigmatismBinding binding;
    AstigmatismViewModel viewModel;
    Typeface font;

    String TAG = "MikeDean";
    private String COLOR = "#000000";
    private int RADIUS = 250;
    private ArrayList<MaterialCardView> btns = new ArrayList<>();
    private ArrayList<MaterialCardView> btns_show = new ArrayList<>();
    private ArrayList<Boolean> flags = new ArrayList<>();
    int delta_angle = 15;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        TransformationLayout.Params params = getIntent().getParcelableExtra("TransformationParams");
        TransitionExtensionKt.onTransformationEndContainer(this, params);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_astigmatism);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        viewModel = ViewModelProviders.of(this).get(AstigmatismViewModel.class);
        binding.setData(viewModel);
        binding.setLifecycleOwner(this);

        initView();
        setListener();

    }

    private void update_font(){
        binding.visionStart.setTypeface(font);
        binding.visionStart.setText(getResources().getString(viewModel.getCenterShow()));
    }

    // ?????????????????????
    private void hidden_line(boolean hidden){
        int state = View.VISIBLE;
        if (hidden) state = View.INVISIBLE;
        for(int i=0;i<btns.size();i++){
            btns.get(i).setVisibility(state);
            btns_show.get(i).setVisibility(state);
        }
    }
    private void ban_click(boolean click){
        for (int i=0;i<btns.size();i++){
            btns.get(i).setClickable(click);
            btns_show.get(i).setClickable(click);
        }
    }

    private void initView(){
        // ????????????
        font = Typeface.createFromAsset(getAssets(), "iconfont.ttf");

        // ??????????????????
        for(float angle=0;angle<360;angle+=delta_angle){
            btns.add(new MaterialCardView(this));
            btns_show.add(new MaterialCardView(this));
            flags.add(false);
        }
        int cnt = 0;
        for(float angle=0;angle<360;angle+=delta_angle){
            // ?????????????????????
            btns.get(cnt).setId(View.generateViewId());
            btns.get(cnt).setRotation(angle);
            binding.layout.addView(btns.get(cnt), new ConstraintLayout.LayoutParams(3,260));
            setConstrain(binding.layout, btns.get(cnt), binding.visionStart, angle, true);

            // ?????????????????????
            btns_show.get(cnt).setId(View.generateViewId());
            btns_show.get(cnt).setRotation(angle);
            binding.layout.addView(btns_show.get(cnt), new ConstraintLayout.LayoutParams(30,280));
            setConstrain(binding.layout, btns_show.get(cnt), binding.visionStart, angle, false);
            btns_show.get(cnt).setCardBackgroundColor(Color.parseColor("#00E93F04")); // ????????????????????????????????????????????????????????????
            btns_show.get(cnt).setCardElevation(0);                                             // ??????????????????
            btns_show.get(cnt).setElevation(0);
            btns_show.get(cnt).setRadius(200);                                                  // ????????????

            cnt += 1;
        }

        // ???????????????
        // ????????????
        hidden_line(true);
        viewModel.reset();
        update_font();
    }

    // ???????????????
    private void clearFlag(){
        for(int i=0;i<flags.size();i++) flags.set(i, false);
    }

    // ??????????????????
    private void _update_color(){
        for(int i=0;i<btns.size();i++){
            if(flags.get(i)){
                btns.get(i).setBackgroundColor(Color.parseColor("#047029"));
                btns_show.get(i).setCardBackgroundColor(Color.parseColor("#99047029"));
            }else{
                btns.get(i).setBackgroundColor(Color.parseColor(COLOR));
                btns_show.get(i).setCardBackgroundColor(Color.parseColor("#00047029"));
            }
        }
    }

    // ????????????
    private void setDirection(){
        for(int i=0;i<flags.size();i++){
            if(flags.get(i)==true){
                viewModel.setDirection(delta_angle*i);
                save();
                break;
            }
        }
    }

    private void setListener(){
        /*
        * 24??????
        * 0-12
        * 1-13
        * ...
        * 11-23
        * */
        // ??????????????????
        View.OnClickListener lines_listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFlag();
                int step = btns.size() / 2;
                for (int i=0;i<btns.size()/2;i++){
                    if (view.getId()==btns.get(i).getId() || view.getId()==btns_show.get(i).getId()){
                        Log.v(TAG,String.valueOf(i));
                        flags.set(i,!flags.get(i));
                        flags.set(i+step,flags.get(i));
                        _update_color();
                        return;
                    }
                }
                for (int i=step;i<btns.size();i++){
                    if (view.getId()==btns.get(i).getId() || view.getId()==btns_show.get(i).getId()){
                        Log.v(TAG,String.valueOf(i));
                        flags.set(i,!flags.get(i));
                        flags.set(i-step,flags.get(i));
                        _update_color();
                        return;
                    }
                }
            }
        };
        for (int i=0;i<btns.size();i++){
            btns.get(i).setOnClickListener(lines_listener);
            btns_show.get(i).setOnClickListener(lines_listener);
        }

        // ????????????????????????
        binding.visionStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.nextStage();
                hidden_line(false);
                if (viewModel.canClick()) ban_click(true);
                else ban_click(false);
                if (viewModel.getStage()==0){
                    clearFlag();
                    _update_color();
                }
            }
        });
        // ??????1
        binding.btnChoose1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDirection();
                viewModel.nextStage();
                update_font();
                if (viewModel.canClick()) ban_click(true);
                else ban_click(false);
            }
        });
        // ??????2
        binding.btnChoose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ?????????????????????
                viewModel.setDirection(-1);
                save();
                viewModel.jumpState(viewModel.getMAX_STAGE());
                update_font();
                if (viewModel.canClick()) ban_click(true);
                else ban_click(false);
            }
        });

        // left_eye
        binding.imgLeftEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.imgLeftEye.setImageResource(R.drawable.eye_check);
                binding.imgRightEye.setImageResource(R.drawable.eye_uncheck);
                viewModel.setCurrentEye(0);
                binding.astigmatismSetting.button2.setImageResource(R.drawable.left_eye);
            }
        });
        // right_eye
        binding.imgRightEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.imgLeftEye.setImageResource(R.drawable.eye_uncheck);
                binding.imgRightEye.setImageResource(R.drawable.eye_check);
                binding.astigmatismSetting.button2.setImageResource(R.drawable.right_eye);
                viewModel.setCurrentEye(1);
            }
        });

        // help
        // exchange eyes
        binding.astigmatismSetting.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewModel.getCurrentEye()==1){
                    ToastUtil.makeText(AstigmatismActivity.this,"??????????????????");
                    viewModel.setCurrentEye(0);
                    binding.imgLeftEye.setImageResource(R.drawable.eye_check);
                    binding.imgRightEye.setImageResource(R.drawable.eye_uncheck);
                    //Toast.makeText(MainActivity.this,,Toast.LENGTH_SHORT).show();
                    binding.astigmatismSetting.button2.setImageResource(R.drawable.left_eye);
                }else{
                    ToastUtil.makeText(AstigmatismActivity.this,"??????????????????");
                    viewModel.setCurrentEye(1);
                    binding.imgLeftEye.setImageResource(R.drawable.eye_uncheck);
                    binding.imgRightEye.setImageResource(R.drawable.eye_check);
                    //Toast.makeText(MainActivity.this,,Toast.LENGTH_SHORT).show();
                    binding.astigmatismSetting.button2.setImageResource(R.drawable.left_eye);
                }
            }
        });
        // help
        binding.astigmatismSetting.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(AstigmatismActivity.this);
                builder.setTitle("?????????");
                builder.setMessage(R.string.astigmatism_help);
                builder.setNeutralButton("????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AstigmatismActivity.this,"???????????????????????????",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });


        // back
        binding.btnBack.setOnClickListener(new BackListener());
    }

    private void save(){
        MainActivity.visionDBManager.insert(String.valueOf(System.currentTimeMillis()), "2", String.valueOf(viewModel.getCurrentEye()),String.valueOf(viewModel.getDirection()));
    }

    private void setConstrain(ConstraintLayout layout, View trgView, View center, float angle, boolean setColor){
        /*
         * layout: ????????????
         * trgView: ??????????????????
         * center: ??????
         * angle: ????????????
         * */
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        if (setColor) trgView.setBackgroundColor(Color.parseColor(COLOR));
        trgView.setClickable(false);
        set.constrainCircle(trgView.getId(), center.getId(), RADIUS, (int)angle);
        set.applyTo(layout);
    }
}
