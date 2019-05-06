package cat.flx.plataformes.game.characters;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameObject;

public class Teleport extends GameObject {


    public Teleport(Game game, int x, int y) {
        super(game, x, y);

        this.addTag("tp");
        this.addSpriteSequence(0, 13);
    }

    @Override public void physics(long deltaTime) { }

    // The collision rect around the coin
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 12, y + 12);
    }

}
