package net.eric_nicolas.sword.mechanism;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TAtom - tree structure operations.
 */
class TAtomTest {

    private TAtom parent;
    private TAtom child1;
    private TAtom child2;
    private TAtom child3;

    @BeforeEach
    void setUp() {
        parent = new TAtom();
        child1 = new TAtom();
        child2 = new TAtom();
        child3 = new TAtom();
    }

    @Test
    void testInitialState() {
        TAtom atom = new TAtom();
        assertNull(atom.next());
        assertNull(atom.previous());
        assertNull(atom.son());
        assertNull(atom.father());
    }

    @Test
    void testInsertFirstChild() {
        child1.insertIn(parent);

        assertEquals(parent, child1.father());
        assertEquals(child1, parent.son());
        assertNull(child1.next());
        assertNull(child1.previous());
    }

    @Test
    void testInsertMultipleChildren() {
        child1.insertIn(parent);
        child2.insertIn(parent);
        child3.insertIn(parent);

        // Check parent-child relationships
        assertEquals(parent, child1.father());
        assertEquals(parent, child2.father());
        assertEquals(parent, child3.father());

        // Check sibling relationships
        assertEquals(child1, parent.son());
        assertEquals(child2, child1.next());
        assertEquals(child3, child2.next());
        assertNull(child3.next());

        // Check previous links
        assertNull(child1.previous());
        assertEquals(child1, child2.previous());
        assertEquals(child2, child3.previous());
    }

    @Test
    void testRemoveFirstChild() {
        child1.insertIn(parent);
        child2.insertIn(parent);

        child1.remove();

        assertEquals(child2, parent.son());
        assertNull(child1.father());
        assertNull(child2.previous());
    }

    @Test
    void testRemoveMiddleChild() {
        child1.insertIn(parent);
        child2.insertIn(parent);
        child3.insertIn(parent);

        child2.remove();

        assertEquals(child3, child1.next());
        assertEquals(child1, child3.previous());
        assertNull(child2.father());
    }

    @Test
    void testRemoveLastChild() {
        child1.insertIn(parent);
        child2.insertIn(parent);

        child2.remove();

        assertNull(child1.next());
        assertNull(child2.father());
    }

    @Test
    void testLast() {
        child1.insertIn(parent);
        child2.insertIn(parent);
        child3.insertIn(parent);

        assertEquals(child3, child1.last());
        assertEquals(child3, child2.last());
        assertEquals(child3, child3.last());
    }

    @Test
    void testBringToFront() {
        child1.insertIn(parent);
        child2.insertIn(parent);
        child3.insertIn(parent);

        // Bring child1 to front
        child1.bringToFront();

        // child1 should now be last
        assertEquals(child2, parent.son());
        assertEquals(child3, child2.next());
        assertEquals(child1, child3.next());
        assertNull(child1.next());
    }

    @Test
    void testBringToFrontWhenAlreadyLast() {
        child1.insertIn(parent);
        child2.insertIn(parent);

        // child2 is already last
        child2.bringToFront();

        // Nothing should change
        assertEquals(child1, parent.son());
        assertEquals(child2, child1.next());
        assertNull(child2.next());
    }

    @Test
    void testBringToFrontWithNoParent() {
        // Should not throw exception
        assertDoesNotThrow(() -> child1.bringToFront());
    }
}
