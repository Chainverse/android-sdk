package com.chainverse.sdk.listener;

import android.view.View;

public interface OnItemActionListener {
    void onItemClicked(int position, View v);
    void onItemLongClicked(int position, View v);
}
