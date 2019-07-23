package edu.utep.cs5381.tappydefender.tdgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.Random;
import edu.utep.cs5381.tappydefender.R;

/**
 * The Enemy Ship of the Tappy Defender game.
 */
public class Enemy extends Ship {
    private static final Random rng = new Random();
    private Context context;
    private boolean hasMagic;

    public Enemy(Context c, int screenX, int screenY) {
        context = c;
        setBitmap(context);
        scaleBitmap(x);
        maxX = screenX;
        maxY = screenY - bitmap.getHeight();
        speed = rng.nextInt(10) + 10;
        x = screenX;
        y = rng.nextInt(maxY) - bitmap.getHeight();
        super.setCollisionBox();
        hasMagic = false;
    }

    private void setBitmap(Context c) {
        switch ( rng.nextInt(3) ) {
            case 0:
                bitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.enemy);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.enemy2);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.enemy3);
                break;
        }
    }

    private void scaleBitmap(int x) {
        float scale = x < 1000 ? 3 : (x < 1200 ? 2 : 1.5f);
        bitmap = Bitmap.createScaledBitmap(bitmap,(int) (bitmap.getWidth() / scale), (int) (bitmap.getHeight() / scale), false);
    }

    /**
     * Updates the Enemy's Ship location on the screen depending on
     * the previous location and the Player's Ship speed.
     * @param s the Player's current speed
     */
    @Override
    public void update(int s) {
        x -= s;
        x -= speed;
        if ( x<minX - bitmap.getWidth() ) {
            if ( !hasMagic ) { //don't randomize image
                setBitmap(context);
                scaleBitmap(x);
            }
            speed = rng.nextInt(10)+10;
            x = maxX;
            y = rng.nextInt(maxY) - bitmap.getHeight();
        }
        super.update();
    }

    //there can be only one
    public void hasMagic(Context c, int x) {
        bitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.enemy0);
        hasMagic = true;
        bitmap = Bitmap.createScaledBitmap(bitmap,(bitmap.getWidth() / 15),(bitmap.getHeight() / 15), false);
    }

    public boolean givesLife() {
        return hasMagic;
    }
}
