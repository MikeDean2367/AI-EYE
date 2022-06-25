package com.example.vision;

import static com.example.vision.login.LoginActivity.userDBManager;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.vision.MyBroadCast.MyBroadCastReceiver;
import com.example.vision.database.DBHelper;
import com.example.vision.database.DBUserDataManager;
import com.example.vision.database.DBVisionDataManager;
import com.example.vision.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.skydoves.transformationlayout.TransformationLayout;
import com.skydoves.transformationlayout.TransitionExtensionKt;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
//    public static DBUserDataManager userDBManager;
    public static String UserName;
    public static DBVisionDataManager visionDBManager;
    public static SettingConfig settingConfig;
    public static String IP;
    public static int PORT;
    public static String BROADCAST_NAME = "com.example.pad.VISION_DATA";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (getIntent().getExtras().containsKey("signal")){
            TransitionExtensionKt.onTransformationStartContainer(this);
            Log.v("MainActivity", "Not have");
        }else{
            TransformationLayout.Params params = getIntent().getParcelableExtra("TransformationParams");
            TransitionExtensionKt.onTransformationEndContainer(this, params);
        }

        //卡片转场
//        TransitionExtensionKt.onTransformationStartContainer(this);

        //加载布局文件
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        //设置状态栏文字为深色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //找到底部导航对象
        BottomNavigationView bottomNavigationView = binding.nav;

        //找到当前界面的NavHost对象
        NavController controller = Navigation.findNavController(this,R.id.fragment);
        //底部导航和navigation进行绑定
        NavigationUI.setupWithNavController(bottomNavigationView,controller);

        // just for test!
        init_db();

        // BroadCast
        registerBroadCast();
    }

    private void registerBroadCast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_NAME);
        MyBroadCastReceiver receiver = new MyBroadCastReceiver();
        receiver.setOnReceivedBroadCastListener(new MyBroadCastReceiver.OnReceivedBroadCast() {
            @Override
            public void deal(String data) {
                // received data
                // "5.1,0"      value:5.1, 0:left
                visionDBManager.insert(String.valueOf(System.currentTimeMillis()), "0", data.split(",")[1], data.split(",")[0]);
                Log.v("MainActivityBroadCast", data);
            }
        });
        registerReceiver(receiver, filter);
    }

    private void init_db(){
        Intent intent = getIntent();
        String user = intent.getExtras().getString("account");
        UserName = user;
//        String testUser = "Test";
//        String testPassword = "123";
        String dataBaseName = "AIEYE.db";
        visionDBManager = new DBVisionDataManager(getApplicationContext(), dataBaseName, user);
//        userDBManager = new DBUserDataManager(getApplicationContext(), dataBaseName);
//        if(userDBManager.check(testUser)) userDBManager.add(testUser, testPassword);
        String _config = userDBManager.getConfig(user);     // 0-0 useAudio-distance
        Log.v("hhh", _config);
        boolean useAudio = Integer.parseInt(_config.split("-")[0])==1;
        int distance = Integer.parseInt(_config.split("-")[1]);

        settingConfig = new SettingConfig(useAudio, distance);
    }
}
