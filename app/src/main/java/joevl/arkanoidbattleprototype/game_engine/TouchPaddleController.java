package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class TouchPaddleController implements PaddleController
{
    private RectF leftArea, rightArea;
    private Controls current = Controls.NONE;
    private static final ArrayList<TouchPaddleController> tpcs = new ArrayList<TouchPaddleController>();
    public static final View.OnTouchListener listener = new View.OnTouchListener(){
        public boolean onTouch(View v, MotionEvent event)
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                for(TouchPaddleController tpc : tpcs)
                {
                    if (tpc.leftArea.contains(event.getX(), event.getY())) {
                        tpc.current = Controls.LEFT;
                        return true;
                    } else if (tpc.rightArea.contains(event.getX(), event.getY())) {
                        tpc.current = Controls.RIGHT;
                        return true;
                    }
                }
            }
            else if(event.getAction() == MotionEvent.ACTION_UP)
            {
                for(TouchPaddleController tpc : tpcs)
                {
                    tpc.current = Controls.NONE;
                }
                return true;
            }
            return false;
        }
    };
    public TouchPaddleController(RectF left, RectF right)
    {
        leftArea = left;
        rightArea = right;
        tpcs.add(this);
    }
    public Controls getMovement()
    {
        return current;
    }
}