package io.blueapps.lightspace;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;

public class ColorActivity extends Activity implements ColorPicker.OnColorSelectedListener {
    private PHHueSDK phHueSDK;

    private ColorPicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);
        phHueSDK = PHHueSDK.create();
        picker = (ColorPicker) findViewById(R.id.picker);
        picker.setOnColorSelectedListener(this);
    }

    public void onColorSelected(int color) {
        setHueColor(color);
    }

    public void setHueColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        Log.d("colorpicker", "color= " + color);
        setHueColor(r, g, b);
    }

    public void setHueColor(int r, int g, int b) {
        Log.d("colorpicker", "r=" + r + " g=" + g + " b= " + b);

        if (r == 0 && g == 0 && b == 0) {
            Log.d("colorpicker", "bulb can not display black");
        }
        else {
            PHBridge bridge = phHueSDK.getSelectedBridge();

            if (bridge != null) {
                List<PHLight> allLights = bridge.getResourceCache().getAllLights();

                for (PHLight light : allLights) {
                    float xy[] = PHUtilities.calculateXYFromRGB(r, g, b, light.getModelNumber());
                    PHLightState lightState = new PHLightState();
                    lightState.setX(xy[0]);
                    lightState.setY(xy[1]);
                    lightState.setAlertMode(PHLight.PHLightAlertMode.ALERT_SELECT);
                    lightState.setColorMode(PHLight.PHLightColorMode.COLORMODE_XY);
                    lightState.setBrightness(50);

                    // lightState.setHue(rand.nextInt(MAX_HUE));
                    // To validate your lightstate is valid (before sending to the bridge) you can use:
                    // String validState = lightState.validateState();
                    // bridge.updateLightState(light, lightState, listener);
                    bridge.updateLightState(light, lightState); // If no bridge response is required then use this simpler form.
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        phHueSDK.disableAllHeartbeat();
    }
}
