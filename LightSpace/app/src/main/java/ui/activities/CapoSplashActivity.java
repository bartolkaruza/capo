package ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import com.http.GameRESTfulService;
import com.http.data.DeviceAddress;
import com.http.data.Game;
import com.philips.lighting.quickstart.GameActivity;

import java.lang.reflect.Method;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.blueapps.lightspace.BuildConfig;
import io.blueapps.lightspace.ColorActivity;
import io.blueapps.lightspace.DiscoActivity;
import io.blueapps.lightspace.R;
import io.blueapps.lightspace.bleutooth.DeviceScanActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CapoSplashActivity extends Activity {

    @InjectView(R.id.splashscreen_logo)
    View logo;

    @InjectView(R.id.menu_button_holder)
    View buttonHolder;

    View content;

    @InjectView(R.id.device_button)
    View deviceButton;

    @InjectView(R.id.device_color)
    View colorButton;

    // TODO fix deviceAddress
    public static DeviceAddress address;
    GameRESTfulService gameService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capo_splash);
        setUpDeviceAddress();

        gameService = GameRESTfulService.getInstance(address);
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

        if (BuildConfig.DEBUG) {
            deviceButton.setVisibility(View.VISIBLE);
            colorButton.setVisibility(View.VISIBLE);
        }
        else {
            deviceButton.setVisibility(View.GONE);
            colorButton.setVisibility(View.GONE);
        }

        doSomeInitialComputation();
    }

    private void doSomeInitialComputation() {

        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        sendEmptyMessageDelayed(1, 1000);
                        break;
                    case 1:
                        gameReady();
                        break;
                }
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    public void gameReady() {
        logo.animate().setDuration(200).y(-logo.getHeight() / 8);
        buttonHolder.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                buttonHolder.getViewTreeObserver().removeOnPreDrawListener(this);
                buttonHolder.setVisibility(View.VISIBLE);
                buttonHolder.setAlpha(0);
                buttonHolder.animate().alpha(1).setStartDelay(200).setDuration(500);
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
    public void onStartGameClick(View v) {
        askForGameID(GameActivity.MODE_HOST);
    }

    @OnClick(R.id.joingame_button)
    public void onJoinGameClick(View v) {
        askForGameID(GameActivity.MODE_JOIN);
    }

    public void startGame(final int mode, String gameId)
    {
        gameService.joinGame(gameId, new Callback<Game>() {
            @Override
            public void success(Game game, Response response) {
                Intent inte = new Intent(CapoSplashActivity.this, GameActivity.class);
                inte.putExtra(GameActivity.KEY_MODE, mode);
                inte.putExtra(GameActivity.KEY_ADRESS, address.getDeviceAddress());
                inte.putExtra(GameActivity.KEY_GAME_ID, game.getName());
                startActivity(inte);
            }

            @Override
            public void failure(RetrofitError error) {
                Crouton.makeText(CapoSplashActivity.this, "Error creating Game. " + error.getMessage(), Style.ALERT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    public void askForGameID(final int mode)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Title");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Play", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startGame(mode, input.getText().toString());
            }
        });
        builder.show();
    }

    @OnClick(R.id.device_button)
    public void onDeviceButtonClick(View v) {
        Intent inte = new Intent(this, DeviceScanActivity.class);
        startActivity(inte);
    }

    @OnClick(R.id.device_color)
    public void onDeviceColorButtonClick(View v) {
        Intent inte = new Intent(this, ColorActivity.class);
        startActivity(inte);
    }

    @OnClick(R.id.device_disco)
    public void onDeviceDisco(View v) {
        Intent inte = new Intent(this, DiscoActivity.class);
        startActivity(inte);
    }

    public void setUpDeviceAddress() {
        try {

            BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();

            Method getUUIDsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids");
            ParcelUuid[] dUUIDs = (ParcelUuid[]) getUUIDsMethod.invoke(mBluetoothAdapter, null);

            address = new DeviceAddress(dUUIDs[0].getUuid().toString());
        }
        catch (Exception e) {
            Log.e("BLEService", e.getMessage());
        }
    }
}
