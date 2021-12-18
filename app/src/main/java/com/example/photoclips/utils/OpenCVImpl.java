package com.example.photoclips.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class OpenCVImpl {
    private static final String TAG = "koutuTest";
    Mat firstMask;
    Mat bgModel;
    Mat fgModel;
    Mat source;
    Mat img;
    Mat foreground;
    public Bitmap cupBitmap(Bitmap bitmap,int x,int y,int width,int height) {
         img = new Mat();
        //缩小图片尺寸
         Bitmap bm = Bitmap.createScaledBitmap(bitmap,512,512,true);
        //bitmap->mat
        Utils.bitmapToMat(bm, img);
        //转成CV_8UC3格式
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2RGB);
        Rect rect=null;
        //设置抠图范围的左上角和右下角
        if (x!=0&&y!=0&&width!=0&&height!=0) {
            rect = new Rect(x, y, width, height);
        }else {
            rect = new Rect(10, 10, bm.getWidth() - 10, bm.getHeight() - 10);
        }
        //生成遮板
         firstMask = new Mat();
         bgModel = new Mat();
         fgModel = new Mat();
         source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
        //这是实现抠图的重点，难点在于rect的区域，为了选取抠图区域，我借鉴了某大神的自定义裁剪View，返回坐标和宽高
        Imgproc.grabCut(img, firstMask, rect, bgModel, fgModel, 5, Imgproc.GC_INIT_WITH_RECT);
        Core.compare(firstMask, source, firstMask, Core.CMP_EQ);

        //抠图
        foreground = new Mat(img.size(), CvType.CV_8UC3, new Scalar(255,255,255));
        img.copyTo(foreground, firstMask);

        //mat->bitmap
        Bitmap bitmap1 = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(foreground, bitmap1);
        return bitmap1;
    }

    public Bitmap testCutImage(Bitmap bitmap,Bitmap backgroundBitmap,int x,int y,int width,int height) {

        try {

            backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, bitmap.getWidth(), bitmap.getHeight(), true);
            Log.d(TAG, "testCutImage| 开始变换 bitmap : " + bitmap);
            /*FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CutOutPeople/test2.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(fis);*/
            Log.d(TAG, "testCutImage| bitmap : " + bitmap);
            Mat img = new Mat();
            //缩小图片尺寸
            Bitmap bm = Bitmap.createScaledBitmap(bitmap, 512, 512, true);
            //bitmap->mat
            Utils.bitmapToMat(bm, img);
            //转成CV_8UC3格式
            Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2RGB);
            Rect rect=null;
            //这个是抠图范围左上角和右下角，如果有问题修改这里
//            Rect rect = new Rect(10, 10, bm.getWidth() - 50, bm.getHeight() - 10);
            if (x!=0&&y!=0&&width!=0&&height!=0) {
                 rect = new Rect(x, y, width, height);
            }else {
                 rect = new Rect(10, 10, bm.getWidth() - 10, bm.getHeight() - 10);
            }
            //生成遮板
            Mat mask = new Mat();
            Mat bgModel = new Mat();
            Mat fgModel = new Mat(0, 0, CvType.CV_8U, new Scalar(155, 15, 10));
            Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
//            firstMask.create(img.size(), CvType.CV_8UC1, new Scalar(0));
//            bgModel.create(img.size(),CvType.CV_8UC1);
//            fgModel.create(img.size(),CvType.CV_8UC1);
            Imgproc.grabCut(img, mask, rect, bgModel, fgModel, 5, Imgproc.GC_INIT_WITH_RECT);
            Core.compare(mask, source, mask, Core.CMP_EQ);

//            边缘模糊处理
            Mat blurMask = new Mat();

            Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
            //膨胀
            Imgproc.dilate(mask, mask, element, new Point(-1, -1), 1);
            //腐蚀
            Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
            Imgproc.erode(mask, mask, kernelErode);
//            双边模糊
//            Imgproc.bilateralFilter(mask, blurMask, 3, 200, 200);
            //高斯模糊
            Imgproc.GaussianBlur(mask, blurMask, new Size(3, 3), 0, 0);
            //保存模糊后的mask图像
            Bitmap b = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(blurMask, b);

            //图片保存
            saveBitmap(b,   Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"mask" + System.currentTimeMillis() + ".png");

            //抠图
