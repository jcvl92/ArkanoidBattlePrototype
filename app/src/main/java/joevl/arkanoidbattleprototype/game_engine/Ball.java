package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Ball extends GameShape
{
    private double speed=5, angle=-45;
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
        //calculate new trajectory and direction and adjust
        //find the side it hit the object on

        //reflect angle based on... things
        angle = (angle+90)%360;
    }

    public void bounceOff(RectF rect)
    {
        //bounce off of two sides

        //bounce off of one side
        angle = (angle+90)%360;
    }

    public void advance()
    {
        //move the ball along the trajectory
        float x = (float)(speed*Math.sin(Math.toRadians(angle))),
                y = -(float)(speed*Math.cos(Math.toRadians(angle)));
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