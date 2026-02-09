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

        if (inHash > 0 || inMemMan != 0) {
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
        if (artistString == "" || songString == "") {
            return "Cannot insert.";
        }
        else {
            this.artists.insert(artistString);
            this.songs.insert(songString);
            return "";
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
        
        if(type.equals("artist")) {
           if( this.artists.remove(nameString)) {
               return "";
           }
            
        }
        else if (type.equals("song")) {
            if (this.songs.remove(nameString)) {
                return "";
            }
            
        }
        return "Cannot remove.";
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
        else {
            return "Cannot print.";
        }
    }
}
