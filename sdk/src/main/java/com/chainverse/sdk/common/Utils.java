package com.chainverse.sdk.common;

import android.content.Context;
import android.util.DisplayMetrics;

import com.google.gson.JsonElement;

public class Utils {
    public static int convertDPToPixels(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float fpixels = metrics.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        return pixels;
    }

    public static int getErrorCodeResponse(JsonElement jsonElement){
        return jsonElement.getAsJsonObject().get("error_code").getAsInt();
    }

}
