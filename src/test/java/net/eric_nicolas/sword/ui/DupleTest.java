package net.eric_nicolas.sword.ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Duple — getters, equality, and hashing.
 */
class DupleTest {

    // ===== Getters =====

    @Test
    void testGetters() {
        Duple<String, Integer> d = new Duple<>("hello", 42);
        assertEquals("hello", d.x());
        assertEquals(42,      d.y());
    }

    @Test
    void testNullValues() {
        Duple<String, Integer> d = new Duple<>(null, null);
        assertNull(d.x());
        assertNull(d.y());
    }

    // ===== equals =====

    @Test
    void testEqualsSameInstance() {
        Duple<String, Integer> d = new Duple<>("x", 1);
        assertEquals(d, d);
    }

    @Test
    void testEqualsEqualComponents() {
        assertEquals(new Duple<>("x", 1), new Duple<>("x", 1));
    }

    @Test
    void testNotEqualsDifferentX() {
        assertNotEquals(new Duple<>("x", 1), new Duple<>("y", 1));
    }

    @Test
    void testNotEqualsDifferentY() {
        assertNotEquals(new Duple<>("x", 1), new Duple<>("x", 2));
    }

    @Test
    void testNotEqualsNull() {
        assertNotEquals(null, new Duple<>("x", 1));
    }

    @Test
    void testNotEqualsDifferentType() {
        assertNotEquals("x", new Duple<>("x", 1));
    }

    @Test
    void testEqualsNullComponents() {
        assertEquals(new Duple<>(null, null), new Duple<>(null, null));
    }

    @Test
    void testNotEqualsOneNullX() {
        assertNotEquals(new Duple<>(null, 1), new Duple<>("x", 1));
    }

    // ===== hashCode =====

    @Test
    void testHashCodeConsistency() {
        Duple<String, Integer> d = new Duple<>("x", 1);
        assertEquals(d.hashCode(), d.hashCode());
    }

    @Test
    void testHashCodeEqualObjects() {
        assertEquals(
            new Duple<>("x", 1).hashCode(),
            new Duple<>("x", 1).hashCode());
    }

    // ===== toString =====

    @Test
    void testToStringContainsValues() {
        String s = new Duple<>("hello", 99).toString();
        assertTrue(s.contains("hello"));
        assertTrue(s.contains("99"));
    }

    // ===== Use as map key =====

    @Test
    void testUsableAsMapKey() {
        java.util.Map<Duple<String, Integer>, String> map = new java.util.HashMap<>();
        Duple<String, Integer> key = new Duple<>("a", 1);
        map.put(key, "value");
        assertEquals("value", map.get(new Duple<>("a", 1)));
    }
}
