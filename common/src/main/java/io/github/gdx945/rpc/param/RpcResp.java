package io.github.gdx945.rpc.param;

import java.io.Serializable;

import io.github.gdx945.rpc.method.RpcMethod;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-02-22 20:34:31
 * @since : 0.1
 */
public class RpcResp implements Serializable {

    private static final long serialVersionUID = 7822483378851974617L;

    public RpcResp(int rpcId) {
        this.rpcId = rpcId;
    }

    private int rpcId;

    private RpcMethod method;

    private Serializable result;

    public int getRpcId() {
        return rpcId;
    }

    public RpcMethod getMethod() {
        return method;
    }

    public void setMethod(RpcMethod method) {
        this.method = method;
    }

    public Serializable getResult() {
        return result;
    }

    public void setResult(Serializable result) {
        this.result = result;
    }
}
