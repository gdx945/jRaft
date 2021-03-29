package io.github.gdx945.jraft.server.rpc.param;

import java.io.Serializable;

/**
 * 请求选票参数
 *
 * @author : gc
 * Created on 2021-02-22 13:56:04
 * @since : 0.1
 */
public class RequestVoteReq implements Serializable {

    private static final long serialVersionUID = 3941940943760999343L;

    private int term;

    private String candidateId;

    private long lastLogIndex;

    private int lastLogTerm;

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

    public int getLastLogTerm() {
        return lastLogTerm;
    }

    public void setLastLogTerm(int lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }
}
