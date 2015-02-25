package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public abstract class GameShape
{
    protected RectF bounds;
    public Paint paint;

    protected GameShape(Paint paint)
    {
        this.paint = paint;
    }
    public abstract void draw(Canvas canvas);
    protected RectF getBounds()
    {
        return bounds;
    }
}