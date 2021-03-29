package io.github.gdx945.jraft.server.rpc.method;

import io.github.gdx945.rpc.method.RpcMethod;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-03 09:57:33
 * @since : 0.1
 */
public final class ServerRpcMethod {

    public static final RpcMethod REQUEST_VOTE = RpcMethod.getInst("REQUEST_VOTE");

    public static final RpcMethod HEARTBEAT = RpcMethod.getInst("HEARTBEAT");

    public static final RpcMethod APPEND_ENTRIES = RpcMethod.getInst("APPEND_ENTRIES");
}
