package cat.flx.plataformes.characters;

import cat.flx.plataformes.GameEngine;
import cat.flx.plataformes.Input;
import cat.flx.plataformes.Scene;

public class Bonk extends Character {

    private static final int[][] ANIMATIONS = new int[][] {
            new int[] { 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12,
                        12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 13, 14 },   // 0 = STANDING
            new int[] { 6, 7, 8, 9 },                                       // 1 = WALKING LEFT
            new int[] { 0, 1, 2, 3 },                                       // 2 = WALKING RIGHT
    };
    @Override int[][] getAnimations() { return ANIMATIONS; }

    private int vy;
    private static final int MAX_VELOCITY = 4;
    private static final int JUMP_VELOCITY = -8;

    public Bonk(GameEngine gameEngine, int x, int y) {
        super(gameEngine, x, y);
        vy = 0;
    }

    @Override void updatePhysics(int delta) {
        int newState;

        // Analyze user input
        Input input = gameEngine.getInput();
        if (input.isLeft()) {
            x = Math.max(x - 2, 0);
            newState = 1;
        }
        else if (input.isRight()) {
            x = Math.min(x + 2, gameEngine.getScene().getWidth() - 24);
            newState = 2;
        }
        else {
            newState = 0;
        }
        if (input.isJump()) {
            vy = JUMP_VELOCITY;
            input.clearJump();
        }
        if (input.isKeyboard()) {
            input.stopLR();
            input.setKeyboard(false);
        }

        // Apply physics
        y += vy;
        vy++;
        if (vy > MAX_VELOCITY) vy = MAX_VELOCITY;

        // Check ground
        Scene scene = gameEngine.getScene();
        int top = y / 16;
        int bottom = (y + 31) / 16;
        int left = x / 16;
        int right = (x + 23) / 16;
        for (int c = left ; c <= right; c++) {
            if (scene.isWall(top, c)) y = (top + 1) * 16;
            if (scene.isGround(bottom, c)) y = bottom * 16 - 32;
        }
        top = y / 16;
        bottom = (y + 31) / 16;
        for (int r = top; r <= bottom; r++) {
            if (scene.isWall(r, left)) x = (left + 1) * 16;
            if (scene.isWall(r, right)) x = right * 16 - 24;
        }
        if (newState != state) {
            state = newState;
            sprite = 0;
        }
    }

    @Override void updateCollisionRect() {
        collisionRect.set(x + 2, y, x + 23, y + 32);
    }
}

