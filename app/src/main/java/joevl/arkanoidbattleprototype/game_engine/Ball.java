package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import java.io.IOException;

public class Ball extends GameShape
{
    private double speed=30, angle=-45;
    private float lastX, lastY;
    public Ball(float height, float width, float x, float y, Paint paint)
    {
        super(paint);
        bounds = new RectF(x, y, x+width, y+height);
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeFloat(bounds.left);
        out.writeFloat(bounds.top);
        out.writeFloat(bounds.right);
        out.writeFloat(bounds.bottom);
        out.writeObject(paint);

        out.writeDouble(speed);
        out.writeDouble(angle);
        out.writeFloat(lastX);
        out.writeFloat(lastY);
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        bounds = new RectF(
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                in.readFloat()
        );
        paint = (Paint)in.readObject();

        speed = in.readDouble();
        angle = in.readDouble();
        lastX = in.readFloat();
        lastY = in.readFloat();
    }

    public void move(float dx, float dy)
    {
        bounds.offset(dx, dy);
    }

    public boolean collides(GameShape gameShape)
    {
        //TODO: implement circle on circle collision
        if(RectF.intersects(bounds, gameShape.getBounds()))
        {
            //if the bounding box of the circle does not hang over the edge of the gameShape's
            //rectangle by more than half the width of the circle
            float circleX = bounds.centerX(), circleY = bounds.centerY();
            if(circleX > gameShape.getBounds().left && circleX < gameShape.getBounds().right)
                return true;

            if(circleY > gameShape.getBounds().top && circleY < gameShape.getBounds().bottom)
                return true;

            //otherwise, we are intersecting with a corner of the rectangle and it is possible that
            //the circle does not touch any of the gameShape's rectangle edges

            //so, go through all corners of the box and see if they are close enough to the center
            //of the circle to constitute intersection

            //top left
            if(contains(gameShape.getBounds().left, gameShape.getBounds().top))
                return true;

            //top right
            if(contains(gameShape.getBounds().right, gameShape.getBounds().top))
                return true;

            //bottom left
            if(contains(gameShape.getBounds().left, gameShape.getBounds().bottom))
                return true;

            //bottom right
            if(contains(gameShape.getBounds().right, gameShape.getBounds().bottom))
                return true;
        }

        return false;
    }

    public boolean contains(float x, float y) {
        float circleX = bounds.centerX(), circleY = bounds.centerY(), circleRadius = bounds.width()/2;

        float dist = (float)Math.sqrt(
                Math.pow((circleX-x), 2) +
                        Math.pow((circleY-y), 2));

        if(dist <= circleRadius)
            return true;
        else
            return false;
    }

    public void bounceOff(GameShape gameShape)
    {
        //the bounce angle off of a paddle is equal to the angel between the center of the paddle and the center of the ball
        if(gameShape.getClass() == Paddle.class)//bouncing off of paddles
        {
            angle = Math.toDegrees(Math.atan2(bounds.centerX() - gameShape.getBounds().centerX(), gameShape.getBounds().centerY() - bounds.centerY()));
            reAdvance();
        }
        else if(gameShape.getClass() == Ball.class)//bouncing off of balls
        {
            angle = Math.toDegrees(Math.atan2(bounds.centerX() - gameShape.getBounds().centerX(), gameShape.getBounds().centerY() - bounds.centerY()));
            reAdvance();
        }
        else//bouncing off of other rectangles
            bounceOff(gameShape.getBounds());
    }

    public void bounceOff(RectF rect)
    {
        //TODO: this doesn't work. fix it
        //the smallest difference between corresponding x and corresponding y values(of the ball and the rect) determines which side was hit
        float yDist = Math.abs(rect.top - bounds.centerY());
        if(Math.abs(rect.bottom - bounds.centerY()) < yDist)
            yDist = Math.abs(rect.bottom - bounds.centerY());

        float xDist = Math.abs(rect.left - bounds.centerX());
        if(Math.abs(rect.right - bounds.centerY()) < xDist)
            xDist = Math.abs(rect.right - bounds.centerX());

        if(xDist < yDist) {//if hit one of the vertical sides
            double lastAngle = angle;
            angle = (0 - angle) % (360);//flip the angle across the y axis
            Log.v("angles", "bouncing off of top or bottom, angles: "+lastAngle+"->"+angle);
        }
        else {//if hit one of the horizontal sides
            double lastAngle = angle;
            angle = (180 - angle) % (360);//flip the angle across the x axis
            Log.v("angles", "bouncing off of left or right, angles: "+lastAngle+"->"+angle);
        }

        reAdvance();
    }

    private void reAdvance()
    {
        //undo the last advancement
        bounds.offset(-lastX, -lastY);
        advance();
    }

    public void advance()
    {
        //move the ball along the trajectory
        float x = (float)(speed*Math.sin(Math.toRadians(angle))),
                y = -(float)(speed*Math.cos(Math.toRadians(angle)));
        lastX = x;
        lastY = y;
        bounds.offset(x, y);

        paint.setShader(new RadialGradient(bounds.centerX(), bounds.centerY(),
                bounds.height() / 2, Color.RED, Color.GREEN, Shader.TileMode.CLAMP));
    }

    public void multSpeed(double ds)
    {
        //cap the speed
        if(speed*ds < bounds.width())
            speed *= ds;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawOval(bounds, paint);
    }
}