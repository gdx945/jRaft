package io.github.gdx945.jraft.server.rpc.param;

import java.io.Serializable;
import java.util.List;

import io.github.gdx945.jraft.server.model.LogEntry;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-02-23 14:00:40
 * @since : 0.1
 */
public class AppendEntriesReq implements Serializable {

    private static final long serialVersionUID = -6908693864250739055L;

    private int term;

    private String leaderId;

    private int prevLogIndex;

    private int prevLogTerm;

    private List<LogEntry> entries;

    private int leaderCommit;

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public int getPrevLogIndex() {
        return prevLogIndex;
    }

    public void setPrevLogIndex(int prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public int getPrevLogTerm() {
        return prevLogTerm;
    }

    public void setPrevLogTerm(int prevLogTerm) {
        this.prevLogTerm = prevLogTerm;
    }

    public List<LogEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<LogEntry> entries) {
        this.entries = entries;
    }

    public int getLeaderCommit() {
        return leaderCommit;
    }

    public void setLeaderCommit(int leaderCommit) {
        this.leaderCommit = leaderCommit;
    }
}
