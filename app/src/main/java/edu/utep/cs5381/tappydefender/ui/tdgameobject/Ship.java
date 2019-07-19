package edu.utep.cs5381.tappydefender.ui.tdgameobject;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Ships in the gameplay of Tappy Defender.
 */
class Ship extends GameObject {
    Rect collisionBox;
    Bitmap bitmap;

    Ship() {
        super();
    }

    void setCollisionBox() {
        collisionBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    @Override
    void update() {
        collisionBox.set(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
    }

    @Override
    void update(int s) {

    }

    /* ************************ Getter Methods ************************ */
    public Bitmap bitmap() {
        return bitmap;
    }

    public Rect collisionBox() {
        return collisionBox;
    }
}
