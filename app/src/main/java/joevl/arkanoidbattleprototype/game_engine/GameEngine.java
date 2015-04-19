package joevl.arkanoidbattleprototype.game_engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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
import java.util.Map;

import joevl.arkanoidbattleprototype.GameView;

//TODO: implement closeable
public abstract class GameEngine {
    protected GameView gameView;
    private static final long refreshTime = (long) ((1.0 / 30) * 1000);//30 Hz - milliseconds
    protected HashMap<String, ArrayList<GameShape>> gameShapes;
    private Thread ticker;
    private boolean closing = false, resetting = true;
    private Paint textPaint, resetTextPaint, overlayPaint;
    private Vibrator vibrator;
    private int resetCounter = 0, resetValue = 3;

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

                    //initialize the engine
                    init();
                }
                while (!closing) {
                    long time = System.nanoTime();
                    //tick
                    if (!resetting)
                        tick();
                    else
                        resetTick();
                    gameView.postInvalidate();
                    //wait for remaining amount of time
                    try {
                        long sleepTime = refreshTime - (System.nanoTime() - time) / 1000000;
                        if (sleepTime > 0) {
                            Thread.sleep(sleepTime);
                        }
                    } catch (InterruptedException ie) {
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

    public void pause() {
        ticker.suspend();
    }

    public void resume() {
        ticker.resume();
    }

    public byte[] getSerializedState() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            for (Map.Entry<String, ArrayList<GameShape>> e : gameShapes.entrySet()) {
                if (!e.getKey().equals("paddles")) {
                    out.writeObject(e.getKey());
                    out.writeObject(e.getValue());
                }
            }
            //out.writeObject(gameShapes);
            byte[] bytes = bos.toByteArray();
            out.close();
            bos.close();
            return bytes;
        } catch (IOException ioe) {
            Log.println(Log.ASSERT, "error", Log.getStackTraceString(ioe));
            return null;
        }
    }

    public void setSerializedState(byte[] bytes) {
        synchronized (gameShapes) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream in = new ObjectInputStream(bis);
                try {
                    while (true) {
                        Object str = in.readObject();
                        Object lst = in.readObject();
                        gameShapes.put((String) str, (ArrayList<GameShape>) lst);
                    }
                    //gameShapes = (HashMap<String, ArrayList<GameShape>>) obj;
                } catch (EOFException eofe) {
                    in.close();
                    bis.close();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void init();

    protected void reset() {
        resetting = true;
    }

    private void resetTick() {
        resetValue = 3 - resetCounter++ / 30;

        if (resetValue == 0) {
            resetValue = 3;
            resetCounter = 0;
            resetting = false;
        }
    }

    private final void tick() {
        doTick();

        int right = (int) gameView.bounds.right,
                bottom = (int) gameView.bounds.bottom;

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
                b.backUp();
                b.flipVertical();
                b.advance();
            }
            else if (ball.getBounds().intersects(right, 0, right, bottom)) {//right wall
                Ball b = (Ball) ball;
                b.backUp();
                b.flipVertical();
                b.advance();
            }
            /*else if(ball.getBounds().intersects(0, 0, right, 0)) {//top wall
                Ball b = (Ball) ball;
                b.backUp();
                b.flipHorizontal();
                b.advance();
            }
            else if(ball.getBounds().intersects(0, bottom, right, bottom)) {//bottom wall
                Ball b = (Ball) ball;
                b.backUp();
                b.flipHorizontal();
                b.advance();
            }*/

            //if the ball went over an edge, push it back to the edge
            RectF ballBounds = ball.getBounds();
            if (ballBounds.left + ballBounds.width() < 0)
                ballBounds.offsetTo(-1, ballBounds.top);
            if (ballBounds.top + ballBounds.height() < 0)
                ballBounds.offsetTo(ballBounds.left, -1);
            if (ballBounds.right - ballBounds.width() > right)
                ballBounds.offsetTo(right - ballBounds.width() + 1, ballBounds.top);
            if (ballBounds.bottom - ballBounds.height() > bottom)
                ballBounds.offsetTo(ballBounds.left, bottom - ballBounds.height() + 1);
        }
    }

    public void draw(Canvas canvas) {
        //draw the score
        canvas.drawText(getScore(), 0, 150, textPaint);

        //draw the objects on the screen
        synchronized (gameShapes) {
            for (ArrayList<GameShape> gameShapeList : gameShapes.values())
                for (GameShape gameShape : gameShapeList)
                    gameShape.draw(canvas);
        }

        //draw the reset overlay
        if (resetting) {
            //draw transparent rectangle overlay
            canvas.drawRect(gameView.bounds, overlayPaint);

            //draw text
            String val = String.valueOf(resetValue);
            Rect bounds = new Rect();
            resetTextPaint.getTextBounds(val, 0, val.length(), bounds);
            canvas.drawText(val,
                    gameView.bounds.centerX() - bounds.width() / 2,
                    gameView.bounds.centerY() + bounds.height() / 2,
                    resetTextPaint);
        }
    }

    protected abstract void doTick();

    protected void ballHit(GameShape ball, GameShape object, Iterator iter) {
        vibrator.vibrate(50);
    }

    public final HashMap<String, ArrayList<GameShape>> getGameShapes() {
        return gameShapes;
    }

    public abstract String getDescription();

    public abstract String getStatus();

    public abstract String getScore();
}