package cat.flx.plataformes.characters;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import cat.flx.plataformes.GameEngine;

abstract public class Character {
    GameEngine gameEngine;
    private Paint paint;
    int x, y, state, sprite;
    Rect collisionRect;

    public int getX() { return x; }
    public int getY() { return y; }
    public int getState() { return state; }

    public Character(GameEngine gameEngine, int x, int y) {
        this.gameEngine = gameEngine;
        this.x = x;
        this.y = y;
        this.state = 0;
        this.sprite = 0;
        this.paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.YELLOW);
        this.collisionRect = new Rect();
    }

    int[][] getAnimations() { return null; }

    public void physics(int delta) {
        this.updatePhysics(delta);
        updateCollisionRect();
    }

    Rect getCollisionRect() { return collisionRect; }

    public void draw(Canvas canvas) {
        try {
            int[] animation = getAnimations()[state];
            int bitmap = animation[sprite];
            canvas.drawBitmap(gameEngine.getBitmap(bitmap), x, y, null);
            sprite++;
            sprite %= animation.length;
            if (collisionRect != null) canvas.drawRect(collisionRect, paint);
        }
        catch (Exception ignored) { }
    }

    abstract void updatePhysics(int delta);
    abstract void updateCollisionRect();
}