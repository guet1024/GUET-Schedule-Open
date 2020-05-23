package com.example.commonapplications;

import android.app.Application;

public class MyApplication extends Application {

    public static final int COLOR_STAY = -1;
    public static final int COLOR_BLACK = 0;
    public static final int COLOR_WHITE = 1;

    public int color;

    @Override
    public void onCreate() {
        super.onCreate();
        color = COLOR_WHITE;
    }

    public static String getColorName(int colorCode) {
        switch (colorCode) {
            case COLOR_BLACK:
                return "black";
            case COLOR_WHITE:
                return "white";
            case COLOR_STAY:
                return "keep it as it is";
            default:
                return "invalid color";
        }
    }
}
