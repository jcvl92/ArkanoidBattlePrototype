package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import java.io.IOException;

public class Ball extends GameShape {
    private double speed = 900, angle = 135, acceleration = 1.01;
    private long lastTick = -1;

    public Ball(float height, float width, float x, float y, Paint paint) {
        super(paint);
        bounds = new RectF(x, y, x + width, y + height);
        paint.setShader(new RadialGradient(bounds.centerX(), bounds.centerY(),
                bounds.height() / 2, Color.RED, Color.GREEN, Shader.TileMode.CLAMP));
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeFloat(bounds.left);
        out.writeFloat(bounds.top);
        out.writeFloat(bounds.right);
        out.writeFloat(bounds.bottom);
        out.writeObject(paint);

        out.writeDouble(speed);
        out.writeDouble(angle);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        bounds = new RectF(
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                in.readFloat()
        );
        paint = (Paint) in.readObject();

        speed = in.readDouble();
        angle = in.readDouble();
    }

    public boolean collides(GameShape gameShape) {
        //TODO: implement circle on circle collision
        if (RectF.intersects(bounds, gameShape.getBounds())) {
            //if the bounding box of the circle does not hang over the edge of the gameShape's
            //rectangle by more than half the width of the circle
            float circleX = bounds.centerX(), circleY = bounds.centerY();
            if (circleX > gameShape.getBounds().left && circleX < gameShape.getBounds().right)
                return true;

            if (circleY > gameShape.getBounds().top && circleY < gameShape.getBounds().bottom)
                return true;

            //otherwise, we are intersecting with a corner of the rectangle and it is possible that
            //the circle does not touch any of the gameShape's rectangle edges

            //so, go through all corners of the box and see if they are close enough to the center
            //of the circle to constitute intersection

            //top left
            if (contains(gameShape.getBounds().left, gameShape.getBounds().top))
                return true;

            //top right
            if (contains(gameShape.getBounds().right, gameShape.getBounds().top))
                return true;

            //bottom left
            if (contains(gameShape.getBounds().left, gameShape.getBounds().bottom))
                return true;

            //bottom right
            if (contains(gameShape.getBounds().right, gameShape.getBounds().bottom))
                return true;
        }

        return false;
    }

    public boolean contains(float x, float y) {
        float circleX = bounds.centerX(), circleY = bounds.centerY(), circleRadius = bounds.width() / 2;

        float dist = (float) Math.sqrt(
                Math.pow((circleX - x), 2) +
                        Math.pow((circleY - y), 2));

        return dist <= circleRadius;
    }

    public void bounceOff(GameShape gameShape) {//the bounce angle off of a paddle is equal to the angel between the center of the paddle and the center of the ball
        if (gameShape.getClass() == Paddle.class) {//bouncing off of paddles
            setAngle(Math.toDegrees(Math.atan2(gameShape.getBounds().centerX() - bounds.centerX(), bounds.centerY() - gameShape.getBounds().centerY())));
        } else if (gameShape.getClass() == Ball.class) {//bouncing off of balls
            setAngle(Math.toDegrees(Math.atan2(gameShape.getBounds().centerX() - bounds.centerX(), bounds.centerY() - gameShape.getBounds().centerY())));
        } else {//bouncing off of other rectangles
            bounceOff(gameShape.getBounds());
        }
        //
        bounds.offsetTo(bounds.left, bounds.top);
    }

    public void bounceOff(RectF rect) {
        //get the angles
        double ballAngle, topRightAngle, topLeftAngle, bottomRightAngle, bottomLeftAngle;
        ballAngle = Math.toDegrees(Math.atan2(rect.centerY() - bounds.centerY(), bounds.centerX() - rect.centerX()));

        topRightAngle = Math.toDegrees(Math.atan2(rect.centerY() - rect.top, rect.centerX() - rect.left));
        topLeftAngle = Math.toDegrees(Math.atan2(rect.centerY() - rect.top, rect.centerX() - rect.right));
        bottomRightAngle = Math.toDegrees(Math.atan2(rect.centerY() - rect.bottom, rect.right - rect.centerX()));
        bottomLeftAngle = Math.toDegrees(Math.atan2(rect.centerY() - rect.bottom, rect.left - rect.centerX()));

        if ((ballAngle > topRightAngle && ballAngle < topLeftAngle) ||
                (ballAngle < bottomRightAngle && ballAngle > bottomLeftAngle)) {//if hit one of the horizontal sides
            flipHorizontal();//flip the angle across the x axis
        } else {//if hit one of the vertical sides
            flipVertical();//flip the angle across the y axis
        }
    }

    public void flipHorizontal() {
        setAngle(180 - angle);
    }

    public void flipVertical() {
        setAngle(0 - angle);
    }

    private void setAngle(double angle) {
        angle %= 360;

        if (angle < 0)
            angle += 360;

        this.angle = angle;
    }

    public void advance() {
        //move the ball along the trajectory
        float x = -(float) (Math.sin(Math.toRadians(angle))),
                y = (float) (Math.cos(Math.toRadians(angle)));
        move(x, y);

        paint.setShader(new RadialGradient(bounds.centerX(), bounds.centerY(),
                bounds.height() / 2, Color.RED, Color.GREEN, Shader.TileMode.CLAMP));
    }

    private void move(float x, float y) {
        if(lastTick>0) {
            long difference = System.currentTimeMillis()-lastTick;
            bounds.offset(
                    (float) ((speed*difference)/1000f) * x,
                    (float) ((speed*difference)/1000f) * y
            );

            //TODO: re-implement this
            //cap the speed
            /*double aDiff = (acceleration * difference) / 1000;
            if (speed * aDiff < bounds.width()*100)
                speed *= aDiff;*/
        }
        lastTick = System.currentTimeMillis();
    }

    public void draw(Canvas canvas) {
        canvas.drawOval(bounds, paint);
    }
}