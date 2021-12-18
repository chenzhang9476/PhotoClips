package com.example.photoclips.Display;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.photoclips.R;
import com.example.photoclips.utils.ContactApp;
import com.example.photoclips.utils.MyImageview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class EditingPictureActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Uri> dishs;
    private Uri background;
    private RelativeLayout backgroundpicture;
    private MyImageview dish1,dish2,dish3,dish4,dish5,dish6,dish7;
    private Button editsubmit,editnext;
    private int index=0;
    Canvas canvas;
    MyImageview currentImage;
    private Bitmap currentBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.editing_picture_activity);
        //先获取送上一个activity传过来的图片Uri.
        Intent intent=getIntent();
        background=Uri.parse(intent.getStringExtra("background"));
        //由于是Linkedlist 所以只能从application传递值
         ContactApp app = (ContactApp)getApplication();
        dishs=app.getUris();
//      提交
        editsubmit=(Button)findViewById(R.id.editingbutton);
        editsubmit.setOnClickListener(this);
        editnext=(Button)findViewById(R.id.editnext);
        editnext.setOnClickListener(this);
        init();
    }
    private void init(){
        initpic();
        //背景图设置
        backgroundpicture=(RelativeLayout)findViewById(R.id.EditingBackground);
        try {
            Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(background));
            currentBackground=bitmap;
            backgroundpicture.setBackground(new BitmapDrawable(bitmap));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void initpic() {
        //初始化图片
        dish1=(MyImageview)findViewById(R.id.editrowpic1);
        dish2=(MyImageview)findViewById(R.id.editrowpic2);
        dish3=(MyImageview)findViewById(R.id.editrowpic3);
        dish4=(MyImageview)findViewById(R.id.editrowpic4);
        dish5=(MyImageview)findViewById(R.id.editrowpic5);
        dish6=(MyImageview)findViewById(R.id.editrowpic6);
        dish7=(MyImageview)findViewById(R.id.editrowpic7);
        //配器里面的图片不能拖拽，所以要建立7个ImageView
        try {
            //把第一个图片不显示
            //需要把URI转bitmap
            dish1.setImageBitmap(BitmapFactory.decodeStream(this.getContentResolver().openInputStream(dishs.get(1))));
            dish1.setDrawingCacheEnabled(true);
            //第一次把dishs给current
            currentImage=dish1;
            index=1;
        }catch (IndexOutOfBoundsException | FileNotFoundException e){
        }
    }

    private MyImageview getcurrentImage(int localindex) {
        switch (localindex) {
            case 1:
                dish1.setVisibility(View.VISIBLE);
                dish2.setVisibility(View.GONE);
                dish3.setVisibility(View.GONE);
                dish4.setVisibility(View.GONE);
                dish5.setVisibility(View.GONE);
                dish6.setVisibility(View.GONE);
                dish7.setVisibility(View.GONE);
                return dish1;
            case 2:
                dish1.setVisibility(View.GONE);
                dish2.setVisibility(View.VISIBLE);
                try {
                    dish2.setImageBitmap(BitmapFactory.decodeStream(this.getContentResolver().openInputStream(dishs.get(2))));
                } catch (FileNotFoundException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Toast.makeText(this,R.string.NotExist,Toast.LENGTH_SHORT).show();
                }
                dish2.setDrawingCacheEnabled(true);
                dish3.setVisibility(View.GONE);
                dish4.setVisibility(View.GONE);
                dish5.setVisibility(View.GONE);
                dish6.setVisibility(View.GONE);
                dish7.setVisibility(View.GONE);
                return dish2;
            case 3:
                dish1.setVisibility(View.GONE);
                dish2.setVisibility(View.GONE);
                dish3.setVisibility(View.VISIBLE);
                try {
                    dish3.setImageBitmap(BitmapFactory.decodeStream(this.getContentResolver().openInputStream(dishs.get(3))));
                } catch (FileNotFoundException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Toast.makeText(this,R.string.NotExist,Toast.LENGTH_SHORT).show();
                }
                dish3.setDrawingCacheEnabled(true);
                dish4.setVisibility(View.GONE);
                dish5.setVisibility(View.GONE);
                dish6.setVisibility(View.GONE);
                dish7.setVisibility(View.GONE);
                return dish3;

            case 4:
                dish1.setVisibility(View.GONE);
                dish2.setVisibility(View.GONE);
                dish3.setVisibility(View.GONE);
                dish4.setVisibility(View.VISIBLE);
                try {
                    dish4.setImageBitmap(BitmapFactory.decodeStream(this.getContentResolver().openInputStream(dishs.get(4))));
                } catch (FileNotFoundException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Toast.makeText(this,R.string.NotExist,Toast.LENGTH_SHORT).show();
                }
                dish4.setDrawingCacheEnabled(true);
                dish5.setVisibility(View.GONE);
                dish6.setVisibility(View.GONE);
                dish7.setVisibility(View.GONE);
                return dish4;
            case 5:
                dish1.setVisibility(View.GONE);
                dish2.setVisibility(View.GONE);
                dish3.setVisibility(View.GONE);
                dish4.setVisibility(View.GONE);
                dish5.setVisibility(View.VISIBLE);
                try {
                    dish5.setImageBitmap(BitmapFactory.decodeStream(this.getContentResolver().openInputStream(dishs.get(5))));
                } catch (FileNotFoundException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Toast.makeText(this,R.string.NotExist,Toast.LENGTH_SHORT).show();
                }
                dish5.setDrawingCacheEnabled(true);
                dish6.setVisibility(View.GONE);
                dish7.setVisibility(View.GONE);
                return dish5;

            case 6:
                dish1.setVisibility(View.GONE);
                dish2.setVisibility(View.GONE);
                dish3.setVisibility(View.GONE);
                dish4.setVisibility(View.GONE);
                dish5.setVisibility(View.GONE);
                dish6.setVisibility(View.VISIBLE);
                try {
                    dish6.setImageBitmap(BitmapFactory.decodeStream(this.getContentResolver().openInputStream(dishs.get(6))));
                } catch (FileNotFoundException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Toast.makeText(this,R.string.NotExist,Toast.LENGTH_SHORT).show();
                }
                dish6.setDrawingCacheEnabled(true);
                dish7.setVisibility(View.GONE);
                return dish6;

            case 7:
                dish1.setVisibility(View.GONE);
                dish2.setVisibility(View.GONE);
                dish3.setVisibility(View.GONE);
                dish4.setVisibility(View.GONE);
                dish5.setVisibility(View.GONE);
                dish6.setVisibility(View.GONE);
                dish7.setVisibility(View.VISIBLE);
                try {
                    dish7.setImageBitmap(BitmapFactory.decodeStream(this.getContentResolver().openInputStream(dishs.get(7))));
                } catch (FileNotFoundException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Toast.makeText(this,R.string.NotExist,Toast.LENGTH_SHORT).show();
                }
                dish7.setDrawingCacheEnabled(true);
                return dish7;
        }
        return null;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editingbutton:
                //保存最终图片
                save(currentBackground);
                break;
            case R.id.editnext:
                //下一张图片
                if (index<7) {
                    currentBackground=convert(backgroundpicture);
                    backgroundpicture.setBackground(new BitmapDrawable(currentBackground));
                    index+=1;
                    //使下一个图片显示
                    getcurrentImage(index);
                }else if (index==7){
                    currentBackground=convert(backgroundpicture);
                }
                else {
                    Toast.makeText(this,"Please press submit button",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public Bitmap convert(RelativeLayout i1) {
        //使控件可以进行缓存
        i1.setDrawingCacheEnabled(true);
        //获取缓存的 Bitmap
        Bitmap drawingCache = i1.getDrawingCache();
        //复制获取的 Bitmap
        drawingCache = Bitmap.createBitmap(drawingCache);
        //关闭视图的缓存
        i1.setDrawingCacheEnabled(false);

        if (drawingCache != null) {
            return drawingCache;
        } else {
            return null;
        }

    }
    @SuppressLint("NewApi")
    private void save(Bitmap bitmap){
        //保存到本地
        int storagePermission = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else {
            if(saveImageToGallery(this, bitmap)){
                Toast.makeText(this,R.string.Successful,Toast.LENGTH_SHORT).show();
            }
        }
    }
    //保存文件到指定路径
    public  boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "photoclips";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName =  System.currentTimeMillis()+"final.jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

//                    currentImage = getcurrentImage(index);
//                    currentBackground=convert(currentImage);
//                    currentImage = getcurrentImage(7);
//                    currentBackground=convert(currentImage);
//        // 保存叠加的图片
//        //如果是第一张
//        BitmapDrawable bd=null;
//        Bitmap bitmap=null;
//        if (index==1) {
//            bd= (BitmapDrawable) backgroundpicture.getBackground();
//            bitmap=bd.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
//        }else {
//            bitmap=currentBackground;
//            bitmap.copy(Bitmap.Config.ARGB_8888, true);
//        }
//        if (canvas == null) {
//            canvas = new Canvas(bitmap);
//        }
//        // 根据位置绘制
//        System.out.println("Editingpicture的起始X位置"+i1.getImageleft()+",Y位置"+i1.getImagetop());
//        if (i1!=null) {
//            canvas.drawBitmap(i1.getImageBitmap(), i1.getImageleft(), i1.getImagetop(), null);
//        }
//        BitmapDrawable result= new BitmapDrawable(bitmap);
//        // 显示在界面上
//        backgroundpicture.setBackground(result);
//        // 将图片设置不可见
//        i1.setVisibility(View.GONE);
//       return result.getBitmap();
//    float mCurrentScale = 1;
//    float last_x = -1;
//    float last_y = -1;
//    float baseValue;
//    int lastX, lastY;
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        ImageView current =(ImageView)v;
//        BitmapDrawable bd = (BitmapDrawable) current.getDrawable();
//        // TODO Auto-generated method stub
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            lastX = (int) event.getRawX();
//            lastY = (int) event.getRawY();
////            baseValue = 0;
////            float x = last_x = event.getRawX();
////            float y = last_y = event.getRawY();
//        }
//        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            if (event.getPointerCount() == 2) {
////                float x = event.getX(0) - event.getX(1);
////                float y = event.getY(0) - event.getY(1);
//                float x = event.getX(0);
//                float y = event.getY(0);
//                float scaleWidth = ((float) lastX) / x;
//                float scaleHeight = ((float) lastY) / y;
//                Matrix matrix = new Matrix();
//                matrix.postScale(scaleWidth, scaleHeight);
//                Bitmap bitmapOrg=bd.getBitmap();
//                Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, lastX, lastY, (int)x, (int)y, matrix, true);
//                ((ImageView) v).setImageBitmap(resizedBitmap);
////                float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
////                if (baseValue == 0) {
////                    baseValue = value;
////                }
////                else {
////                    if (value - baseValue >= 10 || value - baseValue <= -10) {
////                        float scaleWidth = ((float) newWidth) / width;
////                        float scaleHeight = ((float) newHeight) / height
//////                        float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
//////                        System.out.println();
//////                        img_scale(scale);  //缩放图片
////                    }
////                }
//            }
//            else if (event.getPointerCount() == 1) {
////                float x = event.getRawX();
////                float y = event.getRawY();
////                x -= last_x;
////                y -= last_y;
////                if (x >= 10 || y >= 10 || x <= -10 || y <= -10)
////                    img_transport(x, y); //移动图片位置
////                last_x = event.getRawX();
////                last_y = event.getRawY();
//                int dx = (int) event.getRawX() - lastX;
//                 int dy = (int) event.getRawY() - lastY;
//                 int left = v.getLeft() + dx;
//                 int top = v.getTop() + dy;
//                 int right = v.getRight() + dx;
//                 int bottom = v.getBottom() + dy;
//                 v.layout(left, top, right, bottom);
//                 lastX = (int) event.getRawX();
//                 lastY = (int) event.getRawY();
//
//            }
//        }
//        else if (event.getAction() == MotionEvent.ACTION_UP) {
//
//        }
//        return true;

//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastX = (int) event.getRawX();
//                lastY = (int) event.getRawY();
//                break;
//         case MotionEvent.ACTION_MOVE:
//                 int dx = (int) event.getRawX() - lastX;
//                 int dy = (int) event.getRawY() - lastY;
//                 int left = v.getLeft() + dx;
//                 int top = v.getTop() + dy;
//                 int right = v.getRight() + dx;
//                 int bottom = v.getBottom() + dy;
//                 v.layout(left, top, right, bottom);
//                 lastX = (int) event.getRawX();
//                 lastY = (int) event.getRawY();
//                 break;
//          case MotionEvent.ACTION_UP:
//                 break;
//            }
//            return true;
// }
