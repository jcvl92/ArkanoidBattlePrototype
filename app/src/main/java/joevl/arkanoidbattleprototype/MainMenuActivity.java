package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainMenuActivity extends Activity {
    static ArrayList<String> scores = new ArrayList<String>();//TODO: make this a global thing
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        MediaPlayer mediaPlayer = MediaPlayer.create(MainMenuActivity.this, R.raw.spacesound);
        mediaPlayer.start();


        textView = (TextView) findViewById(R.id.playGameButton);
        Typeface playDigitFont = Typeface.createFromAsset(getAssets(),"fonts/dfont.TTF");
        textView.setTypeface(playDigitFont);

        textView = (TextView) findViewById(R.id.scoreButton);
        Typeface scoreDigitFont = Typeface.createFromAsset(getAssets(),"fonts/dfont.TTF");
        textView.setTypeface(scoreDigitFont);

        textView = (TextView) findViewById(R.id.optionButton);
        Typeface optionDigitFont = Typeface.createFromAsset(getAssets(),"fonts/dfont.TTF");
        textView.setTypeface(optionDigitFont);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 0 && resultCode == RESULT_OK) {
            String res = data.getStringExtra("mode")+"\t-\t"+data.getStringExtra("score");
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