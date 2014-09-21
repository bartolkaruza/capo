package com.http.data;

/**
 * Created by bartolkaruza on 21/09/14.
 */
public class ColorUpdate {

    private GameColor currentColor;
    private String targetColor;

    public GameColor getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(GameColor currentColor) {
        this.currentColor = currentColor;
    }

    public String getTargetColor() {
        return targetColor;
    }

    public void setTargetColor(String targetColor) {
        this.targetColor = targetColor;
    }
}
