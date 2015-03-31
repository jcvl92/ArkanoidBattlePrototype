package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GameOverActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        //fill in text view
        String result = getIntent().getStringExtra("result");
        TextView tv = (TextView)findViewById(R.id.gameOverStatus);
        tv.setText(result);
    }
}