package entities;

import org.json.simple.JSONObject;

public final class EntityFactory {
    private static EntityFactory instance = null;

    private EntityFactory() {
    }

    /**
     * Singleton method (Thread Safe Lazy Instantiation with double checked locking principle)
     *
     * @return instance of factory
     */
    public static EntityFactory getInstance() {
        if (instance == null) {
            synchronized (EntityFactory.class) {
                if (instance == null) {
                    instance = new EntityFactory();
                }
            }
        }

        return instance;
    }

    /**
     * Generic Factory Method
     *
     * @param type   type of object to be created
     * @param object object used to pull data from
     * @return new instance of object
     */
    public Entity createEntity(final EntityType type, final JSONObject object) {
        if (type == EntityType.CONSUMER) {
            return new Consumer(
                    ((Long) object.get("id")).intValue(),
                    ((Long) object.get("initialBudget")).intValue(),
                    ((Long) object.get("monthlyIncome")).intValue()
            );
        } else if (type == EntityType.DISTRIBUTOR) {
            return new Distributor(
                    ((Long) object.get("id")).intValue(),
                    ((Long) object.get("contractLength")).intValue(),
                    ((Long) object.get("initialBudget")).intValue(),
                    ((Long) object.get("initialInfrastructureCost")).intValue(),
                    ((Long) object.get("initialProductionCost")).intValue()
            );
        }
        throw new IllegalArgumentException("Unrecognized object type.");
    }
}
