package io.github.gdx945.jraft.statemachine.impl;

import java.util.HashMap;
import java.util.Map;

import io.github.gdx945.jraft.statemachine.StateMachine;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-22 14:33:38
 * @since : 0.1
 */
public class DefaultStateMachine<T, R> implements StateMachine<T, R> {

    private Map<T, R> map = new HashMap<>();

    @Override
    public R apply(T o) {
        return null;
    }
}
