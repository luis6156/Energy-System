package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Distributor extends Entity {
    private final int contractLength;
    private final List<Contract> contracts = new ArrayList<>();
    private int infrastructureCost;
    private int productionCost;
    private int contractCost = 0;

    /**
     * Constructor for distributor
     *
     * @param id                 distributor's ID
     * @param contractLength     distributor's contract length
     * @param budget             distributor's initial budget
     * @param infrastructureCost distributor's infrastructure cost
     * @param productionCost     distributor's production cost
     */
    public Distributor(final int id, final int contractLength, final int budget,
                       final int infrastructureCost, final int productionCost) {
        super(id, budget);
        this.contractLength = contractLength;
        this.infrastructureCost = infrastructureCost;
        this.productionCost = productionCost;
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
     * Updates the current costs with new ones
     *
     * @param newInfrastructureCost new infrastructure cost
     * @param newProductionCost     new production cost
     */
    public void updateCosts(final int newInfrastructureCost, final int newProductionCost) {
        infrastructureCost = newInfrastructureCost;
        productionCost = newProductionCost;
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
     * @param contract to be added to the list of distributor's contracts
     */
    @Override
    public void signContract(final Contract contract) {
        contracts.add(contract);
    }

    public int getContractLength() {
        return contractLength;
    }

    public int getContractCost() {
        return contractCost;
    }

    public List<Contract> getContracts() {
        return Collections.unmodifiableList(contracts);
    }
}
