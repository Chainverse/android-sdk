package com.chainverse.sdk.manager;

import android.content.Context;

import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.model.service.ChainverseService;
import com.chainverse.sdk.model.service.Service;

public class ServiceManager {
    ChainverseService chainverseService;
    String address;

    public ServiceManager(Context mContext, String address) {
        chainverseService = EncryptPreferenceUtils.getInstance().init(mContext).getService();
        this.address = address;
    }

    public Service getService() {
        Service service = null;
        if (chainverseService != null) {
            for (int i = 0; i < chainverseService.getServices().size(); i++) {
                if (address.toLowerCase().equals(chainverseService.getServices().get(i).getAddress().toLowerCase())) {
                    service = chainverseService.getServices().get(i);
                }
            }
        }
        return service;
    }
}
