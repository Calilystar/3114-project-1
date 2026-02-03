
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
     /**
     * Create a new MemManager object.
     *
     * @param startSize
     *            Initial size of the memory pool
     */
    public MemManager(int startSize) {
        memPool = new byte[startSize];
        
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
    
    
    
}
