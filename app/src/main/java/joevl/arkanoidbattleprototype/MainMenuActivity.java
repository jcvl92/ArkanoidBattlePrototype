package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainMenuActivity extends Activity {
    static ArrayList<String> scores;
    static final String scoresFileName = "ArkanoidScores.dat";
    TextView textView;
    static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        scores = readScores();

        mediaPlayer = MediaPlayer.create(MainMenuActivity.this, R.raw.spacesound2);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        textView = (TextView) findViewById(R.id.playGameButton);
        Typeface playDigitFont = Typeface.createFromAsset(getAssets(), "fonts/dfont.TTF");
        textView.setTypeface(playDigitFont);

        textView = (TextView) findViewById(R.id.scoreButton);
        Typeface scoreDigitFont = Typeface.createFromAsset(getAssets(), "fonts/dfont.TTF");
        textView.setTypeface(scoreDigitFont);

        textView = (TextView) findViewById(R.id.optionButton);
        Typeface optionDigitFont = Typeface.createFromAsset(getAssets(), "fonts/dfont.TTF");
        textView.setTypeface(optionDigitFont);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0 && resultCode == RESULT_OK) {
            String res = data.getStringExtra("mode") + "\t-\t" + data.getStringExtra("score");
            scores.add(res);
            writeScores();
            gameOver(data.getStringExtra("status"));
        }
    }

    private void writeScores() {
        try {
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(openFileOutput(scoresFileName, Context.MODE_PRIVATE));
                out.writeObject(scores);
            } finally {
                if (out != null)
                    out.close();
            }
        } catch (IOException ioe) {
        }
    }

    private void deleteScores() {
        getApplicationContext().deleteFile(scoresFileName);
    }

    private ArrayList<String> readScores() {
        ArrayList<String> obj;

        try {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(openFileInput(scoresFileName));
                obj = (ArrayList<String>) in.readObject();
            } finally {
                if (in != null)
                    in.close();
            }
        } catch (Exception e) {
            obj = new ArrayList<String>();
        }

        return obj;
    }

    public void scores(View view) {
        startActivity(new Intent(this, ScoreActivity.class));
    }

    public void play(View view) {
        startActivityForResult(new Intent(this, GameActivity.class), 0);
    }

    public void options(View view) {
        startActivity(new Intent(this, OptionsActivity.class));
    }

    public void gameOver(String result) {
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("result", result);
        startActivity(intent);
    }
}