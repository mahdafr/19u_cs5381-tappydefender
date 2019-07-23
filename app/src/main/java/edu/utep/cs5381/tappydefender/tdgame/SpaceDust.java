package edu.utep.cs5381.tappydefender.tdgame;

import java.util.Random;

/**
 * The specs of dust in the background of the Game.
 */
public class SpaceDust extends GameObject {
    private static final Random rng = new Random();

    public SpaceDust(int x, int y) {
        super();
        maxX = x; maxY = y;
        speed = rng.nextInt(10);
        x = rng.nextInt(maxX);
        y = rng.nextInt(maxY);
    }

    @Override
    public void update(int s) {
        x -=s;
        x-=speed;

        if ( x<0 ) {
            x = maxX;
            y = rng.nextInt(maxY);
            speed = rng.nextInt(15);
        }
    }
}
