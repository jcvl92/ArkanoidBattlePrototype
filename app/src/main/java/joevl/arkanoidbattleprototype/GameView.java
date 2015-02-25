package joevl.arkanoidbattleprototype;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import joevl.arkanoidbattleprototype.game_engine.GameEngine;
import joevl.arkanoidbattleprototype.game_engine.GameShape;
import joevl.arkanoidbattleprototype.game_modes.VersusGame;

public class GameView extends View
{
    GameEngine gameEngine;
    public int width = 1000, height = 1630;//TODO: compute these instead of guessing

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
        //to optimize, you can just take the union of the rectangles of previous locations of objects, and new locations of objects, all union-ed
        super.onDraw(canvas);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(3);
        p.setColor(Color.RED);
        canvas.drawRect(new RectF(0, 1500, 200, 1700), p);
        p.setColor(Color.BLUE);
        canvas.drawRect(new RectF(800, 1500, 1000, 1700), p);

        //draw the objects on the screen
        for(ArrayList<GameShape> gameShapes : gameEngine.getGameShapes())
                for (GameShape gameShape : gameShapes)
                    gameShape.draw(canvas);
    }
}