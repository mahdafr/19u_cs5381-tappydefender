package edu.utep.cs5381.tappydefender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("HiScores",MODE_PRIVATE);
        android.widget.Button butt = findViewById(R.id.button);
        butt.setOnClickListener(view -> {
            startActivity(new android.content.Intent(this,GameActivity.class));
            finish(); //kills this activity
        });
        android.widget.TextView textView = findViewById(R.id.highscore);
        textView.setText("Fastest Time: " + (prefs.getLong("FastestTime",1000000))/1000 + " s");
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