//            Mat foreground = new Mat(img.size(), CvType.CV_8U, new Scalar(196, 15, 24, 0));
            Mat destMat = new Mat(img.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));

            Mat backgroundMat = new Mat();
            Utils.bitmapToMat(backgroundBitmap, backgroundMat);
            Imgproc.cvtColor(backgroundMat, backgroundMat, Imgproc.COLOR_RGBA2RGB);

            Bitmap c = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(destMat, c);
            //将img图像的mask选中的图像扣出来放在foreground中
            img.copyTo(destMat, blurMask);
            //进行像素替换
            double[] m, backgroundColor, imgColor;
            int blackSum = 0;
            int whiteSum = 0;
            int otherSum = 0;

            //用来计算权重
            double w;
            double imgColor1, imgColor2, imgColor3;
            double backgroundColor1, backgroundColor2, backgroundColor3;

            double[] mixColor = new double[3];
            int length1 = 0;
            int length2 = 0;
            int length3 = 0;

            int imgLength1 = 0;
            int imgLength2 = 0;
            int imgLength3 = 0;

            int backgroundLength1 = 0;
            int backgroundLength2 = 0;
            int backgroundLength3 = 0;

            //这里是比较重要，进行图像的替换和原图边缘和背景图的元素融合处理
            for (int i = 0; i < img.rows(); i++) {
                for (int j = 0; j < img.cols(); j++) {
                    //当前元素数组
                    m = blurMask.get(i, j);
                    if (m[0] == 255) {//抠图部分
                        destMat.put(i, j, img.get(i, j));
                        whiteSum++;
                    } else if (m[0] == 0) {//背景黑色部分用别的图片代替
                        destMat.put(i, j, backgroundMat.get(i, j));
                        blackSum++;
                    } else {//mask 被模糊处理之后的人像边缘区域，要进行元素融合处理，让抠图效果更好一点
                        otherSum++;
                        //计算权重
                        w = m[0] / 255.0;
                        //获取前景元素
                        imgColor = img.get(i, j);
                        //获取背景元素
                        backgroundColor = backgroundMat.get(i, j);

                        //前景元素
                        imgColor1 = imgColor[0];
                        imgColor2 = imgColor[1];
                        imgColor3 = imgColor[2];

                        //背景元素
                        backgroundColor1 = backgroundColor[0];
                        backgroundColor2 = backgroundColor[1];
                        backgroundColor3 = backgroundColor[2];

                        //元素点混合
                        mixColor[0] = imgColor1 * w + backgroundColor1 * (1.0 - w);
                        mixColor[1] = imgColor2 * w + backgroundColor2 * (1.0 - w);
                        mixColor[2] = imgColor3 * w + backgroundColor3 * (1.0 - w);

                        //进行存放
                        destMat.put(i, j, mixColor);
                    }
                }
            }
            Log.d(TAG, "替换完毕，展示各个累加值，blackSum : " + blackSum + ",whiteSum : " + whiteSum + ",otherSum : " + otherSum);
            Log.d(TAG, "替换完毕，展示各个累加值，length1 : " + length1 + ",length2 : " + length2 + ",length3 : " + length3);
            Log.d(TAG, "替换完毕，展示各个累加值，backgroundLength1 : " + backgroundLength1 + ",backgroundLength2 : " + backgroundLength2 + ",backgroundLength3 : " + backgroundLength3);
            Log.d(TAG, "替换完毕，展示各个累加值，imgLength1 : " + imgLength1 + ",imgLength2 : " + imgLength2 + ",imgLength3 : " + imgLength3);

            //将处理的foreground图像保存
            Utils.matToBitmap(destMat, b);

//            mImage.setImageBitmap(b);
            Mat img3 = new Mat();
            img.copyTo(img3);
            img3.setTo(new Scalar(0), blurMask);
            Bitmap img3Three = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(img3, img3Three);

            //图像保存
//            saveBitmap(b,  Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+System.currentTimeMillis() + ".png");
//            saveBitmap(c,  Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"forMat" + System.currentTimeMillis() + ".png");
//            saveBitmap(backgroundBitmap,  Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"background" + System.currentTimeMillis() + ".png");
//            saveBitmap(img3Three, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"imgThree" + System.currentTimeMillis() + ".png");
            return b;
        } catch (Exception e) {
            Log.e(TAG, "异常信息为 : " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public static void saveBitmap(Bitmap bitmap, String path) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            savePath = path;
        } else {
            Log.d(TAG, "saveBitmap failure : sdcard not mounted");
            return;
        }
//        Log.d(TAG, "saveBitmap savePath : " + savePath);
        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.d(TAG, "saveBitmap: " + e.getMessage());
            return;
        }
        Log.d(TAG, "saveBitmap success: " + filePic.getAbsolutePath());
    }
}
