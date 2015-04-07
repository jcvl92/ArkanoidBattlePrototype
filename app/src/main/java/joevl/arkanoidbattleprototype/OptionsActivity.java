package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class OptionsActivity extends Activity {
    AudioManager audioManager;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainMenuActivity.mediaPlayer.start();

        setContentView(R.layout.activity_options);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        SeekBar volControl = (SeekBar)findViewById(R.id.volbar);
        volControl.setMax(maxVolume);
        volControl.setProgress(curVolume);
        volControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
            }
        });

        textView = (TextView) findViewById(R.id.MusicCheckBox);
        Typeface musicFont = Typeface.createFromAsset(getAssets(),"fonts/dfont.TTF");
        textView.setTypeface(musicFont);

        textView = (TextView) findViewById(R.id.SoundEffectBox);
        Typeface soundEffectFont = Typeface.createFromAsset(getAssets(),"fonts/dfont.TTF");
        textView.setTypeface(soundEffectFont);
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