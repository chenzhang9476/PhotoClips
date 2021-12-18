package com.example.photoclips.Display;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.photoclips.utils.MyView;
import com.example.photoclips.utils.OpenCVImpl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.photoclips.R;

import java.io.FileNotFoundException;

public class ConvertingActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    private int x;//绘画开始的横坐标
    private int y;//绘画开始的纵坐标
    private int m;//绘画结束的横坐标
    private int n;//绘画结束的纵坐标
    private int width;//绘画的宽度
    private int height;//绘画的高度
    private Bitmap bitmap;//生成的位图
    private MyView myView;//绘画选择区域
    private ImageView destpic;
    private Button deleteButton;
    private Button convertButton;
    private Button RemainButton;
    private int position;
    private Uri uri;
    private OpenCVImpl openCV=new OpenCVImpl();
    private Bitmap result;
    private ImageView readyImage;
    private Bitmap backgroundpicture;
    int toHeight;
    private ScrollView scrollView;
    public static final int DELETE_PHOTO_DISH = 3;
    public static final int REMAIN_PHOTO_DISH = 4;
    private int picturewidth;
    private int pictureheight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.converting_display);
        Intent intent=getIntent();
        uri= Uri.parse(intent.getStringExtra("Uri"));
        //还要传给position，方便删除
        position=intent.getIntExtra("position",8);
        //还要背景图片
        Uri backgroundUri=Uri.parse(intent.getStringExtra("backgroundpicture"));
        try {
            backgroundpicture=BitmapFactory.decodeStream(getContentResolver().openInputStream(backgroundUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        readyImage=(ImageView)findViewById(R.id.originalpic);
        destpic=(ImageView)findViewById(R.id.destpic);
        deleteButton=(Button)findViewById(R.id.dialog_delete);
        convertButton=(Button)findViewById(R.id.dialog_edit);
        RemainButton=(Button)findViewById(R.id.dialog_confirm);
        scrollView=(ScrollView) findViewById(R.id.convert_scrollview);
        deleteButton.setOnClickListener(this);
        convertButton.setOnClickListener(this);
        RemainButton.setOnClickListener(this);
        readyImage.setImageURI(uri);
        // 在图片上监听触摸事件
        readyImage.setOnTouchListener(this);
        Bitmap original=null;
        try {
            original=BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //设置刻画区域的长宽高
        picturewidth=original.getWidth();
        pictureheight=original.getHeight();
        myView = new MyView(this);
        myView.postInvalidate();
        // 添加 view
        this.addContentView(myView, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
    }
    /**
     * 获取选择框在屏幕的位置
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 记录初始位置
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            x = 0;
            y = 0;
            width = 0;
            height = 0;
            x = (int) event.getX();
            y = (int) event.getY();
            scrollView.requestDisallowInterceptTouchEvent(true);
//            System.out.println(  "onTouch: x = "+x +" y = "+y);
        }

        // 移动的时候进行绘制框
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            //防止超出框
            m=(int) event.getX()<picturewidth?(int) event.getX():picturewidth;
            n=(int) event.getY()<pictureheight?(int) event.getY():pictureheight;
            myView.setSeat(x, y, m, n);
            myView.postInvalidate();
            System.out.println( "onTouch: x = "+x +" y = "+y +" m = "+m +" n = "+n);
        }

        // 抬起的时候整理绘制的框 长宽
        if(event.getAction() == MotionEvent.ACTION_UP){
            scrollView.requestDisallowInterceptTouchEvent(false);
            //长，判断是否在图片中
            int end_x=(int) event.getX()<picturewidth?(int) event.getX():picturewidth;
            if(end_x>x){
                width = end_x-x;
            }else{
                width = x-end_x;
                x = end_x;
            }
            //宽，判断是否在图片中
            int end_y=(int) event.getY()<pictureheight?(int) event.getY():pictureheight;
            if( end_y>y){
                height =  end_y-y;
            }else{
                height =  y-end_y;
                y =  end_y;
            }

            System.out.println( "onTouch: x = "+x +" y = "+y +" m = "+m +" n = "+n);

            // 设置图片显示
//            readyImage.setImageBitmap(getBitmap(this));
            Toast.makeText(this,"Your selected area has been recorded", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    /**
     * 根据绘制的区域生成图片
     * @param activity
     * @return
     */
    private Bitmap getBitmap(Activity activity){
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        bitmap = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
         toHeight = frame.top;
//        bitmap = Bitmap.createBitmap(bitmap,(int) (x+(screen_width*0.15)), y,(int) (width+(screen_width*0.15)) , (int) (height+(screen_height*0.12)));
//        bitmap = Bitmap.createBitmap(bitmap, x, y+2*toHeight, width, height);
        bitmap = Bitmap.createBitmap(bitmap, x, y+2*toHeight, width, height);

        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_delete:
                //删除
                Intent intent=new Intent();
                intent.putExtra("position",position);
                this.setResult(DELETE_PHOTO_DISH,intent);
                this.finish();
                break;
            case R.id.dialog_confirm:
                if (result!=null) {
                    Intent intent1 = new Intent();
                    Uri resulturi = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), result, null, null));
                    intent1.putExtra("position", position);
                    intent1.putExtra("resulturl", String.valueOf(resulturi));
                    this.setResult(REMAIN_PHOTO_DISH, intent1);
                    this.finish();
                }else {
                    Toast.makeText(this,R.string.ConvertFirst,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.dialog_edit:
                //下一步
                //获取activity中的背景图
                final ProgressDialog show=ProgressDialog.show(this, "Editing", "Loading", true, true, null);
                Handler handler=new Handler(){
                    public void handleMessage(Message msg){
                        //接受数据
                        // 更新ui
                        destpic.setImageBitmap((Bitmap) msg.obj);
                    }
                };
                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        try {
//                            Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), readyImage, null, null))));
                            BitmapDrawable drawable=(BitmapDrawable) readyImage.getDrawable();
                            Bitmap bitmap=drawable.getBitmap();
//                            ,backgroundpicture
//                            result =openCV.testCutImage(bitmap,backgroundpicture,x, y+2*toHeight, width, height);
                            result =openCV.cupBitmap(bitmap,x, y+2*toHeight, width, height);
                            //子线程不能操作UI，所以用handler+message
                            Message msg=Message.obtain();
                            msg.obj=result;
                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            show.dismiss();
                        }
                    }
                };
                show.show();
                mThread.start();
                break;

        }
    }
}