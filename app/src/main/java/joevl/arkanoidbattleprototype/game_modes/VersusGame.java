package joevl.arkanoidbattleprototype.game_modes;

import android.graphics.Color;
import android.graphics.Paint;

import joevl.arkanoidbattleprototype.GameView;
import joevl.arkanoidbattleprototype.game_engine.Ball;
import joevl.arkanoidbattleprototype.game_engine.Brick;
import joevl.arkanoidbattleprototype.game_engine.GameEngine;
import joevl.arkanoidbattleprototype.game_engine.GameShape;
import joevl.arkanoidbattleprototype.game_engine.Paddle;

public class VersusGame extends GameEngine {
    public VersusGame(GameView gameView)
    {
        super(gameView);
    }

    private int ballDiameter, brickLength, paddleLength;
    private Ball mainBall;

    protected void init()
    {
        //define the dimension
        ballDiameter = 50;
        brickLength = 100;
        paddleLength = gameView.width;

        //add one ball
        Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ballPaint.setColor(Color.CYAN);
        mainBall = new Ball(ballDiameter, ballDiameter, 500, 500, ballPaint);
        balls.add(mainBall);

        //add a few bricks
        for(int i=0; i<3; i++)
            for(int j=0; j<3; j++)
            {
                Paint brickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                brickPaint.setColor(Color.MAGENTA);
                bricks.add(new Brick(brickLength / 2, brickLength, 100+(j*400), 380+(i*400), brickPaint));
            }

        //add two paddles
        Paint opponentPaddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        opponentPaddlePaint.setColor(Color.RED);
        paddles.add(new Paddle(50, paddleLength/2, gameView.width/4, 10, opponentPaddlePaint));
        Paint userPaddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        userPaddlePaint.setColor(Color.BLUE);
        paddles.add(new Paddle(50, paddleLength/2, gameView.width/4, gameView.height-10, userPaddlePaint));
    }

    protected void doTick()
    {
        mainBall.multSpeed(1.001);
    }

    @Override
    protected void ballHit(GameShape ball, GameShape object)
    {
        if(object.getClass()==Brick.class)
            object.paint.setColor(Color.rgb((int) (Math.random() * 256) - 1, (int) (Math.random() * 256) - 1, (int) (Math.random() * 256) - 1));

        ((Ball) ball).bounceOff(object);
    }
}