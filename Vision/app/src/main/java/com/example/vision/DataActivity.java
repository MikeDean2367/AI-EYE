package com.example.vision;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.vision.common.BackListener;
import com.example.vision.databinding.ActivityDataBinding;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.material.card.MaterialCardView;
import com.skydoves.transformationlayout.TransformationLayout;
import com.skydoves.transformationlayout.TransitionExtensionKt;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class DataActivity extends AppCompatActivity {
    /*
    * 可以针对色盲用户动态更改颜色
    * */
    DataViewModel viewModel;
    ActivityDataBinding binding;

    // 散光
    int delta_angle = 15;
    private int RADIUS = 150;
    private String COLOR = "#ffffff";
    private ArrayList<MaterialCardView> single_line_inner_1 = new ArrayList<>();
    private ArrayList<MaterialCardView> single_line_outer_1 = new ArrayList<>();
    private ArrayList<Boolean> single_line_flags_1 = new ArrayList<>();
    private ArrayList<MaterialCardView> single_line_inner_2 = new ArrayList<>();
    private ArrayList<MaterialCardView> single_line_outer_2 = new ArrayList<>();
    private ArrayList<Boolean> single_line_flags_2 = new ArrayList<>();
    private ArrayList<MaterialCardView> single_line_inner_3 = new ArrayList<>();
    private ArrayList<MaterialCardView> single_line_outer_3 = new ArrayList<>();
    private ArrayList<Boolean> single_line_flags_3 = new ArrayList<>();

    Typeface font;
    // 下降 不变 上升 三种状态对应的颜色
    final int[] STATE_COLOR = new int[]{Color.parseColor("#FF0000"), Color.parseColor("#00FF00"), Color.parseColor("#00FF00")};
    final int[] CURVE_ALPHA_COLOR = new int[]{Color.parseColor("#EE047029"), Color.parseColor("#00FFFFFF"),
    Color.parseColor("#EEEE0000"), Color.parseColor("#00FFFFFF")};
    final int[] CURVE_COLOR = new int[]{Color.parseColor("#047029"), Color.parseColor("#EE0000")};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        TransformationLayout.Params params = getIntent().getParcelableExtra("TransformationParams");
        TransitionExtensionKt.onTransformationEndContainer(this, params);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_data);

        //设置状态栏文字为深色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);
        binding.setData(viewModel);
        binding.setLifecycleOwner(this);

        init();
    }

    private void init(){
        viewModel.setContext(getApplicationContext());
        viewModel.init();

        font = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        init_single_astigmatism();
        init_single_sight();
        init_single_achromate();
        init_time_sight();
        init_time_count();
        init_time_astigmatism();

        setListener();
    }

    private void setListener(){
        binding.btnBack.setOnClickListener(new BackListener());
    }

    // 单个散光初始化
    private void init_single_astigmatism(){
        int angle;
        util_load_astigmatism(single_line_outer_1, single_line_inner_1, single_line_flags_1, binding.itemAstigmatism.layoutLeft, binding.itemAstigmatism.leftSingleAstigmatism);
        util_load_astigmatism(single_line_outer_2, single_line_inner_2, single_line_flags_2, binding.itemAstigmatism.layoutRight, binding.itemAstigmatism.rightSingleAstigmatism);
        // load recent data
        if(viewModel.leftEyeAstigValue.size()>0){   // exist
            if (viewModel.leftEyeAstigValue.get(viewModel.leftEyeAstigValue.size()-1)==-1){
                binding.itemAstigmatism.leftSingleAstigmatism.setText("无散光");
            }else{
                angle = viewModel.leftEyeAstigValue.get(viewModel.leftEyeAstigValue.size()-1);
                util_set_angle(single_line_inner_1, single_line_outer_1, single_line_flags_1, angle, true);
                binding.itemAstigmatism.leftSingleAstigmatism.setText(String.valueOf(angle)+"°");
                Log.v("MikeDeanLeftAstig:", String.valueOf(angle));
            }
        }else{
            binding.itemAstigmatism.leftSingleAstigmatism.setText("未测试");
        }
        if(viewModel.rightEyeAstigValue.size()>0){
            if(viewModel.rightEyeAstigValue.get(viewModel.rightEyeAstigValue.size()-1)==-1){
                binding.itemAstigmatism.rightSingleAstigmatism.setText("无散光");
            }else{
                angle = viewModel.rightEyeAstigValue.get(viewModel.rightEyeAstigValue.size()-1);
                util_set_angle(single_line_inner_2, single_line_outer_2, single_line_flags_2, angle, true);
                binding.itemAstigmatism.rightSingleAstigmatism.setText(String.valueOf(angle)+"°");
                Log.v("MikeDeanRightAstig:", String.valueOf(angle));
            }
        }else{
            binding.itemAstigmatism.rightSingleAstigmatism.setText("未测试");
        }

    }
    private void util_load_astigmatism(ArrayList<MaterialCardView> out, ArrayList<MaterialCardView> in,
                                       ArrayList<Boolean> flag, ConstraintLayout layout, View center){
        for(float angle=0;angle<360;angle+=delta_angle){
            in.add(new MaterialCardView(this));
            out.add(new MaterialCardView(this));
            flag.add(false);
        }
        int cnt = 0;
        int[][] states = new int[][]{
            new int[]{android.R.attr.state_pressed},
            new int[]{android.R.attr.state_focused},
            new int[]{android.R.attr.state_activated},
            new int[]{}
        };
        int[] colors = new int[]{
            Color.parseColor("#AAE93F04"), Color.RED, Color.GRAY, Color.BLUE
        };
        for(float angle=0;angle<360;angle+=delta_angle){
            // 设置显示的线条
            in.get(cnt).setId(View.generateViewId());
            in.get(cnt).setRotation(angle);

            layout.addView(in.get(cnt), new ConstraintLayout.LayoutParams(3,100));
            util_set_constrain(layout, in.get(cnt), center, angle, true);

            // 设置外面的框框
            out.get(cnt).setId(View.generateViewId());
            out.get(cnt).setRotation(angle);
            out.get(cnt).setRippleColor(new ColorStateList(states, colors));

            layout.addView(out.get(cnt), new ConstraintLayout.LayoutParams(30,120));
            util_set_constrain(layout, out.get(cnt), center, angle, false);
            out.get(cnt).setCardBackgroundColor(Color.parseColor("#00E93F04")); // 设置背景颜色，此处为透明，因为用户没有点
            out.get(cnt).setCardElevation(0);                                             // 设置没有阴影
            out.get(cnt).setElevation(0);
            out.get(cnt).setRadius(200);                                                  // 设置圆角
            out.get(cnt).setClickable(true);
            cnt += 1;
        }
    }
    private void util_set_constrain(ConstraintLayout layout, View trgView, View center, float angle, boolean setColor){
        /*
         * layout: 目标布局
         * trgView: 待添加的控件
         * center: 圆心
         * angle: 旋转角度
         * */
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        if (setColor) trgView.setBackgroundColor(Color.parseColor(COLOR));
        trgView.setClickable(false);
        set.constrainCircle(trgView.getId(), center.getId(), RADIUS, (int)angle);
        set.applyTo(layout);
    }
    private void util_hidden_line(ArrayList<MaterialCardView> in, ArrayList<MaterialCardView> out,
                                  boolean hidden){
        // true 表示隐藏
        int state = View.VISIBLE;
        if (hidden) state = View.INVISIBLE;
        for(int i=0;i<in.size();i++){
            in.get(i).setVisibility(state);
            out.get(i).setVisibility(state);
        }
    }
    private void util_set_angle(ArrayList<MaterialCardView> in, ArrayList<MaterialCardView> out,
                                ArrayList<Boolean> flag, int angle, boolean clearAll){
        // 清除其他颜色重新设定
        if (angle>=180) angle -= 180;
        if (clearAll) for(int i=0;i<flag.size();i++) flag.set(i, false);
        flag.set(angle/delta_angle, true);
        flag.set(angle/delta_angle+flag.size()/2, true);
        _util_update_color(in, out, flag);
    }
    private void util_set_angle(ArrayList<MaterialCardView> in, ArrayList<MaterialCardView> out,
                                ArrayList<Boolean> flag, int angle, boolean clearAll, String color){
        // 清除其他颜色重新设定
        if (angle>=180) angle -= 180;
        if (clearAll) for(int i=0;i<flag.size();i++) flag.set(i, false);
        flag.set(angle/delta_angle, true);
        flag.set(angle/delta_angle+flag.size()/2, true);
        _util_update_color(in, out, flag, color);
    }
    private void _util_update_color(ArrayList<MaterialCardView> in, ArrayList<MaterialCardView> out,
                                   ArrayList<Boolean> flag){
        for(int i=0;i<in.size();i++){
            if(flag.get(i)){
                in.get(i).setBackgroundColor(Color.parseColor("#047029"));
                out.get(i).setCardBackgroundColor(Color.parseColor("#99047029"));
            }else{
                in.get(i).setBackgroundColor(Color.parseColor(COLOR));
                out.get(i).setCardBackgroundColor(Color.parseColor("#00047029"));
            }
        }
    }
    private void _util_update_color(ArrayList<MaterialCardView> in, ArrayList<MaterialCardView> out,
                                    ArrayList<Boolean> flag, String color){
        for(int i=0;i<in.size();i++){
            if(flag.get(i)){
                in.get(i).setBackgroundColor(Color.parseColor("#"+color));
                out.get(i).setCardBackgroundColor(Color.parseColor("#99"+color));
            }
        }
    }

    // 单个视力初始化
    private void init_single_sight(){
        // 最近七次的数据 先随机生成
//        Random random = new Random();
        ArrayList<Entry> _left_data = new ArrayList<>();         // 左眼数据
        ArrayList<Entry> _right_data = new ArrayList<>();        // 右眼数据
        int cnt = 0;
        boolean flag_left = true, flag_right=true;
        if(viewModel.leftEyeVisionValue.size()==0){ // no data
            binding.itemSight.singleSightLeftCurve.setNoDataText("暂无历史数据");
            util_update_state(binding.itemSight.singleSightLeftArrow,1);
            flag_left = false;
        }else{
            if(viewModel.leftEyeVisionValue.size()<7){ // less than 7
                for(int i=0;i<viewModel.leftEyeVisionValue.size();i++){
                    _left_data.add(new Entry(i, viewModel.leftEyeVisionValue.get(i)));
                }
            }else{ // large than 7
                cnt = 0;
                for(int i=viewModel.leftEyeVisionValue.size()-7;i<viewModel.leftEyeVisionValue.size();i++){
                    _left_data.add(new Entry(cnt, viewModel.leftEyeVisionValue.get(i)));
                    cnt++;
                }
            }
        }

        if(viewModel.rightEyeVisionValue.size()==0){ // no data
            binding.itemSight.singleSightRightCurve.setNoDataText("暂无历史数据");
            util_update_state(binding.itemSight.singleSightRightArrow,1);
            flag_right = false;
        }else{
            if(viewModel.rightEyeVisionValue.size()<7){ // less than 7
                for(int i=0;i<viewModel.rightEyeVisionValue.size();i++){
                    _right_data.add(new Entry(i, viewModel.rightEyeVisionValue.get(i)));
                }
            }else{ // large than 7
                cnt = 0;
                for(int i=viewModel.rightEyeVisionValue.size()-7;i<viewModel.rightEyeVisionValue.size();i++){
                    _right_data.add(new Entry(cnt, viewModel.rightEyeVisionValue.get(i)));
                    cnt++;
                }
            }
        }


        // 设置显示其他数据 如视力 趋势等
        if(flag_left){
            binding.itemSight.singleSightLeftValue.setText(
                    util_one_decimal(_left_data.get(_left_data.size()-1).getY())
            );
            util_update_state(binding.itemSight.singleSightLeftArrow, util_judge_trend(_left_data));
            LineDataSet _left = new LineDataSet(_left_data,"左眼");
            util_set_curve_config(_left,"ONLY_CURVE", -1);
            ArrayList<ILineDataSet> _left_dataSets = new ArrayList<>();
            _left_dataSets.add(_left);
            LineData left_data = new LineData(_left_dataSets);
            binding.itemSight.singleSightLeftCurve.setData(left_data);
            util_set_chart_config(binding.itemSight.singleSightLeftCurve, "ONLY_CURVE");
        }

        if(flag_right){
            binding.itemSight.singleSightRightValue.setText(
                    util_one_decimal(_right_data.get(_right_data.size()-1).getY())
            );
            util_update_state(binding.itemSight.singleSightRightArrow, util_judge_trend(_right_data));
            // 开始绘制
            LineDataSet _right = new LineDataSet(_right_data,"右眼");
            util_set_curve_config(_right,"ONLY_CURVE", -1);
            // 加载到控件中
            ArrayList<ILineDataSet> _right_dataSets = new ArrayList<>();
            _right_dataSets.add(_right);
            LineData right_data = new LineData(_right_dataSets);
            binding.itemSight.singleSightRightCurve.setData(right_data);
            // 设置图表格式
            util_set_chart_config(binding.itemSight.singleSightRightCurve, "ONLY_CURVE");
        }


    }
    private void util_set_chart_config(LineChart chart, String type){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd"); // yyyy-
        switch (type){
            case "ONLY_CURVE":
                // 不显示数字
                chart.getData().setDrawValues(false);

                // 禁止显示左右线
                chart.getAxisLeft().setEnabled(false);
                chart.getAxisRight().setEnabled(false);

                // 禁止显示x轴
                chart.getXAxis().setEnabled(false);

                // 禁止显示图例
                chart.getLegend().setEnabled(false);

                // 禁止显示描述
                chart.getDescription().setEnabled(false);
                break;
            case "VISION":
                chart.getAxisRight().setEnabled(false); // 禁止右侧线
                chart.getLegend().setEnabled(false);    // 禁止显示图例
                chart.getDescription().setEnabled(false);   // 禁止显示描述

                chart.getAxisLeft().enableAxisLineDashedLine(10f,10f,10f);  // 实线长度，虚线长度，周期
                chart.getAxisLeft().setGranularity(0.1f);   // 间隔
                chart.getAxisLeft().setGridColor(Color.parseColor("#FFD9D9D9"));

                chart.getXAxis().setDrawGridLines(false);   // 不显示竖线
                chart.getXAxis().setGranularityEnabled(true);   // 控制x轴的间隔
                chart.getXAxis().setGranularity(1);             // 间隔为1
                chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                chart.getXAxis().setDrawLabels(true);
                chart.getXAxis().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        ArrayList<Long> tmp;
                        if(vision_left_time_stamp.size()>vision_right_time_stamp.size()) tmp=vision_left_time_stamp;
                        else tmp=vision_right_time_stamp;
                        if (tmp.size()>(int)value && (int)value>=0)
                            return sdf.format(tmp.get((int)value));
                        else
                            return "null";
                    }
                });
                break;
            case "ASTIG":
                chart.getAxisRight().setEnabled(false); // 禁止右侧线
                chart.getLegend().setEnabled(false);    // 禁止显示图例
                chart.getDescription().setEnabled(false);   // 禁止显示描述

                chart.getAxisLeft().enableAxisLineDashedLine(10f,10f,10f);  // 实线长度，虚线长度，周期
                chart.getAxisLeft().setGranularity(0.1f);   // 间隔
                chart.getAxisLeft().setGridColor(Color.parseColor("#FFD9D9D9"));

                chart.getXAxis().setDrawGridLines(false);   // 不显示竖线
                chart.getXAxis().setGranularityEnabled(true);   // 控制x轴的间隔
                chart.getXAxis().setGranularity(1);             // 间隔为1
                chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                chart.getXAxis().setDrawLabels(true);
                chart.getXAxis().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        ArrayList<Long> tmp;
                        if(astig_left_time_stamp.size()>astig_right_time_stamp.size()) tmp = astig_left_time_stamp;
                        else tmp = astig_right_time_stamp;
                        if (tmp.size()>(int)value && (int)value>=0)
                            return sdf.format(tmp.get((int)value));
                        else
                            return "null";
                    }
                });
                break;
            default:
                break;
        }


        // 禁止缩放
