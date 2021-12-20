package com.chainverse.sdk.listener;

import android.view.View;

public interface OnWalletListener {
    void onCreated();
    void onImported();
    void onImportedFailed();
}
