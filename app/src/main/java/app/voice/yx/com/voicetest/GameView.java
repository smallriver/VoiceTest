package app.voice.yx.com.voicetest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

import app.voice.yx.com.voicetest.util.TunnerThread;

/**
 * Created by Jzh on 2016/09/19.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback,
        Runnable {
    private SurfaceHolder holder;
    private Canvas canvas;
    private Bitmap background;
    private Bitmap lineplayer;
    private Bitmap videoplayer;
    private Bitmap videoplayer2;
    private boolean isRunning = true;
    private int dy, dy2;        //用于背景移动
    private Rect srcRect;      //用于放大图片, 和存储图片的位置
    private Rect destRect;
    private int with, height;
    private Context icontext;

    private int firstplace = 0;
    private int secondplace  = 0;

    private Rect destRect2;

    private TunnerThread tunner;

    public GameView(Context context, int mSurfaceViewWidth, int mSurfaceViewHeight) {
        super(context);
        icontext = context;
        this.setFocusable(true);
        with = mSurfaceViewWidth;
        height = mSurfaceViewHeight;
        dy = 0;
        dy2 = -mSurfaceViewHeight;
        holder = this.getHolder();//这个this指的是这个Surface
        holder.addCallback(this); //这个this表示实现了Callback接口
        srcRect = new Rect(0, 0, mSurfaceViewWidth, mSurfaceViewHeight);//原始图片大小
        destRect = new Rect(0, 0, mSurfaceViewWidth, mSurfaceViewHeight);//目标屏幕大小. 并带有移动变量dy. 对应图片1
        destRect2 = new Rect(0, 0, mSurfaceViewWidth, mSurfaceViewHeight);//屏幕上方的图片,对应图片2

        background = decodeBitmap(context, R.mipmap.backup, mSurfaceViewWidth, mSurfaceViewHeight);

        lineplayer = decodeBitmap(context, R.mipmap.heng, mSurfaceViewWidth, 10);
        videoplayer = decodeBitmap(context, R.mipmap.videoyellow, 70, height / 6);
        videoplayer2 = decodeBitmap(context, R.mipmap.videoyellow, 70, height / 6);
    }

    public GameView(Context context) {
        super(context);
        DisplayMetrics dm = new DisplayMetrics();
       // context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mSurfaceViewWidth = dm.widthPixels;
        int mSurfaceViewHeight = dm.heightPixels;
        icontext = context;
        this.setFocusable(true);
        with = mSurfaceViewWidth;
        height = mSurfaceViewHeight;
        dy = 0;
        dy2 = -mSurfaceViewHeight;
        holder = this.getHolder();//这个this指的是这个Surface
        holder.addCallback(this); //这个this表示实现了Callback接口
        srcRect = new Rect(0, 0, mSurfaceViewWidth, mSurfaceViewHeight);//原始图片大小
        destRect = new Rect(0, 0, mSurfaceViewWidth, mSurfaceViewHeight);//目标屏幕大小. 并带有移动变量dy. 对应图片1
        destRect2 = new Rect(0, 0, mSurfaceViewWidth, mSurfaceViewHeight);//屏幕上方的图片,对应图片2

        background = decodeBitmap(context, R.mipmap.backup, mSurfaceViewWidth, mSurfaceViewHeight);

        lineplayer = decodeBitmap(context, R.mipmap.heng, mSurfaceViewWidth, 10);
        videoplayer = decodeBitmap(context, R.mipmap.videoyellow, 70, height / 6);
        videoplayer2 = decodeBitmap(context, R.mipmap.videoyellow, 70, height / 6);
    }


    /**
     * @param displayWidth  需要显示的宽度
     * @param displayHeight 需要显示的高度
     * @return Bitmap
     */
    public Bitmap decodeBitmap(Context context, int id, int displayWidth, int displayHeight) {
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

    @Override
    public void run() {
        while (isRunning) {
            drawView();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private Handler handler = new Handler();
    private Runnable callback = new Runnable() {

        public void run() {
            updateText(tunner.getCurrentFrequency());
        }
    };

    private void updateText(double currentFrequency) {
        while (currentFrequency < 82.41) {
            currentFrequency = currentFrequency * 2;
        }
        while (currentFrequency > 164.81) {
            currentFrequency = currentFrequency * 0.5;
        }
        //currentFrequency即为通过快速傅立叶变换计算出的声音频率。
        BigDecimal a = new BigDecimal(currentFrequency);
        BigDecimal result = a.setScale(2, RoundingMode.DOWN);
        Toast.makeText(icontext,
                "当前声音频率:"+String.valueOf(result)+"\n"
                +"当前声音长度"+Content.bufferlength +"\n"
                +"当前计算时间"+Content.subtime+"ms", Toast.LENGTH_LONG).show();
    }

    private void startTunning() {
        tunner = new TunnerThread(handler, callback);
        tunner.start();
    }

    private void drawView() {
        try {
            if (holder != null) {
                canvas = holder.lockCanvas();
                canvas.drawColor(Color.WHITE);

                //画背景, 并使其移动
                dy += 4;
                dy2 += 4;
                destRect.set(0, dy, with, height + dy);
                destRect2.set(0, dy2, with, height + dy2);
                canvas.drawBitmap(background, srcRect, destRect, null);
                canvas.drawBitmap(background, srcRect, destRect2, null);

                //判断是否到达屏幕底端, 到达了则使其回到屏幕上端
                if (dy >= height) {
                    dy = -height;
                    destRect = new Rect(0, 0, with, height);
                }
                if (dy2 >= height) {
                    dy2 = -height;
                    destRect2 = new Rect(0, 0, with, height);
                }

                if (height / 6 + dy >= 780) {
                    if (firstplace <= 20) {
                        //画线
                        canvas.drawBitmap(videoplayer, with / 4 + firstplace, dy, null);
                        videoplayer = decodeBitmap(icontext, R.mipmap.videored, 70, height / 6);
                        firstplace = firstplace + 5;
                    } else {
                        //画线
                        canvas.drawBitmap(videoplayer, with / 4, dy, null);
                        videoplayer = decodeBitmap(icontext, R.mipmap.videored, 70, height / 6);
                    }

                } else {
                    firstplace = 0;
                    canvas.drawBitmap(videoplayer, with / 4, dy, null);
                    videoplayer = decodeBitmap(icontext, R.mipmap.videoyellow, 70, height / 6);

                }

                if (height / 3 + dy >= 780) {
                    if (secondplace <= 20) {
                        //画线
                        canvas.drawBitmap(videoplayer2, with / 2 + secondplace, dy+height / 6, null);
                        videoplayer2 = decodeBitmap(icontext, R.mipmap.videored, 70, height / 6);
                        secondplace = secondplace + 5;
                    } else {
                        //画线
                        canvas.drawBitmap(videoplayer2, with / 2, dy+height / 6, null);
                        videoplayer2 = decodeBitmap(icontext, R.mipmap.videored, 70, height / 6);
                    }

                } else {
                    secondplace = 0;
                    canvas.drawBitmap(videoplayer, with / 2, dy+height / 6, null);
                    videoplayer = decodeBitmap(icontext, R.mipmap.videoyellow, 70, height /6);
                }

                canvas.drawBitmap(lineplayer, 0, 780, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        new Thread(this).start();
        onRecord(isRunning);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        new Thread(this).stop();
        new Thread(this).destroy();
        tunner.close();
        tunner.destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            isRunning = false;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isRunning) {
            isRunning = false;
            onRecord(isRunning);
            Toast.makeText(icontext,
                    "暂停动画，录音继续", Toast.LENGTH_LONG).show();
        } else {
            isRunning = true;
            //onRecord(isRunning);
            Toast.makeText(icontext,
                    "暂停动画，录音继续", Toast.LENGTH_LONG).show();

            new Thread(this).start();
        }
        return super.onTouchEvent(event);
    }

    private void onRecord(boolean startRecording) {
        if (startRecording) {
            startTunning();
        } else {
            stopTunning();
        }
    }

    private void stopTunning() {
        //tunner.stop();
    }
}