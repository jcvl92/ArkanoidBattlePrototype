package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.IOException;
import java.io.Serializable;

public abstract class GameShape implements Serializable {
    protected transient RectF bounds;
    public transient Paint paint;

    protected GameShape(Paint paint) {
        this.paint = paint;
    }

    public abstract void draw(Canvas canvas, float xRatio, float yRatio);

    public RectF getBounds() {
        return new RectF(bounds);
    }
}