package entities;

public final class Contract {
    private final int contractorID;
    private final int contracteeID;
    private final int price;
    private int remainedMonths;
    private int penalty = 0;
    private boolean paid = false;
    private boolean canceled = false;

    /**
     * Constructor for contract
     *
     * @param contractorID   ID of contractor
     * @param contracteeID   ID of contractee
     * @param price          contract's price
     * @param remainedMonths contract's period
     */
    public Contract(final int contractorID, final int contracteeID, final int price,
                    final int remainedMonths) {
        this.contractorID = contractorID;
        this.contracteeID = contracteeID;
        this.price = price;
        this.remainedMonths = remainedMonths;
    }

    /**
     * @return original price of contract
     */
    public int getOriginalPrice() {
        return price;
    }

    /**
     * @return price of contract with penalties
     */
    int getCurrentPrice() {
        return price + penalty;
    }

    /**
     * Removes one month from the contract, adds/removes a penalty if necessary and checks is the
     * contract reached its end
     *
     * @param contracteePaid true if contractee paid, otherwise false
     */
    void advanceContract(final boolean contracteePaid) {
        // Based on the penalty and the paid flag, advance contract accordingly
        if (contracteePaid && penalty != 0) {
            // Contract had debt but it was paid, remove penalty
            penalty = 0;
            --remainedMonths;
        } else if (!contracteePaid) {
            // Contract could not be paid this month, create penalty
            final float penaltyMargin = 1.2f;
            penalty = (int) Math.round(Math.floor(penaltyMargin * price));
            --remainedMonths;
        } else {
            // Contract was paid and no debts were present
            --remainedMonths;
        }

        // Checks if the contract is finished
        if (remainedMonths <= 0 && penalty == 0) {
            paid = true;
        }
    }

    /**
     * Sets the canceled flag to true (one party is bankrupt)
     */
    void terminateContract() {
        canceled = true;
    }

    /**
     * @return true if the contract is fully paid, otherwise false
     */
    boolean isPaid() {
        return paid;
    }

    /**
     * @return true if the contract is canceled, otherwise false
     */
    boolean isCanceled() {
        return canceled;
    }

    /**
     * @return true if the contract has a penalty
     */
    boolean hasPenalty() {
        return penalty != 0;
    }

    public int getContractorID() {
        return contractorID;
    }

    public int getContracteeID() {
        return contracteeID;
    }

    public int getRemainedMonths() {
        return remainedMonths;
    }
}
