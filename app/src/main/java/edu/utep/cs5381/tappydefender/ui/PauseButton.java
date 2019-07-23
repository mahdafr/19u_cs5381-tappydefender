package edu.utep.cs5381.tappydefender.ui;

class PauseButton {
    private int left, right;
    private int top, bottom;
    private boolean active;

    PauseButton(int screenWidth, int screenHeight) {
        active = false;
        setBounds(screenWidth,screenHeight);
    }

    private void setBounds(int maxX, int maxY) {
        left = maxX - 100;
        right = maxX - 10;
        top = 10;
        bottom = top + 100;
    }

    /* **************** Listener/Event Handling Methods **************** */
        /**
     * Checks if the Button was clicked to handle the event.
     * @param x the x-coordinate whether the TouchEvent occurred
     * @param y the y-coordinate whether the TouchEvent occurred
     * @return true if the touch was in bounds of the PauseButton
     */
    public boolean isClicked(float x, float y) {
        if ( x>=left && x<=right && y>=top && y<=bottom ) {
            active = !active; //if button was pressed before, it is not now
            return true;
        } return false;
    }

    /* **************** Getter Methods **************** */
    boolean isActive() {
        return active;
    }
    int left() {
        return left;
    }
    int right() {
        return right;
    }
    int top() {
        return top;
    }
    int bottom() {
        return bottom;
    }
}
