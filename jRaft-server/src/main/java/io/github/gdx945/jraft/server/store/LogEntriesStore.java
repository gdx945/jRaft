package io.github.gdx945.jraft.server.store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import io.github.gdx945.jraft.server.model.LogEntry;
import io.github.gdx945.util.NumberUtil;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-05 13:26:23
 * @since : 0.1
 */
public class LogEntriesStore {

    private static final Logger logger = LoggerFactory.getLogger(LogEntriesStore.class);

    public LogEntriesStore(String storePath) {
        this.logEntries = FileUtil.touch(storePath + File.separator + "log-entries");
        this.logEntryIndex = FileUtil.touch(storePath + File.separator + "log-entry-index");
        init();
    }

    private File logEntries;

    private File logEntryIndex;

    private int firstIndex;

    private int lastIndex = -1; // 未初始化

    private LogEntry lastLogEntry;

    private RandomAccessFile randomAccessLogEntries;

    private RandomAccessFile randomAccessLogEntryIndex;

    private int countOfRandomAccessForRead = 8;

    private RandomAccessFile[] randomAccessLogEntriesForRead = new RandomAccessFile[countOfRandomAccessForRead];

    private RandomAccessFile[] randomAccessLogEntryIndexForRead = new RandomAccessFile[countOfRandomAccessForRead];

    private void init() {
        initRandomAccesses();
        initIndex();
        this.lastLogEntry = getList(getLastLogEntryIndex(), 1).stream().findFirst().orElse(new LogEntry(-1, this.lastIndex, null, null));
    }

    private void initRandomAccesses() {
        try {
            this.randomAccessLogEntries = new RandomAccessFile(this.logEntries, "rw");
            this.randomAccessLogEntryIndex = new RandomAccessFile(this.logEntryIndex, "rw");
            for (int i = 0; i < this.countOfRandomAccessForRead; i++) {
                randomAccessLogEntriesForRead[i] = new RandomAccessFile(this.logEntries, "r");
                randomAccessLogEntryIndexForRead[i] = new RandomAccessFile(this.logEntryIndex, "r");
            }
        }
        catch (FileNotFoundException e) {
            throw ExceptionUtil.wrapRuntime(e);
        }
    }

    private void initIndex() {
        try {
            synchronized (LogEntriesStore.class) {
                this.randomAccessLogEntryIndex.seek(0);
                if (this.randomAccessLogEntryIndex.length() != 0) {
                    this.firstIndex = this.randomAccessLogEntryIndex.readInt();
                    this.lastIndex = getLastLogEntryIndex();
                }
                else {
                    this.randomAccessLogEntryIndex.writeLong(1L << 32); //初始化索引
                    // 索引: int(first idx) int...(first log pointer...)
                    //       1              0
                    this.firstIndex = 1;
                    this.lastIndex = 0;
                }
            }
        }
        catch (IOException e) {
            throw ExceptionUtil.wrapRuntime(e);
        }
    }

    public int getLastLogEntryIndex() {
        if (lastIndex == -1) {
            try {
                this.lastIndex = (int) ((this.randomAccessLogEntryIndex.length() - Long.BYTES) / Integer.BYTES) + this.firstIndex - 1;
            }
            catch (IOException e) {
                throw ExceptionUtil.wrapRuntime(e);
            }
        }
        return this.lastIndex;
    }

