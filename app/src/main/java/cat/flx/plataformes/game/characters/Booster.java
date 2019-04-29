package cat.flx.plataformes.game.characters;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameObject;

public class Booster extends GameObject {


    public Booster(Game game, int x, int y) {
        super(game, x, y);
        this.addTag("booster");

    }
}
