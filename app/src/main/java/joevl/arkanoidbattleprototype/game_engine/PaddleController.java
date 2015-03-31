package joevl.arkanoidbattleprototype.game_engine;

public interface PaddleController
{
    enum Controls{
        LEFT,
        RIGHT,
        NONE
    }
    public Controls getMovement();
    public float getSpeed();
}
