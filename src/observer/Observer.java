package observer;

public interface Observer {
    /**
     *
     * @param observable subject used by the observer (classic java implementation)
     * @param arg used to store the ID of the producer that changed (in this case)
     */
    void update(Observable observable, Object arg);
}