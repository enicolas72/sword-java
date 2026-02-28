package net.eric_nicolas.sword.ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Triple — getters, equality, and hashing.
 */
class TripleTest {

    // ===== Getters =====

    @Test
    void testGetters() {
        Triple<String, Integer, Boolean> t = new Triple<>("hello", 42, true);
        assertEquals("hello", t.x());
        assertEquals(42,      t.y());
        assertEquals(true,    t.z());
    }

    @Test
    void testNullValues() {
        Triple<String, Integer, Boolean> t = new Triple<>(null, null, null);
        assertNull(t.x());
        assertNull(t.y());
        assertNull(t.z());
    }

    // ===== equals =====

    @Test
    void testEqualsSameInstance() {
        Triple<String, Integer, Boolean> t = new Triple<>("x", 1, true);
        assertEquals(t, t);
    }

    @Test
    void testEqualsEqualComponents() {
        assertEquals(new Triple<>("x", 1, true), new Triple<>("x", 1, true));
    }

    @Test
    void testNotEqualsDifferentX() {
        assertNotEquals(new Triple<>("x", 1, true), new Triple<>("y", 1, true));
    }

    @Test
    void testNotEqualsDifferentY() {
        assertNotEquals(new Triple<>("x", 1, true), new Triple<>("x", 2, true));
    }

    @Test
    void testNotEqualsDifferentZ() {
        assertNotEquals(new Triple<>("x", 1, true), new Triple<>("x", 1, false));
    }

    @Test
    void testNotEqualsNull() {
        assertNotEquals(null, new Triple<>("x", 1, true));
    }

    @Test
    void testNotEqualsDifferentType() {
        assertNotEquals("x", new Triple<>("x", 1, true));
    }

    @Test
    void testEqualsNullComponents() {
        assertEquals(new Triple<>(null, null, null), new Triple<>(null, null, null));
    }

    @Test
    void testNotEqualsOneNullX() {
        assertNotEquals(new Triple<>(null, 1, true), new Triple<>("x", 1, true));
    }

    @Test
    void testNotEqualsOneNullZ() {
        assertNotEquals(new Triple<>("x", 1, null), new Triple<>("x", 1, true));
    }

    // ===== hashCode =====

    @Test
    void testHashCodeConsistency() {
        Triple<String, Integer, Boolean> t = new Triple<>("x", 1, true);
        assertEquals(t.hashCode(), t.hashCode());
    }

    @Test
    void testHashCodeEqualObjects() {
        assertEquals(
            new Triple<>("x", 1, true).hashCode(),
            new Triple<>("x", 1, true).hashCode());
    }

    // ===== toString =====

    @Test
    void testToStringContainsValues() {
        String s = new Triple<>("hello", 99, false).toString();
        assertTrue(s.contains("hello"));
        assertTrue(s.contains("99"));
        assertTrue(s.contains("false"));
    }

    // ===== Use as map key =====

    @Test
    void testUsableAsMapKey() {
        java.util.Map<Triple<String, Integer, Boolean>, String> map = new java.util.HashMap<>();
        Triple<String, Integer, Boolean> key = new Triple<>("a", 1, true);
        map.put(key, "value");
        assertEquals("value", map.get(new Triple<>("a", 1, true)));
    }
}
