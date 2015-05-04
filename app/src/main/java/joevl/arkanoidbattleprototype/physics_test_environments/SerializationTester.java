package joevl.arkanoidbattleprototype.physics_test_environments;

import joevl.arkanoidbattleprototype.GameView;
import joevl.arkanoidbattleprototype.game_modes.VersusGame;

public class SerializationTester extends VersusGame {
    byte[] state = null;
    int counter = 0;

    public SerializationTester(GameView gameView) {
        super(gameView);
    }

    @Override
    protected void reset() {
        super.reset();
        state = null;
    }

    @Override
    protected void tick() {
        super.tick();

        if (++counter == 100) {
            counter = 0;
            if (state != null) {
                setSerializedState(state);
            }
            state = getSerializedState();
        }
    }
}