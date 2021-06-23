package com.chainverse.sdk.wl.config;


import com.chainverse.sdk.R;
import com.chainverse.sdk.model.WL;

import java.util.ArrayList;

public class SupportWL {
    public static ArrayList<WL> getSupportWallets(){
        ArrayList<WL> wls = new ArrayList<WL>();
        wls.add(createWL(1,"TrustWallet", R.drawable.com_chainverse_sdk_ic_close));
        return wls;
    }

    public static WL createWL(int id, String name, int logo){
        WL wl = new WL();
        wl.setId(id);
        wl.setName(name);
        wl.setLogo(logo);
        return wl;
    }
}
