package app.voice.yx.com.voicetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import app.voice.yx.com.voicetest.videointerface.VideoStopAwaken;

/**
 * Created by Jzh on 2016/09/19.
 */
public class MainActivity extends AppCompatActivity implements VideoStopAwaken {

    private GameView gv;
    private ImageView img_video_setting;
    private LinearLayout ll_video_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        gv = (GameView)findViewById(R.id.game_view);
        gv.setOntitleClick(this);
        img_video_setting = (ImageView)findViewById(R.id.img_video_setting);
        ll_video_setting = (LinearLayout)findViewById(R.id.ll_video_setting);

    }

    @Override
    public void videoStopAwaken(boolean isstop) {
        //是否暂停
        if(isstop){
            img_video_setting.setVisibility(View.INVISIBLE);
            ll_video_setting.setVisibility(View.INVISIBLE);
        }else{
            img_video_setting.setVisibility(View.VISIBLE);
            ll_video_setting.setVisibility(View.VISIBLE);
        }
    }
}
