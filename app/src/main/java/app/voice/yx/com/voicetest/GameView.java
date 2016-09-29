package app.voice.yx.com.voicetest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

import app.voice.yx.com.voicetest.util.CanvasUtil;
import app.voice.yx.com.voicetest.util.TunnerThread;
import app.voice.yx.com.voicetest.videointerface.VideoStopAwaken;

/**
 * Created by Jzh on 2016/09/19.
 */
public class GameView extends SurfaceView implements
        SurfaceHolder.Callback,Runnable{
    //横线所处位置
    public static  int lineplace = 800;

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
    private VideoStopAwaken listener;

    /**
     *
     * 新构造函数
     *@author Jzh
     *@time 2016/9/29 14:08
     *
     *
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.setFocusable(true);
        holder = this.getHolder();//这个this指的是这个Surface
        holder.addCallback(this); //这个this表示实现了Callback接口
        icontext = context;
    }

    /**
     * author Jzh
     *@time 2016/9/27 13:43
     *
     *
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("TEST","Width="+ this.getWidth()+"|Height="+this.getHeight());
        int mSurfaceViewWidth =this.getWidth();
        int mSurfaceViewHeight = this.getHeight();
        with = mSurfaceViewWidth;
        height = mSurfaceViewHeight;
        dy = 0;
        dy2 = -mSurfaceViewHeight;
        srcRect = new Rect(0, 0, mSurfaceViewWidth, mSurfaceViewHeight);//原始图片大小
        destRect = new Rect(0, 0, mSurfaceViewWidth, mSurfaceViewHeight);//目标屏幕大小. 并带有移动变量dy. 对应图片1
        destRect2 = new Rect(0, 0, mSurfaceViewWidth, mSurfaceViewHeight);//屏幕上方的图片,对应图片2
        background = CanvasUtil.decodeBitmap(icontext, R.mipmap.backup, mSurfaceViewWidth, mSurfaceViewHeight);
        lineplayer = CanvasUtil.decodeBitmap(icontext, R.mipmap.heng, mSurfaceViewWidth, 25);
        videoplayer = CanvasUtil.decodeBitmap(icontext, R.mipmap.videoyellow, 180, height / 4);
        videoplayer2 = CanvasUtil.decodeBitmap(icontext, R.mipmap.videoyellow, 180, height / 4);
        isRunning = true;
        new Thread(this).start();
        onRecord(isRunning);
    }


    @Override
    public void run() {
        while (isRunning) {
            drawView();
            try {
                Thread.sleep(50);
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
                 "当前声音频率: "+String.valueOf(result)+"\n"
                +"当前声音长度: "+Content.bufferlength +"\n"
                +"当前计算时间: "+Content.subtime+"ms"+"\n"
                +"当前时间: "+Content.datetime+"\n"
                +"当前采样率: "+Content.sampleRate+"HZ(赫兹)", Toast.LENGTH_SHORT).show();
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

                videoplayer = CanvasUtil.canvasDisplayVideo
                        (icontext,canvas,videoplayer,height / 4 + dy  ,with/4,dy,height / 4);
                videoplayer2 = CanvasUtil.canvasDisplayVideo
                        (icontext,canvas,videoplayer2,height / 2 + dy  ,with/2,dy+height / 4,height / 4);

                canvas.drawBitmap(lineplayer, 0, lineplace, null);
                if(dy==4){
                    isRunning =false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    //退出方法时，线程销毁
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
            listener.videoStopAwaken(isRunning);
            onRecord(isRunning);
            Toast.makeText(icontext,
                    "暂停动画，录音继续", Toast.LENGTH_LONG).show();
        } else {
            isRunning = true;
            listener.videoStopAwaken(isRunning);
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

    public void setOntitleClick(VideoStopAwaken listener) {
        this.listener = listener;
    }
}