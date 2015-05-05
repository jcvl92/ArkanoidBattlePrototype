package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

public class MainMenuActivity extends Activity {
    static ArrayList<String> scores;
    static final String scoresFileName = "ArkanoidScores.dat";
    private static Context context;
    TextView textView;
    static MediaPlayer musicPlayer;
    static SoundPool SFXPlayer;
    public static int PADDLE_SFX_ID, BRICK_SFX_ID, SCORE_SFX_ID, BEGIN_SFX_ID;
    static float SFXVolume = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        context = getApplicationContext();

        scores = readScores();

        //initialize and start the music player
        musicPlayer = MediaPlayer.create(MainMenuActivity.this, R.raw.space_music);
        musicPlayer.setLooping(true);
        musicPlayer.start();

        //initialize the sound effects sound pool and fill it with sounds
        SFXPlayer = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        BEGIN_SFX_ID = SFXPlayer.load(this, R.raw.begin_sound, 1);
        BRICK_SFX_ID = SFXPlayer.load(this, R.raw.brick_hit, 1);
        PADDLE_SFX_ID = SFXPlayer.load(this, R.raw.paddle_hit, 1);
        SCORE_SFX_ID = SFXPlayer.load(this, R.raw.score_sound, 1);

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

    public static void playSoundEffect(int soundID) {
        SFXPlayer.play(soundID, SFXVolume, SFXVolume, 1, 0, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicPlayer.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        musicPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicPlayer.start();
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

    public static void deleteScores() {
        context.deleteFile(scoresFileName);
        scores = new ArrayList<String>();
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