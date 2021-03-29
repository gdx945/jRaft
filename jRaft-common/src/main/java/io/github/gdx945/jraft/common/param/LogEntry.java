package io.github.gdx945.jraft.common.param;

import java.io.Serializable;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-02-23 10:35:02
 * @since : 0.1
 */
public class LogEntry implements Serializable {

    private static final long serialVersionUID = 3750023318834658453L;

    public LogEntry(String command, Serializable param) {
        this.command = command;
        this.param = param;
    }

    private String command;

    private Serializable param;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Serializable getParam() {
        return param;
    }

    public void setParam(Serializable param) {
        this.param = param;
    }
}
