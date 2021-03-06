package cat.flx.plataformes.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.Locale;

import cat.flx.plataformes.R;
import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameEngine;
import cat.flx.plataformes.engine.GameObject;
import cat.flx.plataformes.engine.OnContactListener;
import cat.flx.plataformes.engine.TiledScene;
import cat.flx.plataformes.engine.Touch;
import cat.flx.plataformes.game.characters.Bonk;
import cat.flx.plataformes.game.characters.Booster;
import cat.flx.plataformes.game.characters.Coin;
import cat.flx.plataformes.game.characters.Crab;
import cat.flx.plataformes.game.characters.EndScene;
import cat.flx.plataformes.game.characters.KeyPlant;
import cat.flx.plataformes.game.characters.PreviousScene;
import cat.flx.plataformes.game.characters.Teleport;

import static cat.flx.plataformes.game.Scene01.totalScore;
import static cat.flx.plataformes.game.characters.Bonk.STATE_STANDING_FRONT;

public class Scene03 extends TiledScene implements OnContactListener {

    private Paint paintKeySymbol, paintKeyBackground, paintScore,
            paintButton, paintPauseText, paintRetryButton, paintRetryText,
            paintExitButton, paintExitText, paintBigBackgroundSquare, paintLivesBg,
            paintLivesScore, paintSpendMoneyBG, paintSpendMoneyText, paintNoSpendMoneyBG, paintNoSpendMoneyText, paintPayString1, paintPayString2;
    int tpCount = 0;
    private Bonk bonk;
    boolean isPaused = false;
    boolean keyPlantTaken = false;
    int CoinValue = 10;
    boolean tpRepaired = false;
    boolean showTpMessage = false;

    public Scene03(Game game) {
        super(game);
        GameEngine gameEngine = game.getGameEngine();
        gameEngine.loadBitmapSet(R.raw.sprites, R.raw.sprites_info, R.raw.sprites_seq);

        // Create the main character (player)
        bonk = new Bonk(game, 20, 170);

        this.add(bonk);
        // Set the follow camera to the player
        this.setCamera(bonk);

        // The screen will hold 16 rows of tiles (16px height each)
        this.setScaledHeight(16 * 16);
        // Pre-loading of sound effects
        game.getAudio().loadSoundFX(new int[]{ R.raw.coin, R.raw.die, R.raw.pause, R.raw.tpsound } );
        // Load the scene tiles from resource
        this.loadFromFile(R.raw.scene2);
        // Add contact listeners by tag names
        this.addContactListener("bonk", "enemy", this);
        this.addContactListener("bonk", "coin", this);
        this.addContactListener("bonk", "booster", this );
        this.addContactListener("bonk", "endObject", this);
        this.addContactListener("bonk", "tp", this);
        this.addContactListener("bonk", "key", this);
        this.addContactListener("bonk", "previousScene", this);
        bonk.setScore(totalScore);
        // Prepare the painters for drawing
        paintKeyBackground = new Paint();
        paintButton = new Paint();
        paintRetryButton = new Paint();
        paintExitButton = new Paint();
        paintRetryText = new Paint();
        paintExitText = new Paint();
        paintLivesBg = new Paint();
        paintLivesScore = new Paint();
        paintBigBackgroundSquare = new Paint();
        paintSpendMoneyBG = new Paint();
        paintSpendMoneyText = new Paint();
        paintNoSpendMoneyBG = new Paint();
        paintNoSpendMoneyText = new Paint();
        paintPayString1 = new Paint();
        paintPayString2 = new Paint();
        paintKeyBackground.setColor(Color.argb(20, 0, 0, 0));
        paintKeySymbol = new Paint();
        paintPauseText = new Paint();
        paintLivesBg.setColor(Color.rgb(255, 48, 11));
        paintLivesScore.setTextSize(6);
        paintLivesScore.setColor(Color.WHITE);
        paintBigBackgroundSquare.setColor(Color.argb(200, 254, 255, 255));
        paintSpendMoneyBG.setColor(Color.argb(20, 67,74, 76));
        paintNoSpendMoneyBG.setColor(Color.argb(20, 67, 74,76));
        paintNoSpendMoneyText.setColor(Color.RED);
        paintNoSpendMoneyText.setTextSize(6);
        paintSpendMoneyText.setColor(Color.RED);
        paintSpendMoneyText.setTextSize(6);
        paintKeySymbol.setColor(Color.GRAY);
        paintKeySymbol.setTextSize(10);
        paintPayString1.setColor(Color.WHITE);
        paintPayString1.setTextSize(6);
        paintPayString2.setColor(Color.WHITE);
        paintPayString2.setTextSize(5);
        paintScore = new Paint(paintKeySymbol);
        Typeface typeface = ResourcesCompat.getFont(this.getContext(), R.font.dseg);
        paintScore.setTypeface(typeface);
        paintScore.setColor(Color.RED);
        paintPauseText.setColor(Color.WHITE);
        paintPauseText.setTextSize(6);
        paintExitButton.setColor(Color.rgb(246,51,255));
        //paintExitButton.setColor(Color.argb(100, 246,51,255));
        // paintRetryButton.setColor(Color.argb(100, 0,0,0));
        paintRetryButton.setColor(Color.rgb( 1, 189 , 254));
        paintRetryText.setColor(Color.WHITE);
        paintRetryText.setTextSize(6);

        paintExitText.setColor(Color.WHITE);
        paintExitText.setTextSize(6);

        paintButton.setTypeface(typeface);
        paintButton.setColor(Color.rgb(255, 179, 15));
    }

