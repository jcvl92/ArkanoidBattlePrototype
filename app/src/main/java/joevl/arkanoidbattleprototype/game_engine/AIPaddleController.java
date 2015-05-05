package joevl.arkanoidbattleprototype.game_engine;

public class AIPaddleController implements PaddleController {
    Ball ball;
    Paddle paddle;

    public AIPaddleController(Ball ball, Paddle paddle) {
        this.ball = ball;
        this.paddle = paddle;
    }

    @Override
    public Controls getMovement() {
        if (paddle.getBounds().centerX() - ball.getBounds().centerX() > 0)
            return Controls.LEFT;
        else if (paddle.getBounds().centerX() - ball.getBounds().centerX() < 0)
            return Controls.RIGHT;
        else
            return Controls.NONE;
    }

    @Override
    public float getSpeed() {
        return Math.abs(paddle.getBounds().centerX() - ball.getBounds().centerX()) * 30;
    }
}