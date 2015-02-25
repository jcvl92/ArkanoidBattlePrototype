package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Brick extends GameShape
{
    public Brick(float height, float width, float x, float y, Paint paint)
    {
        super(paint);
        bounds = new RectF(x, y, x+width, y+height);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawRect(bounds, paint);
    }
}