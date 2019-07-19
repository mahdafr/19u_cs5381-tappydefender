package edu.utep.cs5381.tappydefender;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;

import edu.utep.cs5381.tappydefender.ui.TDView;

public class GameActivity extends AppCompatActivity {
    private TDView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        view = new TDView(this,size.x,size.y);
        setContentView(view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.resume();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if ( keycode== KeyEvent.KEYCODE_BACK ) {
            finish();
            return true;
        }
        return false;
    }
}
