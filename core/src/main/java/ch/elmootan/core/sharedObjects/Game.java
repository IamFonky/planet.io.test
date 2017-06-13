package ch.elmootan.core.sharedObjects;

import ch.elmootan.core.universe.Bonus;
import ch.elmootan.core.universe.Universe;

import java.io.Serializable;
import java.util.ArrayList;
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

    //!
    private ArrayList<Player> playersInGame;

    //!
    private int gameId;

    //Je déconseille sérieusement de mettre un Univers dans la Game
    //Déja simplement parce qu'on lance une fenetre des qu'on l'appelle donc
    //chaque fois que tu fais new Game() tu lance une GUI du jeu, donc tu attend pas de join
//    private final Universe universe = new Universe();

    public Game(String name, HashSet<Bonus> bonuses, int nbPlayersMax) {
        this.name = name;
        this.availableBonuses = bonuses;
        this.nbPlayersMax = nbPlayersMax;
        playersInGame = new ArrayList<>();
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

//    public void join(String playerName, int skin) {
//        universe.showUI();
//        universe.generateMyPlanet(playerName, skin);
//    }

    public int getNbPlaylersCurrent() {
        return nbPlaylersCurrent;
    }

    public void setNbPlaylersCurrent(int nbPlaylersCurrent) {
        this.nbPlaylersCurrent = nbPlaylersCurrent;
    }

    public void addPlayer() {
        nbPlaylersCurrent ++;
    }

    public void removePlayer() {
        nbPlaylersCurrent --;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
