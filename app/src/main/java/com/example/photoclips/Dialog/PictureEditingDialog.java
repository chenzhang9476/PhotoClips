package com.example.photoclips.Dialog;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.example.photoclips.utils.ContactApp;
import com.example.photoclips.utils.DrawableImageview;
import com.example.photoclips.utils.MyView;
import com.example.photoclips.utils.OpenCVImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import androidx.core.content.FileProvider;

public class PictureEditingDialog extends Dialog implements View.OnClickListener, View.OnTouchListener {

    private Context context;
    private Uri originalpicurl;
    private List<Uri> uris;
    private int position;
    private Button delete;
    private Button edit;
    private Button confirm;
    private ImageView destpic;
    private Bitmap result;
    private Bitmap backgroundpicture;
    private static int DELETE_PHOTO_DISH=3;
    ImageView original;
    ScrollView scrollView;
    private MODE mode = MODE.NONE;// 默认模式

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private enum MODE {
        NONE, DRAG, ZOOM
    }

    /**
     * 自定义Dialog监听器
     */
    public interface PriorityListener {
        /**
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
         */
         void refreshPriorityUI(List uris);
    }

    private PriorityListener listener;

    public PictureEditingDialog(Context context,Uri originalpicurl,List<Uri> uris,int position,Bitmap backgroundpicture, PriorityListener listener) {
        super(context);
        this.context=context;
        this.originalpicurl=originalpicurl;
        this.uris=uris;
        this.position=position;
        this.listener = listener;
        this.backgroundpicture=backgroundpicture;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_picture_editing);
        delete=(Button)findViewById(R.id.dialog_delete);
        edit=(Button)findViewById(R.id.dialog_edit);
        confirm=(Button)findViewById(R.id.dialog_confirm);
        scrollView=(ScrollView)findViewById(R.id.scrollview);
        original=(ImageView)findViewById(R.id.originalpic);
        destpic=(ImageView)findViewById(R.id.destpic);
        delete.setOnClickListener(this);
        edit.setOnClickListener(this);
        confirm.setOnClickListener(this);
        //初始化图片
        init();
    }
    private void init() {
        original.setImageURI(originalpicurl);
    }


    @Override
    public void onClick(View v) {
        Activity current = findActivity(context);
        switch (v.getId()){
            case R.id.dialog_delete:
                uris.remove(position);
                dismiss();
                listener.refreshPriorityUI(uris);
                break;
            case R.id.dialog_edit:

                //获取activity中的背景图
                final ProgressDialog show=ProgressDialog.show(context, "Editing", "Loading", true, true, null);
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
                            Bitmap bitmap=BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uris.get(position)));
//                            BitmapDrawable drawable=(BitmapDrawable) original.getDrawable();
//                            Bitmap bitmap=drawable.getBitmap();
//                            ,backgroundpicture
                            System.out.println("Bitmap:"+bitmap.getWidth()+"....Orginal"+original.getWidth());
                            System.out.println("Bitmap:"+bitmap.getHeight()+"....Orginal"+original.getHeight());
//                            result =openCV.testCutImage(bitmap,backgroundpicture);
//                            result =openCV.cupBitmap(bitmap);
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
            case R.id.dialog_confirm:
                //保留，并剪裁图片
                if (result!=null) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        uris.set(position,FileProvider.getUriForFile(context,
                                "com.example.lenovo.cameraalbumtest.fileprovider", getFile(result)));
                    } else {
                        Uri uriForFile = Uri.fromFile(getFile(result));
                        uris.set(position, uriForFile);
                    }
                    listener.refreshPriorityUI(uris);
                    dismiss();
                }else {
                    Toast.makeText(context,R.string.ConvertFirst,Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    public File getFile(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        File file = new File(context.getExternalCacheDir()+ "/converteddish"+ System.currentTimeMillis()+".jpg");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            int x = 0;
            byte[] b = new byte[1024 * 100];
            while ((x = is.read(b)) != -1) {
                fos.write(b, 0, x);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) context;
            return findActivity(wrapper.getBaseContext());
        } else {
            return null;
        }
    }

}