package io.netty.handler.codec.serialization;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
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
        String s;
        for (int i = 0; i < 65536; i++) {
            s = String.valueOf(i);
        }

        //        FileOutputStream fileOutputStream = new FileOutputStream(FileUtil.touch("/Users/gc/Workpath/zzz"), true);
        int J = 10;

        long start = System.currentTimeMillis();
        FileOutputStream xxxfileOutputStream = new FileOutputStream(FileUtil.touch("/Users/gc/Workpath/xxx"));
        for (int i = 0; i < 65536; i++) {
            Map.map.Builder mapBuilder = Map.map.newBuilder();
            Any.any any = Any.any.newBuilder().setDoubleField(i).build();
            for (int j = 0; j < J; j++) {
                mapBuilder.putMap(String.valueOf(j), any);
            }
            //            mapBuilder.build().writeTo(xxxfileOutputStream);
            xxxfileOutputStream.write(mapBuilder.build().toByteArray());
            xxxfileOutputStream.flush();
        }
        System.out.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        FileOutputStream yyyfileOutputStream = new FileOutputStream(FileUtil.touch("/Users/gc/Workpath/yyy"));
        //        CompactObjectOutputStream compactObjectOutputStream = new CompactObjectOutputStream(yyyfileOutputStream);
        for (int i = 0; i < 65536; i++) {
            java.util.Map<String, Long> map = new HashMap<>();
            for (int j = 0; j < J; j++) {
                map.put(String.valueOf(j), (long) i);
            }
            yyyfileOutputStream.write(ObjectUtil.serialize(map));
            yyyfileOutputStream.flush();
            //            compactObjectOutputStream.writeObject(map);
            //            compactObjectOutputStream.flush();
        }
        System.out.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        FileOutputStream zzzfileOutputStream = new FileOutputStream(FileUtil.touch("/Users/gc/Workpath/zzz"));
        //        FileWriter fileWriter = new FileWriter("/Users/gc/Workpath/zzz");
        for (int i = 0; i < 65536; i++) {
            java.util.Map<String, Long> map = new HashMap<>();
            for (int j = 0; j < J; j++) {
                map.put(String.valueOf(j), (long) i);
            }
            zzzfileOutputStream.write(JSONUtil.parse(map).toString().getBytes());
            zzzfileOutputStream.flush();
            //            fileWriter.write(JSONUtil.parse(map).toString() + "\n");
            //            fileWriter.flush();
        }
        System.out.println(System.currentTimeMillis() - start);

        //        Map.map.
    }
}
