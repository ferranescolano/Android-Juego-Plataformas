package cat.flx.plataformes.engine;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

// Basic Game Activity with Game Engine loading included by design
public abstract class GameEngineActivity extends AppCompatActivity
        implements ViewTreeObserver.OnGlobalLayoutListener {

    protected GameEngine gameEngine;    // Game Engine reference

    // On create the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FULL SCREEN GAME, NO ACTION BAR (theme also must be some "no-actionbar")
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Volume controls to control the music and effects volume for the game
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Create a Game Engine instance
        gameEngine = new GameEngine(this);
        // Wait for the layout is completed
        ViewTreeObserver vto = gameEngine.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(this);
        // Activate the view
        setContentView(gameEngine);
    }

    // Ignore orientation changes (related to manifest.xml settings)
    @Override public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // When the layout has been finished
    @SuppressWarnings("deprecation")
    @Override public void onGlobalLayout() {
        // Remove listener... (two methods based on API version)
        ViewTreeObserver vto = gameEngine.getViewTreeObserver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            vto.removeOnGlobalLayoutListener(this);
        }
        else {
            vto.removeGlobalOnLayoutListener(this);
        }
        // Delegate to real implementation here
        this.onActivityLoaded();
        // Start and resume (needed on activity load)
        gameEngine.start();
        gameEngine.resume();
    }

    // Delegate methods to GameEngine
    @Override public void onStart() {
        super.onStart();
        gameEngine.start();
    }
    @Override public void onResume() {
        super.onResume();
        gameEngine.resume();
    }
    @Override public void onPause() {
        super.onPause();
        gameEngine.pause();
    }
    @Override public void onDestroy() {
        super.onDestroy();
        gameEngine.stop();
    }

    // Needed for the keyboard input from the computer when run in emulator
    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        return (gameEngine == null) || gameEngine.dispatchKeyEvent(event);
    }

    // The concrete Activity will have to implement here the loading of the game
    // A very general game activity will have to override only this method
    // The purpose will be to instance a Game object and assign common settings to the engine
    public abstract void onActivityLoaded();

}