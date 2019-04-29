package cat.flx.plataformes.engine;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

@SuppressWarnings({"unused", "SameParameterValue"})
public class Audio {

    private Context context;
    private MediaPlayer mediaPlayer;    // media player for background music
    private SoundPool soundPool;        // sound pool for sound effects
    private int[] fx;                   // sound effects holder

    // Constructor
    @SuppressWarnings("deprecation")
    Audio(Context context) {
        this.context = context;
        // Prepping the sound pool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(10).build();
        }
        else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }
    }

    // Loads a song for background playing from a resource
    public void loadMusic(int resource) {           // R.raw.music
        // Prepping the media player
        mediaPlayer = MediaPlayer.create(context, resource);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.25f, 0.25f);
    }

    // Loads the fx resources from an array of resources
    public void loadSoundFX(int[] resources) {      // { R.raw.coin, R.raw.die, R.raw.pause }
        fx = new int[resources.length];
        for(int i = 0; i < resources.length; i++) {
            fx[i] = soundPool.load(context, resources[i], 1);
        }
    }

    // Start background music playing
    public void startMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    // Stop background music playing
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    // Play a sound FX by index
    public void playSoundFX(int n) {
        if (fx == null) return;
        if (n >= fx.length) return;
        soundPool.play(fx[n], 1, 1, 1, 0, 1);
    }

}