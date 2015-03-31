package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchPaddleController implements OnTouchListener, PaddleController
{
    private RectF leftArea, rightArea;
    private Controls current = Controls.NONE;

    public TouchPaddleController(RectF left, RectF right)
    {
        leftArea = left;
        rightArea = right;
    }

    public Controls getMovement()
    {
        return current;
    }

    public boolean onTouch(View v, MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if(leftArea.contains(event.getX(), event.getY())) {
                current = Controls.LEFT;
                return true;
            } else if(rightArea.contains(event.getX(), event.getY())) {
                current = Controls.RIGHT;
                return true;
            }
        }
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            current = Controls.NONE;
            return true;
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE)
        {
            if(leftArea.contains(event.getX(), event.getY())) {
                current = Controls.LEFT;
                return true;
            } else if(rightArea.contains(event.getX(), event.getY())) {
                current = Controls.RIGHT;
                return true;
            } else {
                current = Controls.NONE;
                return true;
            }
        }
        return false;
    }
}