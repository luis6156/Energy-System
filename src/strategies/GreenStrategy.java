package strategies;

import entities.Producer;

import java.util.List;

public class GreenStrategy implements EnergyChoiceStrategy {
    private final List<Producer> producers;

    public GreenStrategy(List<Producer> producers) {
        this.producers = producers;
    }

    @Override
    public void sortProducersByStrategy() {
        producers.sort((o1, o2) -> {
            int solution;

            solution = -Boolean.compare(o1.isRenewable(), o2.isRenewable());

            if (solution == 0) {
                solution = Double.compare(o1.getPrice(), o2.getPrice());

                if (solution == 0) {
                    solution = -Integer.compare(o1.getEnergyPerDistributor(),
                            o2.getEnergyPerDistributor());
                    if (solution == 0) {
                        solution = Integer.compare(o1.getID(), o2.getID());
                    }
                }
            }

            return solution;
        });
    }
}
