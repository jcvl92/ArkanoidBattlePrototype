package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.RectF;

import java.util.ArrayList;

import joevl.arkanoidbattleprototype.GameView;

public abstract class GameEngine
{
    protected GameView gameView;
    private static final int refreshTime = 10;//millisecond time between ticks
    protected ArrayList<GameShape> balls;
    protected ArrayList<GameShape> paddles;
    protected ArrayList<GameShape> bricks;
    protected ArrayList<ArrayList<GameShape>> gameShapes;

    protected GameEngine(final GameView gameView)
    {
        this.gameView = gameView;

        //create the shape containers
        gameShapes = new ArrayList<ArrayList<GameShape>>();
        gameShapes.add(balls = new ArrayList<GameShape>());
        gameShapes.add(paddles = new ArrayList<GameShape>());
        gameShapes.add(bricks = new ArrayList<GameShape>());

        //TODO: save the thread to allow pausing
        new Thread(new Runnable() {
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

        int right = (int)gameView.bounds.right,
                bottom = (int)gameView.bounds.bottom;


        //TODO: do this AFTER advancing, then redo advancing on the bounced ball
        //check to see if the ball is colliding with anything(except other balls for now)
        for(GameShape ball : balls)
        {
            for(ArrayList<GameShape> gameShapeList : gameShapes)
                for(GameShape gameShape : gameShapeList)
                    if(!gameShape.getClass().equals(Ball.class) && ((Ball)ball).collides(gameShape))
                        ballHit(ball, gameShape);

            //TODO: move this to specific implementations, have the engine call specific functions for when the ball COMPLETELY leaves the bounds
            //bounce off of the walls
            if(ball.getBounds().intersects(0, 0, 0, bottom))//left wall
                ((Ball)ball).bounceOff(new RectF(0, 0, 0, bottom));
            else if(ball.getBounds().intersects(right, 0, right, bottom))//right wall
                ((Ball)ball).bounceOff(new RectF(right, 0, right, bottom));

            //TODO: this is just for the prototype, this behavior should be implmented in subclasses(goals or bouncing or whatever)
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