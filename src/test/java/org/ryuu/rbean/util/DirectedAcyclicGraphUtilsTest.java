package org.ryuu.rbean.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DirectedAcyclicGraphUtilsTest {
    @Test
    void topologicalSort() {
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("a", Arrays.asList("b", "c"));
        map.put("b", Collections.singletonList("c"));
        List<String> sortedList = DirectedAcyclicGraphUtils.topologicalSort(map);
        assertEquals(map.size(), sortedList.size());
    }
}