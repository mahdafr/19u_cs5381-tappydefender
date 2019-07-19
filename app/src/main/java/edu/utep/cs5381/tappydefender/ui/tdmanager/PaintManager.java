package edu.utep.cs5381.tappydefender.ui.tdmanager;

import android.graphics.Color;
import android.graphics.Paint;

class PaintManager {
    private final int bgColor = Color.argb(255, 0, 0, 0);
    private final int dustColor = Color.argb(255,255,255,255);
    private final int redColor = Color.RED;
    private final int textColor = Color.argb(255, 255, 255, 255);
    private Paint ship;
    private Paint dust;
    private Paint text;
    private Paint button;
    private final int textSize = 48;
    private final int strokeW = 4;

    PaintManager() {
        ship = new Paint();
        dust = new Paint();
        dust.setColor(dustColor);
        text = new Paint();
        text.setColor(textColor);
        text.setStrokeWidth(strokeW);
        text.setTextSize(textSize);
        button = new Paint();
        button.setColor(Color.WHITE);
        button.setStyle(Paint.Style.STROKE);
        button.setStrokeWidth(5);
    }

    /* ************************ Getters ************************ */
    int textSize() { return textSize; }
    Paint ship() {
        return ship;
    }
    int redColor() {
        return redColor;
    }
    int backgroundColor() {
        return bgColor;
    }
    Paint dustPaint() {
        return dust;
    }
    Paint textPaint() {
        text.setColor(textColor);
        return text;
    }
    Paint buttonPaint() {
        return button;
    }
}
