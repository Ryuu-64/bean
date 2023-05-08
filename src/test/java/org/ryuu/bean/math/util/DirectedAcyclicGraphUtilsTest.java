package org.ryuu.bean.math.util;

import org.junit.jupiter.api.Test;
import org.ryuu.bean.math.CyclicInDirectedAcyclicGraphException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectedAcyclicGraphUtilsTest {
    @Test
    void vertexCount() {
        HashMap<String, List<String>> graph = new HashMap<>();
        graph.put("a", Arrays.asList("b", "c"));
        graph.put("b", Collections.singletonList("c"));
        graph.put("c", Collections.singletonList("b"));
        int vertexCount = DirectedAcyclicGraphUtils.vertexCount(graph);
        assertEquals(vertexCount, 3);
    }

    @Test
    void kahnTopologicalSort() {
        HashMap<String, List<String>> graph = new HashMap<>();
        graph.put("a", Arrays.asList("b", "c"));
        graph.put("b", Collections.singletonList("c"));
        graph.put("d", Collections.singletonList("a"));
        List<String> sortedList = DirectedAcyclicGraphUtils.kahnTopologicalSort(graph);
        assertEquals(Arrays.asList("d", "a", "b", "c"), sortedList);
    }

    @Test
    void kahnTopologicalSortWithCyclicGraph() {
        HashMap<String, List<String>> graph = new HashMap<>();
        graph.put("a", Collections.singletonList("b"));
        graph.put("b", Collections.singletonList("c"));
        graph.put("c", Collections.singletonList("d"));
        graph.put("d", Collections.singletonList("a"));
        try {
            DirectedAcyclicGraphUtils.kahnTopologicalSort(graph);
        } catch (Exception e) {
            assertEquals(CyclicInDirectedAcyclicGraphException.class, e.getClass());
        }
    }
}