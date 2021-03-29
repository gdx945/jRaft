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
public class RpcReq implements Serializable {

    private static final long serialVersionUID = 7822483378851974617L;

    public RpcReq(int rpcId, RpcMethod method, Serializable param) {
        this.rpcId = rpcId;
        this.method = method;
        this.param = param;
    }

    private int rpcId;

    private RpcMethod method;

    private Serializable param;

    public int getRpcId() {
        return rpcId;
    }

    public void setRpcId(int rpcId) {
        this.rpcId = rpcId;
    }

    public RpcMethod getMethod() {
        return method;
    }

    public void setMethod(RpcMethod method) {
        this.method = method;
    }

    public Serializable getParam() {
        return param;
    }

    public void setParam(Serializable param) {
        this.param = param;
    }
}
