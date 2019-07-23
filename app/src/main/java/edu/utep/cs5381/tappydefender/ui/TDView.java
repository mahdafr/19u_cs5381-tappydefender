package edu.utep.cs5381.tappydefender.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import edu.utep.cs5381.tappydefender.tdgame.*;
import edu.utep.cs5381.tappydefender.ui.tdmanager.*;

public class TDView extends SurfaceView implements Runnable {
    private Context context;
    private Thread gameThread;
    private TDManager TDM; //manager for paints, parameters, and sound
    private TDGame game;
    private SurfaceHolder holder;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private PauseButton pauseButton;

    private boolean gotLife;

    public TDView(Context c, int x, int y) {
        super(c);
        context = c;
        holder = getHolder();
        prefs = context.getSharedPreferences("HiScores",context.MODE_PRIVATE);
        editor = prefs.edit();
        TDM = new TDManager(context,x,y);
        pauseButton = new PauseButton(x,y);
        game = new TDGame(context,x,y);
        startGame();
    }

    /**
     * (re)Starts the TappyDefender game: reset the gameobjects and fastest time.
     */
    private void startGame() {
        gotLife = false;
        TDM.reset();
        int x = TDM.width(), y = TDM.height();
        game.reset(x,y);
        game.setFastestTime(prefs.getLong("FastestTime",1000000));
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if ( pauseButton.isClicked(motionEvent.getX(),motionEvent.getY()) ) {
                    if ( pauseButton.isActive() ) {
                        draw();
                        game.pause();
                        pause();
                    } else {
                        game.resume();
                        resume();
                    }
                } else {
                    game.boost();
                    if ( game.ended() )
                        startGame();
                }
                break;
            case MotionEvent.ACTION_UP:
                game.stopBoost();
                break;
        }
        return true;
    }

    /* ************************ Draw Methods ************************ */
    public void draw() {
        if ( holder.getSurface().isValid() ) {
            Canvas canvas = holder.lockCanvas();

            //draw the background, Ships, SpaceDust and text on game state data
            drawGameObjects(canvas);
            drawStateData(canvas);
            if ( game.ended() ) //draw Game Over screen
                drawGameOver(canvas);
            else
                drawButton(canvas);

            holder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Draws the Player and Enemy ships, and SpaceDust, in their updated locations.
     * @param canvas to draw on
     */
    private void drawGameObjects(Canvas canvas) {
        Player p = game.player();
        canvas.drawColor(TDM.backgroundColor());
        canvas.drawBitmap(p.bitmap(),p.X(),p.Y(),TDM.shipPaint());
        for ( Enemy e : game.enemies() )
            canvas.drawBitmap(e.bitmap(), e.X(), e.Y(),TDM.shipPaint());
        for ( SpaceDust d : game.dust() )
            canvas.drawPoint(d.X(),d.Y(),TDM.dustPaint());
    }

    /**
     * Draws the game's state: player's shield, time elapsed, fastest time, and distance remaining.
     * @param canvas to draw on
     */
    private void drawStateData(Canvas canvas) {
        int yy = 50;
        int height = TDM.height();
        int width = TDM.width()/2;
        long fastestTime = game.fastestTime();
        int playerShields = game.player().shieldsRemaining();
        int playerSpeed = game.player().speed();
        long timeTaken = game.elapsedTime();
        float distanceRemaining = game.distanceRemaining();

        if ( fastestTime==Long.MAX_VALUE )
            canvas.drawText("Fastest: --", 10, yy, TDM.textLeft());
        else
            canvas.drawText(formatTime("Fastest", fastestTime), 10, yy, TDM.textLeft());
        canvas.drawText("Shield: " +playerShields, 10, height-yy, TDM.textLeft());
        canvas.drawText(formatTime("Time", timeTaken), width, yy, TDM.textCenter());
        canvas.drawText("Distance: " +distanceRemaining/1000+ " KM", width, height-yy, TDM.textCenter());

        canvas.drawText("Speed: " +playerSpeed*60+ " MPS", width*2-275, height-yy, TDM.textRight());
    }

    /**
     * Draws the Pause/Play button depending on game state.
     * @param canvas to draw on
     */
    private void drawButton(Canvas canvas) {
        int mid = (pauseButton.left()+pauseButton.right())/2;
        int skip = 15;
        int top = pauseButton.top() + skip;
        int bottom = pauseButton.bottom() - skip;
        if ( !pauseButton.isActive() ) {
            canvas.drawRect(mid - 2 * skip, top, mid - skip, bottom, TDM.buttonPaint());
            canvas.drawRect(mid + skip, top, mid + 2 * skip, bottom, TDM.buttonPaint());
        } else {
            int midY = (top+bottom)/2;
            canvas.drawLine(mid-2*skip,top,mid+2*skip,midY,TDM.buttonPaint());
            canvas.drawLine(mid-2*skip,bottom,mid+2*skip,midY,TDM.buttonPaint());
            canvas.drawLine(mid-2*skip,top,mid-2*skip,bottom,TDM.buttonPaint());
        }
    }

    /**
     * Draws the Game Over/Restart screen.
     * @param canvas to draw on
     */
    private void drawGameOver(Canvas canvas) {
        int x = TDM.width()/2;
        int y = TDM.height()/2;
        long fastestTime = game.fastestTime();
        float distanceRemaining = game.distanceRemaining();

        canvas.drawText("Game Over",x,y-150,TDM.endTextTitleRed());
        canvas.drawText(formatTime("Fastest Time",fastestTime),x,y+20,TDM.textCenter());
        canvas.drawText("Distance remaining: " +(distanceRemaining/1000)+ " km",x,y+70,TDM.textCenter());
        canvas.drawText("Tap to replay!",x,y+250,TDM.endTextTitle());
    }

    /**
     * Creates a custom-formatted time as a String message for UI.
     * @param label the message as a String
     * @param time the time in milliseconds as long
     * @return the formatted String
     */
    private String formatTime(String label, long time) {
        return String.format("%s: %d.%03ds", label, time / 1000, time % 1000);
    }

    /* ************************ Thread Control Methods ************************ */
    @Override
    public void run() {
        while ( game.isPlaying() ) {
            update();
            draw();
            control();
        }
    }

    public void pause() {
        game.pausePlaying();
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            //todo
        }
    }

    public void resume() {
        game.continuePlaying();
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void update() {
        for ( SpaceDust d : game.dust() )
            d.update(game.player().speed());
        if ( updateEnemies() )
            playerHit();
        if ( !game.ended() ) {
            game.update();
        }
        if ( game.distanceRemaining()<0 )
            playerWon();
        game.player().update();
    }

    /**
     * Updates the enemies' locations in the game.
     * @return whether the player hit an enemy
     */
    private boolean updateEnemies() {
        boolean flag = false;
        for ( Enemy e: game.enemies() ) {
            //if Player hits an Enemy, enemy dies and Player loses a shield
            if ( android.graphics.Rect.intersects(game.player().collisionBox(),e.collisionBox()) ) {
                e.setX(-e.bitmap().getWidth());
                flag = true;
                gotLife = e.givesLife();
            }
            e.update(game.player().speed());
        }
        return flag;
    }

    /**
     * Plays the appropriate sound when the player hit a ship or heart.
     */
    private void playerHit() {
        if ( gotLife ) {
            game.player().addShield();
            TDM.playHeart();
        } else {
            if ( game.player().loseShield() ) {
                TDM.playDestroyed();
                game.isOver(); //end the game
            } else TDM.playBump();
        }
        gotLife = false; //reset
    }

    /**
     * Ends the game on a Player's win.
     */
    private void playerWon() {
        game.isOver(); //end the game
        TDM.playWin() ;
        long elapsed = game.elapsedTime();
        long fastest = game.fastestTime();
        if ( elapsed<fastest ) {
            editor.putLong("FastestTime",elapsed);
            editor.commit();
            game.setFastestTime(elapsed);
        }
        game.setDistanceRemaining(0);
    }

    /**
     * Control the Thread running the game.
     */
    private void control() {
        try {
            gameThread.sleep(TDM.skips()); //milliseconds
        } catch (InterruptedException e) { }
    }
}
