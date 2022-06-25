package com.example.vision;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.vision.common.BackListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uuzuche.lib_zxing.activity.CaptureActivity;

public class ScanActivity extends CaptureActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //沉浸式状态栏
//        ImmersionBar.with(this).init();
        // et words black
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        FloatingActionButton btn = findViewById(R.id.buttonClose);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ScanActivity.this, MainActivity.class);
//                intent.putExtra("account", MainActivity.UserName);
//                startActivity(intent);
//                finish();
//            }
//        });
        btn.setOnClickListener(new BackListener());
    }
}
