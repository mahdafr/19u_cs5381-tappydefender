package edu.utep.cs5381.tappydefender.ui.tdmanager;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import edu.utep.cs5381.tappydefender.R;

import static edu.utep.cs5381.tappydefender.ui.tdmanager.SoundManager.Sound.START;

class SoundManager {
    public enum Sound {
        START(R.raw.start),
        BUMP(R.raw.bump),
        DESTROYED(R.raw.destroyed),
        WIN(R.raw.win);

        public final int resourceId;
        private int soundId;

        Sound(int resourceId) {
            this.resourceId = resourceId;
        }
    }

    private boolean loaded = false;
    private SoundPool soundPool;

    public SoundManager(Context context) {
        soundPool = new SoundPool.Builder().setMaxStreams(Sound.values().length).build();
        for (Sound sound: Sound.values())
            sound.soundId = soundPool.load(context, sound.resourceId, 1);

        soundPool.setOnLoadCompleteListener((soundPool, i, i1) -> {
            if ( i==START.soundId )
                play(START);
        });
    }

    public void play(Sound sound) {
        soundPool.play(sound.soundId, 1, 1, 0, 0, 1);
    }
}
