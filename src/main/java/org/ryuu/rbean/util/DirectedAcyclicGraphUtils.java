package org.ryuu.rbean.util;

import java.util.*;

public class DirectedAcyclicGraphUtils {
    public static <T> List<T> topologicalSort(Map<T, List<T>> dependencies) {
        Map<T, Integer> inDegree = new HashMap<>();
        for (T vertex : dependencies.keySet()) {
            inDegree.put(vertex, 0);
        }
        for (List<T> vertexes : dependencies.values()) {
            for (T vertex : vertexes) {
                inDegree.put(vertex, getOrDefault(inDegree, vertex, 0) + 1);
            }
        }
        Queue<T> queue = new LinkedList<>();
        for (T vertexes : dependencies.keySet()) {
            if (getOrDefault(inDegree, vertexes, 0) == 0) {
                queue.offer(vertexes);
            }
        }
        List<T> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            T vertex = queue.poll();
            result.add(vertex);
            List<T> targetVertexes = dependencies.get(vertex);
            if (targetVertexes != null) {
                for (T targetVertex : targetVertexes) {
                    inDegree.put(targetVertex, inDegree.get(targetVertex) - 1);
                    if (inDegree.get(targetVertex) == 0) {
                        queue.offer(targetVertex);
                    }
                }
            }
        }

        return result;
    }

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
