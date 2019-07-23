package edu.utep.cs5381.tappydefender.tdgame;

import android.content.Context;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Controller for the TD game play.
 */
public class TDGame {
    private final java.util.Random rng = new java.util.Random();
    private Context context;
    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;
    private long pauseStarted, pauseElapsed;
    private boolean gameEnded;
    private boolean playing;

    private Player player;
    private List<Enemy> enemy = new CopyOnWriteArrayList<>();
    private List<SpaceDust> dust = new CopyOnWriteArrayList<>();

    public TDGame(Context c, int x, int y) {
        context = c;
        reset(x,y);
    }

    public void reset(int x, int y) {
        //reset game objects in the scene
        player = new Player(context,x,y);
        enemy.clear();
        dust.clear();
        int i = 0;
        if ( x>1000 ) i--;
        if ( x>1200 ) i--;
        for ( ; i<rng.nextInt(5) ; i++ )
            enemy.add(new Enemy(context, x, y));
        Enemy magic = new Enemy(context,x,y);
        magic.hasMagic(context,x);
        enemy.add(magic);
        for ( i=0 ; i<rng.nextInt(55) ; i++ )
            dust.add(new SpaceDust(x,y));
        //reset trackers
        distanceRemaining = 10000; // 10 km
        timeTaken = 0;
        pauseStarted = 0;
        pauseElapsed = 0;
        timeStarted = System.currentTimeMillis();
        gameEnded = false;
    }

    /* **************** Methods to control mechanics **************** */
    public void pause() {
        pauseStarted = System.currentTimeMillis();
    }
    public void resume() {
        pauseElapsed += System.currentTimeMillis() - pauseStarted;
    }
    public void setFastestTime(long fastest) {
        fastestTime = fastest;
    }
    public long fastestTime() {
        return fastestTime;
    }
    public long elapsedTime() {
        return timeTaken;
    }
    public void setDistanceRemaining(float distance) {
        distanceRemaining = distance;
    }
    public float distanceRemaining() {
        return distanceRemaining;
    }

    /* **************** Methods to control GameObjects **************** */
    public Player player() {
        return player;
    }
    public void stopBoost() {
        player.setBoosting(false);
    }
    public void boost() {
        player.setBoosting(true);
    }
    public List<Enemy> enemies() {
        return enemy;
    }
    public List<SpaceDust> dust() {
        return dust;
    }

    /* **************** Methods to control game play state **************** */
    public void update() {
        distanceRemaining -= player.speed();
        timeTaken = System.currentTimeMillis()-timeStarted - pauseElapsed;
    }
    public void continuePlaying() {
        playing = true;
    }
    public void pausePlaying() {
        playing = false;
    }
    public boolean isPlaying() {
        return playing;
    }
    public void isOver() {
        gameEnded = true;
    }
    public boolean ended() {
        return gameEnded;
    }
}
