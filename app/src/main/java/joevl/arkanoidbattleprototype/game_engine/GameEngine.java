package joevl.arkanoidbattleprototype.game_engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import joevl.arkanoidbattleprototype.GameView;
import joevl.arkanoidbattleprototype.MainMenuActivity;

//TODO: implement closeable
public abstract class GameEngine {
    protected GameView gameView;
    protected HashMap<String, ArrayList<GameShape>> gameShapes;
    private Thread ticker;
    protected boolean closing = false, resetting, beginning;
    public boolean paused = true;
    private Paint textPaint, resetTextPaint, overlayPaint;
    private Vibrator vibrator;
    private long resetTime;
    private int resetValue;

    protected GameEngine(final GameView gameView) {
        this.gameView = gameView;

        //create the shape containers
        gameShapes = new HashMap<String, ArrayList<GameShape>>();
        gameShapes.put("balls", new ArrayList<GameShape>());
        gameShapes.put("paddles", new ArrayList<GameShape>());
        gameShapes.put("bricks", new ArrayList<GameShape>());

        //initialize the paints
        textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setTextSize(200);

        resetTextPaint = new Paint();
        resetTextPaint.setColor(Color.WHITE);
        resetTextPaint.setTextSize(500);

        overlayPaint = new Paint();
        overlayPaint.setColor(Color.BLACK);
        overlayPaint.setAlpha(128);

        //get the vibrator
        vibrator = (Vibrator) gameView.getContext().getSystemService(Context.VIBRATOR_SERVICE);

        //TODO: use an android mechanism if that would work better
        ticker = new Thread(new Runnable() {
            public void run() {
                //wait for height and width to be measured
                synchronized (gameView.bounds) {
                    try {
                        while (gameView.bounds.isEmpty())
                            gameView.bounds.wait();
                    } catch (InterruptedException ie) {
                    }

                    //set the engine for the first time
                    reset();
                }
                while (!closing) {
                    if (!paused) {
                        if (!resetting)
                            tick();
                        else
                            resetTick();
                    }
                }
            }
        });
        ticker.start();
    }

    public void close() {
        closing = true;
        /*try {
            ticker.join();
        } catch(InterruptedException ie) {}*/
    }

    protected abstract void init();

    protected void reset() {
        synchronized (gameShapes) {
            for (ArrayList<GameShape> gs : gameShapes.values()) {
                gs.clear();
            }
            init();
        }

        resetting = true;
        beginning = false;
        resetTime = System.currentTimeMillis();
    }

    private void resetTick() {
        long difference = System.currentTimeMillis() - resetTime;
        resetValue = (int) (3 - (difference / 1000));
        resetting = difference < 3000;
        if (difference > 2200 && !beginning) {
            beginning = true;
            MainMenuActivity.playSoundEffect(MainMenuActivity.BEGIN_SFX_ID);
        }
    }

    protected void tick() {
        int right = 1080, bottom = 1845;

        //progress the paddles
        for (GameShape paddle : gameShapes.get("paddles"))
            ((Paddle) paddle).advance(0, right);

        //check to see if the ball is colliding with anything
        for (GameShape ball : gameShapes.get("balls")) {
            ((Ball) ball).advance();

            synchronized (gameShapes) {
                for (ArrayList<GameShape> gameShapeList : gameShapes.values())
                    for (Iterator<GameShape> iter = gameShapeList.iterator(); iter.hasNext(); ) {
                        GameShape gameShape = iter.next();
                        if (gameShape != ball && ((Ball) ball).collides(gameShape)) {
                            ballHit(ball, gameShape, iter);
                            break;
                        }
                    }
            }

            //bounce off of the walls
            if (ball.getBounds().intersects(0, 0, 0, bottom)) {//left wall
                Ball b = (Ball) ball;
                //redirect the ball
                b.flipVertical();
                //push it off of the wall
                b.bounds.offsetTo(1, b.bounds.top);
            } else if (ball.getBounds().intersects(right, 0, right, bottom)) {//right wall
                Ball b = (Ball) ball;
                //redirect the ball
                b.flipVertical();
                //push it off of the wall
                b.bounds.offsetTo(right - b.bounds.width(), b.bounds.top);
            }

            //if the ball went over an edge, push it back to the edge
            /*RectF ballBounds = ball.getBounds();
            if (ballBounds.left + ballBounds.width() < 0)
                ballBounds.offsetTo(-1, ballBounds.top);
            if (ballBounds.top + ballBounds.height() < 0)
                ballBounds.offsetTo(ballBounds.left, -1);
            if (ballBounds.right - ballBounds.width() > right)
                ballBounds.offsetTo(right - ballBounds.width() + 1, ballBounds.top);
            if (ballBounds.bottom - ballBounds.height() > bottom)
                ballBounds.offsetTo(ballBounds.left, bottom - ballBounds.height() + 1);*/
        }
    }

    public void draw(Canvas canvas) {
        //draw the score
        textPaint.setTextSize(200*getYRatio());
        canvas.drawText(getScore(), 0, 150*getYRatio(), textPaint);

        //draw the objects on the screen
        synchronized (gameShapes) {
            for (ArrayList<GameShape> gameShapeList : gameShapes.values())
                for (GameShape gameShape : gameShapeList)
                    gameShape.draw(canvas, getXRatio(), getYRatio());
        }

        //draw the reset overlay
        if (resetting) {
            //draw transparent rectangle overlay
            canvas.drawRect(gameView.bounds, overlayPaint);

            //draw text
            String val = String.valueOf(resetValue);
            Rect bounds = new Rect();
            resetTextPaint.setTextSize(500*getYRatio());
            resetTextPaint.getTextBounds(val, 0, val.length(), bounds);
            canvas.drawText(val,
                    gameView.bounds.centerX() - bounds.width() / 2,
                    gameView.bounds.centerY() + bounds.height() / 2,
                    resetTextPaint);
        }
    }

    protected float getXRatio() {
        return (gameView.bounds.right - gameView.bounds.left)/1080;
    }

    protected float getYRatio() {
        return (gameView.bounds.bottom - gameView.bounds.top)/1845;
    }

    protected void ballHit(GameShape ball, GameShape object, Iterator iter) {
        if (MainMenuActivity.vibrateOn)
            vibrator.vibrate(50);
    }

    public final HashMap<String, ArrayList<GameShape>> getGameShapes() {
        return gameShapes;
    }

    public abstract String getDescription();

    public abstract String getStatus();

    public abstract String getScore();
}