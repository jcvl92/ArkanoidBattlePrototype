package joevl.arkanoidbattleprototype;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

public abstract class GameEngine
{
    protected GameView gameView;
    private static final int refreshTime = 10;//millisecond time between ticks
    protected ArrayList<GameShape> balls;
    protected ArrayList<GameShape> paddles;
    protected ArrayList<GameShape> bricks;
    protected ArrayList<ArrayList<GameShape>> gameShapes;

    protected GameEngine(GameView gameView)
    {
        this.gameView = gameView;

        //create the shape containers
        gameShapes = new ArrayList<ArrayList<GameShape>>();
        gameShapes.add(balls = new ArrayList<GameShape>());
        gameShapes.add(paddles = new ArrayList<GameShape>());
        gameShapes.add(bricks = new ArrayList<GameShape>());

        //init();

        //TODO: save the thread to allow pausing
        new Thread(new Runnable() {
            public void run() {
                init();
                while(true) {
                    long time = System.nanoTime();
                    //tick
                    tick();
                    //wait for remaining amount of time
                    try {
                        Thread.sleep((System.nanoTime()-time)/1000000 + refreshTime);
                    } catch (InterruptedException ie) {}
                }
            }
        }).start();
    }

    protected abstract void init();

    private final void tick()
    {
        doTick();

        //check to see if the ball is colliding with anything(except other balls for now)
        for(GameShape ball : balls)
        {
            for(ArrayList<GameShape> gameShapeList : gameShapes)
                for(GameShape gameShape : gameShapeList)
                    if(!gameShape.getClass().equals(Ball.class) && ((Ball)ball).collides(gameShape))
                        ballHit(ball, gameShape);

            //bounce off of the walls
            if(ball.getBounds().intersects(0, 0, 0, gameView.height))//left wall
                ((Ball)ball).bounceOff(new RectF(0, 0, 0, gameView.height));
            else if(ball.getBounds().intersects(gameView.width, 0, gameView.width, gameView.height))//right wall
                ((Ball)ball).bounceOff(new RectF(gameView.width, 0, gameView.width, gameView.height));

            //TODO: this is just for the prototype, this behavior should be implmented in subclasses(goals or bouncing or whatever)
            else if(ball.getBounds().intersects(0, 0, gameView.width, 0))//top wall
                ((Ball)ball).bounceOff(new RectF(0, 0, gameView.width, 0));
            else if(ball.getBounds().intersects(0, gameView.height, gameView.width, gameView.height))//bottom wall
                ((Ball)ball).bounceOff(new RectF(0, gameView.height, gameView.width, gameView.height));

            ((Ball) ball).advance();

            //if the ball went over an edge, push it back to the edge
            RectF ballBounds = ball.getBounds();
            if(ballBounds.left+ballBounds.width()<0)
                ballBounds.offsetTo(-1, ballBounds.top);
            if(ballBounds.top+ballBounds.height()<0)
                ballBounds.offsetTo(ballBounds.left, -1);
            if(ballBounds.right-ballBounds.width()>gameView.width)
                ballBounds.offsetTo(gameView.width-ballBounds.width()+1, ballBounds.top);
            if(ballBounds.bottom-ballBounds.height()>gameView.height)
                ballBounds.offsetTo(ballBounds.left, gameView.height-ballBounds.height()+1);
        }

        //progress the paddles
        for(GameShape paddle : paddles)
            ((Paddle)paddle).advance();

        //TODO: force a redraw
        gameView.postInvalidate();
    }

    protected abstract void doTick();

    protected abstract void ballHit(GameShape ball, GameShape object);

    public final ArrayList<ArrayList<GameShape>> getGameShapes()
    {
        return gameShapes;
    }
}