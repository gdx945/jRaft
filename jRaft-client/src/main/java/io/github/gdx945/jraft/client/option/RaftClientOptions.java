package io.github.gdx945.jraft.client.option;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-02 10:04:11
 * @since : 0.1
 */
public class RaftClientOptions {

    private String nodeAddrList;

    public String getNodeAddrList() {
        return nodeAddrList;
    }

    public void setNodeAddrList(String nodeAddrList) {
        this.nodeAddrList = nodeAddrList;
    }
}
