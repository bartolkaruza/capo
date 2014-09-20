package ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.philips.lighting.quickstart.PHHomeActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.blueapps.lightspace.R;

public class CapoSplashActivity extends Activity {

    @InjectView(R.id.splashscreen_logo)
    View logo;


    @InjectView(R.id.menu_button_holder)
    View buttonHolder;

    View content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capo_splash);

        content = findViewById(android.R.id.content);

        ButterKnife.inject(this);
        logo.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                logo.getViewTreeObserver().removeOnPreDrawListener(this);
                logo.setAlpha(0);
                logo.animate().alpha(1).setDuration(2000).start();
                return true;
            }
        });
        doSomeInitialComputation();
    }

    private void doSomeInitialComputation()
    {

        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 0:
                        sendEmptyMessageDelayed(1,1000);
                        break;
                    case 1:
                        gameReady();
                        break;
                }
            }
        }.sendEmptyMessageDelayed(0,1000);
    }

    public void gameReady()
    {
        logo.animate().y(-logo.getHeight()/8);
        buttonHolder.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                buttonHolder.getViewTreeObserver().removeOnPreDrawListener(this);
                buttonHolder.setVisibility(View.VISIBLE);
                buttonHolder.setAlpha(0);
                buttonHolder.animate().alpha(1).setDuration(1000);
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.capo_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

     @OnClick(R.id.startgame_button)
      public void onStartGameClick(View v)
     {
         Intent inte = new Intent(this, PHHomeActivity.class);
         inte.putExtra(PHHomeActivity.KEY_MODE, PHHomeActivity.MODE_HOST);
         startActivity(inte);

    }

    @OnClick(R.id.joingame_button)
    public void onJoinGameClick(View v)
    {
        Intent inte = new Intent(this, PHHomeActivity.class);
        inte.putExtra(PHHomeActivity.KEY_MODE, PHHomeActivity.MODE_JOIN);
        startActivity(inte);
    }
}
