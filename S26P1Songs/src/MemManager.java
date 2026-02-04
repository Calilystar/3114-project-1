import java.util.Arrays;

/**
 * Memory Manager class.
 * This version uses an array in memory.
 * This version implements the buddy method.
 *
 * @author Jocelyn Chu (jocelynchu), Callie Chiang (ccsea)
 * @version 2026.02.03
 */

public class MemManager {

    private byte[] memPool;
    // An array of free lists.
    private Node[] freeLists;
    // Helper for messages about pool expansion.
    private StringBuilder alertExpand;

    /**
     * Create a new MemManager object.
     *
     * @param startSize
     *            Initial size of the memory pool
     */
    public MemManager(int startSize) {
        memPool = new byte[startSize];
        alertExpand = new StringBuilder();
        int maxLevel = log2(startSize);
        freeLists = new Node[maxLevel + 1];
        addFreeBlock(startSize, 0);

    }


    // Store a record and return a handle to it
    public MemHandle insert(byte[] info) {

    }


    // Release the space associated with a record
    public void release(MemHandle h) {
    }


    // Get back a copy of a stored record
    public byte[] getRecord(MemHandle h) {
        return null;
    }
    // ----------------------------------------------------------------
    // helper methods
    
//    /*
//     * Allocates a block of the input size.
//     */
//    private int alloc(int size) {
//        int level = log2(size);
//        if (level < 0) {
//            return -1;
//        }
//    }
    
    /**
     * Compute log2 of some int for power of two sizes.
     */
    private int log2(int x) {
        if (x <= 0) {
            return -1;
        }
        int lvl = 0;
        int y = x;
        while (y > 1) {
            y /= 2;
            lvl++;
        }
        return lvl;
    }
    
    
    // ----------------------------------------------------------------
    /**
     * Singly linked node to represent free blocks in buddy allocator.
     */
    private static class Node {
        int offset;
        Node next;
        
        Node(int off) {
            offset = off;
        }
    }

}
