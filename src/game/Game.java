package game;

import entities.Consumer;
import entities.Distributor;
import entities.Producer;
import fileio.Writer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Game {
    private final List<Consumer> consumers;
    private final List<Distributor> distributors;
    private final List<Producer> producers;
    private final List<Consumer> bankruptConsumers = new ArrayList<>();
    private final List<Distributor> bankruptDistributors = new ArrayList<>();
    private final int numberOfTurns;
    private final JSONArray monthlyUpdates;

    /**
     * Constructor for Game
     *
     * @param numberOfTurns  the number of turns for the game
     * @param monthlyUpdates the monthly updates for the game
     * @param consumers      the initial consumers for the game
     * @param distributors   the initial distributors for the game
     */
    public Game(final int numberOfTurns, final JSONArray monthlyUpdates,
                final List<Consumer> consumers, final List<Distributor> distributors,
                final List<Producer> producers) {
        this.numberOfTurns = numberOfTurns;
        this.monthlyUpdates = monthlyUpdates;
        this.consumers = consumers;
        this.distributors = distributors;
        this.producers = producers;
    }

    /**
     * Starts game by doing all tasks necessary each month
     */
    public void startGame() {
        GameRules gameRules = new GameRules(consumers, distributors, producers,
                bankruptConsumers, bankruptDistributors);

        gameRules.createObservers();
        gameRules.createProducersHistory(numberOfTurns + 1);

        // Round 0
        gameRules.assignProducers(0);
        gameRules.createContracts();
        gameRules.signContracts();
        gameRules.updatePlayersBudgets();

        // Updates game state each month
        for (int i = 0; i < numberOfTurns; ++i) {
            JSONObject object = (JSONObject) monthlyUpdates.get(i);
            JSONArray distributorChanges = (JSONArray) object.get("distributorChanges");
            JSONArray newConsumers = (JSONArray) object.get("newConsumers");
            JSONArray producersChanges = (JSONArray) object.get("producerChanges");
            gameRules.updateDistributors(distributorChanges);
            gameRules.updateConsumers(newConsumers);
            gameRules.createContracts();
            gameRules.purgePaidContracts();
            gameRules.signContracts();
            gameRules.updatePlayersBudgets();
            gameRules.purgeCanceledContracts();
            gameRules.purgeBrokePlayers();
            gameRules.updateProducers(producersChanges);
            gameRules.prepareProducers(i + 1);
            gameRules.assignProducers(i + 1);
        }
    }

    /**
     * Finish the game by combining and sorting all normal and broke entities by their ID and
     * prepares them for output
     *
     * @param writer used to prepare data for output writing
     * @return JSONObject that contains all entities for output
     */
    public JSONObject finishGame(final Writer writer) {
        // Append and sort consumers and bankrupt consumers
        List<Consumer> totalConsumers = Stream.of(consumers, bankruptConsumers)
                .flatMap(Collection::stream).sorted(Comparator.comparingInt(Consumer::getID))
                .collect(Collectors.toList());

        // Append and sort distributors and bankrupt distributors
        List<Distributor> totalDistributors = Stream.of(distributors, bankruptDistributors)
                .flatMap(Collection::stream).sorted(Comparator.comparingInt(Distributor::getID))
                .collect(Collectors.toList());

        producers.sort(Comparator.comparingInt(Producer::getID));

        return writer.writeFile(totalConsumers, totalDistributors, producers);
    }
}
