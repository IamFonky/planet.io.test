package ch.elmootan.core.sharedObjects;

import ch.elmootan.core.universe.Bonus;

import java.io.Serializable;
import java.util.HashSet;


public class Game implements Serializable {

    //!
    private String name;

    //!
    private HashSet<Bonus> availableBonuses;

    //!
    private int nbPlaylersCurrent;

    //!
    private int nbPlayersMax;

    public Game(String name, HashSet<Bonus> bonuses, int nbPlayersMax) {
        this.name = name;
        this.availableBonuses = bonuses;
        this.nbPlayersMax = nbPlayersMax;
    }

    //! Dumme constructor needed for deserialization
    public Game() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<Bonus> getAvailableBonuses() {
        return availableBonuses;
    }

    public void setAvailableBonuses(HashSet<Bonus> availableBonuses) {
        this.availableBonuses = availableBonuses;
    }

    public int getNbPlayersMax() {
        return nbPlayersMax;
    }

    public void setNbPlayersMax(int nbPlayersMax) {
        this.nbPlayersMax = nbPlayersMax;
    }

    public void join() {
        System.out.println("Game " + name + " joined!");
    }

    public int getNbPlaylersCurrent() {
        return nbPlaylersCurrent;
    }

    public void setNbPlaylersCurrent(int nbPlaylersCurrent) {
        this.nbPlaylersCurrent = nbPlaylersCurrent;
    }
}
