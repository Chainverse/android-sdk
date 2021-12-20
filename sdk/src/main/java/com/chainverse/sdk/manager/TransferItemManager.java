package com.chainverse.sdk.manager;

import android.content.Context;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.listener.OnEmitterListenter;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.PollingXHR;
import io.socket.engineio.client.transports.WebSocket;

public class TransferItemManager {
    private Socket mSocket;
    private Context mContext;
    private Map<String, String> map = new HashMap<String, String>();
    public TransferItemManager(Context context){
        mContext = context;
        map.put("type", "SDK");
        map.put("signature", EncryptPreferenceUtils.getInstance().getXUserSignature());
        map.put("signature_ethers", "false");
        map.put("user_address", EncryptPreferenceUtils.getInstance().getXUserAddress());
        map.put("game_address", ChainverseSDK.gameAddress);

        IO.Options opts = new IO.Options();
        opts.transports = new String[]{Polling.NAME, PollingXHR.NAME, WebSocket.NAME};
        opts.auth = map;
        try {
            mSocket = IO.socket(Constants.URL.urlSocket, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void on(OnEmitterListenter listener){
        mSocket.on("transfer_item_to_user", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                listener.call("transfer_item_to_user",args);
            }
        });

        mSocket.on("transfer_item_from_user", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                listener.call("transfer_item_from_user",args);
            }
        });

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                listener.call(Socket.EVENT_CONNECT,args);
            }
        });

        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                listener.call(Socket.EVENT_DISCONNECT,args);
            }
        });

        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                listener.call(Socket.EVENT_CONNECT_ERROR,args);
            }
        });
    }

    public void connect(){
        mSocket.connect();
    }

    public void disConnect(){
        mSocket.disconnect();
    }
}

