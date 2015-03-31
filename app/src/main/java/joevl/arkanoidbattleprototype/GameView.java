package joevl.arkanoidbattleprototype;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import joevl.arkanoidbattleprototype.game_engine.GameEngine;
import joevl.arkanoidbattleprototype.game_engine.GameShape;

public class GameView extends View
{
    GameEngine gameEngine;
    public RectF bounds = new RectF();

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        synchronized (bounds) {
            bounds.set(0, 0, w, h);
            bounds.notify();
        }
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        /* TODO: this can be optimized http://developer.android.com/training/custom-views/optimizing-view.html
           to optimize, you can just take the union of the rectangles of previous locations of objects,
           and new locations of objects, all union-ed */
        super.onDraw(canvas);

        if(gameEngine == null)
            gameEngine = ((GameActivity)getContext()).gameEngine;

        //draw the objects on the screen
        synchronized(gameEngine.getGameShapes()) {
            for (ArrayList<GameShape> gameShapes : gameEngine.getGameShapes().values())
                for (GameShape gameShape : gameShapes)
                    gameShape.draw(canvas);
        }
    }

    public void onGameOver() {
        ((GameActivity)getContext()).onGameOver(gameEngine.getDescription(), gameEngine.getStatus(), gameEngine.getScore());
    }
}