package com.example.photoclips.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.photoclips.Dialog.PictureEditingDialog;
import com.example.photoclips.Display.ConvertingActivity;
import com.example.photoclips.R;
import com.example.photoclips.utils.ImageUtil;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

public class DishViewAdapter extends RecyclerView.Adapter<DishViewAdapter.ViewHolder> implements View.OnClickListener{
    private List<Uri> uris;
    private Context context;
    public static final int TAKE_PHOTO_DISH = 2;
    public static final int DELETE_PHOTO_DISH = 3;
    public static final int REMAIN_PHOTO_DISH = 4;
    public static final int EDIT_PHOTO_DISH = 5;
    private int nextindex;
    private Bitmap background;
    private Handler mhandler;

    Activity current ;

    public Bitmap getBackground() {
        return background;
    }

    public void setBackgrounduri(Bitmap background) {
        this.background = background;
    }

    public DishViewAdapter(Context context, List<Uri> uris, Handler handler) {
        this.uris=uris;
        this.context=context;
        this.mhandler=handler;
    }

    public List<Uri> getUris() {
        return uris;
    }

    public void setUris(List<Uri> uris) {
        this.uris = uris;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ImageView1;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            ImageView1 = (ImageView) view.findViewById(R.id.rowpic1);
        }

        public ImageView getImageView1() {
            return ImageView1;
        }

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        current= findActivity(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picsrow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageView ImageView=holder.getImageView1();
        Uri currenturi=uris.get(position);
        ImageView.setImageURI(currenturi);
        nextindex=position+1;
        int clicked=getItemCount();
        //绑定添加图片事件
        if (position==0&&clicked<7){
            ImageView.setOnClickListener(this);
        }else {
            if (background != null) {
                //显示编辑弹窗
                ImageView.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        //跳转到新的一页
                        Intent intent=new Intent();
                        intent.setClass(context, ConvertingActivity.class);
                        intent.putExtra("Uri", String.valueOf(currenturi));
                        intent.putExtra("position",position);
                        intent.putExtra("backgroundpicture",String.valueOf(Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), background, null,null))));
                        current.startActivityForResult(intent,EDIT_PHOTO_DISH);
//                        PictureEditingDialog db = new PictureEditingDialog(context, currenturi, uris, position, background, new PictureEditingDialog.PriorityListener() {
//
//                            @Override
//                            public void refreshPriorityUI(List uri) {
//                                Thread thread=new Thread(){
//                                    @Override
//                                    public void run(){
//                                        nextindex-=1;
//                                        Message msg=Message.obtain();
//                                        msg.obj=uri;
//                                        mhandler.sendMessage(msg);
//                                    }
//                                };
//                                thread.start();
//                                uris=uri;
////                                notifyDataSetChanged();
//                            }
//
//                        });
//                        db.show();
//                        Window dialogWindow = db.getWindow();
//                        WindowManager m = dialogWindow.getWindowManager();
//                        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
//                        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
//                        p.height = (int) (d.getHeight() * 0.9); // 高度设置为屏幕的0.9
//                        p.width = (int) (d.getWidth() * 0.86); // 宽度设置为屏幕的0.86
//                        dialogWindow.setAttributes(p);
//                        db.setCancelable(true);
//                        db.setCanceledOnTouchOutside(true);
                        return false;
                    }
                });
            }else {
                    Toast.makeText(context,"Please upload background picture first", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public int getItemCount() {
        return uris.size();
    }
    @Override
    public void onClick(View v) {
        //一直正常在尾部添加到第7张图片
        if (nextindex <= 8) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(current, new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO_DISH);
            } else {
                //创建file对象，用于存储拍照后的图片；
                File outputImage = new File(current.getExternalCacheDir(), "dish" +  System.currentTimeMillis() + ".jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    uris.add(FileProvider.getUriForFile(context,
                            "com.example.lenovo.cameraalbumtest.fileprovider", outputImage));
                } else {
                    uris.add(Uri.fromFile(outputImage));
                }

                //启动相机程序
                if (nextindex==8) {
                    //替换最后一张到第一张去
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uris.get(0));
                    current.startActivityForResult(intent, TAKE_PHOTO_DISH);
                    Toast.makeText(context, R.string.OverMaximum,Toast.LENGTH_SHORT);
                }else {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uris.get(nextindex));
                    current.startActivityForResult(intent, TAKE_PHOTO_DISH);
                }
            }
        }
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
