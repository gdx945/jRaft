package io.github.gdx945.jraft.server.option;

/**
 * 节点参数
 *
 * @author : gc
 * Created on 2021-02-22 13:41:22
 * @since : 0.1
 */
public class NodeOptions {

    public NodeOptions(String nodeId, int port, String otherNodeAddr, int electionTimeout, String storePath) {
        this.nodeId = nodeId;
        this.port = port;
        this.otherNodeAddr = otherNodeAddr;
        this.electionTimeout = electionTimeout;
        this.storePath = storePath;
    }

    private String nodeId;

    private int port;

    private String otherNodeAddr;

    private int electionTimeout;

    private String storePath;

    public int getPort() {
        return port;
    }

    public String getOtherNodeAddr() {
        return otherNodeAddr;
    }

    public int getElectionTimeout() {
        return electionTimeout;
    }

    public String getStorePath() {
        return storePath;
    }

    public String getNodeId() {
        return nodeId;
    }

}
