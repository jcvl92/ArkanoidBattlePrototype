package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.RectF;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import joevl.arkanoidbattleprototype.GameView;

public abstract class GameEngine
{
    protected GameView gameView;
    private static final long refreshTime = (long)((1.0/30) * 1000);//30 Hz - milliseconds
    protected HashMap<String, ArrayList<GameShape>> gameShapes;
    private Thread ticker;

    final static String TAG = "AKBGameEngine";

    protected GameEngine(final GameView gameView)
    {
        this.gameView = gameView;

        //create the shape containers
        gameShapes = new HashMap<String, ArrayList<GameShape>>();
        gameShapes.put("balls", new ArrayList<GameShape>());
        gameShapes.put("paddles", new ArrayList<GameShape>());
        gameShapes.put("bricks", new ArrayList<GameShape>());

        //TODO: use an android mechanism if that would work better
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
                        Thread.sleep(100);
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
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(gameShapes);
            byte[] bytes = bos.toByteArray();
            out.close();
            bos.close();
            return bytes;
        } catch(IOException ioe) {
            Log.d(TAG, "Error Serializing State");
            Log.d(TAG, ioe.toString());
            return null;
        }
    }

    public void setSerializedState(byte[] bytes)
    {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(bis);
            Object obj = in.readObject();
            gameShapes = (HashMap<String, ArrayList<GameShape>>) obj;
            in.close();
            bis.close();
        } catch(IOException|ClassNotFoundException e)
        {
            e.printStackTrace();
        }
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
        for(GameShape paddle : gameShapes.get("paddles"))
            ((Paddle)paddle).advance(0, right);

        //check to see if the ball is colliding with anything
        for(GameShape ball : gameShapes.get("balls"))
        {
            ((Ball) ball).advance();

            for(ArrayList<GameShape> gameShapeList : gameShapes.values())
                for(Iterator<GameShape> iter = gameShapeList.iterator(); iter.hasNext();)
                {
                    GameShape gameShape = iter.next();
                    if (gameShape != ball && ((Ball) ball).collides(gameShape))
                        ballHit(ball, gameShape, iter);//TODO: this is so gross and I hate it, change it to do something cleaner
                }

            //TODO: move this to specific implementations, have the engine call specific functions for when the ball COMPLETELY leaves the bounds
            //bounce off of the walls
            if(ball.getBounds().intersects(0, 0, 0, bottom))//left wall
                ((Ball)ball).bounceOff(new RectF(0, 0, 0, bottom));
            else if(ball.getBounds().intersects(right, 0, right, bottom))//right wall
                ((Ball)ball).bounceOff(new RectF(right, 0, right, bottom));

            //TODO: this is just for the prototype, this behavior should be implemented in subclasses(goals or bouncing or whatever)(SEE ABOVE!)
            else if(ball.getBounds().intersects(0, 0, right, 0))//top wall
                ((Ball)ball).bounceOff(new RectF(0, 0, right, 0));
            else if(ball.getBounds().intersects(0, bottom, right, bottom))//bottom wall
                ((Ball)ball).bounceOff(new RectF(0, bottom, right, bottom));

            //TODO: this doesnt work, fix it
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

    protected abstract void ballHit(GameShape ball, GameShape object, Iterator iter);

    public final HashMap<String, ArrayList<GameShape>> getGameShapes()
    {
        return gameShapes;
    }
}