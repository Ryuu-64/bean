package org.ryuu.bean.string.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {
    @Test
    void empty() {
        boolean isEmpty = StringUtils.EMPTY.isEmpty();
        assertTrue(isEmpty);
    }
}