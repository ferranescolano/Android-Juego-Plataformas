package cat.flx.plataformes;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    GameEngine gameEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FULL SCREEN GAME, NO ACTION BAR (theme also must be some "no-actionbar")
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Volume controls to control the music and effects volume for the game
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // GameView as the content view for the application
        GameView gameView = new GameView(this);
        setContentView(gameView);

        // Create the engine
        gameEngine = new GameEngine(this, gameView);
        gameEngine.start();
    }

    // Delegate methods to GameEngine

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

    // For keyboard input
    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        return (gameEngine == null) || gameEngine.onKeyEvent(event);
    }

}