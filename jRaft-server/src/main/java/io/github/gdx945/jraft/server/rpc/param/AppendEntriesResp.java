package io.github.gdx945.jraft.server.rpc.param;

import java.io.Serializable;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-02-23 14:00:55
 * @since : 0.1
 */
public class AppendEntriesResp implements Serializable {

    private static final long serialVersionUID = -4945462534029520354L;

    private int term;

    private boolean success;

    private int nextLogIndex;

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getNextLogIndex() {
        return nextLogIndex;
    }

    public void setNextLogIndex(int nextLogIndex) {
        this.nextLogIndex = nextLogIndex;
    }
}
