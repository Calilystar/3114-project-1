import java.io.IOException;

/**
 * The database implementation for this project.
 * We have two hash tables and a memory manager.
 *
 * @author Jocelyn Chu (jocelynchu), Callie Chiang (ccsea)
 * @version 2026.02.08
 */
public class SongsDB implements Songs {
    private MemManager memMana;
    private Hash artists;
    private Hash songs;
    private int initHashSize;
    private int initMemSize;
    private boolean init;

    // ----------------------------------------------------------
    /**
     * Create a new SongsDB object.
     * But don't set anything -- that gets done by "create"
     */
    public SongsDB() {
        init = false;
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


    /**
     * Create a brave new World.
     *
     * @param inHash
     *            Initial size for hash tables
     * @param inMemMan
     *            Initial size for the memory manager
     * @return Error messages if appropriate
     */
    public String create(int inHash, int inMemMan) {

        if (inHash <= 0) {
            return "Initial hash table size must be positive";

        }

        if (inMemMan <= 0) {
            return "Initial memory manager size must be positive";

        }

        if (!isPowerOfTwo(inMemMan)) {
            return "Initial memory manager size must be a power of 2";
        }

        this.memMana = new MemManager(inMemMan);
        this.artists = new Hash(inHash, memMana);
        this.songs = new Hash(inHash, memMana);

        this.initHashSize = inHash;
        this.initMemSize = inMemMan;

        init = true;

        return "";

// boolean ensurePowerOfTwo = false;
//
// int num = 0;
// while (power(2, num) <= inMemMan) {
// if (power(2, num) == inMemMan) {
// ensurePowerOfTwo = true;
// }
// num += 1;
// }
//
// if (inHash > 0 || (inMemMan > 0 && ensurePowerOfTwo)) {
//
// }
// else {
// return "Cannot create.";
// }
    }


    /**
     * Re-initialize the database
     * 
     * @return true on successful clear of database
     */
    public boolean clear() {

        if (!init) {
            return false;
        }

        this.memMana = new MemManager(initMemSize);
        this.artists = new Hash(initHashSize, memMana);
        this.songs = new Hash(initHashSize, memMana);
        return true;
    }


    // ----------------------------------------------------------
    /**
     * Insert to the hash table
     *
     * @param artistString
     *            Artist string to insert
     * @param songString
     *            Song string to insert
     * @return Error message if appropriate
     * @throws IOException
     */
    public String insert(String artistString, String songString)
        throws IOException {

        if (!init) {
            return "Database not initialized";
        }

        if (artistString == null || songString == null) {
            return "Input strings cannot be null or empty";
        }

        if (artistString.equals("") || songString.equals("")) {
            return "Input strings cannot be null or empty";
        }

        StringBuilder str = new StringBuilder();

        // for artists
        if (artists.find(artistString) != -1) {
            str.append("|").append(artistString).append(
                "| duplicates a record already in the Artist database\r\n");
        }
        else {
            byte[] artBytes = artistString.getBytes();
            MemHandle artHand = memMana.insert(artBytes);
            str.append(memMana.getExpandMethod());

            int res = artists.insert(artistString, artHand);
            if (res == 2) {
                str.append("Artist hash table size doubled\r\n");
            }
            str.append("|").append(artistString).append(
                "| is added to the Artist database\r\n");
        }

        // for songs
        if (songs.find(songString) != -1) {
            str.append("|").append(songString).append(
                "| duplicates a record already in the Song database");
        }

        else {
            byte[] songBytes = songString.getBytes();
            MemHandle songHand = memMana.insert(songBytes);

            str.append(memMana.getExpandMethod());

            int res2 = songs.insert(songString, songHand);
            if (res2 == 2) {
                str.append("Song hash table size doubled\r\n");
            }
            str.append("|").append(songString).append(
                "| is added to the Song database");
        }

        return str.toString();

    }


    /**
     * Remove from the hash table
     *
     * @param type
     *            The table to be removed
     * @param nameString
     *            The string to be removed from the table
     * @return Error message if appropriate
     * @throws IOException
     */
    public String remove(String type, String nameString) throws IOException {
        if (!init) {
            return "Database not initialized";
        }

        if (type == null || nameString == null) {
            return "Input strings cannot be null or empty";
        }

        if (type.equals("") || nameString.equals("")) {
            return "Input strings cannot be null or empty";
        }

        if (!type.equals("artist") && !type.equals("song")) {
            return "Bad type value |" + type + "| on remove";
        }

        if (type.equals("artist")) {
            MemHandle removed = artists.remove(nameString);
            if (removed == null) {
                return "|" + nameString
                    + "| does not exist in the Artist database";
            }
            memMana.release(removed);
            return "|" + nameString + "| is removed from the Artist database";
        }

        MemHandle removed = songs.remove(nameString);

        if (removed == null) {
            return "|" + nameString + "| does not exist in the Song database";
        }

        memMana.release(removed);

        return "|" + nameString + "| is removed from the Song database";
    }


    /**
     * Print out the hash table contents
     *
     * @param type
     *            Controls what object is being printed
     * @return The string that was printed
     * @throws IOException
     */
    public String print(String type) throws IOException {
        if (!init) {
            return "Database not initialized";
        }

        if (type == null || type.equals("")) {
            return "Input strings cannot be null or empty";
        }
        if (type.equals("artist")) {
            return artists.print(true);
        }
        if (type.equals("song")) {
            return songs.print(false);
        }
        if (type.equals("blocks")) {
            return memMana.printBlocks();
        }

        return "Bad print parameter.";
    }

    // --------------------------------------------------------------
    // helpers


    /**
     * Determine if number is power of two.
     * 
     * @param num
     *            The number.
     * 
     * @return True if is power of 2, false if not.
     */
    private boolean isPowerOfTwo(int num) {
        if (num <= 0) {
            return false;
        }
        return (num & (num - 1)) == 0;
    }
}
