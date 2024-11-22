// GameActivity.java
package com.example.tapsoul;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity implements GameView.GameListener {
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getIntent().getStringExtra("username");
        setContentView(R.layout.activity_game);

        GameView gameView = new GameView(this, this);
        setContentView(gameView);

        Button btnReturn = new Button(this);
        btnReturn.setText("Return to Main");
        btnReturn.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent);
        });
        addContentView(btnReturn, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onLevelUp(int level) {
        runOnUiThread(() -> Toast.makeText(this, "Level Up! You are now at level " + level, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onGameOver(int score) {
        // Save score to database
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        User user = new User(username, score);
        new Thread(() -> {
            db.userDao().insert(user);
            runOnUiThread(() -> {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        }).start();
    }
}