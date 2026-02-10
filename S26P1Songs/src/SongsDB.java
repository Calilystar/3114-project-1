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

    // ----------------------------------------------------------
    /**
     * Create a new SongsDB object.
     * But don't set anything -- that gets done by "create"
     */
    public SongsDB() {
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
        
        boolean ensurePowerOfTwo = false;
        
        int num = 0;
        while( power(2, num) <= inMemMan) {
            if (power(2, num) == inMemMan) {
                ensurePowerOfTwo = true;
            }
            num += 1;
        }

        if (inHash > 0 || (inMemMan > 0 && ensurePowerOfTwo)) {
            this.memMana = new MemManager(inMemMan);
            this.artists = new Hash(inHash, memMana);
            this.songs = new Hash(inHash, memMana);

            this.initHashSize = inHash;
            this.initMemSize = inMemMan;
            return "";
        }
        else {
            return "Cannot create.";
        }
    }


    /**
     * Re-initialize the database
     * 
     * @return true on successful clear of database
     */
    public boolean clear() {
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
        StringBuilder str = new StringBuilder();
        if (artistString == null || artistString.isEmpty() || 
            songString == null || songString.isEmpty()) {
            return "Cannot insert.";
        }

        int insertedArtist = artists.insert(artistString);

        str.append(memMana.getExpandMethod());

        if (insertedArtist == 2) {
            str.append("Artist hash table size doubled\r\n");
        }

        else if (insertedArtist == 0) {
            str.append("|").append(artistString).append(
                "| duplicates a record already in the Artist database\r\n");
        }
        else {
            str.append("|").append(artistString).append(
                "| is added to the Artist database\r\n");
        }

        if (artistString.equals("") || songString.equals("")) {
            return "Cannot insert.";
        }

        int insertedSong = songs.insert(songString);
        str.append(memMana.getExpandMethod());

        if (insertedSong == 2) {
            str.append("Song hash table size doubled\r\n");
        }

        else if (insertedSong == 0) {
            str.append("|").append(songString).append(
                "| duplicates a record already in the Song database\r\n");
        }
        else {
            str.append("|").append(songString).append(
                "| is added to the Song database\r\n");
        }

        if (artistString == "" || songString == "") {
            return "Cannot insert.";
        }

        else {
            
            return str.toString();
        }
    }


    // ----------------------------------------------------------
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
        StringBuilder str = new StringBuilder();
        

        

        if (type.equals("artist")) {
            boolean name = this.artists.remove(nameString);
            if (name) {
                str.append("|").append(nameString).append(
                    "| is removed from the Artist database\r\n");
            }
            else {
                str.append("|").append(nameString).append(
                    "| does not exist.\r\n");
            }
        }

        else if (type.equals("song")) {
            boolean song = this.songs.remove(nameString);
            if (song) {
                str.append("|").append(nameString).append(
                    "| is removed from the Song database\r\n");
            }
            else {
                str.append("|").append(nameString).append(
                    "| does not exist.\r\n");
            }

        }

        
        else {
            return "Bad type value |" + type + "| on remove";
        }
        
        return str.toString();
    }


    // ----------------------------------------------------------
    /**
     * Print out the hash table contents
     *
     * @param type
     *            Controls what object is being printed
     * @return The string that was printed
     * @throws IOException
     */
    public String print(String type) throws IOException {
        if (type.equals("artist")) {
            return artists.toString();
        }
        if (type.equals("song")) {
            return songs.toString();
        }
        if (type.equals("blocks")) {
            return memMana.printBlocks();
        }
        else {  
            return "Cannot print.";
        }
    }
}
