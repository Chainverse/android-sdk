package com.chainverse.sdk.common;

import com.google.gson.Gson;

public class LogUtil {
    public static void log(String key,Object obj){
        Gson gson = new Gson();
        String save = gson.toJson(obj);
        System.out.println(key + "|" + save);
    }
}
