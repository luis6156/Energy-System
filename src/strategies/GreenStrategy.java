package strategies;

import entities.Producer;

import java.util.Comparator;
import java.util.List;

public class GreenStrategy implements EnergyChoiceStrategy {
    private final List<Producer> producers;

    public GreenStrategy(List<Producer> producers) {
        this.producers = producers;
    }

    @Override
    public void sortProducersByStrategy() {
        Comparator<Producer> greenSort =
                Comparator.comparing(Producer::isRenewable, Comparator.reverseOrder())
                        .thenComparing(Producer::getPrice)
                        .thenComparing(Producer::getEnergyPerDistributor,
                                Comparator.reverseOrder())
                        .thenComparing(Producer::getID);
        producers.sort(greenSort);
    }
}
