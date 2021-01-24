package fileio;

import entities.*;
import game.Game;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public final class Reader {
    private final String inputFile;

    /**
     * Constructor for Reader
     *
     * @param inputFile file to read from
     */
    public Reader(final String inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Reads all entities, monthly updates and number of turns and returns a Game instance with
     * the read data
     *
     * @return a Game instance
     */
    public Game readGameData() {
        EntityFactory factory = EntityFactory.getInstance();
        JSONParser parser = new JSONParser();

        int numberOfTurns = 0;
        JSONArray monthlyUpdates = null;
        List<Consumer> consumers = new ArrayList<>();
        List<Distributor> distributors = new ArrayList<>();
        List<Producer> producers = new ArrayList<>();

        // Reads all data
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(inputFile));
            JSONObject initialData = (JSONObject) jsonObject.get("initialData");
            JSONArray initialConsumers = (JSONArray) initialData.get("consumers");
            JSONArray initialDistributors = (JSONArray) initialData.get("distributors");
            JSONArray initialProducers = (JSONArray) initialData.get("producers");

            numberOfTurns = ((Long) jsonObject.get("numberOfTurns")).intValue();
            monthlyUpdates = (JSONArray) jsonObject.get("monthlyUpdates");

            // Uses the factory to create new Consumers
            for (Object consumer : initialConsumers) {
                consumers.add(
                        (Consumer) factory.createEntity(
                                EntityType.CONSUMER, (JSONObject) consumer)
                );
            }

            // Uses the factory to create new Distributors
            for (Object distributor : initialDistributors) {
                distributors.add(
                        (Distributor) factory.createEntity(
                                EntityType.DISTRIBUTOR, (JSONObject) distributor)
                );
            }

            // Uses the factory to create new Producers
            for (Object producer : initialProducers) {
                producers.add(
                        (Producer) factory.createEntity(
                                EntityType.PRODUCER, (JSONObject) producer)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Game(numberOfTurns, monthlyUpdates, consumers, distributors, producers);
    }
}
