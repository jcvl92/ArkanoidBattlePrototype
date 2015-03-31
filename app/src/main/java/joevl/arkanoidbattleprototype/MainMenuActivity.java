package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainMenuActivity extends Activity {
    AnimationDrawable spaceAnimation;
    ArrayList<String> scores = new ArrayList<String>();//TODO: make this a global thing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ImageView space = (ImageView) findViewById(R.id.imageAnimation);
        space.setBackgroundResource(R.drawable.animatedspace);
        spaceAnimation = (AnimationDrawable) space.getBackground();
        spaceAnimation.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0 && resultCode == RESULT_OK) {
            String res = data.getStringExtra("mode")+", "+data.getStringExtra("status")+", "+data.getStringExtra("score");
            scores.add(res);
        }
    }

    public void scores(View view)
    {
        startActivity(new Intent(this, ScoreActivity.class));
    }

    public void play(View view)
    {
        //startActivityForResult(new Intent(this, GameActivity.class), 0);
        startActivityForResult(new Intent(this, GameActivity.class), 0);
    }

    public void options(View view)
    {
        startActivity(new Intent(this, OptionsActivity.class));
    }
}