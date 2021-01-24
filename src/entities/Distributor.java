package entities;

import observer.Observable;
import observer.Observer;
import strategies.EnergyChoiceStrategyType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class Distributor extends SpecialEntity implements Observer {
    private final int contractLength;
    private final List<Contract> consumerContracts = new ArrayList<>();
    private final List<Producer> producers = new ArrayList<>();
    private final int energyNeeded;
    private int infrastructureCost;
    private int contractCost = 0;
    private int productionCost;
    private final EnergyChoiceStrategyType strategy;
    private boolean isAnyProducerAltered = true;

    public Distributor(final int id, final int contractLength, final int budget,
                       final int infrastructureCost, final int energyNeeded,
                       final String strategy) {
        super(id, budget);
        this.contractLength = contractLength;
        this.infrastructureCost = infrastructureCost;
        this.energyNeeded = energyNeeded;
        this.strategy = EnergyChoiceStrategyType.valueOf(strategy);
    }

    /**
     * Removes the fully paid contracts from the distributor
     */
    @Override
    public void purgePaidContract() {
        consumerContracts.removeIf(Contract::isPaid);
    }

    /**
     * Removes the canceled contracts from the distributor
     */
    @Override
    public void purgeCanceledContract() {
        consumerContracts.removeIf(Contract::isCanceled);
    }

    /**
     * Updates distributor's budget by subtracting his monthly expenses
     */
    @Override
    public void advanceBudget() {
        budget -= (infrastructureCost + productionCost * consumerContracts.size());
    }

    /**
     * Adds to the budget the monthly lease and checks if the distributor's bankrupt
     */
    @Override
    public void advanceLease() {
        // Add all contracts' prices to the budget if they were paid this month
        for (Contract contract : consumerContracts) {
            if (!contract.hasPenalty()) {
                budget += contract.getCurrentPrice();
            }
        }

        // Checks if the budget is positive, otherwise file for bankruptcy
        isBankrupt = budget < 0;
        if (isBankrupt) {
            consumerContracts.forEach(Contract::terminateContract);
        }
    }

    public void updateProductionCost() {
        double cost = 0;

        for (Producer producer : producers) {
            cost += (producer.getEnergyPerDistributor() * producer.getPrice());
        }

        productionCost = (int) Math.round(Math.floor(cost / 10));
    }

    /**
     * Updates the current infrastructure cost
     *
     * @param newInfrastructureCost new infrastructure cost
     */
    public void updateCosts(final int newInfrastructureCost) {
        infrastructureCost = newInfrastructureCost;
    }

    /**
     * Updates distributor's contract cost
     */
    public void updateContractCost() {
        final float profitMargin = 0.2f;
        int profit = (int) Math.round(Math.floor(profitMargin * productionCost));

        // Check if this month there are no clients associated with the distributor
        if (consumerContracts.isEmpty()) {
            contractCost = infrastructureCost + productionCost + profit;
            return;
        }

        contractCost =
                (int) Math.round(Math.floor(infrastructureCost / consumerContracts.size())
                        + productionCost + profit);
    }

    public int getEnergyNeeded() {
        return energyNeeded;
    }

    public EnergyChoiceStrategyType getStrategy() {
        return strategy;
    }

    /**
     * @param contract to be added to the list of distributor's contracts
     */
    @Override
    public void signContract(final Contract contract) {
        consumerContracts.add(contract);
    }

    public int getContractLength() {
        return contractLength;
    }

    public int getContractCost() {
        return contractCost;
    }

    public List<Contract> getConsumerContracts() {
        return Collections.unmodifiableList(consumerContracts);
    }

    public void addProducers(Producer producer) {
        producers.add(producer);
    }

    public List<Producer> getProducers() {
        return producers;
    }

    public boolean isAnyProducerAltered() {
        return isAnyProducerAltered;
    }

    public void resetProducers() {
        producers.clear();
        isAnyProducerAltered = false;
    }

    @Override
    public void update(Observable observable, Object arg) {
        for (Producer producer : producers) {
            if (producer.getID() == (int) arg) {
                isAnyProducerAltered = true;
                break;
            }
        }
    }
}
