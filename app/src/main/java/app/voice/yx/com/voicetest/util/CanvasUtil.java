package app.voice.yx.com.voicetest.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import app.voice.yx.com.voicetest.GameView;
import app.voice.yx.com.voicetest.R;

/**
 * Created by Jzh on 2016/9/28.
 * 画图工具类
 *
 */
public  class CanvasUtil {

    /**
     * @param displayWidth  需要显示的宽度
     * @param displayHeight 需要显示的高度
     * @return Bitmap
     */
    public static Bitmap decodeBitmap(Context context, int id, int displayWidth, int displayHeight) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        // op.inJustDecodeBounds = true;表示我们只读取Bitmap的宽高等信息，不读取像素。
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id, op); // 获取尺寸信息
        // op.outWidth表示的是图像真实的宽度
        // op.inSamplySize 表示的是缩小的比例
        // op.inSamplySize = 4,表示缩小1/4的宽和高，1/16的像素，android认为设置为2是最快的。
        // 获取比例大小
        int wRatio = (int) Math.ceil(op.outWidth / (float) displayWidth);
        int hRatio = (int) Math.ceil(op.outHeight / (float) displayHeight);
        // 如果超出指定大小，则缩小相应的比例
        if (wRatio > 1 && hRatio > 1) {
            if (wRatio > hRatio) {
                // 如果太宽，我们就缩小宽度到需要的大小，注意，高度就会变得更加的小。
                op.inSampleSize = wRatio;
            } else {
                op.inSampleSize = hRatio;
            }
        }
        op.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeResource(context.getResources(), id, op);
        // 从原Bitmap创建一个给定宽高的Bitmap
        return Bitmap.createScaledBitmap(bmp, displayWidth, displayHeight, true);
    }


    /**
     *@author Jzh
     *@time 2016/9/29 14:18
     *视屏中每一个小控件处理
     * @param icontext
     * @param canvas
     * @param videoplayer
     * @param with
     * @param height
     * @param position 当前所在位置
     */
    public static Bitmap canvasDisplayVideo(Context icontext, Canvas canvas, Bitmap videoplayer,
                                          int position, int with, int height,int cheight){
        //position 当前所处位置
        if (position >= GameView.lineplace) {
            //若所处位置小于20,执行弹动动画
            if (position-780 <= 20) {
                //画线
                canvas.drawBitmap(videoplayer, with+ position-780, height, null);
                videoplayer = CanvasUtil.decodeBitmap(icontext, R.mipmap.videored, 180, cheight);
            } else {
                //否则划线
                canvas.drawBitmap(videoplayer, with, height, null);
                videoplayer = CanvasUtil.decodeBitmap(icontext, R.mipmap.videored, 180, cheight);
            }
            //在横线之上的时候划线
        } else {
            canvas.drawBitmap(videoplayer, with , height, null);
            videoplayer = CanvasUtil.decodeBitmap(icontext, R.mipmap.videoyellow, 180, cheight);
        }
        return  videoplayer;
    }

}
