package joevl.arkanoidbattleprototype.game_modes;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Iterator;

import joevl.arkanoidbattleprototype.GameView;
import joevl.arkanoidbattleprototype.game_engine.Ball;
import joevl.arkanoidbattleprototype.game_engine.Brick;
import joevl.arkanoidbattleprototype.game_engine.GameEngine;
import joevl.arkanoidbattleprototype.game_engine.GameShape;

public class BounceTester extends GameEngine {
    Ball ball;
    Brick brick;

    public BounceTester(GameView gameView)
    {
        super(gameView);
    }

    @Override
    protected void init() {
        //add brick
        Paint brickPaint = new Paint();
        brickPaint.setColor(Color.BLUE);
        brick = new Brick(300, 300, 400, 750, brickPaint);
        this.gameShapes.get("bricks").add(brick);

        //add ball
        Paint ballPaint = new Paint();
        ballPaint.setColor(Color.GREEN);
        ball = new Ball(100, 100, 500, 500, ballPaint);
        this.gameShapes.get("balls").add(ball);
    }

    @Override
    protected void doTick() {
        int right = (int)gameView.bounds.right, left = (int)gameView.bounds.left,
                bottom = (int)gameView.bounds.bottom, top = (int)gameView.bounds.top;

        //bounce off of top and bottom
        if(ball.getBounds().intersects(0, 0, right, 0))//top wall
            ((Ball)ball).bounceOff(new RectF(0, 0, right, 0));
        else if(ball.getBounds().intersects(0, bottom, right, bottom))//bottom wall
            ((Ball)ball).bounceOff(new RectF(0, bottom, right, bottom));
    }

    @Override
    protected void ballHit(GameShape ball, GameShape object, Iterator iter) {
        ((Ball) ball).bounceOff(object);
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getStatus() {
        return "";
    }

    @Override
    public String getScore() {
        return "";
    }
}