//        chart.setScaleEnabled(false);
    }
    private void util_set_barchart_config(BarChart chart){
        // 设置柱状图
        chart.getAxisRight().setEnabled(false); // 禁止右侧线
        chart.getLegend().setEnabled(false);    // 禁止显示图例
        chart.getDescription().setEnabled(false);   // 禁止显示描述

        chart.getXAxis().setDrawGridLines(false);   // 不显示竖线
        chart.getXAxis().setGranularityEnabled(true);   // 控制x轴的间隔
        chart.getXAxis().setGranularity(1);             // 间隔为1
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }
    private void util_set_curve_config(LineDataSet dataSet, String type, int index){
        switch (type){
            case "ONLY_CURVE":
                // 贝塞尔曲线
                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

                // 不绘制圆心
                dataSet.setDrawCircles(false);
                // 线条颜色
                dataSet.setColor(Color.parseColor("#FFC000"));
                // 线条粗细
                dataSet.setLineWidth(3f);
                break;
            case "STANDARD":
                // 贝塞尔曲线
                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                // 不绘制圆心
                dataSet.setDrawCircles(false);
                dataSet.setDrawHighlightIndicators(true);
                // 线条粗细
                dataSet.setLineWidth(1f);
                // 设置填充
                GradientDrawable gradient = new GradientDrawable();
                gradient.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                gradient.setColors(new int[]{CURVE_ALPHA_COLOR[index*2], CURVE_ALPHA_COLOR[index*2+1]});
                dataSet.setDrawFilled(true);
                dataSet.setFillDrawable(gradient);
                // 设置线条颜色
                dataSet.setColor(CURVE_COLOR[index]);
                break;
            case "ASTIG":
                // 贝塞尔曲线
                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                // 不绘制圆心
                dataSet.setDrawCircles(false);
                dataSet.setDrawHighlightIndicators(true);
                // 线条粗细
                dataSet.setLineWidth(1f);
                // 设置填充
                GradientDrawable gradients = new GradientDrawable();
                gradients.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                gradients.setColors(new int[]{CURVE_ALPHA_COLOR[index*2], CURVE_ALPHA_COLOR[index*2+1]});
                dataSet.setDrawFilled(true);
                dataSet.setFillDrawable(gradients);
                // 设置线条颜色
                dataSet.setColor(CURVE_COLOR[index]);
                dataSet.setLineWidth(1);
                break;
            default:
                break;
        }

    }
    private int util_judge_trend(ArrayList<Entry> data){
        // 判断趋势
        float start = data.get(0).getY();
        float end = data.get(data.size()-1).getY();
        if (start-end>0) return -1 + 1;             // 视力下降
        else if(start-end<0) return 1 + 1;          // 视力上升
        else return 0 + 1;                          // 视力不变
    }
    private String util_one_decimal(float data){
        // 保留一位小数

        return String.format("%.1f", data);
    }
    private void util_update_state(TextView textView, int state){
        // 更新进7天的状态
        // 0-下降 1-不变 2-上升
        textView.setTypeface(font);
        textView.setText(getResources().getString(
                new int[]{R.string.down, R.string.equal, R.string.up}[state]
        ));
        textView.setTextColor(STATE_COLOR[state]);
    }

    // 单个色盲初始化
    private void init_single_achromate(){
        int []draw = new int[]{};
        if(viewModel.achromateValue==null){
            draw = new int[] {R.drawable.level_uncheck, R.drawable.level_uncheck,
                    R.drawable.level_uncheck, R.drawable.level_uncheck};
            binding.itemAchromate.singleAchromateL1.setImageResource(draw[0]);
            binding.itemAchromate.singleAchromateL2.setImageResource(draw[1]);
            binding.itemAchromate.singleAchromateL3.setImageResource(draw[2]);
            binding.itemAchromate.singleAchromateL4.setImageResource(draw[3]);
            binding.itemAchromate.singleAchromateLevel.setText("暂无数据");
            binding.itemAchromate.singleAchromateColor.setText("暂无数据");
            return;
        }
        int level = Integer.parseInt(viewModel.achromateValue.split("-")[0]);  // 只有0/2/4三种情况
        String content = viewModel.achromateValue.split("-")[1];
        String []LEVEL = new String[]{"正常视觉","I~II色弱","III~IV色盲"};

        if (level==0){
            draw = new int[] {R.drawable.level_uncheck, R.drawable.level_uncheck,
                    R.drawable.level_uncheck, R.drawable.level_uncheck};
        } else if(level==2){
            draw = new int[] {R.drawable.level_check, R.drawable.level_check,
                    R.drawable.level_uncheck, R.drawable.level_uncheck};
        } else if(level==4){
            draw = new int[] {R.drawable.level_check, R.drawable.level_check,
                    R.drawable.level_check, R.drawable.level_check};
        }
        binding.itemAchromate.singleAchromateL1.setImageResource(draw[0]);
        binding.itemAchromate.singleAchromateL2.setImageResource(draw[1]);
        binding.itemAchromate.singleAchromateL3.setImageResource(draw[2]);
        binding.itemAchromate.singleAchromateL4.setImageResource(draw[3]);

        binding.itemAchromate.singleAchromateLevel.setText(LEVEL[level/2]);
        binding.itemAchromate.singleAchromateColor.setText(content);
    }


    ArrayList<Long> vision_left_time_stamp = new ArrayList<>();           // 时间戳
    ArrayList<Long> vision_right_time_stamp = new ArrayList<>();           // 时间戳
    // 视力随时间变化初始化
    private void init_time_sight(){
        Log.v("MikeDean","sTART");

        ArrayList<Entry> _left_data = new ArrayList<>();
        ArrayList<Entry> _right_data = new ArrayList<>();

        // 时间戳 左眼 右眼 --> 时间戳 左眼
        // 横坐标还是0~... 但是可以通过函数将其映射从而显示时间

        // 随机生成数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // ***********************************************************************************
        // Log.v("MikeDean","sTART"+String.valueOf(viewModel.leftEyeVisionValue.size()));
        for(int i=0;i<viewModel.leftEyeVisionValue.size();i++){
            vision_left_time_stamp.add(viewModel.leftEyeVisionTimeStamp.get(i));
            _left_data.add(new Entry(i, viewModel.leftEyeVisionValue.get(i)));
        }
        for(int i=0;i<viewModel.rightEyeVisionTimeStamp.size();i++){
            vision_right_time_stamp.add(viewModel.rightEyeVisionTimeStamp.get(i));
            _right_data.add(new Entry(i, viewModel.rightEyeVisionValue.get(i)));
        }
//        Random random = new Random();
//        for(int i=0;i<300;i++){
//            time_stamp.add(System.currentTimeMillis()-random.nextInt(10000));
//            _left_data.add(new Entry(i, (float) (random.nextFloat()*1.3+4)));
//            _right_data.add(new Entry(i, (float) (random.nextFloat()*1.3+4)));
//        }
        // ***********************************************************************************

        if(vision_left_time_stamp.size()==0 && vision_right_time_stamp.size()==0){
            binding.itemTimeSight.chart.setNoDataText("暂无视力的历史数据哦");
            binding.itemTimeSight.timeSightTime.setText("暂无历史数据");
            return;
        }

        // 将数据传入数据集
        LineDataSet _left = new LineDataSet(_left_data,"左眼");
        LineDataSet _right = new LineDataSet(_right_data,"右眼");

        util_set_curve_config(_left, "STANDARD", 0);
        util_set_curve_config(_right, "STANDARD", 1);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(_left);
        dataSets.add(_right);
        LineData data = new LineData(dataSets);

        binding.itemTimeSight.chart.setData(data);
        binding.itemTimeSight.chart.getData().setDrawValues(false);     // 禁止显示数字

        // 设置指示器
        CustomChartMarkerView mv = new CustomChartMarkerView(getApplicationContext());
        mv.setChartView(binding.itemTimeSight.chart);
        binding.itemTimeSight.chart.setMarker(mv);
        // 设置监听事件
        binding.itemTimeSight.chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // error!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Log.v("MikeDean1", String.valueOf(h.getDataSetIndex()));
                if(h.getDataSetIndex()==0) {
                    binding.itemTimeSight.timeSightLeftValue.setText("左眼:"+String.format("%.1f",_left_data.get((int) e.getX()).getY()));
                    binding.itemTimeSight.timeSightTime.setText(sdf.format(vision_left_time_stamp.get((int) e.getX())));
                    if(vision_right_time_stamp.size()>(int)e.getX()){
                        binding.itemTimeSight.timeSightRightValue.setText("右眼:"+String.format("%.1f",_right_data.get((int) e.getX()).getY()));
                    }else{
                        binding.itemTimeSight.timeSightRightValue.setText("右眼:"+"无数据");
                    }
                }else if(h.getDataSetIndex()==1){
                    binding.itemTimeSight.timeSightRightValue.setText("右眼:"+String.format("%.1f",_right_data.get((int) e.getX()).getY()));
                    binding.itemTimeSight.timeSightTime.setText(sdf.format(vision_right_time_stamp.get((int) e.getX())));
                    if(vision_left_time_stamp.size()>(int)e.getX()){
                        binding.itemTimeSight.timeSightLeftValue.setText("左眼:"+String.format("%.1f",_left_data.get((int) e.getX()).getY()));
                    }else{
                        binding.itemTimeSight.timeSightLeftValue.setText("左眼:"+"无数据");
                    }
                }
            }

            @Override
            public void onNothingSelected() {
                binding.itemTimeSight.timeSightTime.setText("单击图表查看内容");
                binding.itemTimeSight.timeSightLeftValue.setText("无数据");
                binding.itemTimeSight.timeSightRightValue.setText("无数据");
            }
        });
        util_set_chart_config(binding.itemTimeSight.chart, "VISION");

        binding.itemTimeSight.materialButton3.setOnClickListener(new View.OnClickListener() {// left
            @Override
            public void onClick(View view) {
                boolean state = binding.itemTimeSight.chart.getLineData().getDataSets().get(0).isVisible();
                if(state==true){
                    binding.itemTimeSight.chart.getLineData().getDataSets().get(0).setVisible(false);
                    binding.itemTimeSight.materialButton3.setBackgroundColor(Color.WHITE);
                }else{
                    binding.itemTimeSight.chart.getLineData().getDataSets().get(0).setVisible(true);
                    binding.itemTimeSight.materialButton3.setBackgroundColor(Color.parseColor("#E5EFE6"));
                }
                binding.itemTimeSight.chart.invalidate();
            }
        });
        binding.itemTimeSight.materialButton.setOnClickListener(new View.OnClickListener() {// right
            @Override
            public void onClick(View view) {
                boolean state = binding.itemTimeSight.chart.getLineData().getDataSets().get(1).isVisible();
                if(state==true){
                    binding.itemTimeSight.chart.getLineData().getDataSets().get(1).setVisible(false);
                    binding.itemTimeSight.materialButton.setBackgroundColor(Color.WHITE);
                }else{
                    binding.itemTimeSight.chart.getLineData().getDataSets().get(1).setVisible(true);
                    binding.itemTimeSight.materialButton.setBackgroundColor(Color.parseColor("#AAFFB6C1"));
                }
                binding.itemTimeSight.chart.invalidate();
            }
        });
    }

    // 测量次数随时间初始化-----------------------------------------------------------------------------------------------------------------------------------------
    private void init_time_count(){
        _init_time_count(0);
        binding.itemTimeCount.materialButton.setOnClickListener(new View.OnClickListener() {// month
            @Override
            public void onClick(View view) {
                _init_time_count(1);
            }
        });
        binding.itemTimeCount.materialButton2.setOnClickListener(new View.OnClickListener() {// day
            @Override
            public void onClick(View view) {
                _init_time_count(0);
            }
        });
        binding.itemTimeCount.materialButton3.setOnClickListener(new View.OnClickListener() {// year
            @Override
            public void onClick(View view) {
                _init_time_count(2);
            }
        });
    }

    private void _init_time_count(int type){
        binding.itemTimeCount.barchart.clear();
        // type=0 day type=1 month type=2 year
        // 默认是天
        // 随机创建数据
        ArrayList<Integer> dataSource;
        if(type==0) dataSource = viewModel.countDay;
        else if(type==1) dataSource = viewModel.countMonth;
        else dataSource = viewModel.countYear;
        ArrayList<BarEntry> _data = new ArrayList<>();
        // *************************************************************************
//        Random random = new Random();
//        for(int i=0;i<12;i++){
//            _data.add(new BarEntry(i, random.nextInt(100)));
//        }
        if(dataSource.size()==0){
            binding.itemTimeCount.barchart.setNoDataText("您暂未进行视力测试哦");
            return;
        }
        for(int i=0;i<dataSource.size();i++){
            if(type==2){
                _data.add(new BarEntry(Integer.parseInt(viewModel.years.get(i)), dataSource.get(i)));
            }else{
                _data.add(new BarEntry(i+1, dataSource.get(i)));
            }
        }
        // *************************************************************************

        // 设置圆角
        CustomBarChartRender barChartRender = new CustomBarChartRender(
                binding.itemTimeCount.barchart,
                binding.itemTimeCount.barchart.getAnimator(),
                binding.itemTimeCount.barchart.getViewPortHandler()
        );
        barChartRender.setRadius(40);
        binding.itemTimeCount.barchart.setRenderer(barChartRender);
        binding.itemTimeCount.barchart.setClickable(false); // 不能点击
        binding.itemTimeCount.barchart.setSelected(false);  // 不能被选中

        BarDataSet dataSet = new BarDataSet(_data,"one");
        dataSet.setColor(Color.parseColor("#047029"));

        BarData data = new BarData();
        data.addDataSet(dataSet);
        binding.itemTimeCount.barchart.setData(data);
        util_set_barchart_config(binding.itemTimeCount.barchart);
        binding.itemTimeCount.barchart.animateX(1000);

        // 设置指示器
        CustomChartMarkerView mv = new CustomChartMarkerView(getApplicationContext());
        mv.setChartView(binding.itemTimeCount.barchart);
        binding.itemTimeCount.barchart.setMarker(mv);
        binding.itemTimeCount.barchart.invalidate();
    }

    // 散光随时间变化初始化---------------------------------------------------------------------------------------------------------------------------------
    ArrayList<Long> astig_left_time_stamp = new ArrayList<>();           // 时间戳
    ArrayList<Long> astig_right_time_stamp = new ArrayList<>();           // 时间戳
    private void init_time_astigmatism() {
        ArrayList<Entry> _left_data = new ArrayList<>();    // 左眼
        ArrayList<Entry> _right_data = new ArrayList<>();   // 右眼


        // *************************************************************************************************************************************

        for(int i=0;i<viewModel.leftEyeAstigTimeStamp.size();i++){
            _left_data.add(new Entry(i, (float) viewModel.leftEyeAstigValue.get(i)));
            astig_left_time_stamp.add(viewModel.leftEyeAstigTimeStamp.get(i));
        }
        for(int i=0;i<viewModel.rightEyeAstigTimeStamp.size();i++){
            _right_data.add(new Entry(i, (float) viewModel.rightEyeAstigValue.get(i)));
            astig_right_time_stamp.add(viewModel.rightEyeAstigTimeStamp.get(i));
        }
        if(_left_data.size()==0 && _right_data.size()==0){
            binding.itemTimeAstigmatism.chart.setNoDataText("暂无散光的历史数据哦");
            binding.itemTimeAstigmatism.timeAstigmatismTime.setText("暂无历史数据");
            return;
        }
        // 随机数
//        Random random = new Random();
//        for(int i=0;i<300;i++){
//            astigmatism_time_stamp.add(System.currentTimeMillis()-random.nextInt(10000));
//            _left_data.add(new Entry(i, (float) (random.nextInt(9)*10)));
//            _right_data.add(new Entry(i, (float) (random.nextInt(9)*10)));
//        }
        // *************************************************************************************************************************************

        // 将数据放入到数据集
        LineDataSet _left = new LineDataSet(_left_data,"左眼");
        LineDataSet _right = new LineDataSet(_right_data,"右眼");
        // 对数据集的展示方式进行输出
        util_set_curve_config(_left, "ASTIG", 0);
        util_set_curve_config(_right, "ASTIG", 1);
        // 将数据集批量添加到控件中
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(_left);
        dataSets.add(_right);
        LineData data = new LineData(dataSets);
        binding.itemTimeAstigmatism.chart.setData(data);
        binding.itemTimeAstigmatism.chart.getData().setDrawValues(false);     // 禁止显示数字

        // 设置指示器
        CustomChartMarkerView mv = new CustomChartMarkerView(getApplicationContext());
        mv.setChartView(binding.itemTimeAstigmatism.chart);
        binding.itemTimeAstigmatism.chart.setMarker(mv);
        // 设置监听事件
        binding.itemTimeAstigmatism.chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String value;
                if(h.getDataSetIndex()==0){// left
                    value = String.format("%.1f",_left_data.get((int) e.getX()).getY());
                    if(value.contains("-")) value="无散光";
                    binding.itemTimeAstigmatism.timeAstigmatismLeftValue.setText(value);
                    binding.itemTimeAstigmatism.timeAstigmatismTime.setText(sdf.format(astig_left_time_stamp.get((int) e.getX())));
                    binding.itemTimeAstigmatism.timeAstigmatismRightValue.setText("无数据");
                    util_set_angle(single_line_inner_3, single_line_outer_3, single_line_flags_3, (int) _left_data.get((int) e.getX()).getY(), true);
                    if((int) e.getX()<_right_data.size()){
                        value = String.format("%.1f",_right_data.get((int) e.getX()).getY());
                        if(value.contains("-")) value="无散光";
                        binding.itemTimeAstigmatism.timeAstigmatismRightValue.setText(value);
                        util_set_angle(single_line_inner_3, single_line_outer_3, single_line_flags_3, (int) _right_data.get((int) e.getX()).getY(), true, "FF0000");
                    }
                }else{// right
                    value = String.format("%.1f",_right_data.get((int) e.getX()).getY());
                    if(value.contains("-")) value="无散光";
                    binding.itemTimeAstigmatism.timeAstigmatismRightValue.setText(value);
                    binding.itemTimeAstigmatism.timeAstigmatismTime.setText(sdf.format(astig_right_time_stamp.get((int) e.getX())));
                    binding.itemTimeAstigmatism.timeAstigmatismLeftValue.setText("无数据");
                    util_set_angle(single_line_inner_3, single_line_outer_3, single_line_flags_3, (int) _right_data.get((int) e.getX()).getY(), true, "FF0000");
                    if((int) e.getX()<_left_data.size()){
                        value = String.format("%.1f",_left_data.get((int) e.getX()).getY());
                        if(value.contains("-")) value="无散光";
                        binding.itemTimeAstigmatism.timeAstigmatismLeftValue.setText(value);
                        util_set_angle(single_line_inner_3, single_line_outer_3, single_line_flags_3, (int) _left_data.get((int) e.getX()).getY(), true);
                        util_set_angle(single_line_inner_3, single_line_outer_3, single_line_flags_3, (int) _right_data.get((int) e.getX()).getY(), true, "FF0000");
                    }
                }
            }

            @Override
            public void onNothingSelected() {
                binding.itemTimeAstigmatism.timeAstigmatismTime.setText("单击图表查看详细内容");
                binding.itemTimeAstigmatism.timeAstigmatismLeftValue.setText("左眼:"+"无数据");
                binding.itemTimeAstigmatism.timeAstigmatismRightValue.setText("右眼:"+"无数据");
            }
        });

        binding.itemTimeAstigmatism.materialButton2.setOnClickListener(new View.OnClickListener() {// both eye
            @Override
            public void onClick(View view) {
                binding.itemTimeAstigmatism.chart.getLineData().getDataSets().get(0).setVisible(true);
                binding.itemTimeAstigmatism.chart.getLineData().getDataSets().get(1).setVisible(true);
                binding.itemTimeAstigmatism.chart.invalidate();
                binding.itemTimeAstigmatism.materialButton2.setBackgroundColor(Color.parseColor("#E5EFE6"));
                binding.itemTimeAstigmatism.materialButton.setBackgroundColor(Color.WHITE);
                binding.itemTimeAstigmatism.materialButton3.setBackgroundColor(Color.WHITE);
            }
        });
        binding.itemTimeAstigmatism.materialButton.setOnClickListener(new View.OnClickListener() {// right
            @Override
            public void onClick(View view) {
                binding.itemTimeAstigmatism.chart.getLineData().getDataSets().get(0).setVisible(false);
                binding.itemTimeAstigmatism.chart.getLineData().getDataSets().get(1).setVisible(true);
                binding.itemTimeAstigmatism.chart.invalidate();
                binding.itemTimeAstigmatism.materialButton2.setBackgroundColor(Color.WHITE);
                binding.itemTimeAstigmatism.materialButton.setBackgroundColor(Color.parseColor("#AAFFB6C1"));
                binding.itemTimeAstigmatism.materialButton3.setBackgroundColor(Color.WHITE);
            }
        });
        binding.itemTimeAstigmatism.materialButton3.setOnClickListener(new View.OnClickListener() {// left
            @Override
            public void onClick(View view) {
                binding.itemTimeAstigmatism.chart.getLineData().getDataSets().get(0).setVisible(true);
                binding.itemTimeAstigmatism.chart.getLineData().getDataSets().get(1).setVisible(false);
                binding.itemTimeAstigmatism.chart.invalidate();
                binding.itemTimeAstigmatism.materialButton2.setBackgroundColor(Color.WHITE);
                binding.itemTimeAstigmatism.materialButton.setBackgroundColor(Color.WHITE);
                binding.itemTimeAstigmatism.materialButton3.setBackgroundColor(Color.parseColor("#E5EFE6"));
            }
        });
        util_set_chart_config(binding.itemTimeAstigmatism.chart, "ASTIG");

        // 加载散光
        util_load_astigmatism(single_line_outer_3, single_line_inner_3, single_line_flags_3, binding.itemTimeAstigmatism.layout, binding.itemTimeAstigmatism.timeAstigmatismCenter);
    }

}

