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
    int player2Score = 0, player1Score = 0;
    PaddleController tpc = null, apc = null;

    public VersusGame(GameView gameView) {
        super(gameView);
        this.gameView = gameView;
    }

    protected void init() {
        synchronized (gameShapes) {
            //define the dimension
            int ballDiameter = 100;
            int brickLength = 100;
            int paddleLength = 270;

            //TODO: the engine should handle the adding of objects(balls, bricks, and paddles)

            //add one ball
            Paint ballPaint = new SerialPaint();
            Ball mainBall = new Ball(ballDiameter, ballDiameter, 500, 1645, ballPaint);
            gameShapes.get("balls").add(mainBall);

            //add a few bricks
            for (int i = 0; i < 5; i++)
                for (int j = 0; j < 8; j++) {
                    if (j == 3 || j == 4)
                        continue;
                    Paint brickPaint = new SerialPaint();
                    Random rnd = new Random();
                    brickPaint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    gameShapes.get("bricks").add(new Brick(brickLength / 2, brickLength, 40 + (j * 130), 615 + (i * 100), brickPaint));
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
            Paddle opponentPaddle = new Paddle(brickLength/2, paddleLength, 270, 10, opponentPaddlePaint);
            apc = new AIPaddleController(mainBall, opponentPaddle);
            opponentPaddle.setPaddleController(new AIPaddleController(mainBall, opponentPaddle));
            gameShapes.get("paddles").add(opponentPaddle);

            //player
            Paint userPaddlePaint = new SerialPaint();
            userPaddlePaint.setColor(Color.BLUE);
            Paddle playerPaddle = new Paddle(brickLength/2, paddleLength, 270, 1795, userPaddlePaint);
            playerPaddle.setPaddleController(tpc);
            gameShapes.get("paddles").add(playerPaddle);
        }
    }

    @Override
    protected void tick() {
        int bottom = 1845;

        for (GameShape ball : gameShapes.get("balls")) {
            if (ball.getBounds().centerY() < 0) {//top wall
                MainMenuActivity.playSoundEffect(MainMenuActivity.SCORE_SFX_ID);
                if (++player1Score >= 3)
                    close();
                else
                    reset();
                return;
            } else if (ball.getBounds().centerY() > bottom) {//bottom wall
                MainMenuActivity.playSoundEffect(MainMenuActivity.SCORE_SFX_ID);
                if (++player2Score >= 3)
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
        } else if (object.getClass() == Paddle.class) {
            MainMenuActivity.playSoundEffect(MainMenuActivity.PADDLE_SFX_ID);
        }

        ((Ball) ball).bounceOff(object);
    }

    public String getDescription() {
        return "Singleplayer";
    }

    public String getStatus() {
        return player1Score > player2Score ? "YOU WIN!" : "YOU LOSE!";
    }

    public String getScore() {
        return player1Score + "-" + player2Score;
    }

    @Override
    public void close() {
        super.close();
        gameView.onGameOver();
    }
}