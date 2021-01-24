package entities;

public final class Consumer extends Entity {
    private final int monthlyIncome;
    private Contract contract = null;

    /**
     * Constructor for consumer
     *
     * @param id            consumer's ID
     * @param budget        consumer's initial budget
     * @param monthlyIncome consumer's monthly income
     */
    public Consumer(final int id, final int budget, final int monthlyIncome) {
        super(id, budget);
        this.monthlyIncome = monthlyIncome;
    }

    /**
     * Removes the consumer's contract if it is fully paid
     */
    @Override
    public void purgePaidContract() {
        if (contract == null) {
            return;
        }
        if (contract.isPaid()) {
            contract = null;
        }
    }

    /**
     * Removes the consumer's contract if it was canceled (one party went bankrupt)
     */
    @Override
    public void purgeCanceledContract() {
        if (contract == null) {
            return;
        }
        if (contract.isCanceled()) {
            contract = null;
        }
    }

    /**
     * Updates consumer's budget by adding his monthly income
     */
    @Override
    public void advanceBudget() {
        budget += monthlyIncome;
    }

    /**
     * Subtracts from the budget the monthly lease and checks if the consumer's bankrupt
     */
    @Override
    public void advanceLease() {
        // Check if the consumer can pay the lease
        if (contract.getCurrentPrice() > budget) {
            // Check if the contract already has a penalty in order to file for bankruptcy
            if (contract.hasPenalty()) {
                isBankrupt = true;
                contract.terminateContract();
                return;
            }
            contract.advanceContract(false);
        } else {
            budget -= contract.getCurrentPrice();
            contract.advanceContract(true);
        }
    }

    /**
     * @param newContract to be assigned to the consumer
     */
    @Override
    public void signContract(final Contract newContract) {
        contract = newContract;
    }

    /**
     * @return true if the consumer has a contract, otherwise false
     */
    public boolean signedContract() {
        return contract != null;
    }
}
