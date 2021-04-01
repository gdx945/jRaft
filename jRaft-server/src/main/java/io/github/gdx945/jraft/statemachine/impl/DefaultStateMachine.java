package io.github.gdx945.jraft.statemachine.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.gdx945.jraft.statemachine.StateMachine;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-22 14:33:38
 * @since : 0.1
 */
public class DefaultStateMachine implements StateMachine {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStateMachine.class);

    private Map<Object, Serializable> map = new HashMap<>();

    @Override
    public Serializable apply(Object o, Object o1) {
//        logger.info("{}, {}", o, o1);
        Serializable result = null;
        switch ((String) o) {
            case "put":
                map.putAll((Map) o1);
                break;
            case "get":
                result = map.get(o1);
            default:
                break;
        }
        return result;
    }
}
