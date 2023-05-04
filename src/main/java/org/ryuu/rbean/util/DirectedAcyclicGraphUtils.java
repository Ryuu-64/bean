package org.ryuu.rbean.util;

import java.util.*;

public class DirectedAcyclicGraphUtils {
    public static List<String> topologicalSort(Map<String, List<String>> dependencies) {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String beanName : dependencies.keySet()) {
            inDegree.put(beanName, 0);
        }
        for (List<String> dependenciesList : dependencies.values()) {
            for (String dependency : dependenciesList) {
                inDegree.put(dependency, getOrDefault(inDegree, dependency, 0) + 1);
            }
        }
        Queue<String> queue = new LinkedList<>();
        for (String beanName : dependencies.keySet()) {
            if (getOrDefault(inDegree, beanName, 0) == 0) {
                queue.offer(beanName);
            }
        }
        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String beanName = queue.poll();
            result.add(beanName);
            List<String> dependsOn = dependencies.get(beanName);
            if (dependsOn != null) {
                for (String dependency : dependsOn) {
                    inDegree.put(dependency, inDegree.get(dependency) - 1);
                    if (inDegree.get(dependency) == 0) {
                        queue.offer(dependency);
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
