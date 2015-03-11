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
import joevl.arkanoidbattleprototype.game_engine.Paddle;
import joevl.arkanoidbattleprototype.game_engine.TouchPaddleController;

public class VersusGame extends GameEngine {
    public VersusGame(GameView gameView)
    {
        super(gameView);
    }

    private int ballDiameter, brickLength, paddleLength;
    private Ball mainBall;

    protected void init()
    {
        int width = (int)(gameView.bounds.right-gameView.bounds.left),
                height = (int)(gameView.bounds.bottom-gameView.bounds.top);

        //define the dimension
        ballDiameter = 100;
        brickLength = 100;
        paddleLength = width/4;

        //TODO: the engine should handle the adding of objects(balls, bricks, and paddles)

        //add one ball
        Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ballPaint.setShadowLayer(20, 50, 50, Color.LTGRAY);//TODO: fix this
        mainBall = new Ball(ballDiameter, ballDiameter, 500, height-200, ballPaint);
        gameShapes.get("balls").add(mainBall);

        //add a few bricks
        for(int i=0; i<3; i++)
            for(int j=0; j<7; j++)
            {
                Paint brickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                brickPaint.setColor(Color.MAGENTA);
                gameShapes.get("bricks").add(new Brick(brickLength / 2, brickLength, 40+(j*150), (height/3)+(i*100), brickPaint));
            }

        //add two touch paddles
        int touchAreaSize = 300;
        TouchPaddleController tpc = new TouchPaddleController(
                new RectF(0, height-touchAreaSize, touchAreaSize, height),
                new RectF(width-touchAreaSize, height-touchAreaSize, width, height));

        Paint opponentPaddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        opponentPaddlePaint.setColor(Color.RED);
        gameShapes.get("paddles").add(new Paddle(50, paddleLength, width/4, 10, opponentPaddlePaint, tpc));

        Paint userPaddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        userPaddlePaint.setColor(Color.BLUE);
        gameShapes.get("paddles").add(new Paddle(50, paddleLength, width/4, height-50, userPaddlePaint, tpc));

        //add the touch paddle listener to our view
        gameView.setOnTouchListener(TouchPaddleController.listener);
    }

    protected void doTick()
    {
        mainBall.multSpeed(1.001);
    }

    @Override
    protected void ballHit(GameShape ball, GameShape object, Iterator iter)
    {
        if(object.getClass()==Brick.class)
            iter.remove();

        ((Ball) ball).bounceOff(object);
    }
}