package com.example.vision;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CustomChartMarkerView extends MarkerView{
    private final int DEFAULT_INDICATOR_COLOR = 0xffFD9138;//指示器默认的颜色
    private final int ARROW_HEIGHT = dp2px(10); // 箭头的高度
    private final int ARROW_WIDTH = dp2px(20); // 箭头的宽度
    private final float ARROW_OFFSET = dp2px(2);//箭头偏移量 距离图片的高度
    private final float BG_CORNER = dp2px(2);//背景圆角
    private final TextView tvContent;//文本
    private Bitmap bitmapForDot;//选中点图片
    private int bitmapWidth;//点宽
    private int bitmapHeight;//点高

    public CustomChartMarkerView(Context context) {
        super(context, R.layout.chart_marker_view);
        tvContent = findViewById(R.id.tvContent);
        //图片自行替换
        bitmapForDot = BitmapFactory.decodeResource(getResources(), R.drawable.point);
        bitmapWidth = bitmapForDot.getWidth();
        bitmapHeight = bitmapForDot.getHeight();
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            tvContent.setText(Utils.formatNumber(ce.getHigh(), 1, true));
        } else {
            tvContent.setText(Utils.formatNumber(e.getY(), 1, true));
        }

        super.refreshContent(e, highlight);
    }


    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        Chart chart = getChartView();
        if (chart == null) {
            super.draw(canvas, posX, posY);
            return;
        }

        //指示器背景画笔
        Paint bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(DEFAULT_INDICATOR_COLOR);
        //剪头画笔
        Paint arrowPaint = new Paint();
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setAntiAlias(true);
        arrowPaint.setColor(DEFAULT_INDICATOR_COLOR);

        float width = getWidth();
        float height = getHeight();

        int saveId = canvas.save();
        //移动画布到点并绘制点
        canvas.translate(posX, posY);
        canvas.drawBitmap(bitmapForDot, -bitmapWidth / 2f , -bitmapHeight / 2f ,null);

        //画指示器
        Path path = new Path();
        RectF bRectF;
        if (posY < height + ARROW_HEIGHT + ARROW_OFFSET + bitmapHeight / 2f) {//处理超过上边界
            //移动画布并绘制三角形和背景
            canvas.translate(0, height + ARROW_HEIGHT + ARROW_OFFSET + bitmapHeight / 2f);
            path.moveTo(0, -(height + ARROW_HEIGHT));
            path.lineTo(ARROW_WIDTH / 2f, -(height - BG_CORNER));
            path.lineTo(- ARROW_WIDTH / 2f, -(height - BG_CORNER));
            path.lineTo(0, -(height + ARROW_HEIGHT));

            bRectF=new RectF(-width * 2, -height, width * 2, 0);

            canvas.drawPath(path, arrowPaint);
            canvas.drawRoundRect(bRectF, BG_CORNER, BG_CORNER, bgPaint);
            canvas.translate(-width / 2f, -height);
        } else {//没有超过上边界
            //移动画布并绘制三角形和背景
            canvas.translate(0, -height - ARROW_HEIGHT - ARROW_OFFSET - bitmapHeight / 2f);
            path.moveTo(0, height + ARROW_HEIGHT);
            path.lineTo(ARROW_WIDTH / 2f, height - BG_CORNER);
            path.lineTo(- ARROW_WIDTH / 2f, height - BG_CORNER);
            path.lineTo(0, height + ARROW_HEIGHT);

            bRectF=new RectF(-width * 2, 0, width * 2, height);

            canvas.drawPath(path, arrowPaint);
            canvas.drawRoundRect(bRectF, BG_CORNER, BG_CORNER, bgPaint);
            canvas.translate(-width / 2f, 0);
        }
        draw(canvas);
        canvas.restoreToCount(saveId);

    }

    private int dp2px(int dpValues) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dpValues,
                getResources().getDisplayMetrics());
    }
}
