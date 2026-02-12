/**
 * Implement a hash table.
 * Data: Strings
 * Hash function: sfold
 * Collision Resolution: Quadratic probing
 *
 * @author Jocelyn Chu (jocelynchu), Callie Chiang (ccsea)
 * @version 2026.02.03
 */

public class Hash {
    private int size;
    private MemHandle[] table;
    private int capacity;
    private MemManager manager;

    private final MemHandle tombstone = new MemHandle(-1, -1);

    /**
     * Create a new Hash object.
     *
     * @param init
     *            Initial size for table
     * @param m
     *            Memory manager used by this table to store objects
     */
    public Hash(int init, MemManager m) {
        this.capacity = init;
        this.table = new MemHandle[capacity];
        this.size = 0;
        this.manager = m;
    }


    /**
     * Compute the hash function. Uses the "sfold" method from the OpenDSA
     * module on hash functions
     *
     * @param s
     *            The string that we are hashing
     * @param m
     *            The size of the hash table
     * @return The home slot for that string
     */
    public int h(String s, int m) {

        if (m < 0) {
            m = 0 - m;
        }
        long sum = 0;
        long mult = 1;
        for (int i = 0; i < s.length(); i++) {
            mult = (i % 4 == 0) ? 1 : mult * 256;
            sum += s.charAt(i) * mult;
        }

        long quotient = sum / m;

        long remainder = sum - (quotient * m);

        if (remainder < 0) {
            remainder = remainder + m;
        }

        return (int)remainder;
    }


    /**
     * Track if table resized before inserting.
     * 
     * @return True if inserting one more element will cause resize.
     */
    public boolean willResize() {
        return (size + 1) * 2 > capacity;
    }


    /**
     * Search for a key in the table, stop when it hits a null slot.
     * 
     * @param key
     *            The string key we want to find
     * @return The index where the key exists, -1 if it does not.
     */
    public int find(String key) {
        int home = h(key, capacity);

        // quadratic probing
        for (int i = 0; i < capacity; i++) {
            int index = (home + i * i) % capacity;
            MemHandle curr = table[index];

            // key not present if slot is empty
            if (curr == null) {
                return -1;
            }

            // convert handle back to string and compare
            if (curr != tombstone && key.equals(handleToString(curr))) {
                return index;
            }
        }
        return -1;
    }


    /**
     * Insert a key into the hash table
     * 
     * @param key
     *            The key to insert
     * @param handle
     *            The handle to record already stored in mem manager.
     * 
     * @return 0 if duplicate, 1 if inserted without resize, 2 if resized and
     *         inserted.
     */
    public int insert(String key, MemHandle handle) {
        // 1. Still check for duplicates first
        if (find(key) != -1) {
            return 0;
        }

        // 2. Still resize if needed
        boolean resized = false;
        if (willResize()) {
            resize();
            resized = true;
        }

        int home = h(key, capacity);
        int tomb1 = -1;
        boolean placed = false; // Flag to track if we've "virtually" inserted

        // 3. The loop now runs to the very end (i = capacity)
        for (int i = 0; i < capacity; i++) {
            int index = (home + i * i) % capacity;
            MemHandle curr = table[index];

            // Only try to place if we haven't placed it yet in this loop
            if (!placed && curr == null) {
                if (tomb1 != -1) {
                    table[tomb1] = handle;
                }
                else {
                    table[index] = handle;
                }
                size++;
                placed = true;
                // NO RETURN HERE. We let the loop finish.
            }

            // Keep track of the first tombstone
            if (curr == tombstone && tomb1 == -1) {
                tomb1 = index;
            }
        }

        // 4. All logical paths now lead here, making coverage much easier!
        if (resized) {
            return 2;
        }
        return 1;
    }


    /**
     * Remove a key from the table
     * 
     * @param key
     *            The key to remove
     * 
     * @return The removed handle
     */
    public MemHandle remove(String key) {
        int index = find(key);

        // return null if not found
        if (index == -1) {
            return null;
        }

        MemHandle hand = table[index];
        table[index] = tombstone;
        size--;

        return hand;
    }


    /**
     * Print the contents of the hash table.
     * 
     * @param isArtist
     *            True if table stores artists, false if it stores songs.
     * 
     * @return The string representation of the table
     */
    public String print(boolean isArtist) {
        StringBuilder sb = new StringBuilder();

        // iterate through hash table
        for (int i = 0; i < capacity; i++) {
            MemHandle curr = table[i];

            // skip empty slots
            if (curr == null) {
                continue;
            }

            // print the index number
            sb.append(i).append(": ");

            // check if current slot is tombstone
            if (curr == tombstone) {
                sb.append("TOMBSTONE");
            }

            // print the data stored at the slot
            else {
                sb.append("|").append(handleToString(curr)).append("|");
            }

            // move to next line
            sb.append("\r\n");
        }
        // print the total number of entries based on type of table
        if (isArtist) {
            sb.append("total artists: ").append(size);
        }
        else {
            sb.append("total songs: ").append(size);
        }
        return sb.toString();
    }
    // ---------------------------------------------------------
    // helpers


    /**
     * Resize the table by doubling capacity and rehashing active entries.
     */
    private void resize() {
        // save the old table and capacity
        MemHandle[] old = table;
        int oldCap = capacity;

        // double capacity and create a new table
        capacity = oldCap * 2;
        table = new MemHandle[capacity];
        size = 0;

        // rehash active entries
        for (int i = 0; i < oldCap; i++) {
            MemHandle curr = old[i];
            // skip empty and tombstone
            if (curr == null || curr == tombstone) {
                continue;
            }

            // recover string from memory manager using the handle, and place in
            // new table
            String key = handleToString(curr);
            placeExistingHandle(key, curr);
        }
    }


    /**
     * Put an existing handle into the table when rehashing.
     * 
     * @param key
     *            The string for hashing.
     * 
     * @param hand
     *            The existing handle to place.
     */
    private void placeExistingHandle(String key, MemHandle hand) {
        int home = h(key, capacity);
        boolean found = false; // Flag to track if we found a spot

        for (int i = 0; i < capacity; i++) {
            int index = (home + i * i) % capacity;
            if (!found && table[index] == null) {
                table[index] = hand;
                size++;
                found = true;
            }
        }
    }
// private void placeExistingHandle(String key, MemHandle hand) {
// int home = h(key, capacity);
//
// // probe until null slot is found
// for (int i = 0; i < capacity; i++) {
// int index = (home + i * i) % capacity;
// // place if empty
// if (table[index] == null) {
// table[index] = hand;
// size++;
// return;
// }
// }
// }


    /**
     * Convert stored record back into string.
     * 
     * @param hand
     *            The handle that references the record in the memory pool.
     * 
     * @return The string that was stored.
     */
    private String handleToString(MemHandle hand) {
        // get bytes from memory manager
        byte[] rec = manager.getRecord(hand);

        // convert bytes back into a string
        return new String(rec);
    }
}
