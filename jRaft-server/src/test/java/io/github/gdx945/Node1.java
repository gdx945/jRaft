package io.github.gdx945;

import io.github.gdx945.jraft.server.Node;
import io.github.gdx945.jraft.server.option.NodeOptions;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-02-23 17:47:59
 * @since : 0.1
 */
public class Node1 {
    public static void main(String[] args) {
        NodeOptions nodeOptions1 = new NodeOptions("node1", 1111, "127.0.0.1:2222;127.0.0.1:3333", 1000,
            "/Users/gc/Workpath/0自己代码项目/jRaft-parent-store/node1");
        Node node1 = new Node(nodeOptions1);
    }
}
