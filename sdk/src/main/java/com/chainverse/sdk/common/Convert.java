package com.chainverse.sdk.common;

public class Convert {
    public static Boolean hexToBool(String hex){
        if(hex != null && !hex.isEmpty() && !hex.equals("0x")){
            if(Integer.decode(hex) == 1){
                return true;
            }
            return false;
        }
        return false;
    }
}
