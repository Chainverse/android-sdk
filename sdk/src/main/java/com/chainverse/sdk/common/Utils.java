package com.chainverse.sdk.common;

import android.content.Context;
import android.util.DisplayMetrics;

public class Utils {
    public static int convertDPToPixels(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float fpixels = metrics.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        return pixels;
    }
}
