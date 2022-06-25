package com.example.vision.information;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.L;
import com.example.vision.MainActivity;
import com.example.vision.R;
import com.example.vision.common.BackListener;
import com.example.vision.login.LoginActivity;
import com.skydoves.transformationlayout.TransformationLayout;
import com.skydoves.transformationlayout.TransitionExtensionKt;

public class InformationActivity extends AppCompatActivity {

    // 控件
    EditText text_pwd;
    EditText text_confirm_pwd;
    TextView text_name;

    Button btn_clear_pwd, btn_see_pwd;
    Button btn_confirm_clear_pwd, btn_confirm_see_pwd;
    Button btn_modify, btn_exit_login;
    ImageButton btn_back;

    TransformationLayout transformationLayout;

    // Other
    Typeface font;
    Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        TransformationLayout.Params params = getIntent().getParcelableExtra("TransformationParams");
        TransitionExtensionKt.onTransformationEndContainer(this, params);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        //设置状态栏文字为深色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initView();
        setListener();

        activity = this;
    }

    private void initView(){
        text_pwd = findViewById(R.id.text_password);        // 密码框
        btn_clear_pwd = findViewById(R.id.btn_clear_pwd);   // 清空密码框
        btn_see_pwd = findViewById(R.id.btn_see_pwd);       // 显示密码

        text_confirm_pwd = findViewById(R.id.text_confirm_password);
        btn_confirm_clear_pwd = findViewById(R.id.btn_confirm_clear_pwd);
        btn_confirm_see_pwd = findViewById(R.id.btn_confirm_see_pwd);

        btn_modify = findViewById(R.id.btn_modify);
        btn_exit_login = findViewById(R.id.btn_exit_login);

        text_name = findViewById(R.id.text_name);
        text_name.setText(MainActivity.UserName);

        transformationLayout = findViewById(R.id.transition_item);
        btn_back = findViewById(R.id.btn_back);
    }

    private void setListener(){
        font = Typeface.createFromAsset(getAssets(), "iconfont.ttf");

        // back
        btn_back.setOnClickListener(new BackListener());

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

        // confirm modify
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String origin = text_pwd.getText().toString();
                String newPwd = text_confirm_pwd.getText().toString();
                if (LoginActivity.userDBManager.getPassword(MainActivity.UserName).equals(origin)){
                    if (newPwd.length()>0){
                        LoginActivity.userDBManager.update(
                                MainActivity.UserName,
                                newPwd,
                                LoginActivity.userDBManager.getConfig(MainActivity.UserName),
                                LoginActivity.userDBManager.getIsSaved(MainActivity.UserName)
                                );
                        Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "新密码不符合要求", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "原密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_exit_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 如果是自动登陆来的，那么退出登陆后下次就不会自动登陆
                if (LoginActivity.userDBManager.getIsSaved(MainActivity.UserName).equals("2")){
                    LoginActivity.userDBManager.update(
                            MainActivity.UserName,
                            LoginActivity.userDBManager.getPassword(MainActivity.UserName),
                            LoginActivity.userDBManager.getConfig(MainActivity.UserName),
                            "1"
                    );
                }

                Bundle bundle = transformationLayout.withActivity(activity, "transitionNameA");
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.putExtra("TransformationParams", transformationLayout.getParcelableParams());
                intent.putExtra("Exit","exit");
                startActivity(intent, bundle);
                finish();
            }
        });
    }

}
