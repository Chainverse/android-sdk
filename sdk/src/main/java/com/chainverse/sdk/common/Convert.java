package com.chainverse.sdk.common;

public class Convert {
    public static Boolean hexToBool(String hex){
        if(hex != null && !hex.isEmpty()){
            if(Integer.decode(hex) == 1){
                return true;
            }
            return false;
        }
        return false;
    }
}
