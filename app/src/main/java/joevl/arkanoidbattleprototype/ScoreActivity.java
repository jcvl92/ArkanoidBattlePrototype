package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ScoreActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainMenuActivity.mediaPlayer.start();

        setContentView(R.layout.activity_scores);

        ListView lv = (ListView)findViewById(R.id.scoresListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.score_list_item, MainMenuActivity.scores);
        lv.setAdapter(adapter);
    }
    public void finishActivity(View view){
        finish();
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
}