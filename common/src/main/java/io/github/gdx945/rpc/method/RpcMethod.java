package io.github.gdx945.rpc.method;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcMethod implements Serializable {

    private static final Map<String, RpcMethod> cache = new ConcurrentHashMap<>();

    private RpcMethod(String methodName) {
        // todo check null
        this.methodName = methodName;
    }

    private Object readResolve() {
        return getInst(methodName);
    }

    private String methodName;

    public static RpcMethod getInst(String methodName) {
        // todo check null
        RpcMethod result = cache.get(methodName);
        if (result == null) {
            synchronized (RpcMethod.class) {
                result = cache.get(methodName);
                if (result == null) {
                    result = new RpcMethod(methodName);
                    cache.put(methodName, result);
                }
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        return methodName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RpcMethod) {
            return this.methodName.equals(((RpcMethod) obj).methodName);
        }
        return false;
    }
}
