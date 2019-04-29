package cat.flx.plataformes.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.SparseIntArray;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// A TiledScene is an extension of Scene adding tile drawing, loading from file and camera follow
@SuppressWarnings({"unused", "SameParameterValue"})
abstract public class TiledScene extends Scene {
    // Attributes
    private String scene[];                         // The scene as an array of String rows
    private SparseIntArray CHARS;                   // The list of chars used in the scene
    private String GROUND, WALLS;                   // The chars for grounds and walls
    private int WATER_LEVEL;                        // Water level
    private int SKY, WATER_SKY, WATER;              // The bitmaps for the background
    private int tileSize;                           // Size of the tiles (both width and height)
    private int screenWidth, screenHeight;          // Size of the screen (real pixels)
    private int scaledWidth, scaledHeight;          // Scaled screen (translated pixels)
    private int sceneWidth, sceneHeight;            // Size of the scene (in tiles)
    private int offsetX, offsetY;                   // Current screen offset
    private float scale;                            // Current applied scale on canvas
    private GameObject camera;                      // What game object is assigned to camera

    // Constructor
    public TiledScene(Game game) {
        super(game);
        CHARS = new SparseIntArray();
        GROUND = "";
        WALLS = "";
        WATER_LEVEL = 999;                          // Water level defaults very far bottom
        tileSize = 16;                              // Tiles of 16 x 16 pixels
        scaledHeight = 16 * tileSize;               // Height defaults 16rows x 16px/row
        offsetX = offsetY = 0;                      // Screen offset to top-left of scene
        scale = 1.0f;                               // It will be overridden when drawn
    }

    // Camera getter & setter
    public GameObject getCamera() { return camera; }
    protected void setCamera(GameObject camera) { this.camera = camera; }

    // Useful getters
    protected int getScreenWidth() { return screenWidth; }
    protected int getScreenHeight() { return screenHeight; }
    protected int getScaledWidth() { return scaledWidth; }
    protected int getScaledHeight() { return scaledHeight; }
    public int getSceneWidth() { return sceneWidth; }
    public int getSceneHeight() { return sceneHeight; }
    public int getTileSize() { return tileSize; }
    protected float getScale() { return scale; }
    public int getWaterLevel() { return WATER_LEVEL; }

    // Change the desired view scale based on the scaled height
    protected void setScaledHeight(int scaledHeight) { this.scaledHeight = scaledHeight; }

    // Computed full scene width and height in scaled pixels
    public int getSceneFullWidth() { return sceneWidth * tileSize; }
    public int getSceneFullHeight() { return sceneHeight * tileSize; }

    // Returns true if the tile in (r,c) is a ground tile
    public boolean isGround(int r, int c) {
        if (r < 0) return false;
        if (r >= sceneHeight) return false;
        if (c < 0) return false;
        if (c >= sceneWidth) return false;
        char sc = scene[r].charAt(c);
        return (GROUND.indexOf(sc) != -1);
    }

    // Returns true if the tile in (r,c) is a wall tile
    public boolean isWall(int r, int c) {
        if (r < 0) return false;
        if (r >= sceneHeight) return false;
        if (c < 0) return false;
        if (c >= sceneWidth) return false;
        char sc = scene[r].charAt(c);
        return (WALLS.indexOf(sc) != -1);
    }