    @Override
    public void onContact(String tag1, GameObject object1, String tag2, GameObject object2) {


        Log.d("flx", "Contact between a " + tag1 + " and " + tag2);
        Log.d("flx", "Contact between a " + tag1 + " and " + tag2);
        // Contact between Bonk and a coin
        if (tag2.equals("coin")) {
            this.getGame().getAudio().playSoundFX(0);
            object2.removeFromScene();
            bonk.addScore(CoinValue);
        }
        // Contact between Bonk and an enemy
        else if (tag2.equals("enemy")) {
            this.getGame().getAudio().playSoundFX(1);
            //object2.removeFromScene();
            bonk.die();
            bonk.reset(0, 160);

        }
        else if(tag2.equals("booster")){
            // bonk.setScore(bonk.getScore() + 1);
            object2.removeFromScene();
            bonk.addScore(40);
        }
        else if(tag2.equals("endObject")){
            object2.removeFromScene();


        }
        else if(tag2.equals("tp")){
            showTpMessage = true;


            if(tpRepaired) {
                showTpMessage = false;
                switch (tpCount) {
                    case 0:
                        bonk.reset(480, 70);
                        tpCount = 1;

                        break;

                    case 1:
                        bonk.reset(470, 160);
                        tpCount = 0;
                }
                this.getGame().getAudio().playSoundFX(3);
            }else{

            }
        }
        else if(tag2.equals("key")){
            keyPlantTaken = true;
            object2.removeFromScene();

        }
        else if(tag2.equals("previousScene")){

            object2.removeFromScene();

            game.loadScene(new Scene02(game));

        }

    }

    @Override
    protected GameObject parseLine(String cmd, String args) {
        // Lines beginning with "COIN"
        if (cmd.equals("COIN")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int coinX = Integer.parseInt(parts2[0].trim()) * 16;
            int coinY = Integer.parseInt(parts2[1].trim()) * 16;
            return new Coin(game, coinX, coinY);
        }
        // Lines beginning with "CRAB"
        if (cmd.equals("CRAB")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 3) return null;
            int crabX0 = Integer.parseInt(parts2[0].trim()) * 16;
            int crabX1 = Integer.parseInt(parts2[1].trim()) * 16;
            int crabY = Integer.parseInt(parts2[2].trim()) * 16;
            return new Crab(game, crabX0, crabX1, crabY);
        }

        if(cmd.equals("BOOSTER")){
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int boosterX = Integer.parseInt(parts2[0].trim()) * 16;
            int boosterY = Integer.parseInt(parts2[1].trim()) * 16;
            return new Booster(game, boosterX, boosterY);
        }
        if(cmd.equals("ENDOBJECT")){
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int boosterX = Integer.parseInt(parts2[0].trim()) * 16;
            int boosterY = Integer.parseInt(parts2[1].trim()) * 16;
            return new EndScene(game, boosterX, boosterY);
        }

        if(cmd.equals("TELEPORT")){
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int boosterX = Integer.parseInt(parts2[0].trim()) * 16;
            int boosterY = Integer.parseInt(parts2[1].trim()) * 16;
            return new Teleport(game, boosterX, boosterY);
        }
        if(cmd.equals("KEYPLANT")){
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int boosterX = Integer.parseInt(parts2[0].trim()) * 16;
            int boosterY = Integer.parseInt(parts2[1].trim()) * 16;
            return new KeyPlant(game, boosterX, boosterY);
        }
        if(cmd.equals("PREVIOUSSCENE")){
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int boosterX = Integer.parseInt(parts2[0].trim()) * 16;
            int boosterY = Integer.parseInt(parts2[1].trim()) * 16;
            return new PreviousScene(game, boosterX, boosterY);
        }


