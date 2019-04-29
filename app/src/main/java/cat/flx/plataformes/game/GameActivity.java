package cat.flx.plataformes.game;

import cat.flx.plataformes.engine.GameEngineActivity;

// The game activity in the application
public class GameActivity extends GameEngineActivity {

    // We only need to override the abstract method to assign the game to the game engine
    @Override
    public void onActivityLoaded() {
        // Sets the game engine general timing parameters
        gameEngine.setUpdatesPerSecond(15);
        gameEngine.setUpdatesToRedraw(2);
        // Sets the debug mode
        gameEngine.setDebugMode(true);
        // Attach a game instance
        new BonkGame(gameEngine);

    }
}