package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Paddle extends GameShape
{
    //TODO: prevent paddles from moving off screen
    private final PaddleController paddleController;
    public Paddle(float height, float width, float x, float y, Paint paint, PaddleController paddleController)
    {
        super(paint);
        this.paddleController = paddleController;
        bounds = new RectF(x, y, x+width, y+height);
    }

    public void advance()
    {
        if(paddleController.getMovement() == PaddleController.Controls.LEFT)
            bounds.offset(-3, 0);
        else if(paddleController.getMovement() == PaddleController.Controls.RIGHT)
            bounds.offset(3, 0);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawRect(bounds, paint);
    }
}