package org.ryuu.bean.collection.util;

import java.util.Map;

public class MapUtils {
    /**
     * Call requires API level 24 in Android
     * {@link Map#getOrDefault(Object, Object)}
     */
    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        if (map.containsKey(key)) {
            V value = map.get(key);
            if (value != null) {
                return value;
            }
        }

        return defaultValue;
    }
}
