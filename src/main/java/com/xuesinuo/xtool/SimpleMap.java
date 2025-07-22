package com.xuesinuo.xtool;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一个简易的，有序的，String做键的Map。可以方便的创建一个有序Map，尤其在临时创建一个JSON对象时，非常方便。
 * <p>
 * A simple, ordered, String key Map. It is very convenient to create an ordered Map, especially when creating a JSON object temporarily.
 * <p>
 * 实现方式：{@link LinkedHashMap}
 * <p>
 * Implementation method: {@link LinkedHashMap}
 * 
 * @author xuesinuo
 * @since 1.0.0
 */
public class SimpleMap extends LinkedHashMap<String, Object> {
    private static final long serialVersionUID = 2887887422952950023L;

    public SimpleMap() {}

    public static SimpleMap init() {
        return new SimpleMap();
    }

    public static SimpleMap init(Map<?, ?> map) {
        SimpleMap result = new SimpleMap();
        if (map != null) {
            for (Object k : map.keySet()) {
                result.append(k.toString(), map.get(k));
            }
        }
        return result;
    }

    public static SimpleMap init(String k, Object v) {
        return new SimpleMap().append(k, v);
    }

    public SimpleMap append(String k, Object v) {
        this.put(k, v);
        return this;
    }
}