package app.voice.yx.com.voicetest;

import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
/**
 * Created by Jzh on 2016/07/13.
 */
public class AnimationUtil {
	private static final String TAG = AnimationUtil.class.getSimpleName();

	/**
	 * 从控件所在位置移动到控件的底部
	 * @return
	 */
	public static TranslateAnimation moveToViewBottom() {
		TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
		mHiddenAction.setDuration(500);
		return mHiddenAction;
	}

	/**
	 * 从控件的底部移动到控件所在位置
	 * @return
	 */
	public static TranslateAnimation moveToViewLocation() {
		TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				7.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		mHiddenAction.setDuration(100);
//		TranslateAnimation animation = new TranslateAnimation(0, 0,300, 300);
//		animation.setDuration(500);
		return mHiddenAction;
	}
}
