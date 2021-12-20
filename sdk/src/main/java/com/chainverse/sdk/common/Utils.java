package com.chainverse.sdk.common;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.JsonElement;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

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

    public static void openURI(Context context, Uri uri){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        if (packageName == null || TextUtils.isEmpty(packageName)) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        boolean appInstalled;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            appInstalled = false;
        }
        return appInstalled;
    }

    public static boolean isChainverseInstalled(Context context){
        if(!isAppInstalled(context,"org.chainverse.app")){
            openURI(context,Uri.parse("https://play.google.com/store/apps/details?id=org.chainverse.app"));
            return false;
        }
        return true;
    }

    public static String byteToHexString(byte[] payload) {
        if (payload == null) return "<empty>";
        StringBuilder stringBuilder = new StringBuilder(payload.length);
        for (byte byteChar : payload)
            stringBuilder.append(String.format("%02x", byteChar));
        return stringBuilder.toString().toLowerCase();
    }

    public static void copyFromClipboard(Context context, String label, String value){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, value);
        clipboard.setPrimaryClip(clip);
    }
}
