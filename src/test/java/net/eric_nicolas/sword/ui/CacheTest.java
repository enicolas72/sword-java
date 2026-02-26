package net.eric_nicolas.sword.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Cache — FIFO eviction, size limits, and basic operations.
 */
class CacheTest {

    private Cache<String, Integer> cache;

    @BeforeEach
    void setUp() {
        cache = new Cache<>(3);
    }

    // ===== Construction =====

    @Test
    void testMaxSizeStored() {
        assertEquals(3, cache.maxSize());
    }

    @Test
    void testInitiallyEmpty() {
        assertEquals(0, cache.size());
    }

    @Test
    void testMaxSizeOneIsValid() {
        assertDoesNotThrow(() -> new Cache<>(1));
    }

    @Test
    void testMaxSizeZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Cache<>(0));
    }

    @Test
    void testNegativeMaxSizeThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Cache<>(-5));
    }

    // ===== put / get =====

    @Test
    void testGetMissingKeyReturnsNull() {
        assertNull(cache.get("absent"));
    }

    @Test
    void testPutAndGet() {
        cache.put("a", 1);
        assertEquals(1, cache.get("a"));
    }

    @Test
    void testSizeAfterPuts() {
        cache.put("a", 1);
        cache.put("b", 2);
        assertEquals(2, cache.size());
    }

    @Test
    void testUpdateExistingKey() {
        cache.put("a", 1);
        cache.put("a", 99);
        assertEquals(99, cache.get("a"));
    }

    @Test
    void testUpdateDoesNotGrowSize() {
        cache.put("a", 1);
        cache.put("a", 2);
        assertEquals(1, cache.size());
    }

    // ===== FIFO eviction =====

    @Test
    void testFifoEvictsOldestOnOverflow() {
        cache.put("a", 1);   // inserted first
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4);   // "a" should be evicted
        assertNull(cache.get("a"));
    }

    @Test
    void testFifoKeepsNewestEntries() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4);
        assertEquals(2, cache.get("b"));
        assertEquals(3, cache.get("c"));
        assertEquals(4, cache.get("d"));
    }

    @Test
    void testSizeDoesNotExceedMax() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4);
        assertEquals(3, cache.size());
    }

    @Test
    void testFifoWithMaxSizeOne() {
        Cache<String, Integer> tiny = new Cache<>(1);
        tiny.put("a", 1);
        tiny.put("b", 2);   // evicts "a"
        assertNull(tiny.get("a"));
        assertEquals(2, tiny.get("b"));
        assertEquals(1, tiny.size());
    }

    @Test
    void testMultipleEvictions() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4);   // evicts "a"
        cache.put("e", 5);   // evicts "b"
        assertNull(cache.get("a"));
        assertNull(cache.get("b"));
        assertEquals(3, cache.get("c"));
        assertEquals(4, cache.get("d"));
        assertEquals(5, cache.get("e"));
    }

    // ===== contains =====

    @Test
    void testContainsPresentKey() {
        cache.put("x", 10);
        assertTrue(cache.contains("x"));
    }

    @Test
    void testContainsAbsentKey() {
        assertFalse(cache.contains("x"));
    }

    @Test
    void testContainsAfterEviction() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4);   // evicts "a"
        assertFalse(cache.contains("a"));
    }

    // ===== clear =====

    @Test
    void testClearEmptiesCache() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.clear();
        assertEquals(0, cache.size());
    }

    @Test
    void testGetAfterClear() {
        cache.put("a", 1);
        cache.clear();
        assertNull(cache.get("a"));
    }

    @Test
    void testPutAfterClear() {
        cache.put("a", 1);
        cache.clear();
        cache.put("a", 2);
        assertEquals(2, cache.get("a"));
        assertEquals(1, cache.size());
    }
}
