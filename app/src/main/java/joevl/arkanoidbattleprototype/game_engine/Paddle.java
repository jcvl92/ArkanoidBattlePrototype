package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Paddle extends GameShape {
    private PaddleController paddleController;
    public static final int speed = 30;

    public Paddle(float height, float width, float x, float y, Paint paint) {
        super(paint);
        bounds = new RectF(x, y, x + width, y + height);
    }

    public void setPaddleController(PaddleController paddleController) {
        this.paddleController = paddleController;
    }

    public void advance(float min, float max) {
        float aSpeed = paddleController.getSpeed() < speed ? paddleController.getSpeed() : speed;
        if (paddleController.getMovement() == PaddleController.Controls.LEFT)
            bounds.offset(-aSpeed, 0);
        else if (paddleController.getMovement() == PaddleController.Controls.RIGHT)
            bounds.offset(aSpeed, 0);

        if (bounds.left < min)
            bounds.offset(min - bounds.left, 0);
        else if (bounds.right > max)
            bounds.offset(max - bounds.right, 0);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(bounds, paint);
    }
}