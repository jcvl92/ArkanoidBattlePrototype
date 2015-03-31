package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ScoreActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        ScrollView sv = (ScrollView)findViewById(R.id.scoresScrollView);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);

        for(String score : MainMenuActivity.scores) {
            TextView tv = new TextView(this);
            tv.setText(score);
            tv.setTextColor(Color.RED);
            ll.addView(tv);
        }
    }
}