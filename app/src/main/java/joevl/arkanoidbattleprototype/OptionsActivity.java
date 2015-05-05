package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class OptionsActivity extends Activity {
    boolean vibrateOn;
    int mVolume, sVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainMenuActivity.musicPlayer.start();

        setContentView(R.layout.activity_options);

        readOptions();

        final SeekBar musicVolume = (SeekBar) findViewById(R.id.MusicVolumeBar);
        musicVolume.setMax(10);
        musicVolume.setProgress(mVolume);
        musicVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                writeOptions();
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mVolume = progress;
                MainMenuActivity.musicPlayer.setVolume(progress / 10f, progress / 10f);
            }
        });

        final SeekBar SFXVolume = (SeekBar) findViewById(R.id.SFXVolumeBar);
        SFXVolume.setMax(10);
        SFXVolume.setProgress(sVolume);
        SFXVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                writeOptions();
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sVolume = progress;
                MainMenuActivity.SFXVolume = progress / 10f;
            }
        });
    }

    public void writeOptions() {
        try {
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(openFileOutput(MainMenuActivity.optionsFileName, Context.MODE_PRIVATE));
                out.writeInt(mVolume);
                out.writeInt(sVolume);
                out.writeBoolean(vibrateOn);
            } finally {
                if (out != null)
                    out.close();
            }
        } catch (IOException ioe) {
        }
    }

    public void readOptions() {
        try {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(openFileInput(MainMenuActivity.optionsFileName));
                mVolume = in.readInt();
                sVolume = in.readInt();
                vibrateOn = in.readBoolean();
            } finally {
                if (in != null)
                    in.close();
            }
        } catch (Exception e) {
            mVolume = 10;
            sVolume = 8;
            vibrateOn = true;
        }
    }

    public void clearScores(View view) {
        final ProgressDialog pd = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
        pd.setTitle("Please wait.");
        pd.setMessage("Clearing score data.");
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteFile(MainMenuActivity.scoresFileName);
                MainMenuActivity.scores = new ArrayList<String>();

                //sleep for a bit so the user can actually see the dialog
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }

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