package strategies;

import entities.Producer;

import java.util.List;

public class QuantityStrategy implements EnergyChoiceStrategy {
    private final List<Producer> producers;

    public QuantityStrategy(List<Producer> producers) {
        this.producers = producers;
    }

    @Override
    public void sortProducersByStrategy() {
        producers.sort((o1, o2) -> {
            int solution;

            solution = -Integer.compare(o1.getEnergyPerDistributor(),
                    o2.getEnergyPerDistributor());
            if (solution == 0) {
                solution = Integer.compare(o1.getID(), o2.getID());
            }

            return solution;
        });
    }
}
