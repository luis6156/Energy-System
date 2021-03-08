# Stage 2 - Energy System - Copyright Micu Florian-Luis 321CA 2021

## About

Object Oriented Programming
2020-2021

<https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/proiect/etapa2>

## Packages contents:

src/entities:
This package stores all entity related classes. An entity is created using the
factory (implemented with singleton). All types of entities are stored as
enums. The entities known are Consumer, Producer and Distributor. A bond
between the Consumer and Distributor can be created using the Contract class.

Entity (Abstract Class)
All entities will share a common attribute, precisely an ID. The purpose of 
this class is to be used by the factory method.

SpecialEntity (Abstract Class)
This class was created in order to retain the common attributes of the consumer
and distributor classes. A producer does not work the same way as the other two
entities, thus this class inherits the Entity class (used by the factory) and 
is extended by the consumer and distributor. A SpecialEntity object has a 
budget and bankruptcy flag. The method associated with this object are used for
creating contracts, removing contracts and updating its budget. 

All of the attributes and methods of the classes Entity and SpecialEntity were
created so that if a new type of entity is added it can easily inherit all of
the common properties needed for the game.

Consumer (Inherits from Entity):
This class has an extra attribute "monthlyIncome" that is added monthly to the
budget in the "advanceBudget" method. The "advanceLease" method is used to
subtract from the budget the price of the contract associated with this class.
If the contract cannot be paid, I check if there was a penalty already added.
If the condition is true, then I file the consumer for bankruptcy and terminate
the contract, otherwise I tell the contract to add a penalty to the price. The
best contract is automatically sent to the consumer by the class "GameRules".

Distributor (Inherits from Entity):
A distributor has "infrastructureCost", "productionCost", "contractLength" and
"energyNeeded" in addition to the other attributes. The main task of the 
distributor is to compute the price of the contract each month using the two
costs and the number of current clients, thus I have two extra attributes:
"contractCost" and a list of contracts. As opposed to the other entity,
this one advances his budget by subtracting from the budget the monthly costs
and advances its lease by adding to the budget the price of the contracts that
have been paid this month. If after all of the updates the budget is negative,
I file the distributor for bankruptcy and I terminate all of its contracts. 
If the class "GameRules" determines that this distributor has the best price,
I will add a new contract to the list of contracts. I added a list of producers
that is used in order to compute the production cost and a flag that is true if
the producers have changed durring the game (initially the flag is set to true
in order to assign producers in the month 0).

Contract:
I created this class in order to retain the relevant information from a bond
of two entities: the contract's price, the contract's penalty, the remaining
months, the IDs of both parties, a flag that indicates the contract has been
fully paid and a flag that indicates that the contract has been canceled. A
contract can be canceled by both parties when one of them goes bankrupt. This
class has an "advanceContract" method that decrements the number of months
and that applies/deducts a penalty based on weather or not the contractee paid
(a boolean is used as parameter). The price of the contract is always updated
to contain the penalty (penalty is 0 when there is no debt to maintain the
correct price).
Note: I used two methods for getting the price: one that returns the price +
penalty (for the game) and one that returns only the price (for output) as
for the Test 15 I would print the wrong contract price with the first getter.
In addition, I tried to make this accurate to real life, that is why I store
both of the parties IDs, even though its not necessary it helps me make the
class more general.

Producer:
This class was created in order to replace the production cost of the 
distributor. It has the following attributes: "price", "maxDistributors",
"energyType", "energyPerDistributor". In addition, I createed a list of
TreeSets that contains the IDs of the distributors associated with the 
current producer (I used TreeSet to maintain the IDs ordered for 
printing). All of the methods in this class are mainly getters, however
there are some methods that are used for updating the list of distributors'
IDs (add/remove/copy previous month to current month).  

src/observer:
This package is used to store my own implementation of the classes "Observer"
and "Observable". I did this because the standard implementation is deprecated.
These classes where made to reflect the way an Observer Desing Pattern
should be.

src/strategies:
The Strategy Design Pattern is implemented in this package. I used a factory in
order to generate strategies, and each strategy uses methods introduced in 
Java 8 for sorting the producers. 

src/game:
In this package I store the game logic as well as the game updates and
entities.

GameRules:
This class has methods that act as steps in order to progress the game
properly (used in this exact order in the Game class):

"createObservers": adds all distributors as observers; used at the start of
                    the game
"createProducersHistory": initializes the lists that will contain the history
                    of distributors associated with a producer; used at the 
                    start of the game
"updateDistributors": updates the distributor's costs
"updateConsumers": adds new consumers to the game
"createContracts": used to tell all distributor's to compute the price of this
                    month's contracts
"purgePaidContracts": removes all paid contracts from all relevant entities
"signContracts": takes the distributor with the least expensive contract and
                    creates a contract between him and the consumers that
                    do not have a contract
"updatePlayersBudgets": advances all relevant entities budgets and then 
                    advances their leases as well as checking for bankruptcy
"purgeCanceledContracts": removes all canceled contracts from all relevant 
                    entities (one party went bankrupt in the process)
"purgeBrokePlayers": removes all broke entities from the active players
                    and adds them to bankrupt players
"updateProducers": updates the producer's monthly energy budget
"prepareProducers": copies all the old distributors of a producer to this
                    month's distributors (relevant for assigning producers)
"assignProducers": assigns producers to any distributor that has a producer
                    that changed; first all producers remove the distributors
                    from their list; second the distributor clears all the
                    producers assigned to it and then the strategy is applied
                    to sort the producers accordingly; lastly, only the 
                    producers that are not full are assigned

Game:
This class advances the game to the end and prepares the entities for output.

The "startGame" method starts with a round zero in which I assigns observers,
initialize the history lists of the producers, create contracts, assign 
contracts and update the budgets and then I advance the game by calling
the GameRules' methods in the exact above order.

The "finishGame" method appends the list of active players with the list of
bankrupt players and sorts them by ID, in order to print the entities.

src/fileio:
The Reader class is used to get input from a file and create entities using
the Factory as well as to determine the number of turns and to read the
monthly updates. After the function processes the input it calls the Game's
constructor with the read data as parameters.

The Writer class simply transforms all entities into a JSONObject, writes
it to a file and closes it.

The Constants class is used to store constants for output (it helped me
organize the output).

src/Main (class):
In the Main class I create a game instance by reading the input, I start
the game and I print the last game state to the output file.

## Design Patterns:
Observer: distributors are the observers and the "GameRules" class is used as
        the subject. Whenever a producer is updated in GameRules, all of the
        distributors are notified with the producer's ID. If the ID is present
        in the distributor's providers list, it sets the flag 
        "isAnyProducerChanged" to true.

Strategy: used for sorting the producers. There are three strategies, each one
        of them being tied to the producer's attributes.

Factory: used to create entities and strategies.

Singleton: used by all factories.

## Personal Notes:
My implementation from the first stage was modular enough thanks to the use of
abstract classes and design patterns. Therefore, I did not have any problems 
introducing the Producer and the new rules.

In addition, I used a thread safe implementation for the singleton classes with
little overhead (Lazy Instantiation with double checked locking principle). 
This is both safer and faster than a normal implementation.

I also tried to not break encapsulation thus I created my implementation with
as little setters and getters as possible (this is also why some methods have
the default access modifier). Furthermore, I worked around the OOP principle
"Tell, Don't Ask".

## Feedback
Interesting homework, really tested our abilites to implement modular designs.
I consider that they helped a ton in the second stage as this part was really
quickly done. It really showed how important these patterns are and it 
solidified my knowledge of them.
