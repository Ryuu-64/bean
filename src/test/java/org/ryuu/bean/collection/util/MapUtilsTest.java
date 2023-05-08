package org.ryuu.bean.collection.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MapUtilsTest {
    @Test
    void getOrDefault() {
        HashMap<String, String> map = new HashMap<>();
        String value = MapUtils.getOrDefault(map, "foo", "bar");
        assertEquals("bar", value);
    }

    @Test
    void getOrDefaultWithEntry() {
        HashMap<String, String> map = new HashMap<>();
        map.put("foo", "bar");
        String value = MapUtils.getOrDefault(map, "foo", "42");
        assertEquals("bar", value);
    }
}