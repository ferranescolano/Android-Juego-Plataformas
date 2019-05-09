package cat.flx.plataformes.game.characters;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameObject;

public class KeyPlant extends GameObject {


    public KeyPlant(Game game, int x, int y) {
        super(game, x, y);
        this.addTag("key");
        this.addSpriteSequence(0, 14);
    }

    @Override public void physics(long deltaTime) { }

    // The collision rect around the coin
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 12, y + 12);
    }
}
