import student.TestCase;

/**
 * @author Jocelyn Chu (jocelynchu)
 * @version 2026.02.12
 */
public class HashTest extends TestCase {
    private MemManager mem;
    private Hash hash;

    /**
     * Sets up the tests that follow. In general, used for initialization
     */
    public void setUp() {
        // Nothing to do
    }


    /**
     * Test insert normally
     * 
     * @throws Exception
     */
    public void testInsert() throws Exception {
        mem = new MemManager(64);
        hash = new Hash(4, mem);

        MemHandle hand = mem.insert("hi".getBytes());
        int res = hash.insert("hi", hand);
        assertEquals(1, res);

        assertTrue(hash.print(true).contains("|hi|"));
    }


    /**
     * Test insert when there is a duplicate
     * 
     * @throws Exception
     */
    public void testInsertDuplicate() throws Exception {
        mem = new MemManager(64);
        hash = new Hash(4, mem);

        MemHandle hand = mem.insert("hi".getBytes());
        assertEquals(1, hash.insert("hi", hand));

        MemHandle handle = mem.insert("hi".getBytes());
        assertEquals(0, hash.insert("hi", handle));
    }


    /**
     * Test remove to see if it creates a tombstone properly
     * 
     * @throws Exception
     */
    public void testRemove() throws Exception {
        mem = new MemManager(64);
        hash = new Hash(4, mem);

        MemHandle hand = mem.insert("hi".getBytes());
        hash.insert("hi", hand);

        MemHandle bye = hash.remove("hi");
        assertNotNull(bye);

        assertTrue(hash.print(true).contains("TOMBSTONE"));
        assertFalse(hash.print(true).contains("|hi|"));

        assertNull(hash.remove("hi"));

    }


    /**
     * Test insert to see if it resizes properly
     * 
     * @throws Exception
     */
    public void testInsertResize() throws Exception {
        mem = new MemManager(64);
        hash = new Hash(4, mem);

        hash.insert("hi", mem.insert("hi".getBytes()));
        hash.insert("hey", mem.insert("hey".getBytes()));

        int res = hash.insert("hello", mem.insert("hello".getBytes()));
        assertEquals(2, res);
    }


    /**
     * Test resize to see if it skips over tombstones like expected
     * 
     * @throws Exception
     */
    public void testResizeWithTombstone() throws Exception {
        mem = new MemManager(64);
        hash = new Hash(4, mem);

        hash.insert("hi", mem.insert("hi".getBytes()));
        hash.insert("hey", mem.insert("hey".getBytes()));
        hash.remove("hi");

        int res = hash.insert("hello", mem.insert("hello".getBytes()));
        if (res != 2) {
            res = hash.insert("wassup", mem.insert("wassup".getBytes()));
        }
        assertEquals(2, res);

        assertTrue(hash.print(true).contains("|hey|"));
        assertTrue(hash.print(true).contains("|hello|"));
        assertTrue(hash.print(true).contains("|wassup|"));
        assertFalse(hash.print(true).contains("|hi|"));

    }


    /**
     * Test collision after resize
     * 
     * @throws Exception
     */
    public void testCollisionResize() throws Exception {
        mem = new MemManager(64);
        hash = new Hash(4, mem);

        hash.insert("hi", mem.insert("hi".getBytes()));
        hash.insert("hey", mem.insert("hey".getBytes()));

        int res = hash.insert("hello", mem.insert("hello".getBytes()));

        if (res != 2) {
            res = hash.insert("wassup", mem.insert("wassup".getBytes()));

        }

        assertEquals(2, res);

        hash.insert("aloha", mem.insert("aloha".getBytes()));

        assertTrue(hash.print(true).contains("|hi|"));
        assertTrue(hash.print(true).contains("|aloha|"));

    }
}
