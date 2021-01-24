package strategies;

import entities.Producer;

import java.util.List;

public final class EnergyChoiceStrategyFactory {
    private static EnergyChoiceStrategyFactory instance = null;

    private EnergyChoiceStrategyFactory() {
    }

    /**
     * Singleton method (Thread Safe Lazy Instantiation with double checked locking principle)
     *
     * @return instance of factory
     */
    public static EnergyChoiceStrategyFactory getInstance() {
        if (instance == null) {
            synchronized (EnergyChoiceStrategyFactory.class) {
                if (instance == null) {
                    instance = new EnergyChoiceStrategyFactory();
                }
            }
        }

        return instance;
    }

    /**
     * Generic Factory Method for strategies
     *
     * @param type      type of object to be created
     * @param producers list of producers used for sorting
     * @return new strategy type
     */
    public EnergyChoiceStrategy createStrategy(EnergyChoiceStrategyType type,
                                               List<Producer> producers) {
        if (type == EnergyChoiceStrategyType.GREEN) {
            return new GreenStrategy(producers);
        } else if (type == EnergyChoiceStrategyType.PRICE) {
            return new PriceStrategy(producers);
        } else if (type == EnergyChoiceStrategyType.QUANTITY) {
            return new QuantityStrategy(producers);
        }
        throw new IllegalArgumentException("Unrecognized energy strategy type.");
    }
}
