package joevl.arkanoidbattleprototype.game_modes;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import joevl.arkanoidbattleprototype.GameView;
import joevl.arkanoidbattleprototype.game_engine.Ball;
import joevl.arkanoidbattleprototype.game_engine.Brick;
import joevl.arkanoidbattleprototype.game_engine.GameEngine;
import joevl.arkanoidbattleprototype.game_engine.GameShape;
import joevl.arkanoidbattleprototype.game_engine.Paddle;
import joevl.arkanoidbattleprototype.game_engine.PaddleController;
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
                height = (int)(gameView.bounds.bottom-gameView.bounds.right);

        //define the dimension
        ballDiameter = 100;
        brickLength = 100;
        paddleLength = width/4;

        //add one ball
        Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ballPaint.setColor(Color.CYAN);
        mainBall = new Ball(ballDiameter, ballDiameter, 500, 500, ballPaint);
        balls.add(mainBall);

        //add a few bricks
        /*for(int i=0; i<3; i++)
            for(int j=0; j<3; j++)
            {
                Paint brickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                brickPaint.setColor(Color.MAGENTA);
                bricks.add(new Brick(brickLength / 2, brickLength, 100+(j*400), 380+(i*400), brickPaint));
            }*/

        //add a random behavior paddle
        /*Paint opponentPaddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        opponentPaddlePaint.setColor(Color.RED);
        paddles.add(new Paddle(50, paddleLength, width/4, 10, opponentPaddlePaint,
                new PaddleController(){
                    @Override
                    public Controls getMovement() {
                        return Math.random()<0.5 ? Controls.LEFT : Controls.RIGHT;
                    }
                }));

        //add a touch paddle
        Paint userPaddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        userPaddlePaint.setColor(Color.BLUE);
        paddles.add(new Paddle(50, paddleLength, width/4, height-10, userPaddlePaint,
                new TouchPaddleController(
                        new RectF(0, 1500, 200, 1700),
                        new RectF(800, 1500, 1000, 1700)
                )));*/

        //add the touch paddle listener to our view
        gameView.setOnTouchListener(TouchPaddleController.listener);
    }

    protected void doTick()
    {
        mainBall.multSpeed(1.0001);
    }

    @Override
    protected void ballHit(GameShape ball, GameShape object)
    {
        if(object.getClass()==Brick.class)
            object.paint.setColor(Color.rgb((int) (Math.random() * 256) - 1, (int) (Math.random() * 256) - 1, (int) (Math.random() * 256) - 1));

        ((Ball) ball).bounceOff(object);
    }
}