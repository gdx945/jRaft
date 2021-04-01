package io.github.gdx945.jraft.statemachine;

import java.io.Serializable;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-22 14:21:09
 * @since : 0.1
 */
public interface StateMachine {

    Serializable apply(Object p1, Object p2);
}
