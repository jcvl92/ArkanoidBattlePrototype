package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Paint;

import java.io.IOException;
import java.io.Serializable;

public class SerialPaint extends Paint implements Serializable {
    private int flags, shaderShadowColor;
    float shaderRadius, shaderDx, shaderDy;

    public SerialPaint(int flags)
    {
        this.flags = flags;
    }

    @Override
    public void setShadowLayer(float radius, float dx, float dy, int shadowColor) {
        super.setShadowLayer(radius, dx, dy, shadowColor);
        shaderRadius = radius;
        shaderDx = dx;
        shaderDy = dy;
        shaderShadowColor = shadowColor;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(flags);
        out.writeFloat(shaderRadius);
        out.writeFloat(shaderDx);
        out.writeFloat(shaderDy);
        out.writeInt(shaderShadowColor);
        out.writeInt(getColor());
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        setFlags(in.readInt());
        setShadowLayer(
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                in.readInt()
        );
        setColor(in.readInt());
    }
}
