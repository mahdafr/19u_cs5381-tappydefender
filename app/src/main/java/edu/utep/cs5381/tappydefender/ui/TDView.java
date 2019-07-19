package edu.utep.cs5381.tappydefender.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import edu.utep.cs5381.tappydefender.ui.tdmanager.*;
import edu.utep.cs5381.tappydefender.tdgameobject.*;

public class TDView extends SurfaceView implements Runnable {
    private Context context;
    private Thread gameThread;
    private TDManager TDM; //manager for paints, parameters, and sound
    private boolean playing;
    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;
    private boolean gameEnded;
    private SurfaceHolder holder;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private PauseButton pauseButton;

    private Player player;
    private List<Enemy> enemy = new CopyOnWriteArrayList<>();
    private List<SpaceDust> dust = new CopyOnWriteArrayList<>();

    public TDView(Context c, int x, int y) {
        super(c);
        context = c;
        holder = getHolder();
        prefs = context.getSharedPreferences("HiScores",context.MODE_PRIVATE);
        editor = prefs.edit();
        fastestTime = prefs.getLong("FastestTime",1000000);
        TDM = new TDManager(context,x,y);
        pauseButton = new PauseButton(x,y);
        startGame();
    }

    private void startGame() {
        TDM.reset();
        int x = TDM.width(), y = TDM.height();
        //reset game objects in the scene
        player = new Player(context,x,y);
        enemy.clear();
        dust.clear();
        int i = 0;
        if ( x>1000 ) i--;
        if ( x>1200 ) i--;
        for ( ; i<TDM.getInt(5) ; i++ )
            enemy.add(new Enemy(context, x, y));
        for ( i=0 ; i<TDM.getInt(55) ; i++ )
            dust.add(new SpaceDust(x,y));
        //reset trackers
        distanceRemaining = 10000; // 10 km
        timeTaken = 0;
        timeStarted = System.currentTimeMillis();
        gameEnded = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if ( pauseButton.isClicked(motionEvent.getX(),motionEvent.getY()) ) {
                    if ( playing ) pause();
                    else resume();
                } else {
                    player.setBoosting(true);
                    if (gameEnded)
                        startGame();
                }
                break;
            case MotionEvent.ACTION_UP:
                player.setBoosting(false);
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
            drawButton(canvas);
            if ( gameEnded ) //draw Game Over screen
                drawGameOver(canvas);

            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawGameObjects(Canvas canvas) {
        canvas.drawColor(TDM.backgroundColor());
        canvas.drawBitmap(player.bitmap(),player.X(),player.Y(),TDM.shipPaint());
        for ( Enemy e : enemy )
            canvas.drawBitmap(e.bitmap(), e.X(), e.Y(),TDM.shipPaint());
        for ( SpaceDust d : dust )
            canvas.drawPoint(d.X(),d.Y(),TDM.dustPaint());
    }

    private void drawStateData(Canvas canvas) {
        int yy = 50;
        int height = TDM.height();
        int width = TDM.width()/2;
        if ( fastestTime==Long.MAX_VALUE )
            canvas.drawText("Fastest: --", 10, yy, TDM.textLeft());
        else
            canvas.drawText(formatTime("Fastest", fastestTime), 10, yy, TDM.textLeft());
        canvas.drawText("Shield: " +player.shieldsRemaining(), 10, height-yy, TDM.textLeft());
        canvas.drawText(formatTime("Time", timeTaken), width, yy, TDM.textCenter());
        canvas.drawText("Distance: " +distanceRemaining/1000+ " KM", width, height-yy, TDM.textCenter());

        canvas.drawText("Speed: " +player.speed()*60+ " MPS", width*2-275, height-yy, TDM.textRight());
    }

    private void drawButton(Canvas canvas) {
        int mid = (pauseButton.left()+pauseButton.right())/2;
        int skip = 15;
        int top = pauseButton.top() + skip;
        int bottom = pauseButton.bottom() - skip;
        if ( playing ) {
            canvas.drawRect(mid - 2 * skip, top, mid - skip, bottom, TDM.buttonPaint());
            canvas.drawRect(mid + skip, top, mid + 2 * skip, bottom, TDM.buttonPaint());
        } else {
            int midY = (top+bottom)/2;
            canvas.drawLine(mid-2*skip,top,mid+2*skip,midY,TDM.buttonPaint());
            canvas.drawLine(mid-2*skip,bottom,mid+2*skip,midY,TDM.buttonPaint());
            canvas.drawLine(mid-2*skip,top,mid-2*skip,bottom,TDM.buttonPaint());
        }
    }

    private void drawGameOver(Canvas canvas) {
        int x = TDM.width()/2;
        int y = TDM.height()/2;
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
        while ( playing ) {
            update();
            draw();
            control();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            //todo
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void update() {
        for ( SpaceDust d : dust )
            d.update(player.speed());
        if ( updateEnemies() )
            playerHit();
        if ( !gameEnded ) {
            distanceRemaining -= player.speed();
            timeTaken = System.currentTimeMillis()-timeStarted;
        }
        if ( distanceRemaining<0 )
            playerWon();
        player.update();
    }

    private boolean updateEnemies() {
        boolean flag = false;
        for ( Enemy e: enemy ) {
            //if Player hits an Enemy, enemy dies and Player loses a shield
            if ( android.graphics.Rect.intersects(player.collisionBox(),e.collisionBox()) ) {
                e.setX(-e.bitmap().getWidth());
                flag = true;
            }
            e.update(player.speed());
        }
        return flag;
    }

    private void playerHit() {
        if ( player.loseShield() ) {
            TDM.playDestroyed();
            gameEnded = true; //end the game
        } else TDM.playBump();
    }

    private void playerWon() {
        TDM.playWin() ;
        if ( timeTaken<fastestTime ) {
            editor.putLong("FastestTime",timeTaken);
            editor.commit();
            fastestTime = timeTaken;
        }
        distanceRemaining = 0;
        gameEnded = true; //end the game
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
