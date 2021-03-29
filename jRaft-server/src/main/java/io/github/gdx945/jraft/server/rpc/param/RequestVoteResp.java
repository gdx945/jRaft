package io.github.gdx945.jraft.server.rpc.param;

import java.io.Serializable;

/**
 * 请求选票参数
 *
 * @author : gc
 * Created on 2021-02-22 13:56:04
 * @since : 0.1
 */
public class RequestVoteResp implements Serializable {

    private static final long serialVersionUID = 3941940943760999343L;

    private int term;

    private boolean voteGranted;

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public boolean isVoteGranted() {
        return voteGranted;
    }

    public void setVoteGranted(boolean voteGranted) {
        this.voteGranted = voteGranted;
    }
}
