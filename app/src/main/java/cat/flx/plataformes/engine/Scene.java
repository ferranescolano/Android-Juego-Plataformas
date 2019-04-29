package cat.flx.plataformes.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@SuppressWarnings({"unused", "SameParameterValue"})
abstract public class Scene extends ArrayList<GameObject> {
    // Attributes
    protected Game game;
    private HashMap<String, HashMap<String, OnContactListener>> contactListeners;   // Listeners
    Paint paintDebug;

    // Constructor
    public Scene(Game game) {
        super();
        this.game = game;
        // Empty contact listeners
        this.contactListeners = new HashMap<>();
        // Debug painter
        paintDebug = new Paint();
        paintDebug.setColor(Color.GRAY);
        paintDebug.setTextSize(10);
    }

    // Useful setters & getters
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
    public Audio getAudio() { return game.getAudio(); }
    public GameEngine getGameEngine() { return game.getGameEngine(); }
    public BitmapSet getBitmapSet() { return getGameEngine().getBitmapSet(); }
    public Context getContext() { return game.getGameEngine().getContext(); }

    // Clears the contact listeners
    protected void clearContactListeners() {
        this.contactListeners = new HashMap<>();
    }

    // Add a contact listener between game objects based on their tags
    protected void addContactListener(String tag1, String tag2, OnContactListener listener) {
        HashMap<String, OnContactListener> tag1Listeners;
        // If tag1 is not already present, create new HashMap for it
        if (!contactListeners.containsKey(tag1)) {
            contactListeners.put(tag1, new HashMap<String, OnContactListener>());
        }
        tag1Listeners = contactListeners.get(tag1);
        if (tag1Listeners == null) return;
        // Add the listener
        tag1Listeners.put(tag2, listener);
    }

    // Physics cycle: physics, collision rectangles and collision detection
    public void physics(long deltaTime) {
        // Update physics & collision rectangles of all game objects
        for(GameObject gameObject : this) {
            gameObject.physics(deltaTime);
            gameObject.updateCollisionRect();
        }
        // Test for contacts & collisions between objects:
        // Iterate over all game objects
        for (GameObject gameObject1 : this) {
            // And their tags
            for(String tag1 : gameObject1.getTags()) {
                Rect rect1 = gameObject1.getCollisionRect();
                if (rect1 == null) continue;
                // To retrieve the listeners between him and other game objects
                HashMap<String, OnContactListener> tag1Listeners = contactListeners.get(tag1);
                if (tag1Listeners == null) continue;
                // So we search for game objects
                for (GameObject gameObject2 : this) {
                    // And their tags
                    for(String tag2 : gameObject2.getTags()) {
                        // Searching for a matching pair tag1-tag2
                        OnContactListener listener = tag1Listeners.get(tag2);
                        if (listener == null) continue;
                        Rect rect2 = gameObject2.getCollisionRect();
                        if (rect2 == null) continue;
                        // Test the collision
                        if (!rect1.intersect(rect2)) continue;
                        // And call the listener if they intersect
                        listener.onContact(tag1, gameObject1, tag2, gameObject2);
                    }
                }
            }
        }
        // Safely remove all pending "marked for deletion" game objects
        Iterator<GameObject> iterator = this.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isMarkedForDeletion()) {
                iterator.remove();
            }
        }
    }

    // Draw cycle: all game objects are drawn here
    public void draw(Canvas canvas) {
        for (GameObject gameObject : this) {
            gameObject.draw(canvas);
        }
    }

    // User Input cycle: the user input will be analyzed here
    // This method must be overridden in real scene implementation
    abstract public void processInput();
}
