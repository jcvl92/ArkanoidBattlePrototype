package joevl.arkanoidbattleprototype.game_modes;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Random;

import joevl.arkanoidbattleprototype.GameView;
import joevl.arkanoidbattleprototype.MainMenuActivity;
import joevl.arkanoidbattleprototype.game_engine.AIPaddleController;
import joevl.arkanoidbattleprototype.game_engine.Ball;
import joevl.arkanoidbattleprototype.game_engine.Brick;
import joevl.arkanoidbattleprototype.game_engine.GameShape;
import joevl.arkanoidbattleprototype.game_engine.Paddle;
import joevl.arkanoidbattleprototype.game_engine.SerialPaint;
import joevl.arkanoidbattleprototype.game_engine.TouchPaddleController;

public class MultiplayerVersusGame extends VersusGame {
        /*GameView gameView;
        int player2Score = 0, player1Score = 0;
        PaddleController tpc = null, apc = null;*/
        private final int playerNum;

        public MultiplayerVersusGame(GameView gameView, int playerNum) {
            super(gameView);
            this.playerNum = playerNum;
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
                if(playerNum == 2) {
                    opponentPaddle.setPaddleController(tpc);
                }
                gameShapes.get("paddles").add(opponentPaddle);

                //player
                Paint userPaddlePaint = new SerialPaint();
                userPaddlePaint.setColor(Color.BLUE);
                Paddle playerPaddle = new Paddle(brickLength/2, paddleLength, 270, 1795, userPaddlePaint);
                if(playerNum == 1) {
                    playerPaddle.setPaddleController(tpc);
                }
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

        public String getDescription() {
            return "Multiplayer";
        }

        public String getStatus() {
            if(playerNum == 1)
                return player1Score > player2Score ? "YOU WIN!" : "YOU LOSE!";
            else
                return player2Score > player1Score ? "YOU WIN!" : "YOU LOSE!";
        }

        public String getScore() {
            if(playerNum == 1)
                return player1Score + "-" + player2Score;
            else
                return player2Score + "-" + player1Score;
        }
}
