package cat.flx.plataformes.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

// This class holds the Bitmaps and animations used by the game and game objects
@SuppressWarnings({"unused", "SameParameterValue"})
public class BitmapSet {

    private SparseArray<Bitmap> bitmaps;                    // all the bitmaps
    private SparseArray<SpriteSequence> spriteSequences;    // all the sprite sequences

    // Constructor loads the resources and fills the two holders
    public BitmapSet(Context context, int resource, int resourceInfo, int resourceSeq) {
        this.loadSprites(context, resource, resourceInfo);
        this.loadSequences(context, resourceSeq);
    }

    // Retrieve a Bitmap by index
    Bitmap getBitmap(int index) { return bitmaps.get(index); }

    // Gets a clone of the specified sprite sequence by index
    public SpriteSequence getSpriteSequence(int index) {
        SpriteSequence sequence = spriteSequences.get(index);
        return new SpriteSequence(sequence);
    }

    // Method for loading the sprites from sprite PNG and sprite info TXT
    private void loadSprites(Context context, int resource, int resourceInfo) {
        // Load the sprites and tiles from res/raw/spritestes.png
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        Bitmap bitmapsBMP = BitmapFactory.decodeResource(context.getResources(), resource, opts);
        // Prepping the transformations for image rotation
        Matrix rot1 = new Matrix();     // no-rotation
        Matrix rot2 = new Matrix();
        rot2.setScale(-1, 1);           // flip horizontal
        // Load the sprite's and tile's definition file
        bitmaps = new SparseArray<>();
        InputStream in = context.getResources().openRawResource(resourceInfo);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String parts[] = line.split(":");
                if (parts.length != 7) continue;    // empty lines are skipped
                int id = Integer.parseInt(parts[0]);
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int w = Integer.parseInt(parts[3]);
                int h = Integer.parseInt(parts[4]);
                int r = Integer.parseInt(parts[5]);
                Matrix m = (r == 1) ? rot2 : rot1;
                // Get the portion of the original Bitmap and store it in the array
                Bitmap bitmap = Bitmap.createBitmap(bitmapsBMP, x, y, w, h, m, true);
                bitmaps.put(id, bitmap);
            }
            reader.close();
        }
        catch (Exception ignored) { }
        finally {
            try { reader.close(); } catch (Exception ignored) { }
        }

        // Release the resources of the original Bitmap. It's needed no more in the app
        bitmapsBMP.recycle();
    }

    // Loads the sprite sequences from TXT file
    private void loadSequences(Context context, int resourceSeq) {
        // Load the sprite sequences' definition file
        spriteSequences = new SparseArray<>();
        InputStream in = context.getResources().openRawResource(resourceSeq);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            // Search the maximum ID from the file. Needed to know the size of the array
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains("//")) {      // Remove comments
                    String firstSplit[] = line.split("//");
                    line = firstSplit[0].trim();
                }
                String secondSplit[] = line.split(":"); // id:def from line
                if (secondSplit.length != 2) continue;
                int id = Integer.parseInt(secondSplit[0].trim());
                count = (id > count) ? id : count;
            }
            // Reset the stream to re-read the file
            in.reset();
            while ((line = reader.readLine()) != null) {
                if (line.contains("//")) {      // Remove comments
                    String firstSplit[] = line.split("//");
                    line = firstSplit[0].trim();
                }
                String secondSplit[] = line.split(":"); // id:def from line
                if (secondSplit.length != 2) continue;
                int id = Integer.parseInt(secondSplit[0].trim());
                String def = secondSplit[1].trim();
                String thirdSplit[] = def.split(",");
                SpriteSequence spriteSequence = new SpriteSequence(this);
                spriteSequences.put(id, spriteSequence);
                for(String spec : thirdSplit) {
                    if (spec.contains("x")) {
                        String fourthSplit[] = spec.split("x");
                        if (fourthSplit.length != 2) continue;
                        int bitmapIndex = Integer.parseInt(fourthSplit[0].trim());
                        int number = Integer.parseInt(fourthSplit[1].trim());
                        for(int i = 0; i < number; i++) {
                            spriteSequence.addSprite(bitmapIndex);
                        }
                    }
                    else {
                        int bitmapIndex = Integer.parseInt(spec.trim());
                        spriteSequence.addSprite(bitmapIndex);
                    }
                }
            }
            reader.close();
        }
        catch (Exception ignored) { }
        finally {
            try { reader.close(); } catch (Exception ignored) { }
        }
    }
}

