package edu.utep.cs5381.tappydefender.tdgameobject;

/**
 * Every Game Object in the gameplay.
 */
abstract class GameObject {
    int minX, minY;
    int maxX, maxY;
    int x, y;
    int speed;

    GameObject() {
        minX = 0;
        minY = 0;
    }

    void update() {

    }

    void update(int s) {

    }

    public void setX(int nwX) {
        x = nwX;
    }

    /* ************************ Getter Methods ************************ */
    public int speed() {
        return speed;
    }
    public int X() {
        return x;
    }
    public int Y() {
        return y;
    }
}
