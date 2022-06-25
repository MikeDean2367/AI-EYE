package com.example.vision;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vision.common.BackListener;
import com.skydoves.transformationlayout.TransformationLayout;
import com.skydoves.transformationlayout.TransitionExtensionKt;

public class TipsPageActivity extends AppCompatActivity {

    WebView web;
    TextView title;
    ImageButton btn_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        TransformationLayout.Params params = getIntent().getParcelableExtra("TransformationParams");
        TransitionExtensionKt.onTransformationEndContainer(this, params);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipspage);

        // 设置状态栏的颜色为深色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        findView();
        load_view();
    }

    void load_view(){
        Intent intent = getIntent();
        web.loadUrl(intent.getStringExtra("url"));
        title.setText(intent.getStringExtra("title"));
    }

    void findView(){
        web = findViewById(R.id.web);
        title = findViewById(R.id.textView);
        web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        web.getSettings().setBlockNetworkImage(false);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setAllowContentAccess(true);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new BackListener());
    }
}
