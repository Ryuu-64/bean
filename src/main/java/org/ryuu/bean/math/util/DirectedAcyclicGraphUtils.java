package org.ryuu.bean.math.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ryuu.bean.math.CyclicInDirectedAcyclicGraphException;
import org.ryuu.bean.collection.util.MapUtils;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectedAcyclicGraphUtils {
    public static <T> List<T> kahnTopologicalSort(Map<T, List<T>> graph) throws CyclicInDirectedAcyclicGraphException {
        Map<T, Integer> inDegree = new HashMap<>();
        for (T vertex : graph.keySet()) {
            inDegree.put(vertex, 0);
        }
        for (List<T> targetVertexes : graph.values()) {
            for (T targetVertex : targetVertexes) {
                inDegree.put(targetVertex, MapUtils.getOrDefault(inDegree, targetVertex, 0) + 1);
            }
        }
        Queue<T> queue = new LinkedList<>();
        for (T vertexes : graph.keySet()) {
            if (MapUtils.getOrDefault(inDegree, vertexes, 0) == 0) {
                queue.offer(vertexes);
            }
        }
        List<T> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            T vertex = queue.poll();
            result.add(vertex);
            List<T> targetVertexes = graph.get(vertex);
            if (targetVertexes == null) {
                continue;
            }

            for (T targetVertex : targetVertexes) {
                inDegree.put(targetVertex, inDegree.get(targetVertex) - 1);
                if (inDegree.get(targetVertex) == 0) {
                    queue.offer(targetVertex);
                }
            }
        }

        int vertexCount = vertexCount(graph);
        if (result.size() != vertexCount) {
            List<T> cyclicVertexes = inDegree.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() != 0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(LinkedList::new));
            throw new CyclicInDirectedAcyclicGraphException(
                    "In a directed acyclic graph, cycles are not allowed.\n" +
                            "Cyclic vertexes = " + Arrays.toString(cyclicVertexes.toArray()) + "\n" +
                            "Please check the graph."
            );
        }
        return result;
    }

    public static <T> int vertexCount(Map<T, List<T>> graph) {
        HashSet<T> vertexesSet = new HashSet<>();
        for (Map.Entry<T, List<T>> entry : graph.entrySet()) {
            vertexesSet.add(entry.getKey());
            vertexesSet.addAll(entry.getValue());
        }

        return vertexesSet.size();
    }
}
