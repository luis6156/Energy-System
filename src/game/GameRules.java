package game;

import entities.Consumer;
import entities.Distributor;
import entities.EntityFactory;
import entities.Producer;
import entities.EntityType;
import entities.Contract;
import observer.Observable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import strategies.EnergyChoiceStrategy;
import strategies.EnergyChoiceStrategyFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public final class GameRules extends Observable {
    private final List<Consumer> consumers;
    private final List<Distributor> distributors;
    private final List<Consumer> bankruptConsumers;
    private final List<Distributor> bankruptDistributors;
    private final List<Producer> producers;

    /**
     * Constructor for the GameRules class
     *
     * @param consumers            list of consumers
     * @param distributors         list of distributors
     * @param producers            list of producers
     * @param bankruptConsumers    list of bankrupt consumers
     * @param bankruptDistributors list of bankrupt distributors
     */
    GameRules(final List<Consumer> consumers, final List<Distributor> distributors,
              final List<Producer> producers,
              final List<Consumer> bankruptConsumers,
              final List<Distributor> bankruptDistributors) {
        this.consumers = consumers;
        this.distributors = distributors;
        this.bankruptConsumers = bankruptConsumers;
        this.bankruptDistributors = bankruptDistributors;
        this.producers = producers;
    }

    /**
     * Initializes all the history size of all producers in order to be populated during the
     * game
     *
     * @param numberOfTurns used to initialize the history size of all producers
     */
    public void createProducersHistory(int numberOfTurns) {
        for (Producer producer : producers) {
            producer.initializeHistorySize(numberOfTurns);
        }
    }

    /**
     * Updates the distributors' infrastructure cost in the game
     *
     * @param distributorChanges array that contains changes made for any particular distributor
     */
    void updateDistributors(final JSONArray distributorChanges) {
        // Changes a distributor's costs
        for (Object distributorChange : distributorChanges) {
            JSONObject changes = (JSONObject) distributorChange;
            int id = ((Long) changes.get("id")).intValue();
            for (Distributor distributor : distributors) {
                if (distributor.getID() == id) {
                    distributor.updateCosts(
                            ((Long) changes.get("infrastructureCost")).intValue()
                    );
                    break;
                }
            }
        }
    }

    /**
     * Adds new consumers to the game
     *
     * @param newConsumers array that contains new consumers
     */
    void updateConsumers(final JSONArray newConsumers) {
        EntityFactory factory = EntityFactory.getInstance();

        // Adds a new Consumer to the Game
        for (Object newConsumer : newConsumers) {
            consumers.add(
                    (Consumer) factory.createEntity(EntityType.CONSUMER,
                            (JSONObject) newConsumer)
            );
        }
    }

    /**
     * Updates the producers' energy per distributor in the game
     *
     * @param producersChanges array that contains changes made for any particular producer
     */
    void updateProducers(final JSONArray producersChanges) {
        for (Object producerChanges : producersChanges) {
            JSONObject producerChangeData = (JSONObject) producerChanges;
            int id = ((Long) producerChangeData.get("id")).intValue();
            for (Producer producer : producers) {
                if (producer.getID() == id) {
                    producer.setEnergyPerDistributor(((Long) producerChangeData.get(
                            "energyPerDistributor")).intValue());
                    notifyObservers(producer.getID());
                }
            }
        }
    }

    /**
     * Copies the previous month's distributors to the current month. This is done in order to
     * remove selectively any distributor in the list so that the game advances accordingly.
     *
     * @param currentTurn used to get the index of the current turn
     */
    void prepareProducers(int currentTurn) {
        for (Producer producer : producers) {
            producer.updateExpectedDistributors(currentTurn);
        }
    }

    /**
     * Assigns new producers to a distributor that requires one. If a distributor's producer
     * changed, it is removed from the producers' lists and all of the producers assigned to the
     * distributor are deleted. The strategy is applied for sorting after which a producer that
     * is not full is assigned to the distributor.
     *
     * @param currentTurn used to get the index of the current turn
     */
    void assignProducers(int currentTurn) {
        EnergyChoiceStrategyFactory factory = EnergyChoiceStrategyFactory.getInstance();

        // Parse all distributors
        for (Distributor distributor : distributors) {
            // Check if the distributor has a producer that changed its values
            if (distributor.isAnyProducerAltered()) {
                int energy = 0;
                EnergyChoiceStrategy strategy = factory.createStrategy(distributor.getStrategy(),
                        producers);

                // Removes the distributor from all the producers
                for (Producer producer : distributor.getProducers()) {
                    producer.removeDistributor(distributor.getID(), currentTurn);
                }

                // The distributor clears its producers list
                distributor.resetProducers();

                // The strategy is applied for sorting
                strategy.sortProducersByStrategy();

                /*
                Finds the required amount of producers (that are not full), to satisfy the
                energy needs
                 */
                for (Producer producer : producers) {
                    if (!producer.isFull(currentTurn)) {
                        energy += producer.getEnergyPerDistributor();
                        distributor.addProducers(producer);
                        producer.addDistributors(distributor.getID(), currentTurn);
                        if (energy >= distributor.getEnergyNeeded()) {
                            break;
                        }
                    }
                }
                // Updates distributor production cost
                distributor.updateProductionCost();
            }
        }
    }

    /**
     * Creates new contracts for the consumer's to choose from
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

    void createObservers() {
        for (Distributor distributor : distributors) {
            addObserver(distributor);
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
