package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Paddle extends GameShape {
    private transient PaddleController paddleController;
    public static final int speed = 900;
    private long lastTick = -1;

    public Paddle(float height, float width, float x, float y, Paint paint) {
        super(paint);
        bounds = new RectF(x, y, x + width, y + height);
    }

    public void setPaddleController(PaddleController paddleController) {
        this.paddleController = paddleController;
    }

    public void advance(float min, float max) {
        float aSpeed = paddleController.getSpeed() < speed ? paddleController.getSpeed() : speed;

        if(lastTick>0) {
            float mult = (System.currentTimeMillis()-lastTick)/1000f;
            lastTick = System.currentTimeMillis();

            if (paddleController.getMovement() == PaddleController.Controls.LEFT)
                bounds.offset(-aSpeed*mult, 0);
            else if (paddleController.getMovement() == PaddleController.Controls.RIGHT)
                bounds.offset(aSpeed*mult, 0);
        }

        if (bounds.left < min)
            bounds.offsetTo(min, bounds.top);
        else if (bounds.right > max)
            bounds.offsetTo(max-bounds.width(), bounds.top);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(bounds, paint);
    }
}