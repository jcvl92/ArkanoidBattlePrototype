package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainMenuActivity extends Activity {
    static ArrayList<String> scores = new ArrayList<String>();//TODO: make this a global thing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        scores.add("test1 - test2");
        scores.add("test3 - test4");
        scores.add("test5 - test6");
        scores.add("test7 - test8");
        scores.add("test9 - test10");
        scores.add("test11 - test12");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0 && resultCode == RESULT_OK) {
            String res = data.getStringExtra("mode")+" - "+data.getStringExtra("score");
            scores.add(res);
            gameOver(data.getStringExtra("status"));
        }
    }

    public void scores(View view)
    {
        startActivity(new Intent(this, ScoreActivity.class));
    }

    public void play(View view)
    {
        startActivityForResult(new Intent(this, GameActivity.class), 0);
    }

    public void options(View view)
    {
        startActivity(new Intent(this, OptionsActivity.class));
    }

    public void gameOver(String result) {
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("result", result);
        startActivity(intent);
    }
}