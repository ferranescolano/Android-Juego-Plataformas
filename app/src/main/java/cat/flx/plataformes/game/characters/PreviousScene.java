package cat.flx.plataformes.game.characters;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameObject;

public class PreviousScene extends GameObject {


    public PreviousScene(Game game, int x, int y) {
        super(game, x, y);

        this.addTag("previousScene");
        this.addSpriteSequence(0, 15);
        // SpriteSequence spriteSequence = getCurrentSpriteSequence();
        // spriteSequence.randomizeSprite();
    }

    // A coin doesn't move
    @Override public void physics(long deltaTime) { }

    // The collision rect around the coin
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 12, y + 12);
    }
    }

