package io.github.gdx945.jraft.common.param;

import java.io.Serializable;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-03 10:48:22
 * @since : 0.1
 */
public class AddLogEntryReq implements Serializable {

    private static final long serialVersionUID = 7268106146220964178L;

    private LogEntry logEntry;

    public LogEntry getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(LogEntry logEntry) {
        this.logEntry = logEntry;
    }
}
