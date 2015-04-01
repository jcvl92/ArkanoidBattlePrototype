package joevl.arkanoidbattleprototype.game_modes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import joevl.arkanoidbattleprototype.game_engine.Ball;
import joevl.arkanoidbattleprototype.game_engine.Brick;

public class CollisionTester extends View
{
    Brick brick;
    Ball ball;
    Paint greenPaint = new Paint(), redPaint = new Paint(), bluePaint = new Paint(), textPaint = new Paint();

    public CollisionTester(Context context, AttributeSet attrs) {
        super(context, attrs);

        greenPaint.setColor(Color.GREEN);
        redPaint.setColor(Color.RED);
        bluePaint.setColor(Color.BLUE);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);

        brick = new Brick(300, 300, 450, 700, greenPaint);
        ball = new Ball(300, 300, 0, 0, bluePaint);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE ||event.getAction() == MotionEvent.ACTION_DOWN) {
                    RectF b = ball.getBounds();
                    b.offsetTo(event.getX() - ball.getBounds().width() / 2, event.getY() - ball.getBounds().width() / 2);
                    ball = new Ball(300, 300, b.left, b.top, bluePaint);

                    if(ball.collides(brick)) {
                        brick.paint = redPaint;
                        ball.bounceOff(brick);
                    }
                    else
                        brick.paint = greenPaint;

                    postInvalidate();
                    return true;
                }
                else return false;
            }
        });
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        //draw a rectangle in the middle of the screen
        //it will be green unless it overlaps the circle,
        //in which case it will turn red until the overlap stops
        brick.draw(canvas);
        ball.draw(canvas);
    }
}