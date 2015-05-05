package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class OptionsActivity extends Activity {
    AudioManager audioManager;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainMenuActivity.musicPlayer.start();

        setContentView(R.layout.activity_options);

        final SeekBar musicVolume = (SeekBar) findViewById(R.id.musicVolumeBar);
        musicVolume.setMax(10);
        musicVolume.setProgress(10);//TODO: persist this
        musicVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {}
            @Override
            public void onStartTrackingTouch(SeekBar arg0) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MainMenuActivity.musicPlayer.setVolume(progress / 10f, progress / 10f);
            }
        });

        final SeekBar SFXVolume = (SeekBar) findViewById(R.id.SFXVolumeBar);
        SFXVolume.setMax(10);
        SFXVolume.setProgress(10);//TODO: persist this
        SFXVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {}
            @Override
            public void onStartTrackingTouch(SeekBar arg0) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MainMenuActivity.SFXVolume = progress/10f;
            }
        });

        textView = (TextView) findViewById(R.id.MusicCheckBox);
        Typeface musicFont = Typeface.createFromAsset(getAssets(), "fonts/dfont.TTF");
        textView.setTypeface(musicFont);

        textView = (TextView) findViewById(R.id.SoundEffectBox);
        Typeface soundEffectFont = Typeface.createFromAsset(getAssets(), "fonts/dfont.TTF");
        textView.setTypeface(soundEffectFont);
    }

    public void clearScores(View view) {
        final ProgressDialog pd = ProgressDialog.show(this, "Please wait.", "Clearing score data.", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainMenuActivity.deleteScores();

                //sleep for a bit so the user can actually see the dialog
                try{Thread.sleep(1000);}catch(Exception e){}

                pd.dismiss();
            }
        }).start();
    }

    public void finishActivity(View view) {
        finish();
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
}