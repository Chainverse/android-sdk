package com.chainverse.sdk.listener;

import android.view.View;

public interface OnEmitterListenter {
    void call(String event, Object... args);
}
