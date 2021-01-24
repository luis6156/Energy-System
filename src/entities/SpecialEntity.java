package entities;

public abstract class SpecialEntity extends Entity {
    protected int budget;
    protected boolean isBankrupt = false;

    /**
     * Constructor for special entity
     *
     * @param id     entity's id
     * @param budget entity's budget
     */
    protected SpecialEntity(int id, int budget) {
        super(id);
        this.budget = budget;
    }

    /**
     * Updates entity's budget
     */
    public abstract void advanceBudget();

    /**
     * Advances entity's lease
     */
    public abstract void advanceLease();

    /**
     * Assigns a contract to an entity
     *
     * @param contract to be assigned
     */
    public abstract void signContract(Contract contract);

    /**
     * Removes all paid contracts
     */
    public abstract void purgePaidContract();

    /**
     * Removes all canceled contracts
     */
    public abstract void purgeCanceledContract();

    /**
     * @return true if entity is bankrupt, otherwise false
     */
    public boolean isBankrupt() {
        return isBankrupt;
    }

    /**
     * @return entity's budget
     */
    public int getBudget() {
        return budget;
    }
}
