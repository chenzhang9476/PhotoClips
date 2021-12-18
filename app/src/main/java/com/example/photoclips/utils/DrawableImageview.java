package com.example.photoclips.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DrawableImageview extends androidx.appcompat.widget.AppCompatImageView {

//    private int x;//绘画开始的横坐标
//    private int y;//绘画开始的纵坐标
//    private int m;//绘画结束的横坐标
//    private int n;//绘画结束的纵坐标
    private int myleft;//绘画的X
    private int mytop;//绘画的Y
    private int mywidth;//绘画的宽度
    private int myheight;//绘画的高度

    public int getMyleft() {
        return myleft;
    }

    public void setMyleft(int myleft) {
        this.myleft = myleft;
    }

    public int getMytop() {
        return mytop;
    }

    public void setMytop(int mytop) {
        this.mytop = mytop;
    }

    public int getMywidth() {
        return mywidth;
    }

    public void setMywidth(int mywidth) {
        this.mywidth = mywidth;
    }

    public int getMyheight() {
        return myheight;
    }

    public void setMyheight(int myheight) {
        this.myheight = myheight;
    }

    //  声明Paint对象
    private Paint mPaint = null;
    private int StrokeWidth = 5;
    private Rect rect = new Rect(0,0,0,0);//手动绘制矩形
    public DrawableImageview(@NonNull Context context) {
        super(context);
    }

    public DrawableImageview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //构建对象
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置无锯齿
        mPaint.setAntiAlias(true);
//        canvas.drawARGB(50,255,227,0);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(StrokeWidth);
//        mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(100);
        // 绘制绿色实心矩形
//        canvas.drawRect(100, 200, 400, 200 + 400, mPaint);
        mPaint.setColor(Color.RED);
        canvas.drawRect(rect,mPaint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                    mywidth = 0;
                    myheight = 0;
                    rect.right+=StrokeWidth;
                    rect.bottom+=StrokeWidth;
                    invalidate(rect);
                    myleft=rect.left = x;
                    mytop=rect.top = y;
                    rect.right =rect.left;
                    rect.bottom = rect.top;
            case MotionEvent.ACTION_MOVE:
                Rect old = new Rect(rect.left,rect.top,rect.right+StrokeWidth,rect.bottom+StrokeWidth);
                rect.right = x;
                rect.bottom = y;
                old.union(x,y);
                invalidate(old);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                if(event.getX()>myleft){
                    mywidth = (int)event.getX()-myleft;
                }else{
//                    mywidth = originalleft-(int)event.getRawX();
                    mywidth = myleft-(int)event.getX();
//                    x = (int) event.getX();
                }
                if(event.getY()>mytop){
                    myheight = (int)event.getY()-mytop;
                }else{
                    myheight = mytop-(int) event.getY();
//                    y = (int) event.getY();
                }
                System.out.println("event.get:"+event.getX()+",,,"+event.getY());
                System.out.println("react:"+myleft+",,,"+mytop);
                System.out.println("my:"+mywidth+",,,"+myheight);
                update(myleft,mytop,mywidth,myheight);
                break;
            default:
                break;
        }
        return true;//处理了触摸信息，消息不再传递
    }


    private void update(int left,int top,int weight, int height){
        BitmapDrawable drawable=(BitmapDrawable) this.getDrawable();
        Bitmap bt=drawable.getBitmap();
        this.setImageBitmap(Bitmap.createBitmap(bt,left,top,weight,height));
//        System.out.println("转换后的图片:"+this.getHeight()+","+this.getWidth());
        rect = new Rect(0,0,0,0);
    }

}
