package joevl.arkanoidbattleprototype;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import joevl.arkanoidbattleprototype.game_engine.GameEngine;

public class GameView extends View {
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

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void close() {
        gameEngine.close();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        try{
            gameEngine.draw(canvas);
        }catch(NullPointerException npe){}

        invalidate();
    }

    public void onGameOver() {
        ((GameActivity) getContext()).onGameOver(gameEngine.getDescription(), gameEngine.getStatus(), gameEngine.getScore());
    }
}