package com.fatless.fatless;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterTest extends AppCompatActivity {

    @BindView(R2.id.imageView2)
    ImageView gardientBackground;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_test);
        ButterKnife.bind(this);

        gardientBackground.setBackgroundResource(R.drawable.animation_list);

        AnimationDrawable anim = (AnimationDrawable) gardientBackground.getBackground();

        anim.start();

    }

/*

anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);

    @Override
    protected void onResume(){
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }
*/

}
