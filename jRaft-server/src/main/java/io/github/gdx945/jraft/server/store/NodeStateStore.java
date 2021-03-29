package io.github.gdx945.jraft.server.store;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-07 07:50:42
 * @since : 0.1
 */
public class NodeStateStore {

    public NodeStateStore(String storePath) {
        this.nodeStateFile = FileUtil.touch(storePath + File.separator + "node-state");
        init();
    }

    private File nodeStateFile;

    private RandomAccessFile randomAccessNodeState;

    private int currentTerm;

    private String votedFor;

    private void init() {
        try {
            this.randomAccessNodeState = new RandomAccessFile(this.nodeStateFile, "rw");

            this.currentTerm = 0;
            this.votedFor = null;
            if (this.randomAccessNodeState.length() != 0) {
                this.randomAccessNodeState.seek(0);
                this.currentTerm = this.randomAccessNodeState.readInt();

                this.randomAccessNodeState.seek(Long.SIZE);
                this.votedFor = this.randomAccessNodeState.readLine();
                if ("".equals(this.votedFor)) {
                    this.votedFor = null;
                }
            }
        }
        catch (IOException e) {
            throw ExceptionUtil.wrapRuntime(e);
        }

    }

    public int getCurrentTerm() {
        return this.currentTerm;
    }

    public String getVotedFor() {
        return this.votedFor;
    }

    public void setCurrentTerm(int currentTerm) {
        try {
            this.randomAccessNodeState.seek(0);
            this.randomAccessNodeState.writeInt(currentTerm);
        }
        catch (IOException e) {
            throw ExceptionUtil.wrapRuntime(e);
        }
        this.currentTerm = currentTerm;
    }

    public void setVotedFor(String votedFor) {
        if (ObjectUtil.notEqual(this.votedFor, votedFor)) {
            try {
                this.randomAccessNodeState.seek(Long.SIZE);
                this.randomAccessNodeState.write((votedFor == null ? System.lineSeparator() : votedFor + System.lineSeparator()).getBytes());
            }
            catch (IOException e) {
                throw ExceptionUtil.wrapRuntime(e);
            }
            this.votedFor = votedFor;
        }
    }

}
