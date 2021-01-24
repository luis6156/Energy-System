package entities;

import java.util.*;

public final class Producer extends Entity {
    private final List<Set<Integer>> distributorsHistory = new ArrayList<>();
    private final double price;
    private int energyPerDistributor;
    private final int maxDistributors;
    private final EnergyType energyType;

    protected Producer(int id, String energyType, int maxDistributors, double price,
                       int energyPerDistributor) {
        super(id);
        this.energyType = EnergyType.valueOf(energyType);
        this.maxDistributors = maxDistributors;
        this.price = price;
        this.energyPerDistributor = energyPerDistributor;
    }

    public void removeDistributor(int distributorId, int currentTurn) {
        distributorsHistory.get(currentTurn).remove(distributorId);
    }

    public void updateExpectedDistributors(int currentTurn) {
        distributorsHistory.get(currentTurn).addAll(distributorsHistory.get(currentTurn - 1));
    }

    public void initializeHistorySize(int numberOfTurns) {
        for (int i = 0; i < numberOfTurns; ++i) {
            distributorsHistory.add(new TreeSet<>());
        }
    }

    public void setEnergyPerDistributor(int energyPerDistributor) {
        this.energyPerDistributor = energyPerDistributor;
    }

    public void addDistributors(int distributorId, int currentTurn) {
        distributorsHistory.get(currentTurn).add(distributorId);
    }

    public boolean isFull(int currentTurn) {
        return distributorsHistory.get(currentTurn).size() == maxDistributors;
    }

    public boolean isRenewable() {
        return energyType.isRenewable();
    }

    public double getPrice() {
        return price;
    }

    public int getEnergyPerDistributor() {
        return energyPerDistributor;
    }

    public int getMaxDistributors() {
        return maxDistributors;
    }

    public EnergyType getEnergyType() {
        return energyType;
    }

    public List<Set<Integer>> getDistributors() {
        return Collections.unmodifiableList(distributorsHistory);
    }
}
