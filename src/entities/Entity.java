package entities;

public abstract class Entity {
    protected final int id;
    protected int budget;
    protected boolean isBankrupt = false;

    /**
     * Constructor for entity
     *
     * @param id     entity's id
     * @param budget entity's budget
     */
    protected Entity(final int id, final int budget) {
        this.id = id;
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
     * @return entity's ID
     */
    public int getID() {
        return id;
    }

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