    public List<LogEntry> getList(int index, int length) {
        List<LogEntry> result = new ArrayList<>();
        if (this.lastIndex > 0 && length > 0 && index <= this.lastIndex) {
            RandomAccessFile randomAccessLogEntries = getRandomAccessLogEntriesForRead();
            RandomAccessFile randomAccessLogEntryIndex = getRandomAccessLogEntryIndexForRead();

            long startIdxPointer = getStartIdxPointer(index);
            long logEntryStartPointer = NumberUtil.toLong(readFully(startIdxPointer, Integer.BYTES, randomAccessLogEntryIndex));
            long _logEntryStartPointer = logEntryStartPointer;
            long logEntryEndPointer = 0;
            int[][] idxPointersList = new int[Math.min(length, (this.lastIndex - index + 1))][];
            int logEntryOffset = 0;
            int logEntryLength;
            for (int i = 0; i < idxPointersList.length; i++) {
                logEntryEndPointer = NumberUtil.toLong(readFully(startIdxPointer += Integer.BYTES, Integer.BYTES, randomAccessLogEntryIndex));
                logEntryLength = (int) (logEntryEndPointer - _logEntryStartPointer);
                idxPointersList[i] = new int[] {logEntryOffset, logEntryLength};
                _logEntryStartPointer = logEntryEndPointer;
                logEntryOffset += logEntryLength;
            }

            byte[] bytes = readFully(logEntryStartPointer, (int) (logEntryEndPointer - logEntryStartPointer), randomAccessLogEntries);
            for (int[] idxPointers : idxPointersList) {
                result.add(JSONUtil.toBean(new String(bytes, idxPointers[0], idxPointers[1]), LogEntry.class));
            }

        }

        return result;
    }

    public LogEntry getLastLogEntry() {
        return this.lastLogEntry;
    }

    public void addLogEntries(LogEntry logEntry) { // todo 异常处理
        synchronized (LogEntriesStore.class) {
            try {
                this.randomAccessLogEntries.seek(this.randomAccessLogEntries.length());
                this.randomAccessLogEntryIndex.seek(this.randomAccessLogEntryIndex.length());
            }
            catch (IOException e) {
                throw ExceptionUtil.wrapRuntime(e);
            }
            byte[] bytes;
            logEntry.setIndex(this.lastIndex + 1);
            bytes = (JSONUtil.parse(logEntry).toString() + "\n").getBytes();
            try {
                this.randomAccessLogEntries.write(bytes);
                this.randomAccessLogEntryIndex.write(NumberUtil.toBytes(this.randomAccessLogEntries.length(), Integer.BYTES));
                this.lastLogEntry = logEntry;
                this.lastIndex++;
            }
            catch (IOException e) {
                throw ExceptionUtil.wrapRuntime(e);
            }
        }
    }

    public void appendEntries(int startIdx, List<LogEntry> logEntries) {
        long startIdxPointer = getStartIdxPointer(startIdx);
        long endIdxPointer = startIdxPointer + Integer.BYTES;

        try {
            if (this.randomAccessLogEntryIndex.length() >= (endIdxPointer + Integer.BYTES)) {
                this.randomAccessLogEntryIndex.setLength(endIdxPointer);
                long logEntryStartPointer = NumberUtil.toLong(readFully(startIdxPointer, Integer.BYTES, getRandomAccessLogEntryIndexForRead()));
                //                logger.info("{}, {}, {}", startIdxPointer, endIdxPointer, logEntryStartPointer);
                this.randomAccessLogEntries.setLength(logEntryStartPointer);
            }
            this.lastIndex = startIdx - 1;
            for (LogEntry logEntry : logEntries) {
                addLogEntries(logEntry);
            }
        }
        catch (IOException e) {
            throw ExceptionUtil.wrapRuntime(e);
        }
    }

    private long getStartIdxPointer(int startIdx) {
        return (startIdx - this.firstIndex + 1) * (Integer.BYTES);
    }

    private byte[] readFully(long pointer, int length, RandomAccessFile randomAccessFile) {
        byte[] bytes = new byte[length];
        try {
            synchronized (randomAccessFile) {
                randomAccessFile.seek(pointer);
                randomAccessFile.readFully(bytes);
            }
        }
        catch (IOException e) {
            throw ExceptionUtil.wrapRuntime(e);
        }
        return bytes;
    }

    private RandomAccessFile getRandomAccessLogEntriesForRead() {
        return this.randomAccessLogEntriesForRead[RandomUtil.randomInt(0, countOfRandomAccessForRead)];
    }

    private RandomAccessFile getRandomAccessLogEntryIndexForRead() {
        return this.randomAccessLogEntryIndexForRead[RandomUtil.randomInt(0, countOfRandomAccessForRead)];
    }
}
