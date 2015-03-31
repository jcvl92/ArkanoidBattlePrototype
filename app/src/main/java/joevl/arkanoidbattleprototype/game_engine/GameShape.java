package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.IOException;
import java.io.Serializable;

public abstract class GameShape implements Serializable
{
    protected RectF bounds;
    public Paint paint;

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeFloat(bounds.left);
        out.writeFloat(bounds.top);
        out.writeFloat(bounds.right);
        out.writeFloat(bounds.bottom);
        out.writeObject(paint);
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        bounds = new RectF(
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                in.readFloat()
        );
        paint = (Paint)in.readObject();
    }

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