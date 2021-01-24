package entities;

public abstract class Entity {
    protected final int id;

    /**
     * Constructor for entity
     *
     * @param id entity's id
     */
    protected Entity(final int id) {
        this.id = id;
    }

    /**
     * @return entity's ID
     */
    public int getID() {
        return id;
    }
}
