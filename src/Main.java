import fileio.Reader;
import fileio.Writer;
import game.Game;

public final class Main {

    private Main() {
    }

    /**
     * Main method
     *
     * @param args input and output file
     * @throws Exception for IO
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
