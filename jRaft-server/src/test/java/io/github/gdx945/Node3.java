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
public class Node3 {

    public static void main(String[] args) {
        NodeOptions nodeOptions3 = new NodeOptions("node3", 3333, "127.0.0.1:1111;127.0.0.1:2222", 1000,
            "/Users/gc/Workpath/0自己代码项目/jRaft-parent-store/node3");
        Node node3 = new Node(nodeOptions3);
    }
}
