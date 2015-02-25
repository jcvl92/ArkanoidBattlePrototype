package joevl.arkanoidbattleprototype;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class GameView extends View
{
    GameEngine gameEngine;
    int width = 1000, height = 1630;//TODO: compute these instead of guessing

    public GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        //spawn the game engine
        gameEngine = new VersusGame(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        //TODO: this can be optimized http://developer.android.com/training/custom-views/optimizing-view.html
        super.onDraw(canvas);

        //draw the objects on the screen
        for(ArrayList<GameShape> gameShapes : gameEngine.getGameShapes())
                for (GameShape gameShape : gameShapes)
                    gameShape.draw(canvas);
    }
}