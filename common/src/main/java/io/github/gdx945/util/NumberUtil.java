package io.github.gdx945.util;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-17 09:49:24
 * @since : 0.1
 */
public abstract class NumberUtil {

    public static byte[] toBytes(long v, int size) {
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[size - i - 1] = (byte) (v >> (8 * i));
        }
        return result;
    }

    public static long toLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result += (bytes[i] & 0xFFL) << ((bytes.length - i - 1) * 8);
        }
        return result;
    }
}
