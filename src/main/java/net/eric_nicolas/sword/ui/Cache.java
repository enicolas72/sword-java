package net.eric_nicolas.sword.ui;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cache<K, V> - A bounded, FIFO-eviction cache.
 *
 * Stores up to {@code maxSize} key-value pairs.  When a new entry would
 * exceed the capacity, the oldest inserted entry (first in) is evicted
 * before the new entry is stored (first out).
 *
 * Not thread-safe; intended for single-threaded use (e.g. the GLFW main
 * thread in SWORD).
 */
public class Cache<K, V> {

    private final int maxSize;
    private final LinkedHashMap<K, V> map;

    /**
     * @param maxSize maximum number of entries; must be &gt;= 1
     */
    public Cache(int maxSize) {
        if (maxSize < 1) throw new IllegalArgumentException("maxSize must be >= 1");
        this.maxSize = maxSize;
        // insertion-order LinkedHashMap; removeEldestEntry implements FIFO eviction
        this.map = new LinkedHashMap<>(16, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > Cache.this.maxSize;
            }
        };
    }

    /**
     * Return the value associated with {@code key}, or {@code null} if absent.
     */
    public V get(K key) {
        return map.get(key);
    }

    /**
     * Insert or update an entry.  If the cache is full, the oldest entry is
     * evicted first (FIFO policy).
     */
    public void put(K key, V value) {
        map.put(key, value);
    }

    /** Return {@code true} if the cache contains an entry for {@code key}. */
    public boolean contains(K key) {
        return map.containsKey(key);
    }

    /** Current number of entries (0 … maxSize). */
    public int size() {
        return map.size();
    }

    /** Maximum capacity this cache was constructed with. */
    public int maxSize() {
        return maxSize;
    }

    /** Remove all entries. */
    public void clear() {
        map.clear();
    }
}
