package io.github.gdx945.jraft.server.replicate.model;

import java.io.Serializable;

import io.github.gdx945.jraft.server.replicate.util.ReplicateFuture;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-19 11:02:59
 * @since : 0.1
 */
public class IdxAndFuture<V extends Serializable> implements Comparable<IdxAndFuture> {

    public IdxAndFuture(Integer index, ReplicateFuture replicateFuture) {
        this.index = index;
        this.replicateFuture = replicateFuture;
    }

    private Integer index;

    private ReplicateFuture replicateFuture;

    @Override
    public int compareTo(IdxAndFuture o) {
        return index.compareTo(o.index);
    }

    public Integer getIndex() {
        return index;
    }

    public ReplicateFuture getReplicateFuture() {
        return replicateFuture;
    }
}
