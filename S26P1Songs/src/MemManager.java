import java.util.Arrays;

/**
 * Memory Manager class.
 * This version uses an array in memory.
 * This version implements the buddy method.
 *
 * @author Jocelyn Chu (jocelynchu), Callie Chiang (ccsea)
 * @version 2026.02.03
 * 
 * 
 */

public class MemManager {
    // Memory pool.
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


    /**
     * Store a record and return a handle to it
     * 
     * @param info
     *            The byte array of the record
     * 
     * @return The MemHandle where it is stored
     */
    public MemHandle insert(byte[] info) {
        int target = smallestLevel(info.length);
        int search = target;

        while (true) {
            if (search >= freeLists.length) {
                expandPool();
                search = target;
                continue;
            }

            if (freeLists[search] != null) {
                break;
            }

            search = search + 1;

            if (search >= freeLists.length) {
                expandPool();
                search = target;

            }
        }
        Node block = freeLists[search];
        freeLists[search] = block.next;

        int offset = block.offset;

        while (search > target) {
            search = search - 1;
            int half = blockSize(search);
            int rightOffset = offset + half;
            addFreeBlock(half, rightOffset);
        }

        System.arraycopy(info, 0, memPool, offset, info.length);
        return new MemHandle(offset, info.length);

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
        int lvl = smallestLevel(h.getLength());
        int size = blockSize(lvl);
        int offset = h.getStart();

        while (lvl < freeLists.length) {
            int buddy = offset ^ size;

            if (!removeFreeBlock(lvl, buddy)) {
                break;
            }

            if (buddy < offset) {
                offset = buddy;
            }

            size = size * 2;
            lvl = lvl + 1;

        }
        addFreeBlock(size, offset);
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
        System.arraycopy(memPool, h.getStart(), bye, 0, h.getLength());
        return bye;
    }


    /**
     * Print free block info.
     * 
     * @return The free block info.
     */
    public String printBlocks() {
        StringBuilder sb = new StringBuilder();
        boolean free = false;

        for (int lvl = 0; lvl < freeLists.length; lvl++) {
            if (freeLists[lvl] == null) {
                continue;
            }
            free = true;
            sb.append(blockSize(lvl)).append(": ");

            Node curr = freeLists[lvl];
            while (curr != null) {
                sb.append(curr.offset);
                if (curr.next != null) {
                    sb.append(" ");

                }
                curr = curr.next;

            }
            sb.append("\r\n");

        }
        if (!free) {
            return "No free blocks are available.";

        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    // ----------------------------------------------------------------
    // helper methods


    /**
     * Add a free block to the appropriate free list.
     * 
     * @param blockSize
     *            The size of the free block.
     * @param offset
     *            The starting offset of the free block.
     */
    private void addFreeBlock(int blockSize, int offset) {

        if (blockSize <= 0) {
            return;
        }

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
     * Removes free block from a level list.
     * 
     * @param level
     *            The level list.
     * 
     * @param offset
     *            The offset of the block to remove.
     * @return True if the block was removed, false if not.
     */
    private boolean removeFreeBlock(int level, int offset) {
        Node prev = null;
        Node curr = freeLists[level];

        while (curr != null) {
            if (curr.offset == offset) {
                if (prev == null) {
                    freeLists[level] = curr.next;

                }
                else {
                    prev.next = curr.next;
                }
                return true;
            }
            prev = curr;
            curr = curr.next;
        }
        return false;
    }


    /**
     * Expand memory pool
     */
    private void expandPool() {
        int oldSize = memPool.length;
        int newSize = memPool.length * 2;
        byte[] newPool = new byte[newSize];

        System.arraycopy(memPool, 0, newPool, 0, memPool.length);

        memPool = newPool;
///
        Node[] copyFreeLists = new Node[freeLists.length + 1];

        System.arraycopy(freeLists, 0, copyFreeLists, 0, freeLists.length);

        freeLists = copyFreeLists;

        addFreeBlock(oldSize, oldSize);
///
        alertExpand.append("Memory pool expanded to be ").append(newSize)
            .append(" bytes\r\n");
    }


    /**
     * Find the smallest level for 2^level >= length.
     * 
     * @param length
     *            The record length in bytes.
     * 
     * @return The level.
     */
    private int smallestLevel(int length) {
        int level = 0;
        int size = 1;

        while (size < length) {
            size = size * 2;
            level = level + 1;

        }

        return level;
    }


    /**
     * Determine the block size for level.
     * 
     * @param level
     *            The level in the free list.
     * 
     * @return The block size.
     */
    private int blockSize(int level) {
        int size = 1;
        for (int i = 0; i < level; i++) {
            size = size * 2;

        }
        return size;
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

// private int power(int num, int pow) {
// if (num == 0) {
// return 0;
// }
// int total = 1;
// while (pow > 0) {
// total = total * num;
// pow -= 1;
// }
// return total;
//
// }

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

//
//        public void setNext(Node nextNode) {
//            next = nextNode;
//        }
//    }

