package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainMenuActivity.musicPlayer.start();

        setContentView(R.layout.game_over);

        //fill in text view
        String result = getIntent().getStringExtra("result");
        TextView tv = (TextView) findViewById(R.id.gameOverStatus);
        tv.setText(result);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainMenuActivity.musicPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainMenuActivity.musicPlayer.start();
    }

    public void finishActivity(View view) {
        finish();
    }
}