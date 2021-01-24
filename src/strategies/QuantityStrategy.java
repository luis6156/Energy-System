package strategies;

import entities.Producer;

import java.util.Comparator;
import java.util.List;

public class QuantityStrategy implements EnergyChoiceStrategy {
    private final List<Producer> producers;

    /**
     * Quantity strategy constructor
     *
     * @param producers list of producers used for sorting
     */
    public QuantityStrategy(List<Producer> producers) {
        this.producers = producers;
    }

    /**
     * Sort producers first by energy given and then by id
     */
    @Override
    public void sortProducersByStrategy() {
        Comparator<Producer> quantitySort =
                Comparator.comparing(Producer::getEnergyPerDistributor, Comparator.reverseOrder())
                        .thenComparing(Producer::getID);
        producers.sort(quantitySort);
    }
}
