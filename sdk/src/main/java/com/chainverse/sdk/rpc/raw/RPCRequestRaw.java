package com.chainverse.sdk.rpc.raw;

import java.util.ArrayList;

public class RPCRequestRaw {
    private String jsonrpc;
    private String method;
    private ArrayList<RPCParams> params;
    private int id;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ArrayList<RPCParams> getParams() {
        return params;
    }

    public void setParams(ArrayList<RPCParams> params) {
        this.params = params;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
