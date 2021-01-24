package fileio;

import entities.Consumer;
import entities.Contract;
import entities.Distributor;
import entities.Producer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public final class Writer {
    private final FileWriter file;

    /**
     * Constructor for Writer
     *
     * @param path file to write from
     * @throws IOException in case of failure for read/write
     */
    public Writer(final String path) throws IOException {
        this.file = new FileWriter(path);
    }

    /**
     * Takes the lists of consumers and distributors in order to create a JSONObject for output
     *
     * @param consumers    list of consumers to be written
     * @param distributors list of distributors to be written
     * @return JSONObject that contains all the data that needs to be written
     */
    public JSONObject writeFile(final List<Consumer> consumers,
                                final List<Distributor> distributors,
                                final List<Producer> producers) {
        JSONObject result = new JSONObject();
        JSONArray consumersArray = new JSONArray();
        JSONArray distributorsArray = new JSONArray();
        JSONArray producerArray = new JSONArray();

        for (Consumer consumer : consumers) {
            JSONObject consumerData = new JSONObject();
            consumerData.put(Constants.ID, consumer.getID());
            consumerData.put(Constants.IS_BANKRUPT, consumer.isBankrupt());
            consumerData.put(Constants.BUDGET, consumer.getBudget());
            consumersArray.add(consumerData);
        }

        for (Distributor distributor : distributors) {
            JSONObject distributorData = new JSONObject();
            distributorData.put(Constants.ID, distributor.getID());
            distributorData.put(Constants.ENERGY_NEEDED, distributor.getEnergyNeeded());
            distributorData.put(Constants.CONTRACT_COST, distributor.getContractCost());
            distributorData.put(Constants.BUDGET, distributor.getBudget());
            distributorData.put(Constants.PRODUCER_STRATEGY, distributor.getStrategy().toString());
            distributorData.put(Constants.IS_BANKRUPT, distributor.isBankrupt());
            JSONArray contractsArray = new JSONArray();
            for (Contract contract : distributor.getConsumerContracts()) {
                JSONObject contractData = new JSONObject();
                contractData.put(Constants.CONSUMER_ID, contract.getContracteeID());
                contractData.put(Constants.PRICE, contract.getOriginalPrice());
                contractData.put(Constants.CONTRACT_MONTHS, contract.getRemainedMonths());
                contractsArray.add(contractData);
            }
            distributorData.put(Constants.CONTRACTS, contractsArray);
            distributorsArray.add(distributorData);
        }

        for (Producer producer : producers) {
            JSONObject producerData = new JSONObject();
            producerData.put(Constants.ID, producer.getID());
            producerData.put(Constants.MAX_DISTRIBUTORS, producer.getMaxDistributors());
            producerData.put(Constants.PRICE_KW, producer.getPrice());
            producerData.put(Constants.ENERGY_TYPE, producer.getEnergyType().toString());
            producerData.put(Constants.ENERGY_PER_DISTRIBUTOR, producer.getEnergyPerDistributor());
            JSONArray distributorsHistory = new JSONArray();
            for (int i = 1; i < producer.getDistributors().size(); ++i) {
                JSONObject distributorsHistoryData = new JSONObject();
                distributorsHistoryData.put(Constants.MONTH, i);
                distributorsHistoryData.put(Constants.DISTRIBUTORS_IDS,
                        producer.getDistributors().get(i));
                distributorsHistory.add(distributorsHistoryData);
            }
            producerData.put(Constants.MONTHLY_STATS, distributorsHistory);
            producerArray.add(producerData);
        }

        result.put(Constants.CONSUMERS, consumersArray);
        result.put(Constants.DISTRIBUTORS, distributorsArray);
        result.put(Constants.PRODUCERS, producerArray);

        return result;
    }

    /**
     * Writes to file and closes the file
     *
     * @param object to be written from
     */
    public void closeJSON(final JSONObject object) {
        try {
            file.write(object.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
