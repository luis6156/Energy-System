package entities;

import observer.Observable;
import observer.Observer;
import strategies.EnergyChoiceStrategyType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class Distributor extends SpecialEntity implements Observer {
    private final int contractLength;
    private final List<Contract> contracts = new ArrayList<>();
    private final List<Producer> producers = new ArrayList<>();
    private final int energyNeeded;
    private final EnergyChoiceStrategyType strategy;
    private int infrastructureCost;
    private int contractCost = 0;
    private int productionCost;
    private boolean isAnyProducerAltered = true;

    /**
     * Constructor for distributor
     *
     * @param id                 distributor's ID
     * @param contractLength     distributor's contract length
     * @param budget             distributor's budget
     * @param infrastructureCost distributor's infrastructure cost
     * @param energyNeeded       distributor's monthly energy need
     * @param strategy           distributor's strategy
     */
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
        contracts.removeIf(Contract::isPaid);
    }

    /**
     * Removes the canceled contracts from the distributor
     */
    @Override
    public void purgeCanceledContract() {
        contracts.removeIf(Contract::isCanceled);
    }

    /**
     * Updates distributor's budget by subtracting his monthly expenses
     */
    @Override
    public void advanceBudget() {
        budget -= (infrastructureCost + productionCost * contracts.size());
    }

    /**
     * Adds to the budget the monthly lease and checks if the distributor's bankrupt
     */
    @Override
    public void advanceLease() {
        // Add all contracts' prices to the budget if they were paid this month
        for (Contract contract : contracts) {
            if (!contract.hasPenalty()) {
                budget += contract.getCurrentPrice();
            }
        }

        // Checks if the budget is positive, otherwise file for bankruptcy
        isBankrupt = budget < 0;
        if (isBankrupt) {
            contracts.forEach(Contract::terminateContract);
        }
    }

    /**
     * Calculates production cost from the producer's data
     */
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
        if (contracts.isEmpty()) {
            contractCost = infrastructureCost + productionCost + profit;
            return;
        }

        contractCost =
                (int) Math.round(Math.floor(infrastructureCost / contracts.size())
                        + productionCost + profit);
    }

    /**
     * @return monthly energy requirement
     */
    public int getEnergyNeeded() {
        return energyNeeded;
    }

    /**
     * @return distributor's strategy
     */
    public EnergyChoiceStrategyType getStrategy() {
        return strategy;
    }

    /**
     * @param contract to be added to the list of distributor's contracts
     */
    @Override
    public void signContract(final Contract contract) {
        contracts.add(contract);
    }

    /**
     * @return distributor's contract length
     */
    public int getContractLength() {
        return contractLength;
    }

    /**
     * @return distributor's contract price
     */
    public int getContractCost() {
        return contractCost;
    }

    /**
     * @return unmodifiable list of contracts
     */
    public List<Contract> getContracts() {
        return Collections.unmodifiableList(contracts);
    }

    /**
     * @param producer to be added as a provider for the distributor
     */
    public void addProducers(Producer producer) {
        producers.add(producer);
    }

    /**
     * @return unmodifiable list of producers
     */
    public List<Producer> getProducers() {
        return Collections.unmodifiableList(producers);
    }

    /**
     * @return true if any producer changed, otherwise false
     */
    public boolean isAnyProducerAltered() {
        return isAnyProducerAltered;
    }

    /**
     * Clears the list of producers in order to reset the current month providers, also resets
     * the changed producers flag
     */
    public void resetProducers() {
        producers.clear();
        isAnyProducerAltered = false;
    }

    /**
     * @param observable subject used by the observer's method
     * @param arg        used to store the ID of the producer that has been altered
     */
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
