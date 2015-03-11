package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public abstract class GameShape
{
    protected RectF bounds;
    public Paint paint;

    protected GameShape(Paint paint)
    {
        this.paint = paint;
    }
    public abstract void draw(Canvas canvas);
    public RectF getBounds()
    {
        return new RectF(bounds);
    }
}