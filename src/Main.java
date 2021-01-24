import fileio.Reader;
import fileio.Writer;
import game.Game;

/**
 * Entry point to the simulation
 */
public final class Main {

    private Main() { }

    /**
     * Main function which reads the input file and starts simulation
     *
     * @param args input and output files
     * @throws Exception might error when reading/writing/opening files, parsing JSON
     */
    public static void main(final String[] args) throws Exception {
        Reader reader = new Reader(args[0]);
        Writer writer = new Writer(args[1]);

        // Initialize game, play game and finish game
        Game game = reader.readGameData();
        game.startGame();
        writer.closeJSON(game.finishGame(writer));
    }
}
