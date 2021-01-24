package entities;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collections;

public final class Producer extends Entity {
    private final List<Set<Integer>> distributorsHistory = new ArrayList<>();
    private final double price;
    private final int maxDistributors;
    private final EnergyType energyType;
    private int energyPerDistributor;

    /**
     * Constructor for producer
     *
     * @param id                   producer's ID
     * @param energyType           producer's energy source type
     * @param maxDistributors      producer's maximum amount of distributors that can be supported
     * @param price                producer's price
     * @param energyPerDistributor producer's energy per distributor
     */
    protected Producer(int id, String energyType, int maxDistributors, double price,
                       int energyPerDistributor) {
        super(id);
        this.energyType = EnergyType.valueOf(energyType);
        this.maxDistributors = maxDistributors;
        this.price = price;
        this.energyPerDistributor = energyPerDistributor;
    }

    /**
     * Removes a distributor from the current month list
     *
     * @param distributorId distributor to be removed
     * @param currentTurn   used in order to remove the distributor from the right position
     */
    public void removeDistributor(int distributorId, int currentTurn) {
        distributorsHistory.get(currentTurn).remove(distributorId);
    }

    /**
     * Copies the previous distributors to the current distributors
     *
     * @param currentTurn used to determine the source and the destination of the copy
     */
    public void updateExpectedDistributors(int currentTurn) {
        distributorsHistory.get(currentTurn).addAll(distributorsHistory.get(currentTurn - 1));
    }

    /**
     * Initializes all monthly lists of distributors
     *
     * @param numberOfTurns how big the list of TreeSets should be
     */
    public void initializeHistorySize(int numberOfTurns) {
        for (int i = 0; i < numberOfTurns; ++i) {
            distributorsHistory.add(new TreeSet<>());
        }
    }

    /**
     * Adds a distributor to the current month list
     *
     * @param distributorId distributor to be added
     * @param currentTurn   used in order to add the distributor to the right position
     */
    public void addDistributors(int distributorId, int currentTurn) {
        distributorsHistory.get(currentTurn).add(distributorId);
    }

    /**
     * @param currentTurn index of the current turn
     * @return true if the current month distributors reached the maximum amount, otherwise false
     */
    public boolean isFull(int currentTurn) {
        return distributorsHistory.get(currentTurn).size() == maxDistributors;
    }

    /**
     * @return true if energy type is renewable, otherwise false
     */
    public boolean isRenewable() {
        return energyType.isRenewable();
    }

    /**
     * @return unmodifiable list of distributors that have been assigned to this producer
     */
    public List<Set<Integer>> getDistributors() {
        return Collections.unmodifiableList(distributorsHistory);
    }

    /**
     * @return price for 1kW
     */
    public double getPrice() {
        return price;
    }

    /**
     * @return energy per distributor
     */
    public int getEnergyPerDistributor() {
        return energyPerDistributor;
    }

    /**
     * Updates the energyPerDistributor field
     *
     * @param energyPerDistributor new value for the field
     */
    public void setEnergyPerDistributor(int energyPerDistributor) {
        this.energyPerDistributor = energyPerDistributor;
    }

    /**
     * @return maximum amount of distributors
     */
    public int getMaxDistributors() {
        return maxDistributors;
    }

    /**
     * @return type of energy used by the producer
     */
    public EnergyType getEnergyType() {
        return energyType;
    }
}
