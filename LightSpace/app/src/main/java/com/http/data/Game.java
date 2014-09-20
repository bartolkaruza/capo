package com.http.data;

/**
 * Created by Sunil Shetty on 9/20/2014.
 * sunil.shetty@klm.com
 */
public class Game
{
    private String targetColor;

    private GameValues[] values;

    private String status;

    private String name;

    public String getTargetColor ()
    {
        return targetColor;
    }

    public void setTargetColor (String targetColor)
    {
        this.targetColor = targetColor;
    }

    public GameValues[] getValues ()
    {
        return values;
    }

    public void setValues (GameValues[] values)
    {
        this.values = values;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }
}


