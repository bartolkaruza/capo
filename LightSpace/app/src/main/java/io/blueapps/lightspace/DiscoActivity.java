package io.blueapps.lightspace;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by klm37586 on 9/21/2014.
 */
public class DiscoActivity extends ColorActivity {
    @InjectView(R.id.gameView)
    protected View gameView;
    Handler colorChanger = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bridgelistlinear);
        ButterKnife.inject(this);

        Runnable changeColorRunnable = new Runnable() {
            @Override
            public void run() {
                Random rnd = new Random();
                int color1 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                int color2 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                int color3 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                gameView.setBackgroundColor(color1);
                setHueDiscoColor(color1, color2, color3);
                colorChanger.postDelayed(this, 500);
            }
        };

        colorChanger.postDelayed(changeColorRunnable, 100);

    }
}
