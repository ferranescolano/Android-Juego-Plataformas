package cat.flx.plataformes.engine;

/**
 * A touch is an action done by the user on the screen.
 *
 * It stores the X,Y position as well as the action done (ACTION_UP, ACTION_DOWN, ...)
 * It has a "down" boolean to indicate the touching is a "begin touch". Useful for taps.
 * It has also a "touching" boolean to indicate a touching that is being held. useful for pads.
  */
@SuppressWarnings({"unused", "SameParameterValue"})
public class Touch {
    // Attributes
    private int x, y, action;
    private boolean down, touching;

    // Constructor
    Touch(int x, int y, int action, boolean down, boolean touching) {
        this.x = x;
        this.y = y;
        this.action = action;
        this.down = down;
        this.touching = touching;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getAction() { return action; }
    public boolean isDown() { return down; }
    public boolean isTouching() { return touching; }
}
