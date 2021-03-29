package io.github.gdx945;

import io.github.gdx945.util.Timeout;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-01 03:21:45
 * @since : 0.1
 */
public class Timeout1 {

    public static void main(String[] args) {
        Timeout timeout = new Timeout(1000) {
            @Override
            protected void onTimeout() {
                System.out.println("onTimeout");
            }
        };
        timeout.start();
    }
}
