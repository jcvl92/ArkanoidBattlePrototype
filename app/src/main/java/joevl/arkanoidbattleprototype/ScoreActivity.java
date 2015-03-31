package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ScoreActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        ListView lv = (ListView)findViewById(R.id.scoresListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.score_list_item, MainMenuActivity.scores);
        lv.setAdapter(adapter);
    }
}