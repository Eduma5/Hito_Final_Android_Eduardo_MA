// GameView.java
package com.example.tapsoul;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class GameView extends View {
    private Paint paint;
    private float characterX, characterY;
    private float redBallX, redBallY;
    private int level;
    private int score;
    private GameListener listener;
    private Handler handler;
    private Runnable runnable;
    private Random random;
    private int pattern;
    private Bitmap background;
    private boolean isGameOver;
    private int alpha;
    private String levelUpMessage;
    private Handler messageHandler = new Handler();

    public interface GameListener {
        void onLevelUp(int level);
        void onGameOver(int score);
    }

    public GameView(Context context, GameListener listener) {
        super(context);
        this.listener = listener;
        paint = new Paint();
        characterX = 100;
        characterY = 100;
        redBallX = 300;
        redBallY = 300;
        level = 1;
        score = 0;
        random = new Random();
        pattern = random.nextInt(3); // Choose a random pattern
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isGameOver) {
                    updateRedBallPosition();
                    invalidate();
                }
                handler.postDelayed(this, 30);
            }
        };
        handler.post(runnable);
        Bitmap originalBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background_image);
        background = Bitmap.createScaledBitmap(originalBackground, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels, true);
        isGameOver = false;
        alpha = 255;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background, 0, 0, null);
        paint.setColor(Color.BLUE);
        paint.setAlpha(alpha);
        canvas.drawCircle(characterX, characterY, 50, paint);
        paint.setColor(Color.RED);
        paint.setAlpha(255);
        canvas.drawCircle(redBallX, redBallY, 50, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        canvas.drawText("Level: " + level, 50, 100, paint);
        canvas.drawText("Score: " + score, 50, 200, paint);
        if (levelUpMessage != null) {
            paint.setTextSize(80);
            canvas.drawText(levelUpMessage, getWidth() / 2 - paint.measureText(levelUpMessage) / 2, getHeight() / 2, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isGameOver) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                characterX = event.getX();
                characterY = event.getY();
                score += 10;
                if (score >= 100 * level) {
                    level++;
                    listener.onLevelUp(level);
                    levelUpMessage = "Level Up! You are now at level " + level;
                    messageHandler.postDelayed(() -> levelUpMessage = null, 2000); // Clear message after 2 seconds
                }
                if (checkCollision()) {
                    isGameOver = true;
                    animateGameOver();
                }
                invalidate();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private void updateRedBallPosition() {
        switch (pattern) {
            case 0:
                redBallX += level * 5;
                redBallY += level * 5;
                break;
            case 1:
                redBallX += level * 5;
                redBallY -= level * 5;
                break;
            case 2:
                redBallX += level * 5 * Math.sin(redBallY / 100);
                redBallY += level * 5 * Math.cos(redBallX / 100);
                break;
        }
        if (redBallX > getWidth() || redBallY > getHeight() || redBallX < 0 || redBallY < 0) {
            redBallX = random.nextInt(getWidth());
            redBallY = random.nextInt(getHeight());
            pattern = random.nextInt(3); // Choose a new random pattern
        }
    }

    private boolean checkCollision() {
        float dx = characterX - redBallX;
        float dy = characterY - redBallY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < 110; // Reduced collision distance threshold
    }

    private void animateGameOver() {
        new Thread(() -> {
            while (alpha > 0) {
                alpha -= 5;
                postInvalidate();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            listener.onGameOver(score);
        }).start();
    }
}