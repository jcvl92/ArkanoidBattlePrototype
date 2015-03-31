package joevl.arkanoidbattleprototype.game_modes;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Iterator;

import joevl.arkanoidbattleprototype.GameView;
import joevl.arkanoidbattleprototype.game_engine.Ball;
import joevl.arkanoidbattleprototype.game_engine.Brick;
import joevl.arkanoidbattleprototype.game_engine.GameEngine;
import joevl.arkanoidbattleprototype.game_engine.GameShape;
import joevl.arkanoidbattleprototype.game_engine.Paddle;
import joevl.arkanoidbattleprototype.game_engine.SerialPaint;
import joevl.arkanoidbattleprototype.game_engine.TouchPaddleController;

public class VersusGame extends GameEngine {
    GameView gameView;
    int computerScore = 0, humanScore = 0;
    TouchPaddleController tpc = null;

    public VersusGame(GameView gameView)
    {
        super(gameView);
        this.gameView = gameView;

        //add the touch paddle listener to our view
        gameView.setOnTouchListener(TouchPaddleController.listener);
    }

    private int ballDiameter, brickLength, paddleLength;
    private Ball mainBall;

    protected void init()
    {
        synchronized(gameShapes) {
            int width = (int) (gameView.bounds.right - gameView.bounds.left),
                    height = (int) (gameView.bounds.bottom - gameView.bounds.top);

            //define the dimension
            ballDiameter = 100;
            brickLength = 100;
            paddleLength = width / 4;

            //TODO: the engine should handle the adding of objects(balls, bricks, and paddles)

            //add one ball
            Paint ballPaint = new SerialPaint(Paint.ANTI_ALIAS_FLAG);
            ballPaint.setShadowLayer(20, 50, 50, Color.LTGRAY);//TODO: fix this
            mainBall = new Ball(ballDiameter, ballDiameter, 500, height - 200, ballPaint);
            gameShapes.get("balls").add(mainBall);

            //add a few bricks
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 7; j++) {
                    Paint brickPaint = new SerialPaint(Paint.ANTI_ALIAS_FLAG);
                    brickPaint.setColor(Color.MAGENTA);
                    gameShapes.get("bricks").add(new Brick(brickLength / 2, brickLength, 40 + (j * 150), (height / 3) + (i * 100), brickPaint));
                }

            //add two touch paddles
            int touchAreaSize = 300;
            if(tpc == null) {
                tpc = new TouchPaddleController(
                        new RectF(0, height - touchAreaSize, touchAreaSize, height),
                        new RectF(width - touchAreaSize, height - touchAreaSize, width, height));
            }

            Paint opponentPaddlePaint = new SerialPaint(Paint.ANTI_ALIAS_FLAG);
            opponentPaddlePaint.setColor(Color.RED);
            gameShapes.get("paddles").add(new Paddle(50, paddleLength, width / 4, 10, opponentPaddlePaint, tpc));

            Paint userPaddlePaint = new SerialPaint(Paint.ANTI_ALIAS_FLAG);
            userPaddlePaint.setColor(Color.BLUE);
            gameShapes.get("paddles").add(new Paddle(50, paddleLength, width / 4, height - 50, userPaddlePaint, tpc));
        }
    }

    private void reset()
    {
        synchronized(gameShapes) {
            for(ArrayList<GameShape> gs : gameShapes.values()) {
                gs.clear();
            }
            init();
        }
    }

    protected void doTick()
    {
        int right = (int)gameView.bounds.right,
                bottom = (int)gameView.bounds.bottom;

        for(GameShape ball : gameShapes.get("balls")) {
            if (ball.getBounds().intersects(0, 0, right, 0)) {//top wall
                if(++humanScore >= 3)
                    close();
                else
                    reset();
                return;
            }
            else if (ball.getBounds().intersects(0, bottom, right, bottom)) {//bottom wall
                if(++computerScore >= 3)
                    close();
                else
                    reset();
                return;
            }
        }

        mainBall.multSpeed(1.001);
    }

    @Override
    protected void ballHit(GameShape ball, GameShape object, Iterator iter)
    {
        if(object.getClass()==Brick.class)
            iter.remove();

        ((Ball) ball).bounceOff(object);
    }

    public String getDescription() {
        return "User vs. Computer";
    }

    public String getStatus() {
        return "Won";
    }

    public String getScore() {
        return humanScore+"-"+computerScore;
    }

    @Override
    public void close() {
        super.close();
        gameView.onGameOver();
    }
}