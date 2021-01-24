package observer;

import java.util.ArrayList;
import java.util.List;

public class Observable {
    private final List<Observer> observers = new ArrayList<>();

    /**
     *
     * @param observer to be added to the observer list
     */
    public void addObserver(Observer observer){
        observers.add(observer);
    }

    /**
     *
     * @param observer to be removed from the observer list
     */
    public void removeObserver(Observer observer){
        observers.remove(observer);
    }

    /**
     * Updates all observers in the list
     *
     * @param arg used as parameter for the observer's method
     */
    public void notifyObservers(Object arg){
        for (Observer observer : observers) {
            observer.update(this, arg);
        }
    }
}
