package com.example.vision;

import android.Manifest;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.example.vision.common.BackListener;
import com.skydoves.transformationlayout.TransformationLayout;
import com.skydoves.transformationlayout.TransitionExtensionKt;

import java.util.ArrayList;

public class LightActivity extends AppCompatActivity {
    private static final String TAG = "LightActivityMike";
    private Compass compass;
    private ConstraintLayout compassView;
    private TextView curLight, meanLight, tips1, tips2;
    private TextView coordView;
    private Button btn_start;
    private ImageButton btn_back;
    private boolean useDarkTheme;
    private AlertDialog dialogBuilder;
    ArrayList<Button> btns = new ArrayList<>();
    ArrayList<Boolean> flags = new ArrayList<>();
    ArrayList<Float> lightValues = new ArrayList<>();        // light
    GPSTracker gps;
    private boolean isFirstRun;
    private float currentAzimuth;
    final int RADIUS = 300;
    final String COLOR = "#EEEEEE";
    ConstraintLayout layout;
    ImageView center;
    boolean status = false;

    // Standard
    final float StandardLight = 600;        // 基准值
    final float StandardLightBias = 300;    // 浮动范围
    final float StandardVar = 100;           // 方差

    // 这边需要注意以下，如果不等分360份的话，在该颜色的时候需要映射一遍才能对应索引，否则会下标越界的

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TransformationLayout.Params params = getIntent().getParcelableExtra("TransformationParams");
        TransitionExtensionKt.onTransformationEndContainer(this, params);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        // 设置状态栏的颜色为深色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // 获取控件名称
        initViews();
        setListener();

