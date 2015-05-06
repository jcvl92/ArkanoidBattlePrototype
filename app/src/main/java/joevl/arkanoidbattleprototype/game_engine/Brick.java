package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import java.io.IOException;

public class Brick extends GameShape {
    public Brick(float height, float width, float x, float y, Paint paint) {
        super(paint);
        bounds = new RectF(x, y, x + width, y + height);
    }

    public void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeShort((short)bounds.left);
        out.writeShort((short)bounds.top);
        out.writeObject(paint);
    }

    public void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        float left = (float)in.readShort(), top = (float)in.readShort();
        bounds = new RectF(left, top, left+100, top+50);

        paint = (Paint) in.readObject();
    }

    public void draw(Canvas canvas, float xRatio, float yRatio) {
        RectF drawingBounds = new RectF(bounds);
        drawingBounds.left *= xRatio;
        drawingBounds.top *= yRatio;
        drawingBounds.right *= xRatio;
        drawingBounds.bottom *= yRatio;
        canvas.drawRect(drawingBounds, paint);
    }
}