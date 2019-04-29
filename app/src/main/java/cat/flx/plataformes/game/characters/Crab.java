package cat.flx.plataformes.game.characters;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.SpriteSequence;

// An crab-like enemy
public class Crab extends Enemy {
    // Crab specific attributes
    private int x0, x1, incX;

    // Constructor
    public Crab(Game game, int x0, int x1, int y) {
        super(game, x0, y - 5);
        this.x0 = x0;
        this.x1 = x1;
        this.incX = 1;
        this.addTag("crab");
        this.addSpriteSequence(0, 10);
    }

    // The crab moves horizontally between x0 and x1
    @Override public void physics(long deltaTime) {
        this.x += incX;
        if (x <= x0) incX = 1;
        if (x >= x1) incX = -1;
    }

    // The collision rect around the crab will consider the pincers' position
    @Override public void updateCollisionRect() {
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        int currentSpriteIndex = spriteSequence.getCurrentSpriteIndex();
        int top = y + 8 - ((currentSpriteIndex < 6) ? 8 : 0);
        int bottom = y + 22 + ((currentSpriteIndex >= 6) ? 8 : 0);
        collisionRect.set(x, top, x + 32, bottom);

    }

}