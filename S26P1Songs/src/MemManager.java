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
    // Memory pool.
    private byte[] memPool;
    // An array of free lists.
    private Node[] freeLists;
    // Helper for messages about pool expansion.
    private StringBuilder alertExpand;
    // Location of next free byte.
    private int nextFree;

    /**
     * Create a new MemManager object.
     *
     * @param startSize
     *            Initial size of the memory pool
     */
    public MemManager(int startSize) {
        memPool = new byte[startSize];
        alertExpand = new StringBuilder();
        nextFree = 0;
        int maxLevel = log2(startSize);
        freeLists = new Node[maxLevel + 1];
        addFreeBlock(startSize, 0);

    }


    /**
     * Add a free block to the appropriate free list.
     * 
     * @param blockSize
     *            The size of the free block.
     * @param offset
     *            The starting offset of the free block.
     */
    private void addFreeBlock(int blockSize, int offset) {
        // level corresponding to block size
        int level = log2(blockSize);

        // new node to insert
        Node nod = new Node(offset);

        // insert into empty list or at the front if smallest offset
        if (freeLists[level] == null || offset < freeLists[level].offset) {
            nod.next = freeLists[level];
            freeLists[level] = nod;
            return;
        }

        // otherwise insert in sorted position
        Node curr = freeLists[level];

        while (curr.next != null && curr.next.offset < offset) {
            curr = curr.next;
        }

        nod.next = curr.next;
        curr.next = nod;
    }


    /**
     * Store a record and return a handle to it
     * 
     * @param info
     *            The byte array of the record
     * 
     * @return The MemHandle where it is stored
     */
    public MemHandle insert(byte[] info) {
        // expand until there's enough space
        while (nextFree + info.length > memPool.length) {
            expandPool();
        }

        int start = nextFree;

        // copy bytes into memory
        System.arraycopy(info, 0, memPool, start, info.length);

        nextFree += info.length;

        return new MemHandle(start, info.length);
    }


    /**
     * Get expansion message
     * 
     * @return The expansion message.
     */
    public String getExpandMethod() {
        String msg = alertExpand.toString();
        alertExpand.setLength(0);
        return msg;
    }
    
    
    /**
     * Release the space associated with a record
     * 
     * @param h
     *            The handle to record to remove
     */
    public void release(MemHandle h) {
        int offset = h.getStart() - 1;
        int level = log2(offset);
        Node node = new Node(offset);

        node.next = freeLists[level];
        node = freeLists[level];

    }


    /**
     * Get back a copy of a stored record
     * 
     * @param h
     *            The handle to record
     * 
     * @return The copy of record bytes
     */
    public byte[] getRecord(MemHandle h) {
        byte[] bye = new byte[h.getLength()];
        for (int i = 0; i < h.getLength(); i++) {
            bye[i] = memPool[h.getStart() + i];
        }
        return bye;
    }
    
    /**
     * Print free block info.
     * 
     * @return The free block info.
     */
    public String printBlocks() {
        if (nextFree == memPool.length) {
            return "No free blocks available.";
        }
        return (memPool.length - nextFree) + " bytes free";
    }
    // ----------------------------------------------------------------
    // helper methods

// /*
// * Allocates a block of the input size.
// */
// private int alloc(int size) {
// int level = log2(size);
// if (level < 0) {
// return -1;
// }
// }


    /**
     * Expand memory pool
     */
    private void expandPool() {
        int newSize = memPool.length * 2;
        byte[] newPool = new byte[newSize];

        System.arraycopy(memPool, 0, newPool, 0, memPool.length);

        memPool = newPool;

        alertExpand.append("Memory pool expanded to ").append(newSize).append(
            " bytes\r\n");
    }


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


    private int power(int num, int pow) {
        if (num == 0) {
            return 0;
        }
        int total = 1;
        while (pow > 0) {
            total = total * num;
            pow -= 1;
        }
        return total;

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


        public void setNext(Node nextNode) {
            next = nextNode;
        }
    }

}
