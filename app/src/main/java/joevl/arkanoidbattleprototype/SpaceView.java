package joevl.arkanoidbattleprototype;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class SpaceView extends View {
    ArrayList<Star> stars = new ArrayList<Star>();
    private static final long refreshTime = (long)((1.0/30) * 1000);//30 Hz - milliseconds
    boolean closing = false, ready = false;
    Thread ticker = null;
    RectF viewBounds = new RectF();
    Paint starPaint;
    final int numberOfStars=100, varianceDiameter=300, starStartSize=-2, initialSkip=500;
    final float initialStarSpeed=0, initialStarAcceleration=0, initialStarGrowth=0.07f, starGrowthRate=0.004f, starAcceleration=0.025f;

    public SpaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void init() {
        starPaint = new Paint();
        starPaint.setColor(Color.WHITE);

        ticker = new Thread(new Runnable() {
            public void run() {
                synchronized(viewBounds) {
                    try {
                        while(viewBounds.isEmpty())
                            viewBounds.wait();
                    } catch(InterruptedException ie) {}
                }

                for(int i=0; i<numberOfStars; ++i) {
                    float x = (float)((Math.random()-.5)*varianceDiameter), y = (float)((Math.random()-.5)*varianceDiameter);
                    stars.add(new Star(x + viewBounds.centerX(), y + viewBounds.centerY()));
                }

                //stars are ready to be drawn now
                ready = true;

                //this is just to avoid the initial "explosions"
                for(int i=0; i<initialSkip; ++i)
                {
                    tick();
                }

                while(!closing) {
                    long time = System.nanoTime();
                    //tick
                    tick();
                    postInvalidate();
                    //wait for remaining amount of time
                    try {
                        long sleepTime = refreshTime - (System.nanoTime()-time)/1000000;
                        if(sleepTime > 0) {
                            Thread.sleep(sleepTime);
                        }
                    } catch (InterruptedException ie) {}
                }
            }
        });
        ticker.start();
    }

    public void close() {
        closing = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        synchronized (viewBounds) {
            viewBounds.set(0, 0, w, h);
            viewBounds.notify();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(ready) {
            for (Star star : stars)
                star.draw(canvas);
        }
    }

    private void tick() {
        for(Star star : stars) {
            star.advance();
            if(star.isOffScreen()) {
                float x = (float) ((Math.random() - .5) * varianceDiameter), y = (float) ((Math.random() - .5) * varianceDiameter);
                star.set(x + viewBounds.centerX(), y + viewBounds.centerY());
            }
        }
    }

    class Star {
        RectF starBounds;
        float speed = initialStarSpeed, acceleration = initialStarAcceleration, growthRate = initialStarGrowth;

        Star(float x, float y) {
            starBounds = new RectF(x-starStartSize, y-starStartSize, x+starStartSize, y+starStartSize);
        }

        public void set(float x, float y) {
            starBounds.set(x-starStartSize, y-starStartSize, x+starStartSize, y+starStartSize);
            speed=initialStarSpeed;
            acceleration=initialStarAcceleration;
            growthRate= initialStarGrowth;
        }

        public void advance() {
            double angle = Math.atan2(starBounds.centerX() - viewBounds.centerX(), viewBounds.centerY() - starBounds.centerY());
            float x = (float)(speed*Math.sin(angle)),
                    y = -(float)(speed*Math.cos(angle));

            starBounds.offset(x, y);

            speed += (acceleration += starAcceleration);

            growthRate += starGrowthRate;

            starBounds.set(starBounds.left - growthRate,
                    starBounds.top - growthRate,
                    starBounds.right + growthRate,
                    starBounds.bottom + growthRate);
        }

        public boolean isOffScreen() {
            return !viewBounds.contains(starBounds) && !RectF.intersects(viewBounds, starBounds);
        }

        public void draw(Canvas canvas) {
            canvas.drawOval(starBounds, starPaint);
        }
    }
}
