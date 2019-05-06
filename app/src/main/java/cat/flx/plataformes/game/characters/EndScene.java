package cat.flx.plataformes.game.characters;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameObject;

public class EndScene extends GameObject {


    public EndScene(Game game, int x, int y) {
        super(game, x, y);
        this.addTag("endObject");
        this.addSpriteSequence(0, 12);
    }

    @Override public void physics(long deltaTime) { }

    // The collision rect around the coin
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 12, y + 12);
    }

}
