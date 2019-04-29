package cat.flx.plataformes.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "SameParameterValue"})
public class SpriteSequence {

    private BitmapSet bitmapSet;                // Reference to the BitmapSet
    private List<Integer> sprites;              // List of sprites in animation
    private int sprite, counter;                // Internal counters
    private int spriteDuration;                 // Keep the same sprite on screen

    // Normal constructor
    SpriteSequence(BitmapSet bitmapSet) {
        this.bitmapSet = bitmapSet;
        sprites = new ArrayList<>();
        this.spriteDuration = 1;
    }

    // Copy constructor
    SpriteSequence(SpriteSequence sequence) {
        this.bitmapSet = sequence.bitmapSet;
        this.spriteDuration = sequence.spriteDuration;
        this.sprite = sequence.sprite;
        this.counter = sequence.counter;
        this.sprites = new ArrayList<>(sequence.sprites);
    }

    // Adds a sprite to this sprite sequence
    public void addSprite(int sprite) {
        this.sprites.add(sprite);
    }

    // Adds an array of sprites to this sprite sequence
    public void addSprites(int[] sprites) {
        for(int sprite: sprites) {
            this.sprites.add(sprite);
        }
    }

    // Sets the sprite repetition count
    public void setSpriteDuration(int spriteDuration) {
        this.spriteDuration = spriteDuration;
    }

    // Reset the sequence
    public void reset() {
        this.sprite = 0;
    }

    // Randomize the current sprite inside the sprite sequence
    public void randomizeSprite() {
        sprite = (int)(Math.random() * sprites.size());
    }

    // Getter for the currently drawn sprite
    public int getCurrentSpriteIndex() { return sprite; }

    // Moves to the next sprite in this sprite sequence
    private void nextSprite() {
        counter++;
        if (counter % spriteDuration == 0) sprite++;
        sprite %= sprites.size();
    }

    // Draws the next sprite in this sprite sequence on screen
    public void drawSprite(Canvas canvas, float x, float y) {
        int bitmapIndex = sprites.get(sprite);
        Bitmap bitmap = bitmapSet.getBitmap(bitmapIndex);
        canvas.drawBitmap(bitmap, x, y, null);
        this.nextSprite();
    }
}
