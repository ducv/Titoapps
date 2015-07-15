package com.tito.crazy.flash.ultils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import com.tito.crazy.flash.R;

public class SoundUltils {
	private static SoundUltils instance;
	public Context context;
	private MediaPlayer mp;

	private SoundUltils(Context context) {
		this.context = context;
	}

	public static SoundUltils getInstance(Context context) {
		if (instance == null) {
			instance = new SoundUltils(context);
		}
		return instance;
	}

	public void playSound(boolean isFlashOn) {
		if (ManagePreferences.getSound(context)) {
			if (isFlashOn) {
				mp = MediaPlayer.create(context, R.raw.switch_off);
			} else {
				mp = MediaPlayer.create(context, R.raw.switch_on);
			}
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.release();
				}
			});
			mp.start();
		} else
			return;

	}
}