    // Loads a TXT scene definition file
    protected void loadFromFile(int resource) {
        GameEngine gameEngine = game.getGameEngine();
        InputStream res = gameEngine.getResources().openRawResource(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(res));
        List<String> lines = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                line = line.trim();
                if (!line.contains("=")) continue;                  // NO VALID LINE
                if (line.startsWith("=")) continue;                 // COMMENT
                String[] parts = line.split("=", 2);
                String cmd = parts[0].trim();
                String args = parts[1].trim();
                // If it's a Scene line, direct assignment to lines
                if (cmd.equals("SCENE")) {
                    lines.add(args);
                }
                else {
                    // Delegate to the parser (possibly overridden)
                    GameObject gameObject = this.parseLine(cmd, args);
                    if (gameObject != null) this.add(gameObject);
                }
            }
            reader.close();
            // Store and compute sizes
            scene = lines.toArray(new String[0]);
            sceneHeight = scene.length;
            sceneWidth = scene[0].length();
        }
        catch (IOException e) {
            String message = "Error loading scene:" +  e.getMessage();
            Toast.makeText(gameEngine.getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    // Parses a line from a TXT scene definition file
    protected GameObject parseLine(String cmd, String args) {
        // Chars list
        if (cmd.equals("CHARS")) {
            String[] parts2 = args.split(" ");
            for (String def : parts2) {
                String[] item = def.split("=");
                if (item.length != 2) return null;
                char c = item[0].trim().charAt(0);
                int idx = Integer.parseInt(item[1].trim());
                CHARS.put(c, idx);
            }
            return null;
        }
        // Ground chars list
        if (cmd.equals("GROUND")) {
            GROUND = args;
            return null;
        }
        // Walls chars list
        if (cmd.equals("WALLS")) {
            WALLS = args;
            return null;
        }
        // Water level definition
        if (cmd.equals("WATER")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 4) return null;
            WATER_LEVEL = Integer.parseInt(parts2[0].trim());
            SKY = Integer.parseInt(parts2[1].trim());
            WATER_SKY = Integer.parseInt(parts2[2].trim());
            WATER = Integer.parseInt(parts2[3].trim());
            return null;
        }
        // Unknown / unrecognized
        return null;
    }

    // Add the camera follow to the base physics cycle
    @Override
    public void physics(long deltaTime) {
        // Super method: update physics & contact detections
        super.physics(deltaTime);

        // Follow camera: Update screen offsets to always have a GameObject visible
        if (scaledWidth * scaledHeight == 0) return;
        if (camera == null) return;
        int x = camera.getX();
        int y = camera.getY();
        // OFFSET X (100 scaled-pixels margin)
        offsetX = Math.max(offsetX, x - scaledWidth + 124);     // 100 + Bonk Width (24)
        offsetX = Math.min(offsetX, sceneWidth * tileSize - scaledWidth - 1);
        offsetX = Math.min(offsetX, x - 100);
        offsetX = Math.max(offsetX, 0);
        // OFFSET Y (50 scaled-pixels margin)
        offsetY = Math.max(offsetY, y - scaledHeight + 82);     // 50 + Bonk Height (32)
        offsetY = Math.min(offsetY, sceneHeight * tileSize - scaledHeight - 1);
        offsetY = Math.min(offsetY, y - 50);
        offsetY = Math.max(offsetY, 0);
    }

    // Draw the tiled scene before drawing the game objects
    @Override
    public void draw(Canvas canvas) {
        // Refresh scale factor if screen has changed sizes
        if (canvas.getWidth() * canvas.getHeight() != screenWidth * screenHeight) {
            screenWidth = canvas.getWidth();
            screenHeight = canvas.getHeight();
            if (screenWidth * screenHeight == 0) return; // 0 px on screen (not fully loaded)
            // New Scaling factor based on height
            scale = (float) screenHeight / scaledHeight;
            scaledWidth = (int) (screenWidth / scale);
        }
        // --- FIRST DRAW ROUND (scaled)
        canvas.save();
        // Apply the scale
        canvas.scale(scale, scale);
        // Apply the camera offset
        canvas.translate(-offsetX, -offsetY);
        // Background tiles
        // Compute which tiles will be drawn
        int l = Math.max(0, offsetX / 16);
        int r = Math.min(scene[0].length(), offsetX / 16 + screenWidth / 16 + 2);
        int t = Math.max(0, offsetY / 16);
        int b = Math.min(scene.length, offsetY / 16 + screenHeight / 16 + 2);
        // Iterate over the visible rows
        for(int y = t; y < b; y++) {
            // Compute the background index (sky / water)
            int bgIdx = SKY;
            if (y == WATER_LEVEL) bgIdx = WATER_SKY;
            else if (y > WATER_LEVEL) bgIdx = WATER;
            Bitmap bgBitmap = game.getBitmap(bgIdx);
            // Draw all the visible tiles in the current row
            for(int x = l; x < r; x++) {
                // Draw the background tile
                canvas.drawBitmap(bgBitmap, x * 16, y * 16, null);
                // Compute the bitmap index for the current tile
                char c = scene[y].charAt(x);
                int index = CHARS.get(c);
                if (index == SKY) continue;
                Bitmap bitmap = game.getBitmap(index);
                canvas.drawBitmap(bitmap, x * 16, y * 16, null);
            }
        }

        // Super method: draw all game objects onscreen
        super.draw(canvas);

        // --- SECOND DRAW ROUND (no-scaled)
        canvas.restore();
        // Debugging information on screen
        if (getGameEngine().getDebugMode()) {
            String text = "OX=" + offsetX + " OY=" + offsetY;
            canvas.drawText(text, 0, 10, paintDebug);
        }
    }


}
