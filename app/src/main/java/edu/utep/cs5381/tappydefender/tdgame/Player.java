package edu.utep.cs5381.tappydefender.tdgame;

import android.content.Context;
import android.graphics.BitmapFactory;

import edu.utep.cs5381.tappydefender.R;

/**
 * The Player Ship of the Tappy Defender game.
 */
public class Player extends Ship {
    private final int gravity = -12;;
    private boolean boosting;
    private final int boost = 2;
    private final int decelerate = 5;
    private final int minSpeed = 1;
    private final int maxSpeed = minSpeed*20;
    private int shields;

    public Player(Context context, int screenX, int screenY) {
        super();
        shields = 3;
        x = 50;
        y = 50;
        speed = minSpeed;
        bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ship);
        maxY = screenY - bitmap.getHeight();
        maxX = screenX - bitmap.getWidth();
        super.setCollisionBox();
    }

    /**
     * Updates the Player's Ship location on the screen depending on
     * boosting, gravity, and previous location.
     */
    @Override
    public void update() {
        //boosting influences speed
        if ( boosting ) speed+=boost;
        else speed-=decelerate;
        //speed beyond limits (min and max)
        if ( speed<minSpeed ) speed = minSpeed;
        if ( speed>maxSpeed ) speed = maxSpeed;
        //constantly falling
        y -= speed + gravity;
        //location beyond limits (min and max)
        if ( y<minY ) y = minY;
        if ( y>maxY ) y = maxY;
        super.update();
    }

    /**
     * Turns boosting on/off for the Player's Ship.
     * @param flag to turn boosting on (true)/off (false)
     */
    public void setBoosting(boolean flag) {
        boosting = flag;
    }

    /**
     * The Player lost a shield by colliding with an Enemy Ship.
     * @return whether there are no more shields left
     */
    public boolean loseShield() {
        return --shields==0;
    }

    public int shieldsRemaining() {
        return (shields<=0) ? 0 : shields;
    }

    public void addShield() {
        shields++;
    }
}
