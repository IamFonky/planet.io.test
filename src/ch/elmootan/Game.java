package ch.elmootan;

import ch.elmootan.universe.Planet;
import ch.elmootan.universe.Universe;

import java.awt.*;

public class Game {

    public static void main(String... args) {
        new Universe();
        Universe monUnivers = new Universe();

        Planet terre = monUnivers.addNewPlanet("Terre", 0, 0, 5.9736E24, 6371, Color.green);
    }

}