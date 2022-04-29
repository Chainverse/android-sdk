package com.chainverse.sdk.common;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.google.gson.JsonElement;



public class Utils {
    public static int convertDPToPixels(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float fpixels = metrics.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        return pixels;
    }

    public static int getErrorCodeResponse(JsonElement jsonElement) {
        return jsonElement.getAsJsonObject().get("error_code").getAsInt();
    }

    public static void openURI(Context context, Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } catch (Exception e) {
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

    public static boolean isChainverseInstalled(Context context) {
        if (!isAppInstalled(context, "org.chainverse")) {
            openURI(context, Uri.parse("https://play.google.com/store/apps/details?id=org.chainverse"));
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

    public static void copyFromClipboard(Context context, String label, String value) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, value);
        clipboard.setPrimaryClip(clip);
    }

    public static String shortAddress(String address) {
        return address.substring(0, 7) + "..." + address.substring(address.length() - 5, address.length());
    }

    public static org.web3j.utils.Convert.Unit getWei(int decimals) {
        switch (decimals) {
            case 0:
                return org.web3j.utils.Convert.Unit.WEI;
            case 3:
                return org.web3j.utils.Convert.Unit.KWEI;
            case 6:
                return org.web3j.utils.Convert.Unit.MWEI;
            case 9:
                return org.web3j.utils.Convert.Unit.GWEI;
            case 12:
                return org.web3j.utils.Convert.Unit.SZABO;
            case 15:
                return org.web3j.utils.Convert.Unit.FINNEY;
            case 18:
                return org.web3j.utils.Convert.Unit.ETHER;
            case 21:
                return org.web3j.utils.Convert.Unit.KETHER;
            case 24:
                return org.web3j.utils.Convert.Unit.METHER;
            case 27:
                return org.web3j.utils.Convert.Unit.GETHER;
        }

        return org.web3j.utils.Convert.Unit.ETHER;
    }
}