        // Test the common basic parser
        return super.parseLine(cmd, args);
    }

    @Override
    public void processInput() {

        Touch touch;
        while ((touch = game.getGameEngine().consumeTouch()) != null) {
            // Convert the X,Y to percentages of screen
            int x = touch.getX() * 100 / getScreenWidth();
            int y = touch.getY() * 100 / getScreenHeight();
            // Bottom-left corner (left-right)
            if ((y > 75) && (x < 40)) {
                if (!touch.isTouching()) bonk.stopLR();     // STOP
                else if (x < 20) bonk.goLeft();             // LEFT
                else bonk.goRight();                        // RIGHT
            }
            // Bottom-right corner (jump)
            else if ((y > 75) && (x > 80) ) {               // JUMP
                if (touch.isDown()) bonk.jump();
            }
            else if(((y < 60) && (y> 40)) && ((x < 45) && (x > 25))){
                if (!touch.isDown()) return;
                    bonk.setScore(bonk.getScore() - 60);
                    game.resume();
                    bonk.reset(470, 160);
                    showTpMessage = false;
                    tpRepaired = true;


            }
            else if(((y < 60) && (y> 40)) && ((x < 75) && (x > 54))) {
                if (!touch.isDown()) return;
                bonk.reset(470, 160);
                game.resume();
                showTpMessage = false;
                tpRepaired = false;
            }
            else if(((y < 60) && (y> 40)) && ((x < 75) && (x > 54))) {


                if (bonk.isDead) {
                    Toast toast = Toast.makeText(
                            game.getGameEngine().getContext(),
                            "Exit",
                            Toast.LENGTH_SHORT // Short Duration
                    );
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

            //When PAUSE button is clicked
            else if((y < 30) && (x > 80)){
                if (!touch.isDown()) return;


                //onPausePressed();
                //game.pause();
                if (game.isPaused()){
                    game.resume() ;
                    isPaused = false;
                    this.getGame().getAudio().playSoundFX(2);

                }
                else {

                    game.pause();
                    isPaused = true;
                    this.getGame().getAudio().playSoundFX(2);
                }




            }


        }

        // Process the computer's keyboard if the game is run inside an emulator
        int keycode;
        while ((keycode = game.getGameEngine().consumeKeyTouch()) != KeyEvent.KEYCODE_UNKNOWN) {
            switch (keycode) {
                case KeyEvent.KEYCODE_Z:                    // LEFT
                    bonk.goLeft();
                    break;
                case KeyEvent.KEYCODE_X:                    // RIGHT
                    bonk.goRight();
                    break;
                case KeyEvent.KEYCODE_M:                    // JUMP
                    bonk.jump();
                    break;
                case KeyEvent.KEYCODE_P:                    // TOGGLE PAUSE
                    if (game.isPaused()) game.resume();
                    else game.pause();
                    break;
            }
        }

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Translucent keyboard on top
        canvas.save();
        canvas.scale(getScale() * getScaledWidth() / 100, getScale() * getScaledHeight() / 100);
        canvas.drawRect(1, 76, 19, 99, paintKeyBackground);
        canvas.drawText("«", 8, 92, paintKeySymbol);
        canvas.drawRect(21, 76, 39, 99, paintKeyBackground);
        canvas.drawText("»", 28, 92, paintKeySymbol);
        canvas.drawRect(81, 76, 99, 99, paintKeyBackground);
        canvas.drawText("^", 88, 92, paintKeySymbol);
        canvas.drawRect(120, 4, 80, 13, paintLivesBg);
        canvas.drawRect(80, 12, 140, 21, paintButton);
        canvas.drawText("Lives: " + bonk.health, 81, 10, paintLivesScore);

        if(isPaused == true){

            canvas.drawText("RESUME", 81, 19, paintPauseText);
        }else{
            canvas.drawText("PAUSE", 81, 19, paintPauseText);
        }

        if(bonk.isDead() == true){
            canvas.drawRect( 100, 100, -40, -20, paintBigBackgroundSquare);

            canvas.drawRect(25, 50, 45, 60, paintRetryButton);
            canvas.drawRect( 55, 50, 75, 60, paintExitButton);
            canvas.drawText("Retry", 28, 57, paintRetryText);
            canvas.drawText("Exit", 60, 57, paintExitText);
        }

        if(showTpMessage == true){

            canvas.drawRect( 100, 100, -40, -20, paintBigBackgroundSquare);
            canvas.drawRect( 50, 50, 85, 60, paintNoSpendMoneyBG);
            canvas.drawRect( 16, 50, 39, 60, paintSpendMoneyBG);
            canvas.drawText("Pay", 23 , 57, paintSpendMoneyText);
            canvas.drawText("Don't Pay", 55, 57, paintSpendMoneyText);
            canvas.drawText("The Teleport has been destroyed.", 13, 25, paintPayString1);
            canvas.drawText("You need to pay 60 coins in order to repair it.", 2, 40, paintPayString2);
            game.pause();
        }

        canvas.restore();

        // Score on top-right corner
        canvas.scale(getScale(), getScale());
        paintScore.setTextSize(10);
        String score = String.format(Locale.getDefault(), "%06d", totalScore);
        canvas.drawText(score, getScaledWidth() - 50, 10, paintScore);

    }
}