        setupCompass();
        getLocation();
    }

    private void checkPermissions() {
        int permission1 = PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission2 = PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission1 == PermissionChecker.PERMISSION_GRANTED || permission2 == PermissionChecker.PERMISSION_GRANTED) {
            //good to go
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }

    private void getLocation() {
        gps = new GPSTracker(this, coordView);
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            coordView.setText(doubToDMS(latitude, true) + "  " + doubToDMS(longitude, false));
        } else {
            coordView.setText("111");
            //Utils.showToast("Go to Material Compass settings to change location permission", Toast.LENGTH_SHORT, getApplicationContext());
        }
    }

    private void setListener(){
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status==false){
                    btn_start.setVisibility(View.INVISIBLE);
                    status = true;
                    meanLight.setText("测试中");
                }
            }
        });
        btn_back.setOnClickListener(new BackListener());
    }

    private void judgeLight(){
        float sum=lightValues.get(0), var = 0;
        for(int i=1;i<lightValues.size();i++){
            sum += lightValues.get(i);
        }
        float average = sum / lightValues.size();
        meanLight.setText(String.format("%.1f", average)+"LX");

        for(int i=0;i<lightValues.size();i++){
            var += (lightValues.get(i)-average) * (lightValues.get(i)-average);
        }
        var /= lightValues.size();
        if (average>=StandardLight-StandardLightBias && average<=StandardLight+StandardLightBias){
            tips2.setText("当前环境光线强度适中，适合测试");
        }else{
            if (average<StandardLight-StandardLightBias) tips2.setText("当前环境亮度过暗，不适合测试");
            if (average>StandardLightBias+StandardLight) tips2.setText("当前环境亮度过强，不适合测试");
        }
        if (var<StandardVar) tips1.setText("当前环境光线均匀，适合测试");
        else tips1.setText("当前环境光线不均匀，不适合测试");
        Log.v(TAG, String.valueOf(average)+" HHH "+String.valueOf(var));
    }

    private void setConstrain(ConstraintLayout layout, Button trgView, View center, float angle){
        /*
         * layout: 目标布局
         * trgView: 待添加的控件
         * center: 圆心
         * angle: 旋转角度
         * */
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        trgView.setBackgroundColor(Color.parseColor(COLOR));
        trgView.setClickable(false);
        set.constrainCircle(trgView.getId(), center.getId(), RADIUS, (int)angle);
//        layout.setConstraintSet(set);
        set.applyTo(layout);
    }

    private void initViews() {
//        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        layout = findViewById(R.id.compass_layout);
//        compassView = (ImageView) findViewById(R.id.image_dial);
        center = (ImageView) findViewById(R.id.image_dial);// 中心是图片
        int step = 1;
        for(float angle=0;angle<360;angle+=step){
            btns.add(new Button(this));
            flags.add(false);
        }
        int cnt = 0;
        for(float angle=0;angle<360;angle+=step){
            btns.get(cnt).setId(View.generateViewId());
            btns.get(cnt).setRotation(angle);
            if ((int)angle % 20 == 0)
                layout.addView(btns.get(cnt), new ConstraintLayout.LayoutParams(2,40));
            else
                layout.addView(btns.get(cnt), new ConstraintLayout.LayoutParams(2,20));
            setConstrain(layout, btns.get(cnt), findViewById(R.id.image_dial), angle);
            cnt += 1;
        }

        compassView = layout;
        curLight = findViewById(R.id.curLight);
        meanLight = findViewById(R.id.meanLight);
        coordView = findViewById(R.id.noUse);
        btn_start = findViewById(R.id.btn_start);
        btn_back = findViewById(R.id.btn_back);
        tips1 = findViewById(R.id.text_tips_1);
        tips2 = findViewById(R.id.text_tips_2);

        for (int i=0;i<360/step;i++){
            lightValues.add(0F);
        }

    }

    private void updateColor(){
        if(status){
            for(int i=0;i<btns.size();i++){
                if (flags.get(i)==true){
                    btns.get(i).setBackgroundColor(Color.parseColor("#FF0000"));
                }
            }
        }else{
            for(int i=0;i<btns.size();i++){
                flags.set(i, false);
                btns.get(i).setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
    }

    private boolean idDone(){
        if(status){
            for(int i=0;i<btns.size();i++){
                if(flags.get(i)==false) return false;
            }
            return true;
        }
        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
        getLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        compass.stop();
    }

    private void setupCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(float azimuth, float light) {
                adjustArrow(azimuth, light);
            }
        };
        compass.setListener(cl);
    }


    // 旋转动画
    private void adjustArrow(float azimuth, float light) {
        Log.d(TAG, "will set rotation from " + currentAzimuth + " to "
                + azimuth);

        Animation animator = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;
        if ((int) currentAzimuth<flags.size())
            flags.set((int) currentAzimuth, true);
        updateColor();
        if (idDone()){
            status = false;
            judgeLight();
            btn_start.setVisibility(View.VISIBLE);
            for (int i=0;i<lightValues.size();i++){
                lightValues.set(i,0F);
            }
        }
        if (status) lightValues.set((int) currentAzimuth, light);   // add light
        curLight.setText(String.format("%.1f",light)+"LX");
        animator.setDuration(500);
        animator.setRepeatCount(0);
        animator.setFillAfter(true);

        compassView.startAnimation(animator);
    }

    private String doubToDMS(double value, boolean isLat) {
        String direction;
        int degrees = 0, minutes = 0, seconds = 0;
        double val = Math.abs(value), min = 0;
        degrees = (int) val;
        min = (val - (double) degrees) * 60;
        minutes = (int) min;
        seconds = (int) ((min - (double) minutes) * 60);

        if (isLat) {
            if (value < 0) {
                direction = "S";
            } else {
                direction = "N";
            }
        } else {
            if (value < 0) {
                direction = "W";
            } else {
                direction = "E";
            }
        }

        return degrees + "° " + minutes + "' " + seconds + "\" " + direction;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    LightUtils.showToast("Permission denied to access your location", Toast.LENGTH_LONG, this);
                }
                return;
            }
        }
    }
}
