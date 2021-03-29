package io.github.gdx945.jraft.server.replicate.model;

import io.github.gdx945.util.CommonFuture;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-19 11:02:59
 * @since : 0.1
 */
public class IdxAndFuture implements Comparable<IdxAndFuture> {

    public IdxAndFuture(Integer index, CommonFuture commonFuture) {
        this.index = index;
        this.commonFuture = commonFuture;
    }

    private Integer index;

    private CommonFuture commonFuture;

    @Override
    public int compareTo(IdxAndFuture o) {
        return index.compareTo(o.index);
    }

    public Integer getIndex() {
        return index;
    }

    public CommonFuture getCommonFuture() {
        return commonFuture;
    }
}
