package com.example.photoclips.Display;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.photoclips.Adapter.DishViewAdapter;
import com.example.photoclips.R;
import com.example.photoclips.utils.ContactApp;
import com.example.photoclips.utils.FixSizeLinkedList;
import com.example.photoclips.utils.OpenCVImpl;
import com.example.photoclips.utils.SpaceItemDecoration;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class DisplayMainActivity extends  Activity implements View.OnClickListener{
    public static final int TAKE_PHOTO = 1;
    public static final int TAKE_PHOTO_DISH = 2;
    public static final int DELETE_PHOTO_DISH = 3;
    public static final int REMAIN_PHOTO_DISH = 4;
    public static final int EDIT_PHOTO_DISH = 5;
    private ImageView backpic;
    private List<Uri> dishrowuris;
    private Uri imageUri;
    private Bitmap backgroundpicture;
    private RecyclerView DishRow;
    private DishViewAdapter dishViewAdapter;
    private Button submit;
    private static final String TAG = "koutuTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_main);
        //background picsture
        backpic=(ImageView)findViewById(R.id.DisplayPicture);
        backpic.setOnClickListener(this);
        submit=(Button)findViewById(R.id.displaybutton);
        submit.setOnClickListener(this);
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
        + getResources().getResourcePackageName(R.drawable.displayuploadbottom) + "/"
        + getResources().getResourceTypeName(R.drawable.displayuploadbottom) + "/"
        + getResources().getResourceEntryName(R.drawable.displayuploadbottom));
        dishrowuris=new FixSizeLinkedList<Uri>(8);
        //初始化，先把添加图片加入队列
        dishrowuris.add(0, uri);
        //初始化底部的小图片控件
        initpics();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //openCV4Android 需要加载用到
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }
    private void ChangeUri(int readydeleteindex,Uri ReadyChangedUri) {
        if (ReadyChangedUri!=null){
            dishrowuris.set(readydeleteindex,ReadyChangedUri);
            dishViewAdapter.notifyDataSetChanged();
        }
    }

    private void deleteUri(int readydeleteindex) {
        dishrowuris.remove(readydeleteindex);
        dishViewAdapter.notifyDataSetChanged();
    }

    private void initpics() {
        GridLayoutManager GridRow=new GridLayoutManager(this,4);
        DishRow=(RecyclerView)findViewById(R.id.displayrow1);
        //设置间隔
        int space = getResources().getDimensionPixelSize(R.dimen.SubpicMargin);
        DishRow.addItemDecoration(new SpaceItemDecoration(space));
        DishRow.setLayoutManager(GridRow);//初始化一个Handler方便Adapter与Activit通讯
        Handler mhandler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                dishrowuris= (List<Uri>) msg.obj;
                dishViewAdapter.notifyDataSetChanged();
            }
        };
        //shipeiqi
        dishViewAdapter=new DishViewAdapter(this,dishrowuris,mhandler);
        DishRow.setAdapter(dishViewAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.DisplayPicture:
                // 动态申请权限
                if (ContextCompat.checkSelfPermission(DisplayMainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DisplayMainActivity.this, new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO);
                } else {
                    // 创建一个File对象，用于保存摄像头拍下的图片，这里把图片命名为background.jpg
                    // 并将它存放在手机SD卡的应用关联缓存目录下
                    File outputImage = new File(getExternalCacheDir(), "background.jpg");
                    // 对照片的更换设置
                    try {
                        // 如果上一次的照片存在，就删除
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                        // 创建一个新的文件
                        outputImage.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 如果Android版本大于等于7.0
                    if (Build.VERSION.SDK_INT >= 24) {
                        // 将File对象转换成一个封装过的Uri对象
                        imageUri = FileProvider.getUriForFile(this, "com.example.lenovo.cameraalbumtest.fileprovider", outputImage);
                        Log.d("MainActivity", outputImage.toString() + "手机系统版本高于Android7.0");
                    } else {
                        // 将File对象转换为Uri对象，这个Uri标识着output_image.jpg这张图片的本地真实路径
                        Log.d("MainActivity", outputImage.toString() + "手机系统版本低于Android7.0");
                        imageUri = Uri.fromFile(outputImage);
                    }
                    // 启动相机程序
                    startCamera();
                }
                break;
            case R.id.displaybutton:
                Bitmap chckingbm;
                Boolean next=true;
                for (int i = 1; i <dishrowuris.size() ; i++) {
                    try {
                        chckingbm= BitmapFactory.decodeStream(getContentResolver().openInputStream(dishrowuris.get(i)));
                        //判断是否有菜品
                        if(chckingbm!=null){
                            if (chckingbm.getWidth()!=512){
                                next=false;
                            }
                        }else {
                            Toast.makeText(this,R.string.Uploadinfo,Toast.LENGTH_SHORT).show();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                    if (next) {
                        Intent intent = new Intent(this, EditingPictureActivity.class);
                        Bundle buddle = new Bundle();
                        //因为linkedlist不能在intent里面传递值，要用application
                        ContactApp app = (ContactApp) getApplication();
                        app.setUris(dishrowuris);
                        buddle.putString("background", String.valueOf(imageUri));
                        intent.putExtras(buddle);
                        startActivity(intent);
                    }else {
                        System.out.println("Please convert all pictures first");
                        Toast.makeText(this,R.string.ConvertFirst,Toast.LENGTH_SHORT).show();
                    }
                break;
            default:
                break;
        }

    }
    private void startCamera() {
        Intent intent4 = new Intent("android.media.action.IMAGE_CAPTURE");
        // 指定图片的输出地址为imageUri
        intent4.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent4, TAKE_PHOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                //相册照片/背景照片
                if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
                    // 将图片解析成Bitmap对象
                    try {
                        InputStream inputStream =getContentResolver().openInputStream(imageUri);
                        backgroundpicture = BitmapFactory.decodeStream(inputStream);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    backpic.setImageBitmap(backgroundpicture);
                    //将背景图给到适配器
                    dishViewAdapter.setBackgrounduri(backgroundpicture);
                }
                break;
            case TAKE_PHOTO_DISH:
                //相册照片/背景照片
                if (requestCode == TAKE_PHOTO_DISH && resultCode == RESULT_OK) {
                    //获取图片栈
                    dishrowuris = dishViewAdapter.getUris();
                    //重新设置adapter
                    dishViewAdapter.notifyDataSetChanged();
                }
                break;
            case EDIT_PHOTO_DISH:
                //删除图片
                if (requestCode==EDIT_PHOTO_DISH&&resultCode==DELETE_PHOTO_DISH){
                    int readydeleteindex=data.getIntExtra("position",8);
                    if (readydeleteindex!=8) {
                        dishrowuris.remove(readydeleteindex);
                        dishViewAdapter.notifyDataSetChanged();
                    }
                } else if (requestCode==EDIT_PHOTO_DISH&&resultCode==REMAIN_PHOTO_DISH){
                    //替换图片
                    int readydeleteindex=data.getIntExtra("position",8);
                    Uri ReadyChangedUri=Uri.parse(data.getStringExtra("resulturl"));
                    if (ReadyChangedUri!=null){
                        dishrowuris.set(readydeleteindex,ReadyChangedUri);
                        dishViewAdapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
}