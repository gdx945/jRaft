package io.github.gdx945;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.github.gdx945.protobuf.Any;
import io.github.gdx945.protobuf.Map;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-04 16:22:38
 * @since : 0.1
 */
public class TestProtobuf {

    public static void main(String[] args) throws IOException {

        //        FileOutputStream fileOutputStream = new FileOutputStream(FileUtil.touch("/Users/gc/Workpath/zzz"), true);
                FileOutputStream fileOutputStream = new FileOutputStream(FileUtil.touch("/Users/gc/Workpath/zzz"));
        RandomAccessFile randomAccessFile = new RandomAccessFile(FileUtil.touch("/Users/gc/Workpath/zzz"), "rw");
        RandomAccessFile randomAccessFile1 = new RandomAccessFile(FileUtil.touch("/Users/gc/Workpath/zzz1"), "rw");

        long start = System.currentTimeMillis();

        //        for (int i = 0; i < 65536; i++) {
        //            Map.map.Builder mapBuilder = Map.map.newBuilder();
        //            Any.any any = Any.any.newBuilder().setDoubleField(i).build();
        //            for (int j = 0; j < 100; j++) {
        //                mapBuilder.putMap(String.valueOf(j), any);
        //            }
        //            fileOutputStream.write(mapBuilder.build().toByteArray());
        //            fileOutputStream.flush();
        //        }

        for (int i = 0; i < 10; i++) {
            java.util.Map<String, Long> map = new HashMap<>();
            for (int j = 0; j < 10; j++) {
                map.put(String.valueOf(j), RandomUtil.randomLong());
            }
            byte[] bytes = (JSONUtil.parse(map).toString() + "\n").getBytes();
            //            System.out.println(bytes.length);
//            randomAccessFile.seek(randomAccessFile.length());
//            randomAccessFile.write(bytes);

//            long filePointer = randomAccessFile.getFilePointer();
//            randomAccessFile1.seek(0);
//            randomAccessFile1.write((StrUtil.fillAfter(String.valueOf(filePointer - bytes.length), ' ', 15)).getBytes());
//                                fileOutputStream.write(ObjectUtil.serialize(map));
            //                    fileOutputStream.flush();
        }
        // 8589934592
        //        System.out.println(randomAccessFile.getFilePointer());
        //        //        FileWriter fileWriter = new FileWriter("/Users/gc/Workpath/zzz");
        //        for (int i = 0; i < 65536; i++) {
        //            java.util.Map<String, Long> map = new HashMap<>();
        //            for (int j = 0; j < 100; j++) {
        //                map.put(String.valueOf(j), (long) i);
        //            }
        //            fileOutputStream.write(JSONUtil.parse(map).toString().getBytes());
        //            fileOutputStream.flush();
        //            //            fileWriter.write(JSONUtil.parse(map).toString() + "\n");
        //            //            fileWriter.flush();
        //        }

        System.out.println(System.currentTimeMillis() - start);

        //        Map.map.
    }
}
