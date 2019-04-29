package cat.flx.plataformes.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

// Main Game Engine class
@SuppressWarnings({"unused", "SameParameterValue"})
public class GameEngine extends View implements Runnable, SensorEventListener {
    // Useful attributes
    private Game game;
    private Audio audio;
    private BitmapSet bitmapSet;
    private SensorManager sensorManager;
    private float accelerationX, accelerationY, accelerationZ;    // Accelerometer data
    // GAME ENGINE RUNNABLE
    private Handler handler;
    private int updatesPerSecond = 30;  // desired physic's updates/second
    private int updatesToRedraw  = 2;   // how many physics per redraw update
    private long lastTime;      // Holder for deltaTime calculation
    private int count = 0;      // Divider
    // DEBUG MODE
    private boolean debugMode = false;  // Sets the debug mode

    // Constructor
    public GameEngine(Context context) {
        super(context);
        handler = new Handler();
        audio = new Audio(context);
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    // Game getter & setter
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    // Audio getter
    public Audio getAudio() { return audio; }

    // BitmapSet related getter & setter
    public BitmapSet getBitmapSet() { return bitmapSet; }
    public void setBitmapSet(BitmapSet bitmapSet) { this.bitmapSet = bitmapSet; }

    // Retrieve size of screen
    int getScreenWidth() { return this.getMeasuredWidth(); }
    int getScreenHeight() { return this.getMeasuredHeight(); }

    // Timing settings
    public int getUpdatesPerSecond() { return updatesPerSecond; }
    public void setUpdatesPerSecond(int updatesPerSecond) { this.updatesPerSecond = updatesPerSecond; }
    public int getUpdatesToRedraw() { return updatesToRedraw; }
    public void setUpdatesToRedraw(int updatesToRedraw) { this.updatesToRedraw = updatesToRedraw; }

    // Acceleration getters
    public float getAccelerationX() { return accelerationX; }
    public float getAccelerationY() { return accelerationY; }
    public float getAccelerationZ() { return accelerationZ; }

    // Debug mode
    public void setDebugMode(boolean debugMode) { this.debugMode = debugMode; }
    boolean getDebugMode() { return debugMode; }

    // Loads a BitmapSet from resources
    public void loadBitmapSet(int resource, int resourceInfo, int resourceSeq) {
        BitmapSet bitmapSet = new BitmapSet(getContext(), resource, resourceInfo, resourceSeq );
        this.setBitmapSet(bitmapSet);
    }

    // This method will be called on first start
    public void start() {
        if (game != null) game.start();
    }

    // This method will be called on closing
    public void stop() {
        if (game != null) game.stop();
    }

    // This method will be called on activity resume
    public void resume() {
        lastTime = System.currentTimeMillis();
        handler.postDelayed(this, 0);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
        if (game != null) game.resume();
    }

    // This method will be called on activity pause
    public void pause() {
        handler.removeCallbacks(this);
        sensorManager.unregisterListener(this);
        if (game != null) game.pause();
    }

    // This method will be called periodically
    @Override public void run() {
        // post new update
        int period = 1000 / updatesPerSecond;
        handler.postDelayed(this, period);

        // do nothing unless everything is fully loaded
        if (game == null) return;

        // Time elapsed since last execution
        long currentTime = System.currentTimeMillis();
        long deltaTime = (currentTime - lastTime);
        lastTime = currentTime;

        // PROCESS USER INPUT ON EACH UPDATE
        game.processInput();

        // UPDATE PHYSICS EACH UPDATE
        game.physics(deltaTime);

        // REDRAW CANVAS EACH 1/UPDATES_TO_REDRAW TIMES THE PHYSICS UPDATE HAPPENS
        count = (count + 1) % updatesToRedraw;
        if (count == 0) this.invalidate();

        // CLEAR TOUCH & KEY QUEUES
        touches.clear();
        keyTouches.clear();
    }

    // Drawing cycle
    @Override public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.RED);
        if (game == null) return;
        game.draw(canvas);
    }

    // Touch actions will be queued here
    private List<Touch> touches = new ArrayList<>();
    public Touch consumeTouch() {
        if (touches.size() == 0) return null;
        Touch touch = touches.get(0);
        touches.remove(0);
        return touch;
    }

    // Touch event listener: capture and pre-processing
    @SuppressLint("ClickableViewAccessibility")     // accessibility is virtually impossible
    @Override public boolean onTouchEvent(MotionEvent motionEvent) {
        if (game == null) return true;
        int action = motionEvent.getActionMasked();
        boolean down = (action == MotionEvent.ACTION_DOWN) ||
                (action == MotionEvent.ACTION_POINTER_DOWN);
        boolean touching = (action != MotionEvent.ACTION_UP) &&
                (action != MotionEvent.ACTION_POINTER_UP) &&
                (action != MotionEvent.ACTION_CANCEL);
        int i = motionEvent.getActionIndex();
        int x = (int) motionEvent.getX(i);
        int y = (int) motionEvent.getY(i);
        touches.add(new Touch(x, y, action, down, touching));
        return true;
    }

    // Key events will be queued here
    private List<Integer> keyTouches = new ArrayList<>();
    public int consumeKeyTouch() {
        if (keyTouches.size() == 0) return KeyEvent.KEYCODE_UNKNOWN;
        int keycode = keyTouches.get(0);
        keyTouches.remove(0);
        return keycode;
    }

    // Key event listener: capture and pre-processing
    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN) return true;
        keyTouches.add(event.getKeyCode());
        return true;
    }

    // Acceleration listener: capture & pre-processing
    @Override public void onSensorChanged(SensorEvent sensorEvent) {
        this.accelerationX = sensorEvent.values[0];
        this.accelerationY = sensorEvent.values[1];
        this.accelerationZ = sensorEvent.values[2];
    }
    @Override public void onAccuracyChanged(Sensor sensor, int i) { }
}
