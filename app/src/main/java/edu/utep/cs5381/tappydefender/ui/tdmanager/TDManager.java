package edu.utep.cs5381.tappydefender.ui.tdmanager;

import android.content.Context;
import android.graphics.Paint;

public class TDManager {
    private int screenWidth, screenHeight;
    private ParameterManager params;
    private PaintManager paints;
    private SoundManager sound;

    public TDManager(Context c, int x, int y) {
        sound = new SoundManager(c);
        screenWidth = x;
        screenHeight = y;
        reset();
    }

    public int getInt(int bound) {
        return params.getInt(bound);
    }

    public void reset() {
        params = new ParameterManager(screenWidth,screenHeight);
        paints = new PaintManager();
        sound.play(SoundManager.Sound.START);
    }

    /* ************************ SFX ************************ */
    public void playDestroyed() {
        sound.play(SoundManager.Sound.DESTROYED);
    }
    public void playBump() {
        sound.play(SoundManager.Sound.BUMP);
    }
    public void playWin() {
        sound.play(SoundManager.Sound.WIN);
    }

    /* ************************ Parameter Getters ************************ */
    public int skips() {
        return params.frameskips();
    }
    public int width() {
        return params.width();
    }
    public int height() {
        return params.height();
    }
    public int backgroundColor() {
        return paints.backgroundColor();
    }

    /* ************************ Paint Getters ************************ */
    public Paint shipPaint() {
        return paints.ship();
    }
    public Paint dustPaint() {
        return paints.dustPaint();
    }
    public Paint buttonPaint() { return paints.buttonPaint(); }
    public Paint textLeft() {
        Paint t = paints.textPaint();
        t.setTextAlign(Paint.Align.LEFT);
        t.setTextSize(paints.textSize());
        return t;
    }
    public Paint textCenter() {
        Paint t = paints.textPaint();
        t.setTextAlign(Paint.Align.CENTER);
        t.setTextSize(paints.textSize());
        return t;
    }
    public Paint textRight() {
        Paint t = paints.textPaint();
        t.setTextAlign(Paint.Align.CENTER);
        t.setTextSize(paints.textSize());
        return t;
    }
    public Paint endTextTitle() {
        Paint t = textCenter();
        t.setTextSize(80);
        return t;
    }
    public Paint endTextTitleRed() {
        Paint t = textCenter();
        t.setTextSize(80);
        t.setColor(paints.redColor());
        return t;
    }
}
