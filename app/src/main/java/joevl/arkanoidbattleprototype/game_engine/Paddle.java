package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.IOException;

public class Paddle extends GameShape {
    private transient PaddleController paddleController;
    public transient static final int speed = 900;
    private transient long lastTick = -1;

    public Paddle(float height, float width, float x, float y, Paint paint) {
        super(paint);
        bounds = new RectF(x, y, x + width, y + height);
    }

    public void setPaddleController(PaddleController paddleController) {
        this.paddleController = paddleController;
    }

    public void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeFloat(bounds.left);
        out.writeFloat(bounds.top);
        out.writeFloat(bounds.right);
        out.writeFloat(bounds.bottom);
        out.writeObject(paint);
    }

    public void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        bounds = new RectF(
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                in.readFloat()
        );
        paint = (Paint) in.readObject();
    }

    public void advance(float min, float max) {
        if(paddleController == null)
            return;
        
        float aSpeed = paddleController.getSpeed() < speed ? paddleController.getSpeed() : speed;

        if (lastTick > 0) {
            long difference = System.currentTimeMillis() - lastTick;
            lastTick = System.currentTimeMillis();

            if (paddleController.getMovement() == PaddleController.Controls.LEFT)
                bounds.offset((-aSpeed * difference) / 1000f, 0);
            else if (paddleController.getMovement() == PaddleController.Controls.RIGHT)
                bounds.offset((aSpeed * difference) / 1000f, 0);
        } else {
            lastTick = System.currentTimeMillis();
        }

        if (bounds.left < min)
            bounds.offsetTo(min, bounds.top);
        else if (bounds.right > max)
            bounds.offsetTo(max - bounds.width(), bounds.top);
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