package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class Ball extends GameShape
{
    private double speed=10, angle=Math.toRadians(-45);
    public Ball(float height, float width, float x, float y, Paint paint)
    {
        super(paint);
        bounds = new RectF(x, y, x+width, y+height);
    }

    public void move(float dx, float dy)
    {
        bounds.offset(dx, dy);
    }

    public boolean collides(GameShape gameShape)
    {
        //TODO: implement proper oval collision
        return RectF.intersects(bounds, gameShape.getBounds());
    }

    public void bounceOff(GameShape gameShape)
    {
        //the bounce angle off of a paddle is equal to the angel between the center of the paddle and the center of the ball
        if(gameShape.getClass() == Paddle.class)//bouncing off of paddles
            angle = Math.atan2(bounds.centerX()-gameShape.getBounds().centerX(), bounds.centerY()-gameShape.getBounds().centerY());
        else if(gameShape.getClass() == Ball.class)//bouncing off of balls
            angle = Math.atan2(bounds.centerX()-gameShape.getBounds().centerX(), bounds.centerY()-gameShape.getBounds().centerY());
        else//bouncing off of other rectangles
            bounceOff(gameShape.getBounds());
    }

    public void bounceOff(RectF rect)
    {
        //TODO: corner hits should probably not "hard" bounce by reflecting, they should be reflected based on where on the circle they hit(angle within circle will give some sort of transformation)
        //eh, only do this^ if game testing shows we need it

        //the smallest difference between corresponding x and corresponding y values(of the ball and the rect) determines which side was hit
        float yDist = Math.abs(rect.top - bounds.centerY());
        if(Math.abs(rect.bottom - bounds.centerY()) < yDist)
            yDist = Math.abs(rect.bottom - bounds.centerY());
        float xDist = Math.abs(rect.left - bounds.centerX());
        if(Math.abs(rect.right - bounds.centerY()) < xDist)
            xDist = Math.abs(rect.right - bounds.centerX());

        //TODO: any problems here are likely due to the vertical flipping not being constrained to [-pi, pi]

        if(xDist < yDist)//if hit one of the vertical sides
        {
            Log.v("bouncer", "bouncing across |, previous angle:"+Math.toDegrees(angle)+", new angle:"+(Math.PI - angle) % (2 * Math.PI));
            angle = (Math.PI - angle) % (2 * Math.PI);//flip the angle across the y axis
        }
        else//if hit one of the horizontal sides
        {
            Log.v("bouncer", "bouncing across -, previous angle:"+Math.toDegrees(angle)+", new angle:"+(Math.PI - angle) % (2 * Math.PI));
            angle = (-Math.PI - angle) % (2 * Math.PI);//flip the angle across the x axis
        }
    }

    public void advance()
    {
        //move the ball along the trajectory
        float x = (float)(speed*Math.sin(angle)),
                y = -(float)(speed*Math.cos(angle));
        bounds.offset(x, y);
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