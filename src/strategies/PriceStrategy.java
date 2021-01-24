package strategies;

import entities.Producer;

import java.util.Comparator;
import java.util.List;

public class PriceStrategy implements EnergyChoiceStrategy {
    private final List<Producer> producers;

    /**
     * Price strategy constructor
     *
     * @param producers list of producers used for sorting
     */
    public PriceStrategy(List<Producer> producers) {
        this.producers = producers;
    }

    /**
     * Sort producers first by price, by energy given and by id
     */
    @Override
    public void sortProducersByStrategy() {
        Comparator<Producer> priceSort =
                Comparator.comparing(Producer::getPrice)
                        .thenComparing(Producer::getEnergyPerDistributor,
                                Comparator.reverseOrder())
                        .thenComparing(Producer::getID);
        producers.sort(priceSort);
    }
}
