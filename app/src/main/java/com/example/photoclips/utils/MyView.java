package com.example.photoclips.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyView extends androidx.appcompat.widget.AppCompatImageView {

    private int x;
    private int y;
    private int m;
    private int n;
    private int StrokeWidth = 1;
//    private boolean sign;//绘画标记位
    private Paint paint;//画笔

    public MyView(Context context) {
        super(context);
        //构建对象
        paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    }

    /**
     * 设置画笔
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //设置无锯齿
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(StrokeWidth);
//        paint.setColor(Color.RED);
//        paint.setAlpha(100);
        paint.setColor(Color.GREEN);
        paint.setAlpha(80);
        canvas.drawRect(new Rect(x, y, m, n), paint);
        super.onDraw(canvas);
    }

    public void setSeat(int x,int y,int m,int n){
        this.x = x;
        this.y = y;
        this.m = m;
        this.n = n;
    }

//    public boolean isSign() {
//        return sign;
//    }
//
//    public void setSign(boolean sign) {
//        this.sign = sign;
//    }

}
