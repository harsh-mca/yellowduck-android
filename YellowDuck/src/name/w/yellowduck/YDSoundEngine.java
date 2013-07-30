/* 
 *
 * Copyright (C) 2013 The PlayTractor Team (support@playtractor.com)
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, see <http://www.gnu.org/licenses/>.
 */


package name.w.yellowduck;

import java.io.IOException;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;


public class YDSoundEngine extends Object{
	// effects are sounds that less than 5 seconds, better in 3 seconds
	java.util.Hashtable<String, Integer> effectsMap = new java.util.Hashtable<String, Integer>();
	java.util.Hashtable<String, Integer> streamsMap = new java.util.Hashtable<String, Integer>();
	
	// sounds are background sounds, usually longer than 5 seconds
	java.util.Hashtable<String, MediaPlayer> soundsMap = new java.util.Hashtable<String, MediaPlayer>();
	
	SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
	String lastSnd = null;
	
    static YDSoundEngine _sharedEngine = null;

    public static YDSoundEngine sharedEngine() {
        synchronized(YDSoundEngine.class) {
            if (_sharedEngine == null) {
                _sharedEngine = new YDSoundEngine();
            }
        }
        return _sharedEngine;
    }

    public static void purgeSharedEngine() {
        synchronized(YDSoundEngine.class) {
            _sharedEngine = null;
        }
    }
    		
	public int playEffect(AssetFileDescriptor afd, final String assetResource, final boolean loop) {
		Integer sndId = -1;
		sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
	            @Override
	            public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
	            	int streamId = sp.play(sampleId, 1.0f, 1.0f, 1, loop?-1:0, 1f);
	    			streamsMap.put(assetResource, streamId);
	            }
	        });		
		synchronized (effectsMap) {
			sndId = effectsMap.get(assetResource);
			if (sndId == null) {
				try {
					if (afd !=null) {
						sndId = sp.load(afd, 1);
						if (sndId >= 0)
							effectsMap.put(assetResource, sndId);
						afd.close();
					}
					else {
						sndId=-1;
					}
				}
				catch (java.io.IOException e) {
					sndId=-1;
				}
			}
			else {
            	int streamId = sp.play(sndId, 1.0f, 1.0f, 1, loop?-1:0, 1f);
    			streamsMap.put(assetResource, streamId);
			}
		}
		return sndId;
	}
	
	public void stopEffect(Context app, int sndId) {
		if (sndId >= 0) {
			for (String key : effectsMap.keySet()) {
				if (effectsMap.get(key) == sndId) {
					int streamId=streamsMap.get(key);
					sp.stop(streamId);
					break;
				}
			}
		}
	}
	
	
	public void playSound(AssetFileDescriptor afd, String assetResource, boolean loop) {
		if (lastSnd != null) {
			pauseSound();
		}
		
		MediaPlayer mp = null;
		synchronized(soundsMap) {
			mp = soundsMap.get(assetResource);
			if (mp == null) {
				try {
					if (afd !=null) {
						mp = new MediaPlayer();
						mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());		
					}
				}
				catch (java.io.IOException e) {
					mp=null;
				}
				// failed to create
				if(mp != null) {
					soundsMap.put(assetResource, mp);
					try {
						mp.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (mp!=null) {
			lastSnd = assetResource;
			mp.start();
	
			if (loop)
				mp.setLooping(true);
		}
	}
	
	public void pauseSound() {
		if (lastSnd == null)
			return;
		
		MediaPlayer mp = null;
		synchronized(soundsMap) {
			mp = soundsMap.get(lastSnd);
		}
		if (mp!=null)
			mp.pause();
	}
	
	public void resumeSound() {
		if (lastSnd == null)
			return;
		
		MediaPlayer mp = null;
		synchronized(soundsMap) {
			mp = soundsMap.get(lastSnd);
		}
		if (mp!=null)
			mp.start();
	}
	
	public void realesSound(int resId)
	{
		MediaPlayer mp = null;
		synchronized(soundsMap) {
			mp = soundsMap.get(resId);
			if (mp != null) {
				mp.release();
				soundsMap.remove(resId);
			}
		}
	}
	
	public void realesAllSounds() {
		java.util.Collection<MediaPlayer> values=soundsMap.values();
		for (MediaPlayer mp : values) {
			mp.release();
		}
		soundsMap.clear();
	}
	
	public void realesAllEffects() {
		sp.release();
	}

}
