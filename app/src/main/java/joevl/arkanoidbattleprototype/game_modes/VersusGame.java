package joevl.arkanoidbattleprototype.game_modes;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import joevl.arkanoidbattleprototype.GameView;
import joevl.arkanoidbattleprototype.MainMenuActivity;
import joevl.arkanoidbattleprototype.game_engine.AIPaddleController;
import joevl.arkanoidbattleprototype.game_engine.Ball;
import joevl.arkanoidbattleprototype.game_engine.Brick;
import joevl.arkanoidbattleprototype.game_engine.GameEngine;
import joevl.arkanoidbattleprototype.game_engine.GameShape;
import joevl.arkanoidbattleprototype.game_engine.Paddle;
import joevl.arkanoidbattleprototype.game_engine.PaddleController;
import joevl.arkanoidbattleprototype.game_engine.SerialPaint;
import joevl.arkanoidbattleprototype.game_engine.TouchPaddleController;

public class VersusGame extends GameEngine {
    GameView gameView;
    int computerScore = 0, humanScore = 0;
    PaddleController tpc = null, apc = null;

    public VersusGame(GameView gameView) {
        super(gameView);
        this.gameView = gameView;
    }

    private int ballDiameter, brickLength, paddleLength;
    private Ball mainBall;

    protected void init() {
        synchronized (gameShapes) {
            int width = (int) (gameView.bounds.right - gameView.bounds.left),
                    height = (int) (gameView.bounds.bottom - gameView.bounds.top);

            //define the dimension
            ballDiameter = 100;
            brickLength = 100;
            paddleLength = width / 4;

            //TODO: the engine should handle the adding of objects(balls, bricks, and paddles)

            //add one ball
            Paint ballPaint = new SerialPaint();
            mainBall = new Ball(ballDiameter, ballDiameter, 500, height - 200, ballPaint);
            gameShapes.get("balls").add(mainBall);

            //add a few bricks
            for (int i = 0; i < 5; i++)
                for (int j = 0; j < 8; j++) {
                    if (j == 3 || j == 4)
                        continue;
                    Paint brickPaint = new SerialPaint();
                    Random rnd = new Random();
                    brickPaint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    gameShapes.get("bricks").add(new Brick(brickLength / 2, brickLength, 40 + (j * 130), (height / 3) + (i * 100), brickPaint));
                }

            //add the touch paddle listener
            //int touchAreaSize = 300;
            if (tpc == null) {
                tpc = new TouchPaddleController(
                        new RectF(gameView.bounds.left, gameView.bounds.top,
                                gameView.bounds.right / 2, gameView.bounds.bottom),//(0, height - touchAreaSize, touchAreaSize, height),
                        new RectF(gameView.bounds.left + gameView.bounds.right / 2, gameView.bounds.top,
                                gameView.bounds.right, gameView.bounds.bottom));//(width - touchAreaSize, height - touchAreaSize, width, height));

                //add the touch paddle listener to our view
                gameView.setOnTouchListener((TouchPaddleController) tpc);
            }

            //opponent
            Paint opponentPaddlePaint = new SerialPaint();
            opponentPaddlePaint.setColor(Color.RED);
            Paddle opponentPaddle = new Paddle(50, paddleLength, width / 4, 10, opponentPaddlePaint);
            apc = new AIPaddleController(mainBall, opponentPaddle);
            opponentPaddle.setPaddleController(new AIPaddleController(mainBall, opponentPaddle));
            gameShapes.get("paddles").add(opponentPaddle);

            //player
            Paint userPaddlePaint = new SerialPaint();
            userPaddlePaint.setColor(Color.BLUE);
            Paddle playerPaddle = new Paddle(50, paddleLength, width / 4, height - 50, userPaddlePaint);
            playerPaddle.setPaddleController(tpc);
            gameShapes.get("paddles").add(playerPaddle);
        }
    }

    @Override
    public void setSerializedState(byte[] bytes) {
        super.setSerializedState(bytes);

        ArrayList<GameShape> paddles = gameShapes.get("paddles");
        Paddle opponentPaddle = (Paddle) paddles.get(0);
        Ball mainBall = (Ball) gameShapes.get("balls").get(0);
        opponentPaddle.setPaddleController(new AIPaddleController(mainBall, opponentPaddle));

        ((Paddle) paddles.get(1)).setPaddleController(tpc);
    }

    @Override
    protected void tick() {
        int right = (int) gameView.bounds.right,
                bottom = (int) gameView.bounds.bottom;

        for (GameShape ball : gameShapes.get("balls")) {
            if (ball.getBounds().centerY() < 0) {//top wall
                MainMenuActivity.playSoundEffect(MainMenuActivity.SCORE_SFX_ID);
                if (++humanScore >= 3)
                    close();
                else
                    reset();
                return;
            } else if (ball.getBounds().centerY() > bottom) {//bottom wall
                MainMenuActivity.playSoundEffect(MainMenuActivity.SCORE_SFX_ID);
                if (++computerScore >= 3)
                    close();
                else
                    reset();
                return;
            }
        }

        super.tick();
    }

    @Override
    protected void ballHit(GameShape ball, GameShape object, Iterator iter) {
        super.ballHit(ball, object, iter);

        if (object.getClass() == Brick.class) {
            iter.remove();
            MainMenuActivity.playSoundEffect(MainMenuActivity.BRICK_SFX_ID);
        } else if(object.getClass() == Paddle.class) {
            MainMenuActivity.playSoundEffect(MainMenuActivity.PADDLE_SFX_ID);
        }

        ((Ball) ball).bounceOff(object);
    }

    public String getDescription() {
        return "User vs. Computer";
    }

    public String getStatus() {
        return humanScore > computerScore ? "YOU WIN!" : "YOU LOSE!";
    }

    public String getScore() {
        return humanScore + "-" + computerScore;
    }

    @Override
    public void close() {
        super.close();
        gameView.onGameOver();
    }
}