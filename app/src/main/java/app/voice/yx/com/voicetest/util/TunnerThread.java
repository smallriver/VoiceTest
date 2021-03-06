package app.voice.yx.com.voicetest.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.voice.yx.com.voicetest.Content;

/**
 * 通过调用FFT方法来实时计算输入音频的频率
 * @author Young
 */
public class TunnerThread extends Thread {

	static {
		System.loadLibrary("FFT");
	}

	public native double processSampleData(byte[] sample, int sampleRate);
	private static final int[] OPT_SAMPLE_RATES = { 11025, 8000, 22050, 44100 };
	private static final int[] BUFFERSIZE_PER_SAMPLE_RATE = { 8 * 1024,
			8 * 1024, 16 * 1024, 32 * 1024 };
	private int SAMPLE_RATE = 8000;
	private int READ_BUFFERSIZE = 16 * 1024;
	private double currentFrequency;

	private Handler handler;
	private Runnable callback;
	private AudioRecord audioRecord;

	public TunnerThread(Handler handler, Runnable callback) {
		this.handler = handler;
		this.callback = callback;
		initAudioRecord();
	}

	// 每个device的初始化参数可能不同
	private void initAudioRecord() {
		int counter = 0;
		for (int sampleRate : OPT_SAMPLE_RATES) {
			//采样率
			Content.sampleRate = sampleRate;
			initAudioRecord(sampleRate);
			//initAudioRecord(44100);
			if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
				SAMPLE_RATE = sampleRate;
				READ_BUFFERSIZE = BUFFERSIZE_PER_SAMPLE_RATE[counter];
				//READ_BUFFERSIZE = 32 * 1024;
				break;
			}
			counter++;
		}
	}

	@SuppressWarnings("deprecation")
	private void initAudioRecord(int sampleRate) {
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, sampleRate * 6);
	}

	@Override
	public void run() {
		audioRecord.startRecording();
		byte[] bufferRead = new byte[READ_BUFFERSIZE];
		while (audioRecord.read(bufferRead, 0, READ_BUFFERSIZE) > 0) {
			Content.bufferlength = bufferRead.length ;
			long starttime = System.currentTimeMillis();
			currentFrequency = processSampleData(bufferRead, SAMPLE_RATE);
			long endtime = 	System.currentTimeMillis();
			Content.subtime = endtime -starttime ;
			if (currentFrequency > 0) {
				SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日 HH:mm:ss ");
				Date curDate = new Date(System.currentTimeMillis());//获取当前时间
				Content.datetime = formatter.format(curDate);
				handler.post(callback);
				try {
					if (audioRecord.getState() ==  AudioRecord.STATE_INITIALIZED)
						audioRecord.stop();
					//采样间隔
					Thread.sleep(1500);
					audioRecord.startRecording();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void close() {
		if (audioRecord != null
				&& audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
			audioRecord.stop();
			audioRecord.release();
		}
	}

	public double getCurrentFrequency() {
		return currentFrequency;
	}

}
