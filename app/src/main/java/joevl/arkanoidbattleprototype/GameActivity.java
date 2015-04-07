package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import joevl.arkanoidbattleprototype.game_engine.GameEngine;
import joevl.arkanoidbattleprototype.game_modes.VersusGame;
import joevl.arkanoidbattleprototype.physics_test_environments.BounceTester;

public class GameActivity extends Activity {
    public GameEngine gameEngine;
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainMenuActivity.mediaPlayer.start();

        setContentView(R.layout.activity_game);

        gameView = (GameView)findViewById(R.id.gameView);

        //spawn the game engine
        gameEngine = new VersusGame(gameView);
    }

    @Override
    public void onDestroy() {
        gameEngine.close();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainMenuActivity.mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainMenuActivity.mediaPlayer.start();
    } 

    public void onGameOver(String mode, String status, String score) {
        Intent result = new Intent();
        result.putExtra("mode", mode);
        result.putExtra("status", status);
        result.putExtra("score", score);
        setResult(RESULT_OK, result);
        finish();
    }
}