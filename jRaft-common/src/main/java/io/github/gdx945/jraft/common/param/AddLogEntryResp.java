package io.github.gdx945.jraft.common.param;

import java.io.Serializable;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-03 10:48:22
 * @since : 0.1
 */
public class AddLogEntryResp implements Serializable {

    private static final long serialVersionUID = 52911673646662385L;

    private long index;

    private String nodeId;

    private String leaderNodeId;

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getLeaderNodeId() {
        return leaderNodeId;
    }

    public void setLeaderNodeId(String leaderNodeId) {
        this.leaderNodeId = leaderNodeId;
    }
}
