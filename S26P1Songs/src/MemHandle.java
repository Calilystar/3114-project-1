/**
 * Keeps track of where the record is located in the memory pool.
 * 
 * @author Jocelyn Chu (jocelynchu), Callie Chiang (ccsea)
 * @version 2026.02.03
 */
public class MemHandle {
    private int start;
    private int len;

    /**
     * Create a new MemHandle object.
     * 
     * @param start
     *            The index in memory pool where record begins.
     * @param len
     *            The length of record in bytes.
     */
    public MemHandle(int start, int len) {
        this.start = start;
        this.len = len;
    }


    /**
     * Returns the index in memory pool where the record begins.
     * 
     * @return The start index
     */
    public int getStart() {
        return start;
    }


    /*
     * Returns the length of the record in bytes.
     * 
     * @return The number of bytes.
     * 
     */
    public int getLength() {
        return len;
    }
}
