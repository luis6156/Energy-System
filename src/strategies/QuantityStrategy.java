package strategies;

import entities.Producer;

import java.util.Comparator;
import java.util.List;

public class QuantityStrategy implements EnergyChoiceStrategy {
    private final List<Producer> producers;

    public QuantityStrategy(List<Producer> producers) {
        this.producers = producers;
    }

    @Override
    public void sortProducersByStrategy() {
        Comparator<Producer> quantitySort =
                Comparator.comparing(Producer::getEnergyPerDistributor, Comparator.reverseOrder())
                        .thenComparing(Producer::getID);
        producers.sort(quantitySort);
    }
}
