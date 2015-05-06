package joevl.arkanoidbattleprototype.game_engine;

import android.graphics.Paint;

import java.io.IOException;
import java.io.Serializable;

public class SerialPaint extends Paint implements Serializable {

    public SerialPaint() {
        super(Paint.ANTI_ALIAS_FLAG);
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(getColor());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        setColor(in.readInt());
    }
}
