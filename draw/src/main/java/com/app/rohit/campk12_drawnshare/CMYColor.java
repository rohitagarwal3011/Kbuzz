package com.app.rohit.campk12_drawnshare;

/**
 * Created by rohit on 7/1/18.
 */
import android.graphics.Color;

import java.io.Serializable;


public class CMYColor implements Serializable
{

    private float c, m, y;

    public void setC(float c)
    {
        this.c = c;
    }

    public void setM(float m)
    {
        this.m = m;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public float getC()
    {
        return c;
    }

    public float getM()
    {
        return m;
    }

    public float getY()
    {
        return y;
    }

    public int getRGB()
    {
        return CMYColor.toRGB(c, m, y);
    }

    /**
     * Convert from CMY to RGB.
     */
    public static int toRGB(float c, float m, float y)
    {

        int r = (int) ((1 - c) * 255);
        int g = (int) ((1 - m) * 255);
        int b = (int) ((1 - y) * 255);

        return Color.rgb(r, g, b);
    }

    /**
     * Convert from RGB to CMY.
     */
    public static CMYColor fromRGB(int color)
    {
        CMYColor newColor = new CMYColor();


        newColor.setC(1f - Color.red(color) / 255f);
        newColor.setM(1f - Color.green(color) / 255f);
        newColor.setY(1f - Color.blue(color) / 255f);

        return newColor;
    }
}
