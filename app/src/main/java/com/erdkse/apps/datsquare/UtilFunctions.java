package com.erdkse.apps.datsquare;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by erdico on 09.10.2014.
 */
public class UtilFunctions {

    private Activity activity;


    public UtilFunctions(Activity activity) {
        super();
        this.activity = activity;
    }

    public float changeDp(float px, Activity activity) {
        Resources res = activity.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        float dpres = px / (metrics.densityDpi / 160f);
        return dpres;
    }

    public float getScreenWidth(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    public float getScreenHeight(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }
}
