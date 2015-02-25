package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Paddle extends GameShape
{
    public Paddle(float height, float width, float x, float y, Paint paint)
    {
        super(paint);
        bounds = new RectF(x, y, x+width, y+height);
    }

    public void advance()
    {
        //TODO: implement with a behavior class
        bounds.offset((float)(Math.random()-0.5)*10, 0);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawRect(bounds, paint);
    }
}