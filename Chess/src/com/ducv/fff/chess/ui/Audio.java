package com.ducv.fff.chess.ui;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class Audio {

	public static final float MIN_VOLUME = 0;

	public static final float DEFAULT_VOLUME = 0.5f;

	public static final float MAX_VOLUME = 1;

	private static MediaPlayer[] _players;
	private static int[] _resAudioId;
	private static float[] _volume;
	public static void init(Context context, int[] resAudioId) {
		_players = new MediaPlayer[resAudioId.length];
		_resAudioId = new int[resAudioId.length];
		_volume = new float[resAudioId.length];
		for (int i = 0; i < resAudioId.length; i++) {
			_players[i] = MediaPlayer.create(context, resAudioId[i]);
			_resAudioId[i] = resAudioId[i];
		}
	}

	public static void free() {
		for (int id : _resAudioId) {
			Audio.release(id);
		}
		_players = null;
		_resAudioId = null;
		_volume = null;
	}

	public static void prepare(Context context, int resAudioId) {
		if (_resAudioId == null)
			return;
		for (int i = 0; i < _resAudioId.length; i++) {
			if (_resAudioId[i] == resAudioId) {
				_players[i].reset();
				try {
					_players[i].prepare();
				} catch (IllegalStateException e) {

				} catch (IOException e) {

				}

			}
		}
	}

	public static boolean isPlaying(int resAudioId) {
		if (_resAudioId == null)
			return false;
		for (int i = 0; i < _resAudioId.length; i++) {
			if (_resAudioId[i] == resAudioId)
				return _players[i].isPlaying();
		}
		return false;
	}

	public static void play(AudioManager audiomanager, int resAudioId, boolean loop) {
		float maxAudio;
		maxAudio = audiomanager.getStreamMaxVolume(5);
		for (int i = 0; i < _resAudioId.length; i++) {
			if (_resAudioId[i] == resAudioId) {
				_players[i].setLooping(loop);
				_players[i].start();
				_players[i].setVolume((float)audiomanager.getStreamVolume(5)/maxAudio,
						(float)audiomanager.getStreamVolume(5)/maxAudio);

			}
		}
	}

	public static void pause(int resAudioId) {
		if (_resAudioId == null)
			return;
		for (int i = 0; i < _resAudioId.length; i++) {
			if (_resAudioId[i] == resAudioId) {
				if (_players[i] == null)
					return;
				if (_players[i].isPlaying()) {
					try {
						_players[i].pause();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	public static void release(int resAudioId) {
		if (_resAudioId == null)
			return;
		for (int i = 0; i < _resAudioId.length; i++) {
			if (_resAudioId[i] == resAudioId) {
				if (_players[i] == null)
					return;
				if (_players[i].isPlaying()) {
					try {
						_players[i].pause();

					} catch (Exception e) {
					}
				}
				_players[i].stop();
				_players[i].release();
				_volume[i] = 0;
				_resAudioId[i] = -1;
				_players[i] = null;
			}

		}

	}

	public static void setVolume(int resAudioId, float volume) {
		if (volume > MAX_VOLUME)
			volume = MAX_VOLUME;
		for (int i = 0; i < _resAudioId.length; i++) {
			if (_resAudioId[i] == resAudioId) {
				if (_players[i] == null)
					return;
				_players[i].setVolume(volume, volume);
				_volume[i] = volume;
			}
		}
	}

	public static float getVolume(int resAudioId) {
		for (int i = 0; i < _resAudioId.length; i++) {
			if (_resAudioId[i] == resAudioId) {
				return _volume[i];
			}
		}
		return 0;
	}

}
