package com.example.vision.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.InterpolatorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.vision.MainActivity;
import com.example.vision.R;
import com.example.vision.SettingConfig;
import com.example.vision.database.DBUserDataManager;
import com.example.vision.database.DBVisionDataManager;
import com.google.android.material.card.MaterialCardView;
import com.skydoves.transformationlayout.TransformationLayout;
import com.skydoves.transformationlayout.TransitionExtensionKt;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    // 控件
    Button btn_clear_pwd, btn_clear_acc, btn_see_pwd, btn_login;
    EditText text_account, text_pwd;

    Button btn_confirm_clear_pwd, btn_confirm_see_pwd, btn_autologin;
    EditText text_confirm_pwd;

    TextView text_tips1, text_tips2, text_flag;

    LinearLayout confirm_layout;

    RecyclerView recycler;

    MaterialCardView card_user_manage, card_user_list;
    LottieAnimationView lottieLogin;
    CardView loginBlock;

    // 其他
    String TAG = "MikeDeanTest:";
    Typeface font;
    String curAccount, curPwd;
    TransformationLayout transformationLayoutAuto, transformationLayoutNormal;
    private Activity activity;

    ArrayList<String> autoLoginUsers = new ArrayList<>();
    LoginAdapter adapter;

    // database
    public static DBUserDataManager userDBManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (getIntent()!=null && getIntent().getExtras()!=null){
            if (getIntent().getExtras().containsKey("TransformationParams")){
                TransformationLayout.Params params = getIntent().getParcelableExtra("TransformationParams");
                TransitionExtensionKt.onTransformationEndContainer(this, params);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        setListener();
        convert2login();
        init_db();
        setAdapter();
        if (getIntent()!=null && getIntent().getExtras()!=null){
            if (!getIntent().getExtras().containsKey("Exit")){
                autoLogin();
            }else{
                loginBlock.setVisibility(View.INVISIBLE);
            }
        }else{
            autoLogin();
        }
    }

    private void init_db(){
        String dataBaseName = "AIEYE.db";
        userDBManager = new DBUserDataManager(getApplicationContext(), dataBaseName);

    }

    private void initView(){
        // 自上而下
        text_flag = findViewById(R.id.textFlag);            // 指示当前状态

        card_user_manage = findViewById(R.id.card_user_manage); // 用户管理
        card_user_list = findViewById(R.id.card_user_list);
        card_user_list.setVisibility(View.INVISIBLE);

        text_account = findViewById(R.id.text_account);     // 账号
        btn_clear_acc = findViewById(R.id.btn_clear_acc);   // 清除账号

        text_pwd = findViewById(R.id.text_password);        // 密码框
        btn_clear_pwd = findViewById(R.id.btn_clear_pwd);   // 清空密码框
        btn_see_pwd = findViewById(R.id.btn_see_pwd);       // 显示密码

        text_confirm_pwd = findViewById(R.id.text_confirm_password);
        btn_confirm_clear_pwd = findViewById(R.id.btn_confirm_clear_pwd);
        btn_confirm_see_pwd = findViewById(R.id.btn_confirm_see_pwd);
        confirm_layout = findViewById(R.id.layout_confirm);

        btn_autologin = findViewById(R.id.btn_autologin);
        transformationLayoutAuto = findViewById(R.id.item_transition_auto);
        btn_login = findViewById(R.id.btn_login);
        transformationLayoutNormal = findViewById(R.id.item_transition_normal);

        text_tips1 = findViewById(R.id.text_tips_1);
        text_tips2 = findViewById(R.id.text_tips_2);

        activity = this;

        recycler = findViewById(R.id.user_list);

        loginBlock = findViewById(R.id.block_login);
        lottieLogin = findViewById(R.id.lottie_login);

    }

    // 设置监听器
    private void setListener(){
        font = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        // 账号框
        text_account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0) {// 有数据
                    btn_clear_acc.setVisibility(View.VISIBLE);
                    Log.v(TAG, text_pwd.getText().toString());
                } else {
                    btn_clear_acc.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // 密码框
        text_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0){
                    btn_clear_pwd.setVisibility(View.VISIBLE);
                    btn_see_pwd.setVisibility(View.VISIBLE);
                } else{
                    btn_clear_pwd.setVisibility(View.INVISIBLE);
                    btn_see_pwd.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        text_confirm_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0){
                    btn_confirm_clear_pwd.setVisibility(View.VISIBLE);
                    btn_confirm_see_pwd.setVisibility(View.VISIBLE);
                } else{
                    btn_confirm_clear_pwd.setVisibility(View.INVISIBLE);
                    btn_confirm_see_pwd.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        text_account.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.v(TAG,text_account.getText().toString());
                if(b==false) btn_clear_acc.setVisibility(View.INVISIBLE);
                else{
                    if(text_account.getText().toString().equals("")==false) btn_clear_acc.setVisibility(View.VISIBLE);
                }
            }
        });
        text_pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b==false){
                    btn_clear_pwd.setVisibility(View.INVISIBLE);
                    btn_see_pwd.setVisibility(View.INVISIBLE);
                }else{
                    if(text_pwd.getText().toString().equals("")==false){
                        btn_clear_pwd.setVisibility(View.VISIBLE);
                        btn_see_pwd.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        text_confirm_pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b==false){
                    btn_confirm_clear_pwd.setVisibility(View.INVISIBLE);
                    btn_confirm_see_pwd.setVisibility(View.INVISIBLE);
                }else{
                    if(text_confirm_pwd.getText().toString().equals("")==false){
                        btn_confirm_clear_pwd.setVisibility(View.VISIBLE);
                        btn_confirm_see_pwd.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        // 按钮
        // 设置按钮监听
        btn_clear_pwd.setOnClickListener(new View.OnClickListener() {// 密码框清空
            @Override
            public void onClick(View view) {
                text_pwd.setText("");
                btn_clear_pwd.setVisibility(View.INVISIBLE);
            }
        });
        btn_confirm_clear_pwd.setOnClickListener(new View.OnClickListener() {// 密码框清空
            @Override
            public void onClick(View view) {
                text_confirm_pwd.setText("");
                btn_confirm_clear_pwd.setVisibility(View.INVISIBLE);
            }
        });
        btn_clear_acc.setOnClickListener(new View.OnClickListener() {// 账号框清空
            @Override
            public void onClick(View view) {
                text_account.setText("");
                btn_clear_acc.setVisibility(View.INVISIBLE);
            }
        });
        btn_see_pwd.setOnClickListener(new View.OnClickListener() {// 是否查看
            @Override
            public void onClick(View view) {
                if(btn_see_pwd.getText().equals(getResources().getString(R.string.can_see))==true){// 说明此时为可见，下面的操作让其不可见
                    btn_see_pwd.setTypeface(font);
                    btn_see_pwd.setText(getResources().getString(R.string.not_see));
                    text_pwd.setInputType(0x81);// 设置密码不可见
                }else {
                    btn_see_pwd.setTypeface(font);
                    btn_see_pwd.setText(getResources().getString(R.string.can_see));
                    text_pwd.setInputType(0x90);// 设置密码不可见
                }
            }
        });
        btn_confirm_see_pwd.setOnClickListener(new View.OnClickListener() {// 是否查看
            @Override
            public void onClick(View view) {
                if(btn_confirm_see_pwd.getText().equals(getResources().getString(R.string.can_see))==true){// 说明此时为可见，下面的操作让其不可见
                    btn_confirm_see_pwd.setTypeface(font);
                    btn_confirm_see_pwd.setText(getResources().getString(R.string.not_see));
                    text_confirm_pwd.setInputType(0x81);// 设置密码不可见
                }else {
                    btn_confirm_see_pwd.setTypeface(font);
                    btn_confirm_see_pwd.setText(getResources().getString(R.string.can_see));
                    text_confirm_pwd.setInputType(0x90);// 设置密码不可见
                }
            }
        });

        card_user_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long duration = 150;
                ScaleAnimation scaleAnimation;
                AlphaAnimation alphaAnimation;
                AnimationSet animationSet = new AnimationSet(true);
                if (card_user_list.getVisibility()==View.INVISIBLE) {
                    scaleAnimation = new ScaleAnimation(0.5F,1,0,1, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.3F);
                    scaleAnimation.setDuration(duration);
                    alphaAnimation = new AlphaAnimation(0.5F,1);
                    alphaAnimation.setDuration(duration);
                    animationSet.addAnimation(scaleAnimation);
                    animationSet.addAnimation(alphaAnimation);
                    card_user_list.setVisibility(View.VISIBLE);
                    card_user_list.startAnimation(animationSet);
                } else {
                    scaleAnimation = new ScaleAnimation(1,0.5F,1,0, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.3F);
                    scaleAnimation.setDuration(duration);
                    alphaAnimation = new AlphaAnimation(1,0.5F);
                    alphaAnimation.setDuration(duration);
                    animationSet.addAnimation(scaleAnimation);
                    animationSet.addAnimation(alphaAnimation);
                    card_user_list.startAnimation(animationSet);
                    card_user_list.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    // 设置Adapter
    private void setAdapter(){
        autoLoginUsers.clear();
        userDBManager.getAutoLoginUser(autoLoginUsers);

        LinearLayoutManager verticalLinearLayoutManager = new LinearLayoutManager(this);
        verticalLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(verticalLinearLayoutManager);

        adapter = new LoginAdapter(autoLoginUsers);
        adapter.setListener(new LoginAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(String UserName) {
                card_user_list.setVisibility(View.INVISIBLE);
                text_account.setText(UserName);
                text_pwd.setText(userDBManager.getPassword(UserName));
                long duration = 150;
                ScaleAnimation scaleAnimation;
                AlphaAnimation alphaAnimation;
                AnimationSet animationSet = new AnimationSet(true);
                scaleAnimation = new ScaleAnimation(1,0.5F,1,0, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.3F);
                scaleAnimation.setDuration(duration);
                alphaAnimation = new AlphaAnimation(1,0.5F);
                alphaAnimation.setDuration(duration);
                animationSet.addAnimation(scaleAnimation);
                animationSet.addAnimation(alphaAnimation);
                card_user_list.startAnimation(animationSet);
                card_user_list.setVisibility(View.INVISIBLE);
            }
        });
        recycler.setAdapter(adapter);

    }

    // 自动登陆
    private void autoLogin(){
        String name = userDBManager.getAutoLogin();
        if (name!=null){
//            Bundle bundle = transformationLayoutAuto.withActivity(activity, "transitionNameA");
            loginBlock.setVisibility(View.VISIBLE);
            lottieLogin.playAnimation();
            userDBManager.update(name, userDBManager.getPassword(name), userDBManager.getConfig(name), "2");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("signal","signal");
            intent.putExtra("account", name);
            startActivity(intent);
//            startActivity(intent, bundle);
        }else {
            loginBlock.setVisibility(View.INVISIBLE);
            lottieLogin.pauseAnimation();
        }
    }

    private boolean login(){
        curAccount = text_account.getText().toString();
        curPwd = text_pwd.getText().toString();
        if (curAccount==null || curPwd==null) return false;
        if(userDBManager.check(curAccount)==false){
            // exist
            if (userDBManager.getPassword(curAccount).equals(curPwd)){
                return true;
            }else return false;
        }else{
            // not exist
            return false;
        }
    }

    private boolean register(){
        String confirmPwd = text_confirm_pwd.getText().toString();
        curPwd = text_pwd.getText().toString();
        curAccount = text_account.getText().toString();
        if (confirmPwd.equals(curPwd)){
            // equal
            if (!userDBManager.check(curAccount)){
                // exist
                Toast.makeText(getApplicationContext(), "账户名已存在", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                userDBManager.add(curAccount, curPwd);
                return true;
            }
        }else{
            Toast.makeText(getApplicationContext(), "两次密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 切换登录界面
    private void convert2login(){
        text_account.setText("");
        text_confirm_pwd.setText("");
        text_pwd.setText("");

        card_user_manage.setVisibility(View.VISIBLE);
        card_user_list.setVisibility(View.INVISIBLE);

        text_flag.setText("登录");
        text_tips1.setText("还未拥有账户?");
        text_tips2.setText("点击注册");
        btn_autologin.setVisibility(View.VISIBLE);
        confirm_layout.setVisibility(View.INVISIBLE);
        btn_login.setText("登录");

        text_tips2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convert2register();
            }
        });

        btn_autologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (login()){
                    // login successfully
//                    loginBlock.setVisibility(View.VISIBLE);
//                    lottieLogin.playAnimation();
                    Bundle bundle = transformationLayoutAuto.withActivity(activity, "transitionNameA");
                    userDBManager.update(curAccount, curPwd, userDBManager.getConfig(curAccount), "2");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("TransformationParams", transformationLayoutAuto.getParcelableParams());
                    intent.putExtra("account", curAccount);
                    startActivity(intent, bundle);
//                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"账号或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (login()){
                    // login successfully
                    Bundle bundle = transformationLayoutNormal.withActivity(activity, "transitionNameA");
                    userDBManager.update(curAccount, curPwd, userDBManager.getConfig(curAccount), "0");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("TransformationParams", transformationLayoutNormal.getParcelableParams());
                    intent.putExtra("account", curAccount);
                    startActivity(intent, bundle);
//                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"账号或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 切换注册界面
    private void convert2register(){

        card_user_list.setVisibility(View.INVISIBLE);
        card_user_manage.setVisibility(View.INVISIBLE);

        text_account.setText("");
        text_confirm_pwd.setText("");
        text_pwd.setText("");
        text_flag.setText("注册");
        text_tips1.setText("已有账号?");
        text_tips2.setText("点击登录");
        btn_autologin.setVisibility(View.INVISIBLE);
        confirm_layout.setVisibility(View.VISIBLE);
        btn_login.setText("注册");

        text_tips2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convert2login();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(register()){
                    convert2login();
                    text_account.setText(curAccount);
                    text_pwd.setText(curPwd);
                }
            }
        });
    }
}
