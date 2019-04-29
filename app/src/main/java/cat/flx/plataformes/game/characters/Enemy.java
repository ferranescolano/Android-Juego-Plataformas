package cat.flx.plataformes.game.characters;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameObject;

// This class only serves for tagging as "enemy" a collection of GameObjects
abstract public class Enemy extends GameObject {

    // Constructor
    public Enemy(Game game, int x, int y) {
        super(game, x, y);
        this.addTag("enemy");
    }



}