// 柱状图圆角
class CustomBarChartRender extends BarChartRenderer {

    private RectF mBarShadowRectBuffer = new RectF();

    private int mRadius;

    public CustomBarChartRender(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
    }

    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));
        mShadowPaint.setColor(dataSet.getBarShadowColor());
        boolean drawBorder = dataSet.getBarBorderWidth() > 0f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.getBarShadowColor());

            BarData barData = mChart.getBarData();

            float barWidth = barData.getBarWidth();
            float barWidthHalf = barWidth / 2.0f;
            float x;

            int i = 0;
            double count = Math.min(Math.ceil((int) (double) ((float) dataSet.getEntryCount() * phaseX)), dataSet.getEntryCount());
            while (i < count) {

                BarEntry e = dataSet.getEntryForIndex(i);

                x = e.getX();

                mBarShadowRectBuffer.left = x - barWidthHalf;
                mBarShadowRectBuffer.right = x + barWidthHalf;

                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    i++;
                    continue;
                }

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                    break;

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();

                c.drawRoundRect(mBarRect, mRadius, mRadius, mShadowPaint);
                i++;
            }
        }

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        boolean isSingleColor = dataSet.getColors().size() == 1;

        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor());
        }

        int j = 0;
        while (j < buffer.size()) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4;
                continue;
            }

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;

            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
            }

            if (dataSet.getGradientColor() != null) {
                GradientColor gradientColor = dataSet.getGradientColor();
                mRenderPaint.setShader(new LinearGradient(
                        buffer.buffer[j],
                        buffer.buffer[j + 3],
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        gradientColor.getStartColor(),
                        gradientColor.getEndColor(),
                        android.graphics.Shader.TileMode.MIRROR));
            }

            if (dataSet.getGradientColors() != null) {
                mRenderPaint.setShader(new LinearGradient(
                        buffer.buffer[j],
                        buffer.buffer[j + 3],
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        dataSet.getGradientColor(j / 4).getStartColor(),
                        dataSet.getGradientColor(j / 4).getEndColor(),
                        Shader.TileMode.MIRROR));
            }
            Path path2 = roundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3]), mRadius, mRadius, true, true, true, true);
            c.drawPath(path2, mRenderPaint);
            if (drawBorder) {
                Path path = roundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3]), mRadius, mRadius, true, true, true, true);
                c.drawPath(path, mBarBorderPaint);
            }
            j += 4;
        }

    }

    private Path roundRect(RectF rect, float rx, float ry, boolean tl, boolean tr, boolean br, boolean bl) {
        float top = rect.top;
        float left = rect.left;
        float right = rect.right;
        float bottom = rect.bottom;
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width / 2) rx = width / 2;
        if (ry > height / 2) ry = height / 2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        if (tr)
            path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        else {
            path.rLineTo(0, -ry);
            path.rLineTo(-rx, 0);
        }
        path.rLineTo(-widthMinusCorners, 0);
        if (tl)
            path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        else {
            path.rLineTo(-rx, 0);
            path.rLineTo(0, ry);
        }
        path.rLineTo(0, heightMinusCorners);

        if (bl)
            path.rQuadTo(0, ry, rx, ry);//bottom-left corner
        else {
            path.rLineTo(0, ry);
            path.rLineTo(rx, 0);
        }

        path.rLineTo(widthMinusCorners, 0);
        if (br)
            path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
        else {
            path.rLineTo(rx, 0);
            path.rLineTo(0, -ry);
        }

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.

        return path;
    }}
