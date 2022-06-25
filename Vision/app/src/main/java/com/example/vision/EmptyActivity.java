package com.example.vision;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.vision.databinding.ActivityEmptyBinding;
import com.skydoves.transformationlayout.TransformationLayout;
import com.skydoves.transformationlayout.TransitionExtensionKt;

public class EmptyActivity extends AppCompatActivity {

    ActivityEmptyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
//        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
//        getWindow().setSharedElementsUseOverlay(false);
        TransformationLayout.Params params = getIntent().getParcelableExtra("TransformationParams");
        TransitionExtensionKt.onTransformationEndContainer(this, params);

        //加载布局文件
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_empty);


        //获取屏幕尺寸
        Point p = new Point();
        WindowManager windowManager = (WindowManager) this.getSystemService(this.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(p);
        binding.title.setGuidelinePercent((float) (((double)(p.y+binding.textTitle.getPaddingTop())*0.14)/p.y));

        //设置状态栏文字为深色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


//        //******自定义代码
        String name = getIntent().getStringExtra("Name");
        TextView textView = findViewById(R.id.text_title);
        textView.setText(name);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmptyActivity.this.finish();
            }
        });

    }
}