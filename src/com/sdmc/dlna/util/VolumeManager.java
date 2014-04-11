package com.sdmc.dlna.util;

import android.content.Context;
import android.media.AudioManager;

/*
 * “Ù¡øπ‹¿Ì∆˜
 */
public class VolumeManager {

	private static AudioManager mAudioManager;

	public VolumeManager(Context context) {
		mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
	}

	public static void setVolume(int volume) {
		if (mAudioManager != null) {
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
					AudioManager.FLAG_SHOW_UI);
		}
	}

	public static void volumeUp() {
		if (mAudioManager != null) {
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
		}
	}

	public static void volumeDown() {
		if (mAudioManager != null) {
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
		}
	}
}
