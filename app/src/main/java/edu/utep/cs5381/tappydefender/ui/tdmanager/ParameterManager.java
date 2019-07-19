package edu.utep.cs5381.tappydefender.ui.tdmanager;

class ParameterManager {
    private java.util.Random rng;
    private final int frames = 17;
    private int screenWidth;
    private int sscreenHeight;

    ParameterManager(int x, int y) {
        screenWidth = x;
        sscreenHeight = y;
        rng = new java.util.Random();
    }

    int getInt(int bound) {
        return rng.nextInt(bound) + 1;
    }

    /**
     *
     * @return the frames to skip
     */
    int frameskips() {
        return frames;
    }

    int width() {
        return screenWidth;
    }

    int height() {
        return sscreenHeight;
    }
}
