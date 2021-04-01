package io.github.gdx945;

import io.github.gdx945.jraft.common.rpc.CommonRpcMethod;
import io.github.gdx945.jraft.server.model.LogEntry;
import io.github.gdx945.jraft.server.store.LogEntriesStore;
import io.github.gdx945.rpc.RpcService;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-02-23 19:45:57
 * @since : 0.1
 */
public class RpcService1 {

    public static void main(String[] args) {
        LogEntriesStore logEntriesStore = new LogEntriesStore("/Users/gc/Workpath/0自己代码项目/jRaft-parent-store/node1");
        new RpcService(4444).putServiceHandler(CommonRpcMethod.ADD_LOG_ENTRY, serializable -> {
//            logEntriesStore.addLogEntries((LogEntry) serializable, null);
            logEntriesStore.addLogEntries((LogEntry) serializable);
            return ((LogEntry) serializable).getIndex();
        });
    }
}
