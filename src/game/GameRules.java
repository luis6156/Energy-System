package game;

import entities.Consumer;
import entities.Contract;
import entities.Distributor;
import entities.EntityFactory;
import entities.EntityType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public final class GameRules {
    private final List<Consumer> consumers;
    private final List<Distributor> distributors;
    private final List<Consumer> bankruptConsumers;
    private final List<Distributor> bankruptDistributors;

    /**
     * Constructor for GameRules
     *
     * @param consumers            list of consumers that will be modified
     * @param distributors         list of distributors that will be modified
     * @param bankruptConsumers    list of bankrupt consumers that will be modified
     * @param bankruptDistributors list of bankrupt distributors that will be modified
     */
    GameRules(final List<Consumer> consumers, final List<Distributor> distributors,
              final List<Consumer> bankruptConsumers,
              final List<Distributor> bankruptDistributors) {
        this.consumers = consumers;
        this.distributors = distributors;
        this.bankruptConsumers = bankruptConsumers;
        this.bankruptDistributors = bankruptDistributors;
    }

    /**
     * Used to update the game state by changing costs and/or adding Consumers
     *
     * @param factory      used to create entities
     * @param costsChanges used to update costs
     * @param newConsumers used for factory to create a new Consumer
     */
    void processChanges(final EntityFactory factory, final JSONArray costsChanges,
                               final JSONArray newConsumers) {
        // Changes a distributor's costs
        if (!costsChanges.isEmpty()) {
            for (Object costsChange : costsChanges) {
                JSONObject newCosts = (JSONObject) costsChange;
                int id = ((Long) newCosts.get("id")).intValue();
                for (Distributor distributor : distributors) {
                    if (distributor.getID() == id) {
                        distributor.updateCosts(
                                ((Long) newCosts.get("infrastructureCost")).intValue(),
                                ((Long) newCosts.get("productionCost")).intValue()
                        );
                        break;
                    }
                }
            }
        }

        // Adds a new Consumer to the Game
        for (Object newConsumer : newConsumers) {
            consumers.add(
                    (Consumer) factory.createEntity(EntityType.CONSUMER,
                            (JSONObject) newConsumer)
            );
        }
    }

    /**
     * Creates new contracts for the consumer's tho choose from
     */
    void createContracts() {
        for (Distributor distributor : distributors) {
            distributor.updateContractCost();
        }
    }

    /**
     * Takes out the broke entities from the normal lists and adds them to the bankrupt lists
     */
    void purgeBrokePlayers() {
        Iterator<Consumer> itConsumer = consumers.iterator();

        // Consumers
        while (itConsumer.hasNext()) {
            Consumer consumer = itConsumer.next();
            if (consumer.isBankrupt()) {
                bankruptConsumers.add(consumer);
                itConsumer.remove();
            }
        }

        Iterator<Distributor> itDistributor = distributors.iterator();

        // Distributors
        while (itDistributor.hasNext()) {
            Distributor distributor = itDistributor.next();
            if (distributor.isBankrupt()) {
                bankruptDistributors.add(distributor);
                itDistributor.remove();
            }
        }
    }

    /**
     * Removes all paid contracts from all entities
     */
    void purgePaidContracts() {
        for (Consumer consumer : consumers) {
            consumer.purgePaidContract();
        }

        for (Distributor distributor : distributors) {
            distributor.purgePaidContract();
        }
    }

    /**
     * Removes all canceled contracts from all entities
     */
    void purgeCanceledContracts() {
        for (Consumer consumer : consumers) {
            consumer.purgeCanceledContract();
        }

        for (Distributor distributor : distributors) {
            distributor.purgeCanceledContract();
        }
    }

    /**
     * Assigns a contract between the best distributor and all consumers without a contract
     */
    void signContracts() {
        if (!distributors.isEmpty()) {
            // Get the distributor with the minimum cost for a contract
            Distributor distributor = Collections.min(distributors,
                    Comparator.comparingInt(Distributor::getContractCost));

            // Assign contracts for all consumers that don't have a contract
            for (Consumer consumer : consumers) {
                if (!consumer.signedContract()) {
                    Contract contract = new Contract(distributor.getID(), consumer.getID(),
                            distributor.getContractCost(), distributor.getContractLength());
                    consumer.signContract(contract);
                    distributor.signContract(contract);
                }
            }
        }
    }

    /**
     * Updates an entity's budget and advances their monthly lease
     */
    void updatePlayersBudgets() {
        // Consumers get paid their salary and pay their lease
        for (Consumer consumer : consumers) {
            consumer.advanceBudget();
            consumer.advanceLease();
        }

        // Distributors pay their monthly expenses and get paid from the leases
        for (Distributor distributor : distributors) {
            distributor.advanceBudget();
            distributor.advanceLease();
        }
    }
}
