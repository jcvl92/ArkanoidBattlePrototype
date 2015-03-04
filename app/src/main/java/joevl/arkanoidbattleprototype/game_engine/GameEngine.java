package joevl.arkanoidbattleprototype.game_engine;

import android.content.Context;
import android.graphics.RectF;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;

import joevl.arkanoidbattleprototype.GameView;

public abstract class GameEngine
{
    protected GameView gameView;
    private static final long refreshTime = (long)((1.0/30) * 1000);//30 Hz - milliseconds
    protected ArrayList<GameShape> balls;
    protected ArrayList<GameShape> paddles;
    protected ArrayList<GameShape> bricks;
    protected ArrayList<ArrayList<GameShape>> gameShapes;
    private Thread ticker;

    protected GameEngine(final GameView gameView)
    {
        this.gameView = gameView;

        //create the shape containers
        gameShapes = new ArrayList<ArrayList<GameShape>>();
        gameShapes.add(balls = new ArrayList<GameShape>());
        gameShapes.add(paddles = new ArrayList<GameShape>());
        gameShapes.add(bricks = new ArrayList<GameShape>());

        //TODO: use an android construct if that would work better
        ticker = new Thread(new Runnable() {
            public void run() {
                //wait for height and width to be measured
                synchronized (gameView.bounds) {
                    try {
                        while (gameView.bounds.isEmpty())
                            gameView.bounds.wait();
                    } catch (InterruptedException ie) {}

                    //initialize the engine
                    init();
                }
                while(true) {
                    long time = System.nanoTime();
                    //tick
                    tick();
                    //wait for remaining amount of time
                    try {
                        Thread.sleep(refreshTime - (System.nanoTime()-time)/1000000);
                    } catch (InterruptedException ie) {}
                }
            }
        });
        ticker.start();
    }

    public void pause()
    {
        ticker.suspend();
    }

    public void resume()
    {
        ticker.resume();
    }

    public byte[] getSerializedState()
    {
        //TODO: implement this
        return null;
    }

    public void setSerializedState(byte[] bytes)
    {
        //TODO: implement this
    }

    protected abstract void init();

    private final void tick()
    {
        //TODO: adjust speed of things according to tick frequency
        //TODO: instantiate with the size of the screen to stretch to fit
        doTick();

        int right = (int)gameView.bounds.right,
                bottom = (int)gameView.bounds.bottom;

        //progress the paddles
        for(GameShape paddle : paddles)
            ((Paddle)paddle).advance();

        //TODO: do this AFTER advancing, then redo advancing on the bounced ball
        //check to see if the ball is colliding with anything
        for(GameShape ball : balls)
        {
            for(ArrayList<GameShape> gameShapeList : gameShapes)
                for(GameShape gameShape : gameShapeList)
                    if(gameShape != ball && ((Ball)ball).collides(gameShape))
                        ballHit(ball, gameShape);

            //TODO: move this to specific implementations, have the engine call specific functions for when the ball COMPLETELY leaves the bounds
            //bounce off of the walls
            if(ball.getBounds().intersects(0, 0, 0, bottom))//left wall
                ((Ball)ball).bounceOff(new RectF(0, 0, 0, bottom));
            else if(ball.getBounds().intersects(right, 0, right, bottom))//right wall
                ((Ball)ball).bounceOff(new RectF(right, 0, right, bottom));

            //TODO: this is just for the prototype, this behavior should be implemented in subclasses(goals or bouncing or whatever)
            else if(ball.getBounds().intersects(0, 0, right, 0))//top wall
                ((Ball)ball).bounceOff(new RectF(0, 0, right, 0));
            else if(ball.getBounds().intersects(0, bottom, right, bottom))//bottom wall
                ((Ball)ball).bounceOff(new RectF(0, bottom, right, bottom));

            ((Ball) ball).advance();

            //if the ball went over an edge, push it back to the edge
            RectF ballBounds = ball.getBounds();
            if(ballBounds.left+ballBounds.width()<0)
                ballBounds.offsetTo(-1, ballBounds.top);
            if(ballBounds.top+ballBounds.height()<0)
                ballBounds.offsetTo(ballBounds.left, -1);
            if(ballBounds.right-ballBounds.width()>right)
                ballBounds.offsetTo(right-ballBounds.width()+1, ballBounds.top);
            if(ballBounds.bottom-ballBounds.height()>bottom)
                ballBounds.offsetTo(ballBounds.left, bottom-ballBounds.height()+1);
        }

        gameView.postInvalidate();
    }

    protected abstract void doTick();

    protected abstract void ballHit(GameShape ball, GameShape object);

    public final ArrayList<ArrayList<GameShape>> getGameShapes()
    {
        return gameShapes;
    }
}