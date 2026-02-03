
/**
 * Memory Manager class.
 * This version uses an array in memory.
 * This version implements the buddy method.
 *
 * @author Jocelyn Chu (jocelynchu), Callie Chiang (ccsea)
 * @version 12.02.2026
 */

public class MemManager {
    
    private byte[] memPool;
    private MemHandle<T>[] table
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
    public MemHandle insert(byte[] info);

    // Release the space associated with a record
    public void release(MemHandle h);

    // Get back a copy of a stored record
    public byte[] getRecord(MemHandle h);
    
    
    
}
