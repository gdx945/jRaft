package io.github.gdx945;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-16 23:10:54
 * @since : 0.1
 */
public class TestAccessFile {

    public static void main(String[] args) throws IOException {
        File file = FileUtil.touch("/Users/gc/Workpath/0自己代码项目/jRaft-parent-store/file");
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

        for (int i = 0; i < 65536; i++) {
            randomAccessFile.length();
        }

//        long _8gb = (long) Integer.MAX_VALUE * 4;
//        //        randomAccessFile.setLength();
//
//        //        byte[] _1kb;
//        //        for (int i = 0; i < (1024 * 1024 * 8); i++) {
//        //            _1kb = new byte[1024];
//        //            _1kb[0] = (byte) ((i >> 24) & 0xFF);
//        //            _1kb[1] = (byte) ((i >> 16) & 0xFF);
//        //            _1kb[2] = (byte) ((i >> 8) & 0xFF);
//        //            _1kb[3] = (byte) (i & 0xFF);
//        //            randomAccessFile.write(_1kb);
//        //        }
//
//        randomAccessFile.seek(1024 * 1024 * 7);
//        System.out.println(randomAccessFile.readInt());
//
//        byte[] intBytes = new byte[4];
//        FileInputStream fileInputStream = new FileInputStream(file);
//        fileInputStream.skip(1024 * 1024 * 7);
//        fileInputStream.read(intBytes);
//        System.out.println(NumberUtil.toInt(intBytes));
//
//        fileInputStream.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, randomAccessFile.length());
        //        System.out.println((-1 >> 24) & 0xFF);
        //        System.out.println((-1 >> 24));
    }
}
