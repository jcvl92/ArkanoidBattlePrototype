package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import joevl.arkanoidbattleprototype.game_engine.GameEngine;
import joevl.arkanoidbattleprototype.game_modes.VersusGame;

public class GameActivity extends Activity {
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainMenuActivity.musicPlayer.start();

        setContentView(R.layout.activity_game);

        gameView = (GameView) findViewById(R.id.gameView);

        //create the game engine
        gameView.setGameEngine(gameModeFactory());
    }

    @Override
    public void onDestroy() {
        gameView.close();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainMenuActivity.musicPlayer.pause();
        gameView.gameEngine.paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainMenuActivity.musicPlayer.start();
        gameView.gameEngine.paused = false;
    }

    protected GameEngine gameModeFactory() {
        return new VersusGame(gameView);
    }

    final public void onGameOver(String mode, String status, String score) {
        Intent result = new Intent();
        result.putExtra("mode", mode);
        result.putExtra("status", status);
        result.putExtra("score", score);
        setResult(RESULT_OK, result);
        finish();
    }
}