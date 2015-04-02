package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Paint;

import java.io.IOException;
import java.io.Serializable;

public class SerialPaint extends Paint implements Serializable {
    private int shaderShadowColor;
    float shaderRadius, shaderDx, shaderDy;

    public SerialPaint(){}

    @Override
    public void setShadowLayer(float radius, float dx, float dy, int shadowColor) {
        super.setShadowLayer(radius, dx, dy, shadowColor);
        shaderRadius = radius;
        shaderDx = dx;
        shaderDy = dy;
        shaderShadowColor = shadowColor;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeFloat(shaderRadius);
        out.writeFloat(shaderDx);
        out.writeFloat(shaderDy);
        out.writeInt(shaderShadowColor);
        out.writeInt(getColor());
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        setShadowLayer(
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                in.readInt()
        );
        setColor(in.readInt());
    }
}
